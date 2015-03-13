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
   * Used on BigIntegers known to fit into x number of bytes.
   *
   * Discards more significant bytes.
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
    if (unclipped.length <= numberOfBytes) {
      //byte [] reversed = Arrays.copyOf(unclipped, numberOfBytes);
      //unclipped = ByteUtils.reverseArray(reversed);
      return bi;
    }
    byte [] constructionBytes = Arrays.copyOfRange(unclipped,
        unclipped.length  -numberOfBytes , unclipped.length);
    return new BigInteger(constructionBytes);
  }
//
//  /**
//   * Somewhat useless
//   * Discards less significant bytes
//   * @param bi the @{code BigInteger} to truncate
//   * @param numberOfBytes number of bytes to truncate to
//   * @return a new BigInteger
//   */
//  public static BigInteger truncate(BigInteger bi, int numberOfBytes) {
//    byte [] unclipped = bi.toByteArray();
//    if (unclipped.length <= numberOfBytes) {
//      return bi;
//    }
//    byte [] constructionBytes = Arrays.copyOfRange(unclipped,
//        0 , numberOfBytes);
//    return new BigInteger(constructionBytes);
//  }
//
  /**
   * Big endian
   * @param bi to document
   * @param numberOfBytes to document
   * @return to document
   */
  public static byte [] clipToByteArray(BigInteger bi, int numberOfBytes) {
    BigInteger clipped = clip(bi,numberOfBytes);
    byte [] rtn = clipped.toByteArray();
    if (rtn.length == numberOfBytes) {
      return rtn;
    }
    rtn = Arrays.copyOf(rtn, numberOfBytes);
    return ByteUtils.reverseArray(rtn);
  }

  /**
   * Little endian
   * @param bi to document
   * @param numberOfBytes  to document
   * @return to document
   */
  public static byte [] clipToByteArrayLittleEndian(BigInteger bi, int numberOfBytes) {
    BigInteger clipped = clip(bi,numberOfBytes);
    byte [] rtn = clipped.toByteArray();
    if (rtn.length == numberOfBytes) {
      return ByteUtils.reverseArray(rtn);
    }
    rtn = Arrays.copyOf(rtn, numberOfBytes);
    return rtn;
  }


}
