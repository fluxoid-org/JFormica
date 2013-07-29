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
package org.cowboycoders.ant.utils;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class SharedBuffer<V> {
  
  private int length;
  
  private Lock lock = new ReentrantLock();

  private Condition contentsChanged = lock.newCondition();

  private FixedSizeQueue<V> msgBuffer = null;
  
  private boolean clearable = true;
  
  public synchronized void setClearable(boolean flag) {
    this.clearable = flag;
  }
  
  public boolean isClearable() {
    return clearable;
  }
  
  /**
   * Only clears if clearable flag set
   * @return false, if not clearable
   */
  public synchronized boolean clear() {
    if (!isClearable()) return false;
    msgBuffer.clear();
    return true;
  }
      
  /**
   * @return the length
   */
  public int getLength() {
    return length;
  }

  /**
   * @return the lock
   */
  public Lock getLock() {
    return lock;
  }

  /**
   * @return the contentsChanged
   */
  public Condition getContentsChanged() {
    return contentsChanged;
  }

  /**
   * @return the msgBuffer
   */
  public FixedSizeQueue<V> getMsgBuffer() {
    return msgBuffer;
  }

  public SharedBuffer(int length) {
    this.length = length;
    msgBuffer = new FixedSizeFifo<V>(length);
    
  }
  
  public SharedBuffer(FixedSizeQueue<V> buffer) {
    this.length = buffer.size();
    msgBuffer = buffer;
    
  }
  

}
