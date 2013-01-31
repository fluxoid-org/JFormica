package org.cowboycoders.ant.utils;

import java.util.ArrayList;

public class ByteUtils {
  
  private ByteUtils() {}
  
  /**
   * Converts a byte (which is being treated as unsigned) to
   * an int (i.e negative byte values return positive int values)
   * @param byteIn
   * @return
   */
  public static int unsignedByteToInt(byte byteIn) {
    return byteIn & 0xFF;
  }
  
  public static Byte [] boxArray(byte [] unboxed) {
    Byte [] rtn = new Byte[unboxed.length];
    int i =0;
    for (byte b : unboxed) {
      rtn[i++] = b;
    }
    return rtn;
  }
  
  public static byte [] unboxArray(Byte [] boxed) {
    byte [] rtn = new byte[boxed.length];
    int i =0;
    for (byte b : boxed) {
      rtn[i++] = b;
    }
    return rtn;
  }
  
  public static byte [] reverseArray(byte [] input) {
    byte [] rtn = new byte[input.length];
    int i = input.length;
    for (byte b : input) {
      rtn[--i] = b;
    }
    return rtn;
  }
  

}
