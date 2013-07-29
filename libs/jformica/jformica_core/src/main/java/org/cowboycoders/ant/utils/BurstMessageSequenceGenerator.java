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

public class BurstMessageSequenceGenerator {
  
  public static int FINISH_MASK = 0x04;
  public static int START = 0x00;
  public static int RESET = 0x01;
  public static int MAX = 0x03;
  
  private int current = START;
  
  public void reset() {
    current = START;
  }
  
  public int next() {
    if ((current & FINISH_MASK) != 0) {
      throw new IndexOutOfBoundsException("Sequence is finished");
    }
    
    int rtn = current;
    current++;
    
    if (current > MAX) {
      current = RESET;
    }
    
    return rtn;
  }
  
  public int finish() {
    return current |= FINISH_MASK;
  }
  

}
