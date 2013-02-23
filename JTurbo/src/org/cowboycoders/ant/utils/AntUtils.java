package org.cowboycoders.ant.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.cowboycoders.ant.Channel;
import org.cowboycoders.ant.events.MessageCondition;
import org.cowboycoders.ant.events.MessageConditionFactory;
import org.cowboycoders.ant.messages.ChannelMessage;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.StandardMessage;
import org.cowboycoders.ant.messages.data.BroadcastDataMessage;
import org.cowboycoders.ant.messages.responses.ResponseCode;

public class AntUtils {
	
	private AntUtils() {}
	
	public final static Logger LOGGER = Logger.getLogger(AntUtils.class .getName());
	
	public static final MessageCondition CONDITION_CHANNEL_TX = MessageConditionFactory
			.newResponseCondition(MessageId.EVENT, ResponseCode.EVENT_TX);
	
	public static byte[] padToDataLength(int[] array) {
		byte[] rtn = new byte[8];
		for (int i = 0; i < array.length; i++) {
			rtn[i] = (byte) array[i];
		}
		return rtn;
	}
	
	public static StandardMessage sendAndRetry(final Channel channel,final ChannelMessage msg, final MessageCondition condition,final int maxRetries, final long timeoutPerRetry, final TimeUnit timeoutUnit) throws InterruptedException, TimeoutException {
	    StandardMessage response = null;
	    int retries = 0;
	    while (response == null) {
	      try {
	        response = channel.sendAndWaitForMessage(
	            msg, 
	            condition,
	            timeoutPerRetry,timeoutUnit, 
	            null
	            );
	      } catch  (TimeoutException e){
	        LOGGER.finer("sendAndRetry : timeout");
	        retries++;
	        if (retries >= maxRetries) {
	          throw e;
	        }
	      }
	    }
	    return response;
	  }
	
	  public static BroadcastDataMessage buildBroadcastMessage(byte [] data) {
		    BroadcastDataMessage msg = new BroadcastDataMessage();
		    msg.setData(data);
		    return msg;
		}

}
