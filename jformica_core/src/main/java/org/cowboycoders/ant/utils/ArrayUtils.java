package org.cowboycoders.ant.utils;

public class ArrayUtils {

  public static boolean arrayStartsWith(Byte[] pattern, Byte[] data){

    for (int i=0; i<pattern.length; i++) {
        if (data[i] != pattern[i]) {
            return false;
        }
    }
    return true;
}

public static Byte [] intsToBytes(int[] intArray){
    Byte [] byteArray = new Byte [intArray.length];
    for (int i=0; i < intArray.length; i++){
        byteArray[i] = (byte)intArray[i];
    }
    return byteArray;
}

public static int [] unsignedBytesToInts(Byte [] bytes) {
  int [] rtn = new int[bytes.length];
  for (int i=0 ; i < bytes.length ; i++) {
    rtn[i] = ByteUtils.unsignedByteToInt(bytes[i]);
  }
  return rtn;
}




}
