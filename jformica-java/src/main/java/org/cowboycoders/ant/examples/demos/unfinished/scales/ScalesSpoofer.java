package org.cowboycoders.ant.examples.demos.unfinished.scales;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import org.cowboycoders.ant.Channel;
import org.cowboycoders.ant.Node;
import org.cowboycoders.ant.events.BroadcastListener;
import org.cowboycoders.ant.examples.NetworkKeys;
import org.cowboycoders.ant.examples.Utils;
import org.cowboycoders.ant.interfaces.AntTransceiver;
import org.cowboycoders.ant.messages.ChannelType;
import org.cowboycoders.ant.messages.MasterChannelType;
import org.cowboycoders.ant.messages.data.BroadcastDataMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/**
 * Connects to ANT+ enabled scales e.g tanita (untested)
 * 
 * @author will
 * 
 */
public class ScalesSpoofer {
	
	
	private static class Listener implements BroadcastListener<BroadcastDataMessage> {
		
		/*
		 * Once an instance of this class is registered with a channel, 
		 * this is called every time a broadcast message is received
		 * on that channel.
		 * 
		 * (non-Javadoc)
		 * @see org.cowboycoders.ant.events.BroadcastListener#receiveMessage(java.lang.Object)
		 */
		@Override
		public void receiveMessage(BroadcastDataMessage message) {
			/*
			 * getData() returns the 8 byte payload. The current heart rate
			 * is contained in the last byte.
			 * 
			 * Note: remember the lack of unsigned bytes in java, so unsigned values
			 * should be converted to ints for any arithmetic / display - getUnsignedData()
			 * is a utility method to do this.
			 */
			for (byte b : message.getData()) {
				System.out.printf("%02x:",b);
			}
			System.out.println();
		}

	}

	/*
	 * See ANT+ data sheet for explanation
	 */
	private static final int SCALES_CHANNEL_PERIOD = 8192;

	/*
	 * See ANT+ data sheet for explanation
	 */
	private static final int ANTPLUS_CHANNEL_FREQ = 57;

	/*
	 * This should match the device you are connecting with. Some devices are
	 * put into pairing mode (which sets this bit).
	 * 
	 * Note: Many ANT+ sport devices do not set this bit (eg. HRM strap).
	 * 
	 * See ANT+ docs.
	 */
	private static final boolean SCALES_PAIRING_FLAG = false;

	/*
	 * Should match device transmission id (0-255). Special rules apply for
	 * shared channels. See ANT+ protocol.
	 * 
	 * 0: wildcard, matches any value (slave only)
	 */
	private static final int SCALES_TRANSMISSION_TYPE = 0;

	/*
	 * device type for ANT+ heart rate monitor
	 */
	private static final int SCALES_DEVICE_TYPE = 119;

	/*
	 * You should make a note of the device id and use it in preference to the
	 * wild card to pair to a specific device.
	 * 
	 * 0: wild card, matches all device ids any other number: match specific
	 * device id
	 */
	private static final int SCALES_DEVICE_ID = 1;
	
	private static byte page = 80;

	public static final Level LOG_LEVEL = Level.OFF;

	public static void setupLogging() {
		// set logging level
		AntTransceiver.LOGGER.setLevel(LOG_LEVEL);
		ConsoleHandler handler = new ConsoleHandler();
		// PUBLISH this level
		handler.setLevel(LOG_LEVEL);
		AntTransceiver.LOGGER.addHandler(handler);
		// Don't duplicate messages by sending to parent handler as well
		AntTransceiver.LOGGER.setUseParentHandlers(false);
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


		final Channel channel = node.getFreeChannel();

		// Arbitrary name : useful for identifying channel
		channel.setName("C:SCALES");

		// choose slave or master type. Constructors exist to set
		// two-way/one-way and shared/non-shared variants.
		ChannelType channelType = new MasterChannelType();

		// use ant network key "N:ANT+"
		channel.assign(NetworkKeys.ANT_SPORT, channelType);

		// registers an instance of our callback with the channel
		 channel.registerRxListener(new Listener(), BroadcastDataMessage.class);

		/******* start device specific configuration ******/

		channel.setId(SCALES_DEVICE_ID, SCALES_DEVICE_TYPE,
				SCALES_TRANSMISSION_TYPE, SCALES_PAIRING_FLAG);

		channel.setFrequency(ANTPLUS_CHANNEL_FREQ);

		channel.setPeriod(SCALES_CHANNEL_PERIOD);

		/******* end device specific configuration ******/

		// timeout before we give up looking for device
		channel.setSearchTimeout(Channel.SEARCH_TIMEOUT_NEVER);

		// start listening
		channel.open();

		// hack to send new data (very fragile)
		TimerTask tasknew = new TimerTask() {

			@Override
			public void run() {
				BroadcastDataMessage msg = new BroadcastDataMessage();

				BufferedReader br = null;
				String unparsed = null;

				try {

					try {
						br = new BufferedReader(new FileReader("res"
								+ File.separatorChar + "scales_data"));
						unparsed = br.readLine();
					} finally {
						if (br != null) br.close();
					}

				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				String[] split = unparsed.split(":");
				byte[] data = new byte[8];

				int i = 0;
				for (String s : split) {
					data[i] = (byte) Integer.parseInt(s,16);
					i++;
				}
				
				//data[0] = page;
				data[0] = 0x01;
				//page++;
				
				//if(page > 100) {
				//	page = 0;
				//}

				msg.setData(data);
				channel.send(msg);
			}

		};
		Timer timer = new Timer();

		// send updated data once per second
		timer.scheduleAtFixedRate(tasknew, 1000, 250);

		// Listen for 60 seconds
		Thread.sleep(30000);

		// stop listening
		channel.close();

		// optional : demo requesting of channel configuration. If device
		// connected
		// this will reflect actual device id, transmission type etc. This info
		// will allow
		// you to only connect to this device in the future.
		Utils.printChannelConfig(channel);

		// resets channel configuration
		channel.unassign();

		// return the channel to the pool of available channels
		node.freeChannel(channel);

		// cleans up : gives up control of usb device etc.
		node.stop();

	}

}
