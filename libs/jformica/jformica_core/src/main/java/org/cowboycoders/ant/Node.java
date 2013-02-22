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
package org.cowboycoders.ant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.logging.Logger;

import org.cowboycoders.ant.AntLogger.Direction;
import org.cowboycoders.ant.AntLogger.LogDataContainer;
import org.cowboycoders.ant.events.BroadcastListener;
import org.cowboycoders.ant.events.BroadcastMessenger;
import org.cowboycoders.ant.events.EventMachine;
import org.cowboycoders.ant.events.LockExchangeContainer;
import org.cowboycoders.ant.events.LockExchanger;
import org.cowboycoders.ant.events.MessageCondition;
import org.cowboycoders.ant.events.MessageConditionFactory;
import org.cowboycoders.ant.interfaces.AntChipInterface;
import org.cowboycoders.ant.interfaces.AntStatus;
import org.cowboycoders.ant.interfaces.AntStatusUpdate;
import org.cowboycoders.ant.messages.MessageMetaWrapper;
import org.cowboycoders.ant.messages.StandardMessage;
import org.cowboycoders.ant.messages.commands.ChannelRequestMessage;
import org.cowboycoders.ant.messages.commands.ResetMessage;
import org.cowboycoders.ant.messages.config.NetworkKeyMessage;
import org.cowboycoders.ant.messages.notifications.StartupMessage;
import org.cowboycoders.ant.messages.responses.CapabilityCategory;
import org.cowboycoders.ant.messages.responses.CapabilityResponse;
import org.cowboycoders.ant.messages.responses.Capability;
import org.cowboycoders.ant.messages.responses.ChannelResponse;
import org.cowboycoders.ant.messages.responses.ResponseCode;
import org.cowboycoders.ant.utils.FixedSizeFifo;

public class Node {
  
  class ThreadedWait implements Callable<MessageMetaWrapper<? extends StandardMessage>> {
    
    private WaitAdapter waitAdapter;

    public ThreadedWait(WaitAdapter waitAdapter) {
      this.waitAdapter = waitAdapter;
    }


    @Override
    public MessageMetaWrapper<? extends StandardMessage> call() throws InterruptedException, TimeoutException {
        return waitAdapter.execute();
    }
    
    
  }
  
  
  public final static Logger LOGGER = Logger.getLogger(EventMachine.class .getName()); 
  private Set<AntLogger> antLoggers = Collections.newSetFromMap(new WeakHashMap<AntLogger,Boolean>());
  
  private boolean running = false;
  private EventMachine evm;
  
  /**
   * @return the evm
   */
  protected synchronized EventMachine getEvm() {
    return evm;
  }

  private Channel [] channels = new Channel [0] ;
  private NetworkKey [] networks = new NetworkKey[0];
  private CapabilityResponse capabilities;
  private AntChipInterface antChipInterface;
  private BroadcastMessenger<AntStatusUpdate> mStatusMessenger =
      new BroadcastMessenger<AntStatusUpdate>();
  
  private class StatusListener implements BroadcastListener<AntStatusUpdate> {

    @Override
    public void receiveMessage(AntStatusUpdate message) {
      if (message.status == AntStatus.RESET) {
        if (!weReset) {
//          // we don't actually get a reset intent if we send a raw message
//          // NOTE: should we use the reset ANtInterface method?
//          
//          LOGGER.warning("Node: external reset");
        }
//        weReset = false;
      }
      
    }
    
  }
  
  private class MessageListener implements BroadcastListener<StandardMessage> {

    @Override
    public void receiveMessage(StandardMessage message) {
      logMessage(Direction.RECEIVED,message);
      if (message instanceof StartupMessage) {
        if (!weReset) {
        // we don't actually get a reset intent if we send a raw message
        // NOTE: should we use the reset ANtInterface method?
        LOGGER.warning("Node: external reset");
      }
        weReset = false;
      }
      
    }
    
  }
  
  /**
   * flags that we did the reset, as oppose to someone else (externally)
   */
  private boolean weReset;
  
  public Node(AntChipInterface antchip) {
    antChipInterface = antchip;
    evm = new EventMachine(antchip);
    mStatusMessenger.addBroadcastListener(new StatusListener());
  }
  
  /**
   * Returns the antchip. Note: you should never bypass the node and send messages
   * using its send function. 
   * @return
   */
  public AntChipInterface getAntChip() {
    return antChipInterface;
  }
  
  /**
   * Listener for status updates from the ant chip
   * @param listener
   */
  public synchronized void registerStatusListener(BroadcastListener<AntStatusUpdate> listener) {
    mStatusMessenger.addBroadcastListener(listener);
  }
  
  /**
   * Remove listener for status updates from the ant chip
   * @param listener
   */
  public synchronized void removeStatusListener(BroadcastListener<AntStatusUpdate> listener) {
    mStatusMessenger.removeBroadcastListener(listener);
  }

  /**
   * All capabilities
   * @return capabilities of antnode
   */
  public synchronized List<Capability> getCapabiltites() {
    if (capabilities == null) {
      return null;
    }
    List<Capability> rtn = capabilities.getCapabilitiesList(CapabilityCategory.STANDARD);
    rtn.addAll(capabilities.getCapabilitiesList(CapabilityCategory.ADVANCED));
    rtn.addAll(capabilities.getCapabilitiesList(CapabilityCategory.ADVANCED2));
    rtn.addAll(capabilities.getCapabilitiesList(CapabilityCategory.ADVANCED3));
    return rtn;
  }
  
  public synchronized void start() throws AntError {
    if (running) return ;//throw new AntError("already started");
    evm.start();
    antChipInterface.start();
    antChipInterface.registerStatusMessenger(mStatusMessenger);
    evm.registerRxListener(new MessageListener());
    init();

    running=true;
  }
  
//  private void registerChannels(boolean add) {
//    for (Channel channel : channels) {
//      if (add) evm.registerBufferedNodeComponent(channel);
//      else evm.unregisterBufferedNodeComponent(channel);
//    }
//    
//  }
  
//  private void registerSelf(boolean add) {
//    if (add) evm.registerBufferedNodeComponent(this);
//    else evm.unregisterBufferedNodeComponent(this);
//  }

  private CapabilityResponse getCapabilityResponse(final int maxRetries) throws InterruptedException, TimeoutException {
    StandardMessage capabilitiesMessage = new ChannelRequestMessage(
        0,ChannelRequestMessage.Request.CAPABILITIES );
    StandardMessage capabilitiesResponse = null;
    int retries = 0;
    while (capabilitiesResponse == null) {
      MessageCondition condition = MessageConditionFactory.newInstanceOfCondition(CapabilityResponse.class);
      try {
        capabilitiesResponse = sendAndWaitForMessage(
            capabilitiesMessage, 
            condition,
            10L,TimeUnit.SECONDS, 
            null,
            null
            );
      } catch  (TimeoutException e){
        e.printStackTrace();
        LOGGER.warning("getCapabilityResponse : timeout");
        retries++;
        if (retries >= maxRetries) {
          throw e;
        }
      }
    }
      
    return (CapabilityResponse) capabilitiesResponse;
  }
  
  private void init() {
    LOGGER.finer("entering init");
    //reset();
    try {
      // android interface automatically requests ant version response.
      // Can't wait for it here, as it may have already been fired.
      // so we use retries until we get a response (which was not the case
      // if sent before version was requested) 
      capabilities = getCapabilityResponse(5);
    } catch (InterruptedException e) {
      throw new AntError(e);
    } catch (TimeoutException e) {
      throw new AntError(e);
    }
    
    
    networks = new NetworkKey [capabilities.getMaxNetworks()];
    for (int i = 0 ; i< networks.length ; i++) {
      setNetworkKey(i,new NetworkKey(0,0,0,0,0,0,0,0),false);
    }
    
    channels = new Channel[getMaxChannels()];
    for (int i = 0 ; i< channels.length ; i++) {
      channels[i] = new Channel(this,i);
    }
    
    
    
    LOGGER.finer("exiting init");
  }
  
  public synchronized Channel getFreeChannel() {
    
    for (Channel c : channels) {
      if (c.isFree()) return c;
    }
    
    return null;
  }
  
  /**
   * should be called to free and return channel to pool. Channel should
   * then be nulled or reassigned
   * @param channel
   */
  public synchronized void freeChannel(Channel channel) {
    // need to hold synchronisation in case someone is
    // calling getFreeChannel)
    channel.setFree(true);
  }
  
  /**
   * 
   * @param network to assign this key to
   * @param key to assign
   * @param send if true, send to ant chip
   */
  private synchronized void setNetworkKey(int network, NetworkKey key, boolean send) {
    networks[network] = key;
    key.setNumber(network);
    if(send) {
    StandardMessage msg = new NetworkKeyMessage(network,key.getKey());
    MessageCondition condition = MessageConditionFactory.newResponseCondition(msg.getId(),ResponseCode.RESPONSE_NO_ERROR);
      try {
        sendAndWaitForMessage(msg,condition, 5L,TimeUnit.SECONDS, null,null);
      } catch (InterruptedException e) {
        throw new AntError(e);
      } catch (TimeoutException e) {
        throw new AntError(e);
      }
    }
  }
  
  /**
   * Do not set key until after node started
   * @param network must be below that given by getMaxNetworks()
   * @param key to set
   */
  public synchronized void setNetworkKey(int network, NetworkKey key) {
    this.setNetworkKey(network, key, true);
  }
  
  /**
   * @return max number of networks this node supports
   */
  public synchronized int getMaxNetworks() {
    return capabilities.getMaxNetworks();
  }
  
  /**
   * @return max number of channels this node supports
   */
  public synchronized int getMaxChannels() {
    return capabilities.getMaxChannels();
  }
  
  public NetworkKey getNetworkKey(String id) {
    NetworkKey [] networks = Arrays.copyOf(this.networks, this.networks.length);
    Arrays.sort(networks);
    NetworkKey searchKey = new NetworkKey(0,0,0,0,0,0,0,0);
    searchKey.setName(id);
    int key = Arrays.binarySearch((Object [])networks, (Object) searchKey);
    if (key >= 0) {
      return networks[key];
    }
    return null;
  }
  
  private final MessageSender nodeSender = new MessageSender() {

    @Override
    public List<MessageMetaWrapper<? extends StandardMessage>> send(StandardMessage msg) {
    	MessageMetaWrapper<StandardMessage> sentMeta = Node.this.send(msg);
    	List<MessageMetaWrapper<? extends StandardMessage>> rtn = new ArrayList<MessageMetaWrapper<? extends StandardMessage>>(1);
    	rtn.add(sentMeta);
    	return rtn;
    }
    
  };
  
  public StandardMessage sendAndWaitWithAdapter(WaitAdapter waitAdapter, StandardMessage msg,
      LockExchangeContainer lockContainer) throws InterruptedException, TimeoutException {
    return sendAndWaitWithAdapter(waitAdapter, msg,
        lockContainer,nodeSender, null);
  }
  
  
  
  public StandardMessage sendAndWaitWithAdapter(WaitAdapter waitAdapter, StandardMessage msg,
      LockExchangeContainer lockContainer, MessageSender sender, Receipt receipt) 
          throws InterruptedException, TimeoutException {
    Callable<MessageMetaWrapper<? extends StandardMessage>> waitTask= new ThreadedWait(waitAdapter);
    Future<MessageMetaWrapper<? extends StandardMessage>> future = SharedThreadPool.getThreadPool().submit(waitTask);
    MessageMetaWrapper<? extends StandardMessage> receivedMeta = null;
    List<MessageMetaWrapper<? extends StandardMessage>> sentMeta = null;
    
    sender = sender == null ? nodeSender : sender;
    
    //Thread.sleep(100);
    // now we are waiting for a response send message
    if(lockContainer != null) {
      try {
          LockExchanger exchanger = new LockExchanger(lockContainer, 5L, TimeUnit.SECONDS);
          Future<Lock> lockFuture = SharedThreadPool.getThreadPool().submit(exchanger);
          //Future<Lock> lockFuture = Executors.newCachedThreadPool().submit(exchanger);
          Lock lock = lockFuture.get();
          try {
            lock.lock();
            //Thread.sleep(100
            sentMeta = sender.send(msg);
          } finally {
            lock.unlock();
          }
          
          } 
        catch (ExecutionException e ) {
          Throwable cause = e.getCause();
          if (cause instanceof InterruptedException) {
            throw new InterruptedException("interrupted waiting for lock exchange");
          }
          if (cause instanceof TimeoutException) {
            throw new TimeoutException("timeout waiting for lock excahnge");
          }
          throw new RuntimeException(e);
          }
    } else {
      sentMeta = sender.send(msg);
    }
        
    try {
      receivedMeta = future.get();
    } catch (ExecutionException e ) {
      Throwable cause = e.getCause();
      if (cause instanceof InterruptedException) {
        throw new InterruptedException("interrupted waiting for message");
      }
      if (cause instanceof TimeoutException) {
        throw new TimeoutException("timeout waiting for message");
      }
      throw new RuntimeException(e);
    }
    
    // message is in charge of updating sent messages
    if (receipt != null) {
      receipt.addReceived(receivedMeta);
      receipt.addSent(sentMeta);
    }
    
    return receivedMeta.unwrap();
  }
  
  /**
   * We wrap listeners to look for message of specific class - this is a map from the
   * original to the new one
   */
  private Map<Object,BroadcastListener<StandardMessage>> mAdapterListenerMap
    = new HashMap<Object,BroadcastListener<StandardMessage>>();

  public synchronized <V extends StandardMessage> void registerRxListener(
      final BroadcastListener<V> listener, final Class<V> clazz) {
    
      BroadcastListener<StandardMessage> adapter = new BroadcastListener<StandardMessage>() {

        @Override
        public void receiveMessage(StandardMessage message) {
          if (clazz.isInstance(message)) {
            listener.receiveMessage(clazz.cast(message));
          }
          
        }
        
      };
      
      mAdapterListenerMap.put(listener, adapter);
      evm.registerRxListener(adapter);
    
    
  }
  
  public synchronized <V extends StandardMessage> void removeRxListener(
      final BroadcastListener<V> listener) {
   
    BroadcastListener<StandardMessage> adapter = mAdapterListenerMap.get(listener);
    if (adapter != null) {
      evm.removeRxListener(adapter);
    } else {
      LOGGER.warning("removeRxListener: ignoring unknown listener");
    }
    
  }
  
  public StandardMessage sendAndWaitForMessage(
      final StandardMessage msg, 
      final MessageCondition condition,
      final Long timeout, final TimeUnit timeoutUnit,
      final MessageSender sender,
      final Receipt receipt) 
          throws InterruptedException, TimeoutException {
    final LockExchangeContainer lockContainer = new LockExchangeContainer();
    WaitAdapter responseAdapter = new WaitAdapter() {

      @Override
      public MessageMetaWrapper<StandardMessage> execute() throws InterruptedException, TimeoutException {
        return evm.waitForCondition(condition, timeout, timeoutUnit, lockContainer);
      }
      
    };
    
    return sendAndWaitWithAdapter(responseAdapter,msg,lockContainer,sender, receipt);

  }
  
  
  public synchronized void reset(boolean wait) {
    weReset = true;
    
    StandardMessage resetMsg = new ResetMessage();
    if (wait) {
      try {
        MessageCondition condition = MessageConditionFactory.newInstanceOfCondition(StartupMessage.class);
        sendAndWaitForMessage(resetMsg,condition,1L,TimeUnit.SECONDS,null,null);
      } catch (InterruptedException e) {
        throw new AntError(e);
      } catch (TimeoutException e) {
        throw new AntError(e);
      }
    } else {
      send(resetMsg);
    }
    
  }
  
  
  public void reset() {
    reset(true);
  }
  
  private void logMessage(AntLogger.Direction direction, StandardMessage msg) {
    synchronized (antLoggers) {
      for (AntLogger logger : antLoggers) {
        try {
          LogDataContainer data = new LogDataContainer(direction,msg);
          logger.log(data);
        } catch (Exception e) {
          // don't bring us down for the sake of logging
          LOGGER.severe("error logging data");
        }
      }
    }
  }
  
  public synchronized MessageMetaWrapper<StandardMessage> send(StandardMessage msg) {
    LOGGER.finer("sent: " + msg.toString() + " to chip" );
    antChipInterface.send(msg.encode());
    // now that we have sent, inform the loggers
    logMessage(Direction.SENT,msg);
    return new MessageMetaWrapper<StandardMessage>(msg);
  }

  public synchronized void stop() {
    if (!running) return; //throw new AntError("already stopped");
    evm.stop();
    antChipInterface.stop();
    running = false;
  }
  
  public synchronized boolean isRunning() {
    //sync with ant chip
    if(!this.antChipInterface.isRunning()); {
      stop();
    }
    return running;
  }
  
  public void registerAntLogger(AntLogger logger) {
    synchronized(antLoggers) {
      antLoggers.add(logger);
    }
    
  }

  public void unregisterAntLogger(AntLogger logger) {
    synchronized(antLoggers) {
      antLoggers.remove(logger);
    }
  }
  

}
