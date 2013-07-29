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
package org.cowboycoders.ant.interfaces;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.cowboycoders.ant.events.BroadcastMessenger;

public abstract class AbstractAntTransceiver implements AntChipInterface  {

  /**
   * Messengers to inform when rxRecieved
   */
  private Set<BroadcastMessenger<byte[]>> mRxMessengers = Collections.newSetFromMap(new WeakHashMap<BroadcastMessenger<byte[]>,Boolean>());
  

  /**
   * messengers to inform when chip status changes
   */
  private Set<BroadcastMessenger<AntStatusUpdate>> mStatusMessengers = Collections.newSetFromMap(new WeakHashMap<BroadcastMessenger<AntStatusUpdate>,Boolean>());
  
  private final Lock messengerLock = new ReentrantLock();
  
  /**
   * Stores last status update
   */
  private AntStatusUpdate lastStatusUpdate = new AntStatusUpdate();
  
  /**
   * @return the mRxMessengers
   */
  public Set<BroadcastMessenger<byte[]>> getRxMessengers() {
    return mRxMessengers;
  }

  /**
   * @return the mStatusMessengers
   */
  public Set<BroadcastMessenger<AntStatusUpdate>> getStatusMessengers() {
    return mStatusMessengers;
  }

  /**
   * @return the messengerLock
   */
  public Lock getMessengerLock() {
    return messengerLock;
  }

  public AbstractAntTransceiver() {
    super();
  }

  /**
   * keep a reference as stored in a weak list
   */
  @Override
  public void registerRxMesenger(BroadcastMessenger<byte[]> rxMessenger) {
    if(rxMessenger == null) {
      return;
    }
    try {
      messengerLock.lock();
      this.mRxMessengers.add(rxMessenger);
    } finally {
      messengerLock.unlock();
    }
    
    
  }

  /**
   * keep a reference as stored in a weak list
   */
  @Override
  public void registerStatusMessenger(BroadcastMessenger<AntStatusUpdate> statusMessenger) {
    if(statusMessenger == null) {
      return;
    }
    
    try {
      messengerLock.lock();
      this.mStatusMessengers.add(statusMessenger);
    } finally {
      messengerLock.unlock();
    }
  }
  
  protected void broadcastStatus(AntStatus status, Object optionalArg) {
    try {
      getMessengerLock().lock();
      Set<BroadcastMessenger<AntStatusUpdate>> mStatusMessengers = getStatusMessengers();
      if (mStatusMessengers != null) {
        AntStatusUpdate update = new AntStatusUpdate();
        update.status = status;
        update.optionalArg = optionalArg;
        for (BroadcastMessenger<AntStatusUpdate> statusMessenger : mStatusMessengers) {
          if (statusMessenger == null) {
            continue;
          }
          statusMessenger.sendMessage(update);
        }
      }
    } finally {
      getMessengerLock().unlock();
    }
  }
  
  protected void broadcastStatus(AntStatus status) {
    this.broadcastStatus(status, null);
  }
  
  protected void broadcastRxMessage(byte [] ANTRxMessage) {
    try {
      getMessengerLock().lock();
      Set<BroadcastMessenger<byte[]>> mRxMessengers = getRxMessengers();
      if (mRxMessengers == null) {
        return;
      }
      for (BroadcastMessenger<byte[]> rxMessenger : mRxMessengers) {
        if (rxMessenger == null) {
          continue;
        }
        rxMessenger.sendMessage(ANTRxMessage);
      }
    } finally {
     getMessengerLock().unlock();
    } 
   }
  
  /* (non-Javadoc)
   * @see org.cowboycoders.ant.interfaces.AntChipInterface#getStatus()
   */
  @Override
  public AntStatusUpdate getStatus() {
    return lastStatusUpdate;
  }

}