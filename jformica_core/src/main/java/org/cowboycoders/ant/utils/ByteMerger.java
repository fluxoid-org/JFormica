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
package org.cowboycoders.ant.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class that contains methods to
 * converts byte arrays to integers and back.
 * 
 * Could replace with a more generic implementation based on
 * BigInteger {@see BigIntUtils}
 * @author will
 *
 */
public class ByteMerger {
  
  private ByteMerger() {
    
  }

// Should I be using bigIntegers? See BigIntUtils
//  public static BigInteger lsbMergeI(List<Byte> data) {
//    byte [] bytes = new byte[data.size()];
//    for (int i=0 ; i< data.size() ; i++) {
//      bytes[i] = data.get(i);
//    }
//    BigInteger rtn = new BigInteger(bytes);
//    return rtn;
//  }
//  
  
  /**
   * Least significant byte first
   * @param list of bytes to merge into an Integer
   * @return the merger of the bytes
   */
  public static Integer lsbMerge(List<Byte> data) {
    Integer rtn = 0;
    byte count = 0;
    for (Byte b : data) {
      rtn |= b << (count * 8);
      count ++;
    }
    return rtn;
  }
  
  /**
   * Splits a list of bytes ordered with least significant byte first
   * @param in integer to split
   * @param numberOfBytes number of bytes to produce
   * @return
   */
  public static List<Byte> lsbSplit(Integer in, int numberOfBytes) {
    List<Byte> bytes = new ArrayList<Byte>();
    for (byte i = 0 ; i < numberOfBytes ; i++) {
      int mask = 0xff << (i*8);
      bytes.add((byte) ((in & mask) >>> (i*8)) );
    }
    return bytes;
  }
  
  /**
   * Splits a list of bytes ordered with most significant byte first
   * @param in integer to split
   * @param numberOfBytes number of bytes to produce
   * @return
   */
  public static List<Byte> msbSplit(Integer in, int numberOfBytes) {
    List<Byte> rtn = lsbSplit(in,numberOfBytes);
    Collections.reverse(rtn);
    return rtn;
  }
  
  /**
   * Most significant byte first
   * @param list of bytes to merge into an Integer
   * @return the merger of the bytes
   */
  public static Integer msbMerge(List<Byte> data) {
    List<Byte> clone = new ArrayList<Byte>(data);
    Collections.reverse(clone);
    return lsbMerge(clone);
  }
  
  
  
}
