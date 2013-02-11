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
package org.cowboycoders.turbotrainers.bushido.headunit;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.cowboycoders.turbotrainers.TurboCommunicationException;
import org.cowboycoders.turbotrainers.bushido.headunit.BushidoButtonPressDescriptor.Duration;

/**
 * Only dispatches a button press when duration is known
 * @author will
 *
 */
public class ButtonPressDispatcher {
  
  
  // I have left a 500 ms window to catch the repeated transmissions - may need tuning for reliability / response time
  /**
   * Maximum time to listen for a new button press
   */
  private static long TIMEOUT_NANO_SECONDS = TimeUnit.MILLISECONDS.toNanos(525);
  
  /**
   * If an another button press is received within this period, it is deem to be independent
   * and thus triggers another event (channel period is 8hz).
   * 
   * Note : this may no be needed as you should not get too @code {Duration.Short}'s in a row.
   */
  private static long INDEPENDENT_MESSAGE_CUTOFF_NANO_SECONDS = TimeUnit.MILLISECONDS.toNanos(200);
  
  private BushidoInternalListener listener;
  private Lock lock = new ReentrantLock();
  private Condition newPressCondition = lock.newCondition();
  private BushidoButtonPressDescriptor currentDescriptor;
  private BushidoButtonPressDescriptor lastDescriptor;
  private LinkedBlockingQueue<BushidoButtonPressDescriptor> oldDescriptors = 
      new LinkedBlockingQueue<BushidoButtonPressDescriptor>();
  private ArrayList<BushidoButtonPressDescriptor> descriptorDrain = new ArrayList<BushidoButtonPressDescriptor>();
  private boolean newSubmit = false;

  public ButtonPressDispatcher(BushidoInternalListener listener) {
    this.listener = listener;
  }
  
  public void submitButtonPress(BushidoButtonPressDescriptor descriptor) {
    try {
      lock.lock();
      if (currentDescriptor == null) {
        currentDescriptor = descriptor;
        startAwaitSubmissionThread();
        return;
      };
      
      // notify a button press is active
      listener.onButtonPressActive(descriptor);
      
      lastDescriptor = currentDescriptor;
      oldDescriptors.add(lastDescriptor);
      currentDescriptor = descriptor;
      newSubmit = true;
      newPressCondition.signalAll();
      
    } finally {
      lock.unlock();
    }
  }
  
  private void processOldDescriptors() {
    int numberDrained = oldDescriptors.drainTo(descriptorDrain);
    if (numberDrained < 1) {
      return;
    }
    Iterator<BushidoButtonPressDescriptor> iterator = descriptorDrain.iterator();
    BushidoButtonPressDescriptor last = null;
    while (iterator.hasNext()) {
      BushidoButtonPressDescriptor current = iterator.next();
      iterator.remove();
      if (last == null) {
        last = current;
        continue;
      }
      //check duration is progressing in time
      if (current.getDuration().ordinal() <  last.getDuration().ordinal()) {
        listener.onButtonPressFinished(last);
      } else if (current.getDuration() == Duration.SHORT && last.getDuration() == Duration.SHORT)  
          //&& current.getRecievedTimestamp() - last.getRecievedTimestamp() < INDEPENDENT_MESSAGE_CUTOFF_NANO_SECONDS) 
      {
        listener.onButtonPressFinished(last);
      }
      
      last = current;
      
    }
    //check very last value
    if (last != null && currentDescriptor.getDuration().ordinal() < last.getDuration().ordinal()) {
      listener.onButtonPressFinished(last);
    } else if (currentDescriptor.getDuration() == Duration.SHORT && last.getDuration() == Duration.SHORT)  
        //&& currentDescriptor.getRecievedTimestamp() - last.getRecievedTimestamp() < INDEPENDENT_MESSAGE_CUTOFF_NANO_SECONDS) 
    {
      listener.onButtonPressFinished(last);
    }
    
  }
  
  /**
   * Submits if no other button presses received before timeout
   * @return true if submitted, otherwise false
   * @throws InterruptedException
   */
  private boolean awaitNewSubmit() throws InterruptedException {
    long startTimeStamp = System.nanoTime();
    while(!newSubmit) {
      long currentTimeStamp = System.nanoTime();
      long timeLeft = TIMEOUT_NANO_SECONDS - (currentTimeStamp - startTimeStamp);
      boolean status = newPressCondition.await(timeLeft, TimeUnit.NANOSECONDS);
      if (!status) {
        listener.onButtonPressFinished(currentDescriptor);
        return true;
      }
    }
    
    processOldDescriptors();
    
    newSubmit = false;
    return false;
  }
  
  private void startAwaitSubmissionThread() {
    new Thread() {
      @Override
      public void run() {
        try {
          lock.lock();
          while(!awaitNewSubmit());
          reset();
        } catch (InterruptedException e) {
          throw new TurboCommunicationException(e);
        } finally {
          lock.unlock();
        }
      }
    }.start();

  }
  
  private void reset() {
    currentDescriptor = null;
    lastDescriptor = null;
    newSubmit = false;
  }

}
