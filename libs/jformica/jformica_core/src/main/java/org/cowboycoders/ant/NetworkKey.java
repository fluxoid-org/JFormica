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

import java.util.UUID;

public class NetworkKey implements Comparable<NetworkKey> {
  
  private String name;
  private int [] key;
  protected int number;
  
  
  
  /**
   * @return the number
   */
  public int getNumber() {
    return number;
  }

  /**
   * @param number the number to set
   */
  protected void setNumber(int number) {
    this.number = number;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the key
   */
  public int[] getKey() {
    return key;
  }

  /**
   * @param key the key to set
   */
  public void setKey(int[] key) {
    this.key = key;
  }
  
  /**
   * @param name of key
   * @param key truncated to bytes
   */
  public NetworkKey(String name, int[] key) {
    if (key.length > 8 || key.length < 8) {
      throw new IllegalArgumentException("network key must be 8 bytes");
    }
    if (name == null) {
      name = UUID.randomUUID().toString();
    }
    this.name = name;
    this.key = key;
  }
  
  public NetworkKey(int[] key) {
    this(null,key);
  }
  
  public NetworkKey(int byte0, int byte1,int byte2,int byte3,
      int byte4,int byte5,int byte6,int byte7) {
    this(null, new int[] { byte0, byte1, byte2, byte3, byte4,
         byte5, byte6, byte7});
  }

  @Override
  public int compareTo(NetworkKey another) {
    return name.compareTo(another.name);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    NetworkKey other = (NetworkKey) obj;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    return true;
  }
  
  
  

}
