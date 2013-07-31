/**
 *     Copyright (c) 2013, Will Szumski
 *
 *     This file is part of formicidae.
 *
 *     formicidae is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     formicidae is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with formicidae.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cowboycoders.ant.events;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import java.util.logging.Logger;

import org.cowboycoders.ant.interfaces.AntChipInterface;
import org.cowboycoders.ant.messages.AntMessageFactory;
import org.cowboycoders.ant.messages.MessageException;
import org.cowboycoders.ant.messages.MessageMetaWrapper;
import org.cowboycoders.ant.messages.StandardMessage;




public class EventMachine  {
  
  /**
   * guards list of received messages
   */
  //private final Lock rxQueueLock = new ReentrantLock();
  
  //private Condition rxQueueChanged = rxQueueLock.newCondition();
  
  //private boolean rxQueueEmpty = true;
  

  private AntChipInterface chipInterface;
  
  public final static Logger LOGGER = Logger.getLogger(EventMachine.class .getName()); 
  
  private BroadcastMessenger<byte []> rawMessenger;
  
  private BroadcastMessenger<StandardMessage> convertedMessenger;
  
  private boolean running = false;
  
  
  private class EventPump implements BroadcastListener<byte []> {

    @Override
    public void receiveMessage(byte[] message) {
      StandardMessage msg = null;
      try {
       msg = AntMessageFactory.createMessage(message);
     } catch (MessageException e) {
       LOGGER.warning("Error converting raw data to type StandardMessage");
     }
     
      if(msg != null) {
        LOGGER.finer("received :" + msg.getClass());
        convertedMessenger.sendMessage(msg);
      } else {
        LOGGER.warning("Ignoring data packet");
      }
    }
    
  }
  
  private class IncomingMessageListener implements BroadcastListener<StandardMessage> {
	
	private MessageMetaWrapper<StandardMessage> wrappedMessage;
	private Lock messageUpdateLock;
	private Condition replyRecieved;
	private MessageCondition condition;
	
	public IncomingMessageListener(Lock messageUpdateLock, MessageCondition condition){
		this.condition = condition;
		this.messageUpdateLock = messageUpdateLock;
		this.replyRecieved = messageUpdateLock.newCondition();
	}

    @Override
    public void receiveMessage(StandardMessage message) {
    	
    	try {
    		if(!condition.test(message)) return;
    	} catch (Exception e) {
    		// we re-test the condition later back on calling thread
    	}
      
	    MessageMetaWrapper<StandardMessage> wrappedMessage =
	         new MessageMetaWrapper<StandardMessage>(message);
	    
	    try {
	    	messageUpdateLock.lock();
	    	this.wrappedMessage = wrappedMessage;
	    	replyRecieved.signalAll();
	    } finally {
	    	messageUpdateLock.unlock();
	    }

          
     }
    
    public MessageMetaWrapper<StandardMessage> getReply(Long timeout, TimeUnit timeoutUnit) throws InterruptedException, TimeoutException {
        final long timeoutNano = timeout != null ? TimeUnit.NANOSECONDS.convert(timeout, timeoutUnit) : 0L;
        final long initialTimeStamp = MessageMetaWrapper.getCurrentTimestamp();
	    try {
	    	messageUpdateLock.lock();
	    	while (wrappedMessage == null) {
	            if (timeout != null) {
	                long timeoutRemaining = timeoutNano - (MessageMetaWrapper.getCurrentTimestamp() - initialTimeStamp);
	                if(!replyRecieved.await(timeoutRemaining, TimeUnit.NANOSECONDS)) {
	                  throw new TimeoutException("timeout waiting for message");
	                }
	              } else {
	            	  replyRecieved.await();
	              }
	    	}
	    } finally {
	    	messageUpdateLock.unlock();
	    }
	    
	    // retest to throw an exception on calling thread
	    condition.test(wrappedMessage.unwrap());
	    
	    return wrappedMessage;
    }
        

  }

  public EventMachine(AntChipInterface chipInterface) {
    this.chipInterface = chipInterface;
    this.rawMessenger = new BroadcastMessenger<byte []>();
    this.convertedMessenger = new BroadcastMessenger<StandardMessage>();
    chipInterface.registerRxMesenger(rawMessenger);
    rawMessenger.addBroadcastListener(new EventPump());
  }
  
  public void registerRxListener(BroadcastListener<StandardMessage> listener) {
    convertedMessenger.addBroadcastListener(listener);
  }
  
  public void removeRxListener(BroadcastListener<StandardMessage> listener) {
    convertedMessenger.removeBroadcastListener(listener);
  }
  
  public static Logger getLogger() {
    return LOGGER;
  }
  
  
  public MessageMetaWrapper<StandardMessage> waitForCondition( 
      MessageCondition msgCondition,
      Long timeout, TimeUnit timeoutUnit, LockExchangeContainer lockExchanger) 
          throws InterruptedException, TimeoutException {
    
    Lock msgLock = new ReentrantLock();
    
    try {
      msgLock.lock();
      
      IncomingMessageListener listener = new IncomingMessageListener(msgLock, msgCondition);
      
      registerRxListener(listener);
      
      
      if(lockExchanger != null) {
        try {
          lockExchanger.lock.lock();
          lockExchanger.returnLock = msgLock;
          lockExchanger.lockAvailable.signalAll();
        } finally {
          lockExchanger.lock.unlock();
        }
      }
      
      MessageMetaWrapper<StandardMessage> rtn = null;
      
      // don't leave extraneous listeners if an exception is thrown
      try {
          rtn = listener.getReply(timeout, timeoutUnit);
      } finally {
          removeRxListener(listener);
      }
      
      return rtn;
    
      
    } finally {
      msgLock.unlock();
    }
  }
  

  public synchronized void start() {
    if (running) return;
    chipInterface.start();
    running = true;
  }
  
  public synchronized void stop() {
    if (!running) return;
    chipInterface.stop();
    running  = false;
  }
  
  public synchronized  boolean isRunning() {
    return running;
  }

  
  
  

  
  

}
