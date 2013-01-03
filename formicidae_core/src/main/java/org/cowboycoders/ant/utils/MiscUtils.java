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

/**
 * A hotchpotch of utils
 * @author will
 *
 */
public class MiscUtils {
  
  private MiscUtils() {
    
  }
  
  /**
   * Splits a byte array into smaller chunks
   * @param data array to split
   * @param packetLength chunk size
   * @return
   */
  public static List<byte[]> splitByteArray(final byte[] data, final int packetLength) {
    List<byte[]> split = new ArrayList<byte[]>();  
    int wholePackets = (int)(data.length / packetLength);

    int index = 0;
    for (int i = 0 ; i < wholePackets ; i++) {
      byte [] dataPacket = new byte[packetLength];
      for (int j = 0 ; j < packetLength ; j++){
        dataPacket[j] = data[index];
        index++;
      }
      split.add(dataPacket);
    }
    // pad partial
    if (data.length == 0 || index!= data.length ) {
      byte [] dataPacket = new byte[packetLength];
      int i = 0;
      while (index < data.length) {
        dataPacket[i] = data[index];
        index++;
        i++;
      }
      split.add(dataPacket);
    }
    
    return split;
  
  }

}
