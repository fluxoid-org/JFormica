package org.cowboycoders.ant.examples.demos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import org.cowboycoders.ant.Channel;
import org.cowboycoders.ant.DefaultChannelEventHandler;
import org.cowboycoders.ant.Node;
import org.cowboycoders.ant.ChannelId;
import org.cowboycoders.ant.events.BroadcastListener;
import org.cowboycoders.ant.examples.NetworkKeys;
import org.cowboycoders.ant.examples.Utils;
import org.cowboycoders.ant.interfaces.AntTransceiver;
import org.cowboycoders.ant.messages.ChannelType;
import org.cowboycoders.ant.messages.SlaveChannelType;
import org.cowboycoders.ant.messages.data.BroadcastDataMessage;

/**
 * Bit of a hack to demonstrate multiple devices / blacklists . Improvements welcomed ...
 * @author will
 *
 */
public class MultiHeartRateMonitor {
	
	
	/**
	 * Listener class for receiving data
	 * @author will
	 *
	 */
	private static class Listener implements BroadcastListener<BroadcastDataMessage> {
		
		private String id;

		public Listener(String hrmId) {
			this.id = hrmId;
		}
		
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
			System.out.println("Heart rate ("+ id +"): " + message.getUnsignedData()[7]);
		}

	}
	
	/**
	 * This is the listener for the initial search
	 * @author will
	 *
	 */
	private static class SearchListener implements BroadcastListener<BroadcastDataMessage> {
		
		private Channel channel;
		private Set<ChannelId> found;
		private Lock lock;
		private boolean killed = false;

		public void kill() {
			killed = true;
		}
		/**
		 * 
		 * @param channel the search channel
		 * @param list of channels to add to
		 */
		public SearchListener(Channel channel, Lock channelLock, Set<ChannelId> found) {
			this.channel = channel;
			this.found = found;
			this.lock = channelLock;
		}

		@Override
		public void receiveMessage(BroadcastDataMessage message) {
			
			// don't block the messenger thread
			new Thread() {
				public void run() {
					doWork();
				}
			}.start();
			
		}
		
		private void doWork() {
			try {
				lock.lock();
				
				if (killed) return;
				
				ChannelId channelId = Utils.requestChannelId(channel);
				
				// don't add wildcards to found devices list
				if (channelId.equals(HRM_WILDCARD_CHANNEL_ID)) {
					return;
				}
				
				// if already in set
				if(!found.add(channelId)) {
					return;
				}
				
				if (found.size() > 4) {
					System.out.println("reached maximum of 4 devices");
					return;
				}
				
				// close so we can reset it back to a wildcard
				channel.close();
				
				System.out.println("found a device: ");
				Utils.printChannelConfig(channel);
				
				channel.blacklist(found.toArray(new ChannelId[] {}));
				
				// set back to wildcard
				channel.setId(HRM_WILDCARD_CHANNEL_ID);
				
				//reopen
				channel.open();
				
				
			} finally {
				lock.unlock();
			}
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
	private static final boolean HRM_PAIRING_FLAG = false;

	/*
	 * Should match device transmission id (0-255). Special rules
	 * apply for shared channels. See ANT+ protocol.
	 * 
	 * 0: wildcard, matches any value (slave only) 
	 */
	private static final int HRM_TRANSMISSION_TYPE = 0;
	
	/*
	 * device type for ANT+ heart rate monitor 
	 */
	private static final int HRM_DEVICE_TYPE = 120;
	
	private static final ChannelId HRM_WILDCARD_CHANNEL_ID = ChannelId.Builder.newInstance()
			.setDeviceNumber(ChannelId.WILDCARD)
			.setDeviceType(HRM_DEVICE_TYPE)
			.setTransmissonType(HRM_TRANSMISSION_TYPE)
			.build();
	
	
	public static final Level LOG_LEVEL = Level.SEVERE;
	
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
	
	public static void setupHrmChannel(Channel channel, String name, ChannelId id) {
		// Arbitrary name : useful for identifying channel
		channel.setName(name);
		
		// choose slave or master type. Constructors exist to set two-way/one-way and shared/non-shared variants.
		ChannelType channelType = new SlaveChannelType();
		
		// use ant network key "N:ANT+" 
		channel.assign(NetworkKeys.ANT_SPORT, channelType);
		
		
		// registers an instance of our callback with the channel
		channel.registerRxListener(new Listener(name), BroadcastDataMessage.class);
		
		/******* start device specific configuration ******/

		channel.setId(id.getDeviceNumber(), HRM_DEVICE_TYPE, HRM_TRANSMISSION_TYPE, HRM_PAIRING_FLAG);

		channel.setFrequency(HRM_CHANNEL_FREQ);

		channel.setPeriod(HRM_CHANNEL_PERIOD);
		
		/******* end device specific configuration ******/
		
		// timeout before we give up looking for device
		channel.setSearchTimeout(Channel.SEARCH_TIMEOUT_NEVER);
	}
	
	public static void setupSearchChannel(Channel channel, SearchListener listener) {
		// Arbitrary name : useful for identifying channel
		channel.setName("C:HRM_SEARCH");
		
		// choose slave or master type. Constructors exist to set two-way/one-way and shared/non-shared variants.
		ChannelType channelType = new SlaveChannelType();
		
		// use ant network key "N:ANT+" 
		channel.assign(NetworkKeys.ANT_SPORT, channelType);
		
		
		// registers an instance of our callback with the channel
		channel.registerRxListener(listener, BroadcastDataMessage.class);
		
		/******* start device specific configuration ******/

		channel.setId(HRM_WILDCARD_CHANNEL_ID);

		channel.setFrequency(HRM_CHANNEL_FREQ);

		channel.setPeriod(HRM_CHANNEL_PERIOD);
		
		/******* end device specific configuration ******/
		
		// timeout before we give up looking for device
		channel.setSearchTimeout(Channel.SEARCH_TIMEOUT_NEVER);
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		// optional: enable console logging with Level = LOG_LEVEL
		setupLogging();
		
		/*
		 * Choose driver: AndroidAntTransceiver or AntTransceiver
		 * 
		 * AntTransceiver(int deviceNumber)
		 * deviceNumber : 0 ... number of usb sticks plugged in
		 * 0: first usb ant-stick
		 */
		AntTransceiver antchip = new AntTransceiver(0);
		
		// initialises node with chosen driver 
		Node node = new Node(antchip);
		
		/* must be called before any configuration takes place */
		node.start();
		
		/* sends reset request : resets channels to default state */
		node.reset();
		
		Lock channelLock = new ReentrantLock();
		Set<ChannelId> devicesFound = new HashSet<ChannelId>(4);
		
		Channel searchChannel = node.getFreeChannel();
		
		SearchListener searchListener = new SearchListener(searchChannel,channelLock, devicesFound);
		setupSearchChannel(searchChannel,searchListener);
		
		searchChannel.open();
		
		// demonstrate event handlers 
		searchChannel.registerEventHandler(new DefaultChannelEventHandler() {

			@Override
			public void onChannelClosed() {
				System.out.println("Search channel closed...");
			}
			
			
		});
		
		System.out.println("Scanning for 30 seconds");
		
		// search for 30 seconds
		Thread.sleep(30000);
		
		// search listener may still be processing so we guard this
		try {
			channelLock.lock();
			searchChannel.close();
			searchListener.kill();
			node.freeChannel(searchChannel);
		} finally {
			channelLock.unlock();
		}
		
		System.out.println("Opening channels");
		
		List<Channel> channels = new ArrayList<Channel>();
		int count = 0;
		for (ChannelId id : devicesFound) {
			Channel channel = node.getFreeChannel();
			String name = "HRM-" + count++;
			setupHrmChannel(channel,name,id);
			channels.add(channel);
			channel.open();
		}
		
		System.out.println(channels.size() + " devices found");
		
		if (channels.size() > 0) {
			System.out.println("Listening for 60 seconds ...");
			// Listen for 60 seconds
			Thread.sleep(60000);
		}

		
		System.out.println("Shutting down ...");
		
		for (Channel channel: channels) {
			node.freeChannel(channel);
		}
		

		// cleans up : gives up control of usb device etc.
		node.stop();
		
	}
	


}
