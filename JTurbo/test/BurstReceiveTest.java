/*
*    Copyright (c) 2013, Will Szumski
*    Copyright (c) 2013, Doug Szumski
*
*    This file is part of Cyclismo.
*
*    Cyclismo is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Cyclismo is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Cyclismo.  If not, see <http://www.gnu.org/licenses/>.
*/

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import org.cowboycoders.ant.AntError;
import org.cowboycoders.ant.Channel;
import org.cowboycoders.ant.NetworkKey;
import org.cowboycoders.ant.NetworkKeys;
import org.cowboycoders.ant.Node;
import org.cowboycoders.ant.Receipt;
import org.cowboycoders.ant.TransferException;
import org.cowboycoders.ant.events.BroadcastListener;
import org.cowboycoders.ant.events.MessageCondition;
import org.cowboycoders.ant.events.MessageConditionFactory;
import org.cowboycoders.ant.interfaces.AntTransceiver;
import org.cowboycoders.ant.messages.ChannelMessage;
import org.cowboycoders.ant.messages.ChannelType;
import org.cowboycoders.ant.messages.MasterChannelType;
import org.cowboycoders.ant.messages.SlaveChannelType;
import org.cowboycoders.ant.messages.StandardMessage;
import org.cowboycoders.ant.messages.commands.ChannelRequestMessage;
import org.cowboycoders.ant.messages.commands.ChannelRequestMessage.Request;
import org.cowboycoders.ant.messages.commands.ResetMessage;
import org.cowboycoders.ant.messages.data.BroadcastDataMessage;
import org.cowboycoders.ant.messages.nonstandard.CombinedBurst;
import org.cowboycoders.ant.messages.responses.CapabilityResponse;
import org.cowboycoders.ant.messages.responses.ChannelIdResponse;
import org.cowboycoders.ant.utils.ByteUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * 
 * Hack to test BurstMessage. Requires two ant sticks. 
 * Needs to be converted to proper test. 
 * 
 * @author will
 *
 */
public class BurstReceiveTest {
  
  private static AntTransceiver antchip = new AntTransceiver(0);
  
  @BeforeClass 
  public static void beforeClass() {
    AntTransceiver.LOGGER.setLevel(Level.ALL);
    ConsoleHandler handler = new ConsoleHandler();
    // PUBLISH this level
    handler.setLevel(Level.ALL);
    AntTransceiver.LOGGER.addHandler(handler);
    //Node.LOGGER.setLevel(Level.ALL);
    //Node.LOGGER.addHandler(handler);
    StandardMessage msg = new ResetMessage();
    //StandardMessage msg = new BroadcastDataMessage();
    //antchip.start();
    //antchip.send(msg.encode());
    //antchip.send(msg.encode());
    //antchip.stop();
  }
  
  @AfterClass
  public static void afterClass() {
    antchip.stop();
    //antchip.stop();
  }
  
  @Before
  public void before() throws InterruptedException {
    //Thread.sleep(1000);
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
	
	/*
	 * You should make a note of the device id and use it in preference to the wild card
	 * to pair to a specific device.
	 * 
	 * 0: wild card, matches all device ids
	 * any other number: match specific device id
	 */
	private static final int HRM_DEVICE_ID = 0;
	
	
	public static final Level LOG_LEVEL = Level.OFF;
	
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
		ChannelRequestMessage msg = new  ChannelRequestMessage(channel.getNumber(),Request.CHANNEL_ID);
		
		// response should be an instance of ChannelIdResponse
		MessageCondition condition = MessageConditionFactory.newInstanceOfCondition(ChannelIdResponse.class);
		
		try {
			
			// send request (blocks until reply received or timeout expired)
			ChannelIdResponse response = (ChannelIdResponse) channel.sendAndWaitForMessage(
					msg, condition, 5L, TimeUnit.SECONDS, null);
			
			System.out.println();
			System.out.println("Device configuration: ");
			System.out.println("deviceID: " + response.getDeviceNumber());
			System.out.println("deviceType: " + response.getDeviceType());
			System.out.println("transmissionType: " + response.getTransmissionType());
			System.out.println("pairing flag set: " + response.isPairingFlagSet());
			System.out.println();
			
		} catch (Exception e) {
			// not critical, so just print error
			e.printStackTrace();
		}
	}
  
  
  
  BroadcastListener<CombinedBurst> burstListener = new BroadcastListener<CombinedBurst>() {

	@Override
	public void receiveMessage(CombinedBurst message) {
		for (int i : message.getUnsignedData()) {
			System.out.print(i+ " ");
		}
		for (CombinedBurst.StatusFlag flag : message.getStatusFlags()) {
			System.out.println(flag);
		}
		
	}
	  
  };
  
  @Test
  public void test() throws InterruptedException {
		/*
		 * Choose driver: AndroidAntTransceiver or AntTransceiver
		 * 
		 * AntTransceiver(int deviceNumber)
		 * deviceNumber : 0 ... number of usb sticks plugged in
		 * 0: first usb ant-stick
		 */
		AntTransceiver antchip = new AntTransceiver(0);
		
		setupLogging();
		
		// initialises node with chosen driver 
		Node node = new Node(antchip);
		
		
		/* must be called before any configuration takes place */
		node.start();
		
		/* sends reset request : resets channels to default state */
		node.reset();


		Channel channel = node.getFreeChannel();
		
		// Arbitrary name : useful for identifying channel
		channel.setName("C:HRM");
		
		// choose slave or master type. Constructors exist to set two-way/one-way and shared/non-shared variants.
		ChannelType channelType = new SlaveChannelType();
		
		// use ant network key "N:ANT+" 
		channel.assign(NetworkKeys.ANT_SPORT, channelType);
		
		
		/******* start device specific configuration ******/

		channel.setId(HRM_DEVICE_ID, HRM_DEVICE_TYPE, HRM_TRANSMISSION_TYPE, HRM_PAIRING_FLAG);

		channel.setFrequency(HRM_CHANNEL_FREQ);

		channel.setPeriod(HRM_CHANNEL_PERIOD);
		
		/******* end device specific configuration ******/
		
		// timeout before we give up looking for device
		channel.setSearchTimeout(Channel.SEARCH_TIMEOUT_NEVER);
		
		channel.registerBurstListener(burstListener);
		
		System.out.println("timeout: " + channel.getBurstTimeout());
		
		// start listening
		channel.open();
		
		fireBurst();
		
		// Listen for 10 seconds
		Thread.sleep(20000);
		
		// stop listening
		channel.close();
		
		// resets channel configuration
		channel.unassign();

		//return the channel to the pool of available channels
		node.freeChannel(channel);
		
		// cleans up : gives up control of usb device etc.
		node.stop();
  }
  
  public void fireBurst() {
	  new Thread() {
		  public void run() {
				    
				    Node node = new Node(new AntTransceiver(1));
				    
					// ANT+ key 
					
					/* must be called before any configuration takes place */
					node.start();
					
					/* sends reset request : resets channels to default state */
					node.reset();



					Channel channel = node.getFreeChannel();
					
					// Arbitrary name : useful for identifying channel
					channel.setName("C:HRM");
					
					// choose slave or master type. Constructors exist to set two-way/one-way and shared/non-shared variants.
					ChannelType channelType = new MasterChannelType();
					
					// use ant network key "N:ANT+" 
					channel.assign(NetworkKeys.ANT_SPORT, channelType);
				    
				    
					/******* start device specific configuration ******/

					channel.setId(HRM_DEVICE_ID, HRM_DEVICE_TYPE, HRM_TRANSMISSION_TYPE, HRM_PAIRING_FLAG);

					channel.setFrequency(HRM_CHANNEL_FREQ);

					channel.setPeriod(HRM_CHANNEL_PERIOD);
					
					/******* end device specific configuration ******/
					
					// timeout before we give up looking for device
					channel.setSearchTimeout(Channel.SEARCH_TIMEOUT_NEVER);
				    
				    channel.open();
				    
				    try {
								Thread.sleep(4000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				    
				    int length = 248;
				    byte [] test = new byte[length];
				    for (int i = 0 ; i < length ; i ++) {
				     test[i] = (byte) i;
				   }
				    
					for (int i : ByteUtils.unsignedBytesToInts(ByteUtils.boxArray(test))) {
						System.out.print(i+ " ");
					}
				    
				    try {
				      channel.sendBurst(test, 10L, TimeUnit.SECONDS);
				      System.out.println("transfer completed");
				    } catch (TransferException e) {
				      System.out.println(e);
				    } catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				    
				    
				    
				    try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				    
				    channel.close();
				    channel.unassign();
				    
				    node.freeChannel(channel);
				    node.stop();

	  }
	  }.start();
  }

  
  

}
