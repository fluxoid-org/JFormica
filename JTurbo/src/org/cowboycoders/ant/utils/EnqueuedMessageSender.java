package org.cowboycoders.ant.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.cowboycoders.ant.Channel;
import org.cowboycoders.ant.events.MessageCondition;
import org.cowboycoders.ant.messages.ChannelMessage;

public class EnqueuedMessageSender implements ChannelMessageSender {
	
	private boolean send = true;
	private ExecutorService channelExecutorService = Executors.newSingleThreadExecutor();
	public final static Logger LOGGER = Logger.getLogger(AntUtils.class .getName());
	private Channel channel;
	private static final MessageCondition DEFAULT_CONDITION = AntUtils.CONDITION_CHANNEL_TX;
	private static final long DEFAULT_TIMEOUT_DURATION = 10L;
	private static final TimeUnit DEFAULT_TIMEOUT_TIMEUNIT = TimeUnit.SECONDS;
	
	  /**
	   * If locked should not send data packets;
	   */
	  private Lock sendLock = new ReentrantLock();
	  
	public EnqueuedMessageSender(Channel channel) {
		this.channel = channel;
	}
	

    @Override
    public void sendMessage(final ChannelMessage msg, final Runnable callback) {
      channelExecutorService.execute(new Runnable() {

        @Override
        public void run() {
          
          try {
            sendLock.lock();
              if (!send) {
                if (callback != null) {
                  callback.run();
                }
                return;
              }
          } finally {
            sendLock.unlock();
          }
          
          try {
            channel.sendAndWaitForMessage(msg, DEFAULT_CONDITION, DEFAULT_TIMEOUT_DURATION, DEFAULT_TIMEOUT_TIMEUNIT, null);
          } catch (Exception e) {
            LOGGER.severe("Message send failed");
          }
          
          if(callback != null) {
            callback.run();
          }
          
        }});
      
    }

    @Override
    public void sendMessage(ChannelMessage msg) {
      sendMessage(msg,null);
      
    }
    
    public void pause(boolean pause) {
    	try {
    		sendLock.lock();
    		send = !pause;
    	} finally {
    		sendLock.unlock();
    	}
    }

}
