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

import java.util.ArrayList;
import java.util.List;

public class ByteMerger {
  
  private ByteMerger() {
    
  }
  
  public static Integer lsbMerge(List<Byte> data) {
    Integer rtn = 0;
    byte count = 0;
    for (Byte b : data) {
      rtn |= b << (count * 8);
      count ++;
    }
    return rtn;
  }
  
  public static List<Byte> lsbSplit(Integer in, int numberOfBytes) {
    List<Byte> bytes = new ArrayList<Byte>();
    for (byte i = 0 ; i < numberOfBytes ; i++) {
      int mask = 0xff << (i*8);
      bytes.add((byte) ((in & mask) >>> (i*8)) );
    }
    return bytes;
  }
  
  
}
