package org.cowboycoders.ant.examples;

import java.util.concurrent.TimeUnit;

import org.cowboycoders.ant.Channel;
import org.cowboycoders.ant.ChannelId;
import org.cowboycoders.ant.events.MessageCondition;
import org.cowboycoders.ant.events.MessageConditionFactory;
import org.cowboycoders.ant.messages.commands.ChannelRequestMessage;
import org.cowboycoders.ant.messages.commands.ChannelRequestMessage.Request;
import org.cowboycoders.ant.messages.responses.ChannelIdResponse;

/**
 * Contains some example utility functions
 * @author will
 *
 */
public class Utils {

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
	
	/**
	 * Requests a channels current channelId
	 * @param channel
	 * @return
	 */
	public static ChannelId requestChannelId(Channel channel) {
		// build request
		ChannelRequestMessage msg = new  ChannelRequestMessage(channel.getNumber(),Request.CHANNEL_ID);
		
		// response should be an instance of ChannelIdResponse
		MessageCondition condition = MessageConditionFactory.newInstanceOfCondition(ChannelIdResponse.class);
		
		try {
			
			// send request (blocks until reply received or timeout expired)
			ChannelIdResponse response = (ChannelIdResponse) channel.sendAndWaitForMessage(
					msg, condition, 5L, TimeUnit.SECONDS, null);
			
			return response.getChannelId();
			
		} catch (Exception e) {
			// not critical, so just print error
			e.printStackTrace();
		}
		
		return null;
	}
	

}
