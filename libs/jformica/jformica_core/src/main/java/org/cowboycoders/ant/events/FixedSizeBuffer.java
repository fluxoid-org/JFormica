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

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;

public class FixedSizeBuffer<V> extends AbstractQueue<V> {
  
  private LinkedList<V> queue;
  
  private int maxSize;
  
  public FixedSizeBuffer(int maxSize) {
    this.queue = new LinkedList<V>();
    this.maxSize = maxSize;
  }

  @Override
  public boolean offer(V e) {
    if (queue.size() >= maxSize) {
      queue.poll();
    }
    queue.add(e);

    return true;
  }

  @Override
  public V peek() {
    return queue.peek();
  }

  @Override
  public V poll() {
    return queue.poll();
  }

  @Override
  public Iterator<V> iterator() {
    return queue.iterator();
  }

  @Override
  public int size() {
    return queue.size();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "FixedSizeBuffer [queue=" + queue + "]";
  }
  
  

  

}
