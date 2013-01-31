package org.cowboycoders.ant.utils;

public class IntUtils {
  
  private IntUtils(){};
  
  /**
   * Converts an int which is being treated as unsigned to a long
   * @param value
   * @return
   */
  public static Long unsignedIntToLong(int value) {
    return value & 0xffffffffL;
  }

}
