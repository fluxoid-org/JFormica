package org.cowboycoders.ant.examples.demos;

import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import org.cowboycoders.ant.Channel;
import org.cowboycoders.ant.Node;
import org.cowboycoders.ant.events.BroadcastListener;
import org.cowboycoders.ant.events.MessageCondition;
import org.cowboycoders.ant.events.MessageConditionFactory;
import org.cowboycoders.ant.examples.NetworkKeys;
import org.cowboycoders.ant.interfaces.AntTransceiver;
import org.cowboycoders.ant.messages.ChannelType;
import org.cowboycoders.ant.messages.SlaveChannelType;
import org.cowboycoders.ant.messages.commands.ChannelRequestMessage;
import org.cowboycoders.ant.messages.commands.ChannelRequestMessage.Request;
import org.cowboycoders.ant.messages.data.BroadcastDataMessage;
import org.cowboycoders.ant.messages.responses.ChannelIdResponse;

/**
 * Speed and Cadence ANT+ processor.
 * 
 * @author David George
 * @date 11 June 2013
 */
class Listener implements BroadcastListener<BroadcastDataMessage> {
	private static int lastTs = 0;
	private static int lastTc = 0;
	private static int sRR = 0; // previous speed rotation measurement
	private static double totalDistance = 0.0; // total distance
	private static int cRR = 0; // previous cadence rotation measurement
	private static int sCount = 0;
	private static int cCount = 0;
	
	/**
	 * Speed and cadence data is contained in the 8 byte data payload in the
	 * message. Speed and Cadence have the same format. A short integer giving
	 * time since the last reading and a short integer giving the number of
	 * revolutions since the last reading.
	 * <p>
	 * The format is:<br/>
	 * [0][1] - Cadence timing<br/>
	 * [2][3] - Cadence revolutions<br/>
	 * [4][5] - Speed timing<br/>
	 * [6][7] - Speed revolutions<br/>
	 * <p>
	 * Values are little Endian (MSB byte is on the right)
	 * <p>
	 * So for timing: [0] + ([1] << 8) / 1024 gives the time in milliseconds
	 * since the last rollover. Note that you have to account for rollovers of
	 * both time and rotations which happen every 16 seconds/16384 revolutions.
	 * <p>
	 * There is another wrinkle. Messages are sent at at 4Hz rate. Below a
	 * certain rate (240rpm) we will see messages with the same number of
	 * rotations. This doesn't mean the wheel is stopped, just there was no new
	 * data since the last reading. To distinguish this from a stopped wheel a
	 * certain number of same value readings are ignored for speed or cadence
	 * updates.
	 */
	@Override
	public void receiveMessage(BroadcastDataMessage message) {
		double WHEEL_SIZE = 212.372; // 700C23

		int[] data = message.getUnsignedData();

		/*
		 * debug for (int i = 0; i < data.length; i++) {
		 * System.out.print(String.format("%02X ", data[i])); }
		 * System.out.println();
		 */

		// Bytes 0 and 1: TTTT / 1024 = milliSeconds since the last
		// rollover for cadence
		int tC = data[0]
				+ (data[1] << 8);

		// Bytes 2 and 3: Cadence rotation Count
		int cR = data[2]
				+ (data[3] << 8);

		// Bytes 4 and 5: TTTT / 1024 = milliSeconds since the last
		// rollover for speed
		int tS = data[4]
				+ (data[5] << 8);

		// Bytes 6 and 7: speed rotation count.
		int sR = data[6]
				+ (data[7] << 8);

		//System.out
		//		.println("tC " + tC + " cR " + cR + " tS " + tS + " sR " + sR);

		if (lastTs == 0 || lastTc == 0) {
			// first time through, initialize counters and return
			lastTs = tS;
			lastTc = tC;
			sRR = sR;
			cRR = cR;
			return;
		}

		int tD; // time delta
		if (tS < lastTs) {
			// we have rolled over
			tD = tS + (65536 - lastTs);
		} else {
			tD = tS - lastTs;
		}

		int sRD; // speed rotation delta
		if (sR < sRR) {
			// we have rolled over
			sRD = sR + (65536 - sRR);
		} else {
			sRD = sR - sRR;
		}

		double speed = 0.0;
		if (tD > 0) {
			double distanceKM = (sRD * WHEEL_SIZE) / 100000;
			totalDistance += distanceKM;
			double timeS = ((double) tD) / 1024.0;
			speed = distanceKM / (timeS / (60.0 * 60.0));
			sCount = 0;
		} else if (sCount < 12) {
			sCount++;
			speed = -1.0;
		}

		int cTD; // cadence time delta
		if (tC < lastTc) {
			// we have rolled over
			cTD = tC + (65536 - lastTc);
		} else {
			cTD = tC - lastTc;
		}

		int cRD; // cadence rotation delta
		if (cR < cRR) {
			// we have rolled over
			cRD = cR + (65536 - cRR);
		} else {
			cRD = cR - cRR;
		}

		double cadence = 0.0;
		if (cRD > 0) {
			double timeC = ((double) cTD) / 1024.0;
			cadence = cRD * ((1 / timeC) * 60.0);
			cCount = 0;
		} else if (cCount < 12) {
			cadence = -1.0;
			cCount++;
		}

		if (tD > 0) {
				System.out.printf("Distance %.3f km, Speed: %.2f km/h\n", totalDistance, speed);
		}
		if (cTD > 0) {
						System.out.printf("Cadence: %.0f\n", cadence);
		}

		lastTs = tS;
		lastTc = tC;
		cRR = cR;
		sRR = sR;
	}
}


/**
 * @author Will Szumskiroot
 * @author David George
 * 
 *         Based on Will's Heart Rate Monitor example
 */
public class BasicSpeedAndCadence {
	/*
	 * See ANT+ data sheet for explanation
	 */
	private static final int ANT_SPORT_SPEED_PERIOD = 8086;

	/*
	 * See ANT+ data sheet for explanation
	 */
	private static final int ANT_SPORT_FREQ = 57; // 0x39

	/*
	 * This should match the device you are connecting with. Some devices are
	 * put into pairing mode (which sets this bit).
	 * 
	 * Note: Many ANT+ sport devices do not set this bit (eg. HRM strap).
	 * 
	 * See ANT+ docs.
	 */
	private static final boolean HRM_PAIRING_FLAG = false;

	/*
	 * Should match device transmission id (0-255). Special rules apply for
	 * shared channels. See ANT+ protocol.
	 * 
	 * 0: wildcard, matches any value (slave only)
	 */
	private static final int HRM_TRANSMISSION_TYPE = 0;

	/*
	 * device type for ANT+ heart rate monitor
	 */
	
	private static final int ANT_SPORT_SandC_TYPE = 121; // 0x78

	/*
	 * You should make a note of the device id and use it in preference to the
	 * wild card to pair to a specific device.
	 * 
	 * 0: wild card, matches all device ids any other number: match specific
	 * device id
	 */
	private static final int HRM_DEVICE_ID = 0;

	public static final Level LOG_LEVEL = Level.SEVERE;

	public static void setupLogging() {
		// set logging level
		AntTransceiver.LOGGER.setLevel(LOG_LEVEL);
		ConsoleHandler handler = new ConsoleHandler();
		// PUBLISH this level
		handler.setLevel(LOG_LEVEL);
		AntTransceiver.LOGGER.addHandler(handler);
	}

	public static void printChannelConfig(Channel channel) {

		// build request
		ChannelRequestMessage msg = new ChannelRequestMessage(
				channel.getNumber(), Request.CHANNEL_ID);

		// response should be an instance of ChannelIdResponse
		MessageCondition condition = MessageConditionFactory
				.newInstanceOfCondition(ChannelIdResponse.class);

		try {

			// send request (blocks until reply received or timeout expired)
			ChannelIdResponse response = (ChannelIdResponse) channel
					.sendAndWaitForMessage(msg, condition, 5L,
							TimeUnit.SECONDS, null);

			System.out.println();
			System.out.println("Device configuration: ");
			System.out.println("deviceID: " + response.getDeviceNumber());
			System.out.println("deviceType: " + response.getDeviceType());
			System.out.println("transmissionType: "
					+ response.getTransmissionType());
			System.out.println("pairing flag set: "
					+ response.isPairingFlagSet());
			System.out.println();

		} catch (Exception e) {
			// not critical, so just print error
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InterruptedException {

		// optional: enable console logging with Level = LOG_LEVEL
		setupLogging();

		/*
		 * Choose driver: AndroidAntTransceiver or AntTransceiver
		 * 
		 * AntTransceiver(int deviceNumber) deviceNumber : 0 ... number of usb
		 * sticks plugged in 0: first usb ant-stick
		 */
		AntTransceiver antchip = new AntTransceiver(0);

		// initialises node with chosen driver
		Node node = new Node(antchip);

		/* must be called before any configuration takes place */
		node.start();

		/* sends reset request : resets channels to default state */
		node.reset();

		// specs say wait 500ms after reset before sending any more host
		// commands
		Thread.sleep(500);

		Channel channel = node.getFreeChannel();

		// Arbitrary name : useful for identifying channel
		channel.setName("C:SC");

		// choose slave or master type. Constructors exist to set
		// two-way/one-way and shared/non-shared variants.
		ChannelType channelType = new SlaveChannelType();

		// use ant network key "N:ANT+"
		channel.assign(NetworkKeys.ANT_SPORT, channelType);

		// registers an instance of our callback with the channel
		channel.registerRxListener(new Listener(), BroadcastDataMessage.class);

		/******* start device specific configuration ******/

		channel.setId(HRM_DEVICE_ID, ANT_SPORT_SandC_TYPE,
				HRM_TRANSMISSION_TYPE, HRM_PAIRING_FLAG);

		channel.setFrequency(ANT_SPORT_FREQ);

		channel.setPeriod(ANT_SPORT_SPEED_PERIOD);

		/******* end device specific configuration ******/

		// timeout before we give up looking for device
		channel.setSearchTimeout(Channel.SEARCH_TIMEOUT_NEVER);

		// start listening
		channel.open();

		// Listen for 120 seconds
		Thread.sleep(120000);

		// stop listening
		channel.close();

		// optional : demo requesting of channel configuration. If device
		// connected
		// this will reflect actual device id, transmission type etc. This info
		// will allow
		// you to only connect to this device in the future.
		printChannelConfig(channel);

		// resets channel configuration
		channel.unassign();

		// return the channel to the pool of available channels
		node.freeChannel(channel);

		// cleans up : gives up control of usb device etc.
		node.stop();
	}
}