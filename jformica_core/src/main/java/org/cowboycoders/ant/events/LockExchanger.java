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

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.logging.Logger;


public class LockExchanger implements Callable<Lock> {
  
  private TimeUnit timeoutUnit;
  private long timeout;
  private LockExchangeContainer container;
  public final static Logger LOGGER = Logger.getLogger(EventMachine.class .getName()); 

  public LockExchanger(LockExchangeContainer container, long timeout, TimeUnit unit) {
    this.timeout = timeout;
    this.timeoutUnit = unit;
    this.container = container;
  }
  
  @Override
  public Lock call() throws InterruptedException, TimeoutException {
    try {
      container.lock.lock();
      final long timeoutNano = TimeUnit.NANOSECONDS.convert(timeout, timeoutUnit);
      final long initialTimeStamp = System.nanoTime();
        while (container.returnLock == null) {
          long timeoutRemaining = timeoutNano - (System.nanoTime() - initialTimeStamp);
          if(!(container.lockAvailable.await(timeoutRemaining,TimeUnit.NANOSECONDS))) {
            throw new TimeoutException("timeout waiting for exchanged lock");
          }
        }
        LOGGER.finer("call() - returning");
        return container.returnLock;
    } finally {
      container.lock.unlock();
    }
  }
  
  
}