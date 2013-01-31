package org.cowboycoders.ant.utils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Various BigInteger utils.
 * @author will
 *
 */
public class BigIntUtils {
  
  private BigIntUtils() {}
  
  public static int NUMBER_OF_BYTES_BYTE = 1;
  public static int NUMBER_OF_BYTES_SHORT= Short.SIZE / Byte.SIZE;
  public static int NUMBER_OF_BYTES_INT= Integer.SIZE / Byte.SIZE;
  public static int NUMBER_OF_BYTES_LONG = Long.SIZE / Byte.SIZE;
  
  public static BigInteger convertUnsignedByte(byte value) {
    if (value >= 0) {
      return convertByte(value);
    }
    return new BigInteger(ByteBuffer.allocate(NUMBER_OF_BYTES_BYTE + 1).put(1,value).array());
  }
  
  public static BigInteger convertByte(byte value) {
    return new BigInteger(ByteBuffer.allocate(NUMBER_OF_BYTES_BYTE).put(value).array());
  }
  
  public static BigInteger convertShort(short value) {
    return new BigInteger(ByteBuffer.allocate(NUMBER_OF_BYTES_SHORT).putShort(value).array());
  }
  
  public static BigInteger convertUnsignedShort(short value) {
    if (value >= 0) {
      return convertShort(value);
    }
    return new BigInteger(ByteBuffer.allocate(NUMBER_OF_BYTES_SHORT + 1).putShort(1,value).array());
  }
  
  public static BigInteger convertInt(int value) {
    return new BigInteger(ByteBuffer.allocate(NUMBER_OF_BYTES_INT).putInt(value).array());
  }
  
  public static BigInteger convertUnsignedInt(int value) {
    if (value >= 0) {
      return convertInt(value);
    }
    return new BigInteger(ByteBuffer.allocate(NUMBER_OF_BYTES_INT + 1).putInt(1,value).array());
  }
  
  public static BigInteger convertLong(long value) {
    return new BigInteger(ByteBuffer.allocate(NUMBER_OF_BYTES_LONG).putLong(value).array());
  }
  
  public static BigInteger convertUnsignedLong(long value) {
    if (value >= 0) {
      return convertLong(value);
    }
    return new BigInteger(ByteBuffer.allocate(NUMBER_OF_BYTES_LONG + 1).putLong(1,value).array());
  }
  
  public static byte [] toByteArrayLittleEndian(BigInteger bi) {
    byte [] extractedBytes = bi.toByteArray();
    byte [] reversed = ByteUtils.reverseArray(extractedBytes);
    return reversed;
  }
  
  public static byte [] toByteArrayUnsigned(BigInteger bi) {
    byte [] extractedBytes = bi.toByteArray();
    int skipped = 0;
    boolean skip = true;
    for (byte b : extractedBytes) {
      boolean signByte = b == (byte)0x00;
      if (skip && signByte) {
        skipped++;
        continue;
      } else if (skip) {
        skip = false;
      }
    }
    extractedBytes = Arrays.copyOfRange(extractedBytes, skipped, extractedBytes.length);
    return extractedBytes;
  }
  
  public static byte [] toByteArrayLittleEndianUnsigned(BigInteger bi) {
    byte [] extractedBytes = toByteArrayUnsigned(bi);
    byte [] reversed = ByteUtils.reverseArray(extractedBytes);
    return reversed;
  }
  
  public static BigInteger newLittleEndian(byte [] bytes) {
    byte [] constructionBytes = ByteUtils.reverseArray(bytes);
    return new BigInteger(constructionBytes);
  }
  
  /**
   * Discards more significant bytes. Use before getByteArray for 
   * known unsigned numbers to clip to data type length.
   * 
   * {@link BigIntUtils#NUMBER_OF_BYTES_BYTE}
   * {@link BigIntUtils#NUMBER_OF_BYTES_SHORT}
   * {@link BigIntUtils#NUMBER_OF_BYTES_INT}
   * {@link BigIntUtils#NUMBER_OF_BYTES_LONG}
   * 
   * @param bi the @{code BigInteger} to clip
   * @param numberOfBytes number of bytes to clip to
   * @return a new BigInteger
   */
  public static BigInteger clip(BigInteger bi, int numberOfBytes) {
    byte [] unclipped = bi.toByteArray();
    byte [] constructionBytes = Arrays.copyOfRange(unclipped, 
        unclipped.length  -numberOfBytes , unclipped.length);
    return new BigInteger(constructionBytes);
  }
  
  /**
   * Discards less significant bytes
   * @param bi the @{code BigInteger} to truncate
   * @param numberOfBytes number of bytes to truncate to
   * @return a new BigInteger
   */
  public static BigInteger truncate(BigInteger bi, int numberOfBytes) {
    byte [] unclipped = bi.toByteArray();
    byte [] constructionBytes = Arrays.copyOfRange(unclipped, 
        0 , numberOfBytes);
    return new BigInteger(constructionBytes);
  }
  

}
