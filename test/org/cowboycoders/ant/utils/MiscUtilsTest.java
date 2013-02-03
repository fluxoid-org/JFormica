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
       System.out.printf("%x ", b);
     }
     
     System.out.printf("\n");
   }
   
   
  
  }
  
  public static List<byte[]> splitBurst(byte[] data) {
    return MiscUtils.splitByteArray(data, AntDefine.ANT_STANDARD_DATA_PAYLOAD_SIZE);
  }

}
