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
package org.cowboycoders.ant;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SharedThreadPool {
  
  /**
   * Used to concurrently notify listeners
   */
  private static ExecutorService dispatchPool;
  
  /**
   * @return the dispatchPool
   */
  public synchronized static ExecutorService getThreadPool() {
    return dispatchPool;
  }

  /**
   * @param dispatchPool the dispatchPool to set
   */
  public synchronized static void setThreadPool(ExecutorService dispatchPool) {
    SharedThreadPool.dispatchPool = dispatchPool;
  }

  static {
    //initPool(1,Integer.MAX_VALUE,60,TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5));
    // can't have a non unary length queue as we rely on each call to execute in a new thread
    //initPool(1,Integer.MAX_VALUE,60,TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1)); 
    dispatchPool = Executors.newCachedThreadPool(new ThreadFactory() {
 	   @Override
 	   public Thread newThread(Runnable runnable) {
 	      Thread thread = Executors.defaultThreadFactory().newThread(runnable);
 	      thread.setDaemon(true);
 	      return thread;
 	   }
 	});
  }
  
private static void initPool(int coreSize, int maxSize, int timeout, TimeUnit timeoutUnit,
      BlockingQueue<Runnable> backingQueue) {
    dispatchPool = new ThreadPoolExecutor(coreSize,
        maxSize, 
        timeout, 
        timeoutUnit,
        backingQueue);
  }
  
  
  
}
