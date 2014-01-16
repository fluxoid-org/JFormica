package org.cowboycoders.ant.examples.demos;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import org.cowboycoders.ant.Channel;
import org.cowboycoders.ant.ChannelId;
import org.cowboycoders.ant.Node;
import org.cowboycoders.ant.TransferException;
import org.cowboycoders.ant.events.BroadcastListener;
import org.cowboycoders.ant.events.MessageCondition;
import org.cowboycoders.ant.events.MessageConditionFactory;
import org.cowboycoders.ant.examples.NetworkKeys;
import org.cowboycoders.ant.interfaces.AntTransceiver;
import org.cowboycoders.ant.messages.ChannelType;
import org.cowboycoders.ant.messages.DeviceInfoQueryable;
import org.cowboycoders.ant.messages.MasterChannelType;
import org.cowboycoders.ant.messages.SlaveChannelType;
import org.cowboycoders.ant.messages.data.AcknowledgedDataMessage;
import org.cowboycoders.ant.messages.data.BroadcastDataMessage;

/**
 * Sends / receives a burst and prints channel id from extended bytes. Requires 2 ant chips!
 * @author will
 *
 */
public class AcknowledgedDemo {
	
	private static class Listener implements BroadcastListener<BroadcastDataMessage> {
		
		boolean doOnce = true;
		private Channel master;
		boolean printed = false;
		
		Listener(Channel master) {
			this.master = master;
		}

		@Override
		public void receiveMessage(BroadcastDataMessage message) {
			if (doOnce) {
				doOnce = false;
				new Thread() {
					public void run() {
						sendAck();
					}
				}.start();
			}
			if (message instanceof DeviceInfoQueryable) {
				if (!printed) {
					printed = true;
					DeviceInfoQueryable d = (DeviceInfoQueryable) message;
					System.out.println();
					System.out.println("Ex. id:" + d.getDeviceNumber());
					System.out.println("Ex. tran:" + d.getTransmissionType());
					System.out.println("Ex. type:" + (d.getDeviceType()));
					System.out.println();
				}

			}
		}
		
		public void sendAck() {
			sendAnAck(master);

		}
	}
	
	// probably should make this a utility	
	public static void sendAnAck(Channel master) {
		try {
			
		// any left other bytes will be filled with zeros
		AcknowledgedDataMessage msg = new AcknowledgedDataMessage();
		
		final MessageCondition condition = MessageConditionFactory.newAcknowledgedCondition();

		master.sendAndWaitForMessage(msg, condition, 1L, TimeUnit.SECONDS, null);
		
		System.out.println("sent ack ..");
	} catch (TransferException | InterruptedException
			| TimeoutException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	private static class AckListener implements BroadcastListener<AcknowledgedDataMessage> {

		@Override
		public void receiveMessage(AcknowledgedDataMessage message) {
			System.out.println("got ack successfully");
			
		}
		

	}
	
	/*
	 * See ANT+ data sheet for explanation
	 */
	private static final int HRM_CHANNEL_PERIOD = 8070;
	
	/*
	 * See ANT+ data sheet for explanation
	 */
	private static final int HRM_CHANNEL_FREQ = 57;
	
	/*
	 * This should match the device you are connecting with.
	 * Some devices are put into pairing mode (which sets this bit).
	 * 
	 * Note: Many ANT+ sport devices do not set this bit (eg. HRM strap).
	 * 
	 * See ANT+ docs.
	 */
	private static final boolean HRM_PAIRING_FLAG = true;

	/*
	 * Should match device transmission id (0-255). Special rules
	 * apply for shared channels. See ANT+ protocol.
	 * 
	 * 0: wildcard, matches any value (slave only) 
	 */
	private static final int HRM_TRANSMISSION_TYPE = 1;
	
	/*
	 * device type for ANT+ heart rate monitor 
	 */
	private static final int HRM_DEVICE_TYPE = 120;
	
	private static final int MASTER_ID = 1234;
	
	private static final ChannelId HRM_WILDCARD_CHANNEL_ID = ChannelId.Builder.newInstance()
			.setDeviceNumber(ChannelId.WILDCARD)
			.setDeviceType(HRM_DEVICE_TYPE)
			.setTransmissonType(ChannelId.WILDCARD)
			.setPairingFlag(HRM_PAIRING_FLAG)
			.build();
	
	private static final ChannelId HRM_MASTER_CHANNEL_ID = ChannelId.Builder.newInstance()
			.setDeviceNumber(MASTER_ID)
			.setDeviceType(HRM_DEVICE_TYPE)
			.setTransmissonType(HRM_TRANSMISSION_TYPE)
			.setPairingFlag(HRM_PAIRING_FLAG)
			.build();
	
	private static final ChannelType MASTER_TYPE = new MasterChannelType();
	
	private static final ChannelType SLAVE_TYPE = new SlaveChannelType();
	
	public static final Level LOG_LEVEL = Level.SEVERE;
	
	public static void setupHrmChannel(Channel channel, ChannelType type, String name, ChannelId id) {
		// Arbitrary name : useful for identifying channel
		channel.setName(name);
		
		// use ant network key "N:ANT+" 
		channel.assign(NetworkKeys.ANT_SPORT, type);
		
		/******* start device specific configuration ******/

		channel.setId(id);

		channel.setFrequency(HRM_CHANNEL_FREQ);

		channel.setPeriod(HRM_CHANNEL_PERIOD);
		
		/******* end device specific configuration ******/
		
		if (type instanceof SlaveChannelType) {
			// timeout before we give up looking for device
			channel.setSearchTimeout(Channel.SEARCH_TIMEOUT_NEVER);
		}
		

	}
	
	public static void main(String[] args) throws InterruptedException, TransferException, TimeoutException {
		
		setupLogging();
		
		AntTransceiver antchip = new AntTransceiver(0);
		AntTransceiver antchip2 = new AntTransceiver(1);
		
		Node node = new Node(antchip);
		Node node2 = new Node(antchip2);
		
		
		/* must be called before any configuration takes place */
		node.start();
		node2.start();
		
		/* sends reset request : resets channels to default state */
		node.reset();
		
		node.setLibConfig(true, false, false);
		
		final Channel master = node2.getFreeChannel();
		
		setupHrmChannel(master, MASTER_TYPE, "master", HRM_MASTER_CHANNEL_ID);
		
		Channel slave = node.getFreeChannel();
		
		setupHrmChannel(slave, SLAVE_TYPE, "slave", HRM_WILDCARD_CHANNEL_ID);
		
		slave.registerRxListener(new Listener(master), BroadcastDataMessage.class);
		slave.registerRxListener(new AckListener(), AcknowledgedDataMessage.class);
		
		master.open();
		
		// should fail (slave not open yet)
		sendAnAck(master);
	
		slave.open();
		
		Thread.sleep(10000);
		
		node2.freeChannel(master);
		node.freeChannel(slave);
		
		node2.stop();
		node.stop();
		
	}
	
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

	
	

}
