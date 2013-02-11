/**
 *     Copyright (c) 2012, Will Szumski
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import java.util.logging.Logger;

import org.cowboycoders.ant.BufferedNodeComponent;
import org.cowboycoders.ant.interfaces.AntChipInterface;
import org.cowboycoders.ant.messages.AntMessageFactory;
import org.cowboycoders.ant.messages.MessageException;
import org.cowboycoders.ant.messages.MessageMetaWrapper;
import org.cowboycoders.ant.messages.StandardMessage;
import org.cowboycoders.ant.messages.responses.ChannelResponse;
import org.cowboycoders.ant.utils.SharedBuffer;




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
  
  
  private Set<SharedBuffer<MessageMetaWrapper<ChannelResponse>>> ackBuffers = 
      Collections.newSetFromMap(new WeakHashMap<SharedBuffer<MessageMetaWrapper<ChannelResponse>>,Boolean>());
  
  private Set<SharedBuffer<MessageMetaWrapper<StandardMessage>>> msgBuffers = 
      Collections.newSetFromMap(new WeakHashMap<SharedBuffer<MessageMetaWrapper<StandardMessage>>,Boolean>());
  
  /**
   * Shred lock for all buffers
   */
  private Lock bufferLock = new ReentrantLock();
  
  
  public void registerBufferedNodeComponent(BufferedNodeComponent component) {
    try {
      bufferLock.lock();
      ackBuffers.add(component.getAckBuffer());
      msgBuffers.add(component.getMsgBuffer());
      
    } finally {
      bufferLock.unlock();
    }
    
  }
  
  public void unregisterBufferedNodeComponent(BufferedNodeComponent component) {
    try {
      bufferLock.lock();
      ackBuffers.remove(component.getAckBuffer());
      msgBuffers.remove(component.getMsgBuffer());
      
    } finally {
      bufferLock.unlock();
    }
    
  }
  
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
  
  private interface BufferUpdater<V extends StandardMessage> {
    public abstract void updateBuffer(V msg, SharedBuffer<MessageMetaWrapper<V>> buffer );
    
  }
  

  private <V extends StandardMessage> void updateAllBuffers(V msg, Set<SharedBuffer<MessageMetaWrapper<V>>> buffers, 
      BufferUpdater<V> updater) {
    try{
      bufferLock.lock();
      
      for (SharedBuffer<MessageMetaWrapper<V>> buffer : buffers) {
        LOGGER.finer("updating buffer :" + buffer.toString());
        updater.updateBuffer(msg, buffer);
      }
      
      
    } finally {
      bufferLock.unlock();
    }
    

  }
  
  
  private class AckListener implements BroadcastListener<StandardMessage> {

    @Override
    public void receiveMessage(StandardMessage message) {
      if (message instanceof ChannelResponse) {
        
        updateAllBuffers((ChannelResponse)message, ackBuffers, new BufferUpdater<ChannelResponse>() {

          @Override
          public void updateBuffer(ChannelResponse msg, SharedBuffer<MessageMetaWrapper<ChannelResponse>> buffer) {
            Lock lock = buffer.getLock();
            Condition contentsChanged = buffer.getContentsChanged();
            FixedSizeBuffer<MessageMetaWrapper<ChannelResponse>> fixedBuffer = 
                buffer.getMsgBuffer();
            try {
              lock.lock();
              MessageMetaWrapper<ChannelResponse> wrappedMessage =
                   new MessageMetaWrapper<ChannelResponse>(msg);
              fixedBuffer.offer(wrappedMessage);
              //LOGGER.finer(fixedBuffer.toString());
              contentsChanged.signalAll();
              
            } finally {
              lock.unlock();
            }
            
          }
          
        });
        

      }
      
    }
    
  }
  
  private class MessageListener implements BroadcastListener<StandardMessage> {

    @Override
    public void receiveMessage(StandardMessage message) {
      
      updateAllBuffers(message, msgBuffers, new BufferUpdater<StandardMessage>() {

        @Override
        public void updateBuffer(StandardMessage msg, SharedBuffer<MessageMetaWrapper<StandardMessage>> buffer) {
          Lock lock = buffer.getLock();
          Condition contentsChanged = buffer.getContentsChanged();
          FixedSizeBuffer<MessageMetaWrapper<StandardMessage>> fixedBuffer = 
              buffer.getMsgBuffer();
          try {
            lock.lock();
            MessageMetaWrapper<StandardMessage> wrappedMessage =
                 new MessageMetaWrapper<StandardMessage>(msg);
            fixedBuffer.offer(wrappedMessage);
            LOGGER.finer(fixedBuffer.toString());
            contentsChanged.signalAll();
            
          } finally {
            lock.unlock();
          }
          
        }
        
      });
      
    }
    
  }

  public EventMachine(AntChipInterface chipInterface) {
    this.chipInterface = chipInterface;
    this.rawMessenger = new BroadcastMessenger<byte []>();
    this.convertedMessenger = new BroadcastMessenger<StandardMessage>();
    this.convertedMessenger.addBroadcastListener(new AckListener());
    this.convertedMessenger.addBroadcastListener(new MessageListener());
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
  

  public MessageMetaWrapper<StandardMessage> waitForMessage(BufferedNodeComponent component,
      MessageCondition msgCondition, Long timeout, TimeUnit timeoutUnit,
      LockExchangeContainer lockExchanger, Long cutOffTimestamp) 
          throws InterruptedException, TimeoutException {
    return waitForCondition(component.getMsgBuffer(),msgCondition, timeout, timeoutUnit, lockExchanger, cutOffTimestamp);
    
  }
  
  public MessageMetaWrapper<ChannelResponse> waitForAcknowledgement(BufferedNodeComponent component,
      MessageCondition msgCondition, Long timeout, TimeUnit timeoutUnit,
      LockExchangeContainer lockExchanger, Long cutOffTimestamp) 
          throws InterruptedException, TimeoutException {
    return waitForCondition(component.getAckBuffer(),msgCondition, timeout, timeoutUnit, lockExchanger, cutOffTimestamp);
    
  }
  
  public <V extends StandardMessage> MessageMetaWrapper<V> waitForCondition( 
      SharedBuffer<MessageMetaWrapper<V>> sharedBuffer,
      MessageCondition msgCondition, Long timeout, TimeUnit timeoutUnit,
      LockExchangeContainer lockExchanger, Long cutOffTimestamp) 
          throws InterruptedException, TimeoutException {
    
    
    //tim = clearBuffer == null ? true : false;
    
    MessageMetaWrapper<V> message = null;
    FixedSizeBuffer<MessageMetaWrapper<V>> buffer = sharedBuffer.getMsgBuffer();
    Condition bufferChanged = sharedBuffer.getContentsChanged();
    Lock msgLock = sharedBuffer.getLock();
    
    
    final long timeoutNano = timeout != null ? TimeUnit.NANOSECONDS.convert(timeout, timeoutUnit) : 0L;
    final long initialTimeStamp = MessageMetaWrapper.getCurrentTimestamp();
    cutOffTimestamp = cutOffTimestamp == null ? initialTimeStamp : cutOffTimestamp;
       
    try {
      msgLock.lock();
      
      
      if(lockExchanger != null) {
        try {
          lockExchanger.lock.lock();
          lockExchanger.returnLock = msgLock;
          lockExchanger.lockAvailable.signalAll();
        } finally {
          lockExchanger.lock.unlock();
        }
      }
      

      
      while(message == null) {
        

        for (MessageMetaWrapper<V> meta : buffer) {
          // what if we want to wait for multiple messages - one might come in
          // before we are waiting
          //if (!ignoreTimeStamps && meta.getTimestamp() < initialTimeStamp ) {
          //  continue;
          //}
          if (cutOffTimestamp != null && meta.getTimestamp() < cutOffTimestamp) {
            continue;
          }
          StandardMessage msg = meta.unwrap();
          if (msgCondition.test(msg)) {
            message = meta;
            break;
          }
        }
        
        //buffer.clear();
        
        if (message != null) {
          //buffer.remove(message);
          //buffer.clear();
          break;
        }
        
        if (timeout != null) {
          long timeoutRemaining = timeoutNano - (MessageMetaWrapper.getCurrentTimestamp() - initialTimeStamp);
          if(!bufferChanged.await(timeoutRemaining, TimeUnit.NANOSECONDS)) {
            throw new TimeoutException("timeout waiting for message");
          }
        } else {
          bufferChanged.await();
        }
        
        LOGGER.finest("waitForMessage :woken up");
        
      }
      
      return message;
      
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
