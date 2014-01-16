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
 * Utilities for dealing with raw bits
 * @author will
 *
 */
public class BitUtils {
    
  
  /**
   * Calulcates index of left most bit
   * for any positive integer
   * @param number to check
   * @return the index of the left most bit
   */
  public static int getMaxBitIndex(int number) {
    if (number < 0) {
      throw new RuntimeException("negative numbers unsupported");
    }
    int maxBit = 0;
    while((number = number >>> 1) >=0) {
      maxBit++;
      if (number == 0) {
        break;
      }
    }    
    return maxBit;
    
  }
  
  /**
   * Calculates index of left most zero before first bit
   * for any positive integer
   * @param number to check
   * @return the index of the left most bit (zero based), -1 if no zeros
   */
  public static int getMaxZeroBitIndex(int number) {
    if (number < 0) {
      throw new RuntimeException("negative numbers unsupported");
    }
    int maxBit = -1;
    int mask = 1;
    
    while(mask <= number) {
      if((mask & number) != 0) {
        break;
      }
      mask = mask << 1;
      maxBit++;
    }    
    return maxBit;
    
  }
  
  
  private BitUtils() {
    
  }

}
