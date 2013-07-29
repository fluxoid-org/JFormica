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

/**
 * Stores an object with a timestamp of creation
 * @author will
 *
 * @param <V>
 */
public class TimestampContainer<V> implements TimestampQueryable<V> {
  
  V object;
  long timestamp;
  
  public TimestampContainer(V object) {
    this.object = object;
    this.timestamp = System.nanoTime();
  }
  
  /**
   * Compare this with object timestamp
   * @return the current timestamp
   */
  public static long getCurrentTimestamp() {
    return System.nanoTime();
  }

  /* (non-Javadoc)
   * @see org.cowboycoders.ant.utils.Containable#getObject()
   */
  @Override
  public V unwrap() {
    return object;
  }

  /* (non-Javadoc)
   * @see org.cowboycoders.ant.utils.TimestampQueryable#getTimestamp()
   */
  @Override
  public long getTimestamp() {
    return timestamp;
  }
  

}
