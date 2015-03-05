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

import java.util.Arrays;

public class NetworkKey {
  
  private int [] key;
  

  /**
   * @return the key
   */
  public int[] getKey() {
    return key;
  }

  /**
   * @param name of key
   * @param key truncated to bytes
   */
  public NetworkKey(int[] key) {
    if (key.length > 8 || key.length < 8) {
      throw new IllegalArgumentException("network key must be 8 bytes");
    }
    this.key = key;
  }
  
  
  public NetworkKey(int byte0, int byte1,int byte2,int byte3,
      int byte4,int byte5,int byte6,int byte7) {
    this(new int[] { byte0, byte1, byte2, byte3, byte4,
         byte5, byte6, byte7});
  }

@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + Arrays.hashCode(key);
	return result;
}

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	NetworkKey other = (NetworkKey) obj;
	if (!Arrays.equals(key, other.key))
		return false;
	return true;
}

  
  

}
