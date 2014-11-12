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

import static org.junit.Assert.*;


import java.util.List;

import org.cowboycoders.ant.defines.AntDefine;
import org.cowboycoders.ant.utils.MiscUtils;
import org.junit.Test;

public class MiscUtilsTest {

  @Test
  public void testSplitBurst() {
   assertEquals(splitBurst(new byte[0]).size(), 1);
   assertEquals(splitBurst(new byte[8]).size(), 1);
   assertEquals(splitBurst(new byte[16]).size(), 2);
   assertEquals(splitBurst(new byte[25]).size(), 4);

   int length = 25;
   byte [] test = new byte[length];
   for (int i = 0 ; i < length ; i ++) {
    test[i] = (byte) i;
  }

   List<byte[]> split = splitBurst(test);

   for (byte[] a : split) {
     for (byte b : a) {
       System.out.printf("%d ", b);
     }

     System.out.printf("\n");
   }



  }

  public static List<byte[]> splitBurst(byte[] data) {
    return ByteUtils.splitByteArray(data, AntDefine.ANT_STANDARD_DATA_PAYLOAD_SIZE);
  }

}
