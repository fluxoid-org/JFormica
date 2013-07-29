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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
  
	public static byte [] joinArray(byte [] ... arrays) {
		int totalLength = 0;
		for (byte [] array : arrays) {
			totalLength += array.length;
		}
		// allocate
		byte [] joined = new byte[totalLength];
		
		int nextIndex = 0;
		for (byte [] array: arrays ) {
			for (int i = nextIndex ; i < array.length + nextIndex ; i++) {
				joined[i] = array [i - nextIndex];
			}
			nextIndex += array.length;
		}
		
		return joined;
	}
	

	public static int [] unsignedBytesToInts(Byte [] bytes) {
	  int [] rtn = new int[bytes.length];
	  for (int i=0 ; i < bytes.length ; i++) {
	    rtn[i] = unsignedByteToInt(bytes[i]);
	  }
	  return rtn;
	}

	public static Byte [] intsToBytes(int[] intArray){
	    Byte [] byteArray = new Byte [intArray.length];
	    for (int i=0; i < intArray.length; i++){
	        byteArray[i] = (byte)intArray[i];
	    }
	    return byteArray;
	}

	public static boolean arrayStartsWith(Byte[] pattern, Byte[] data){
		return ArrayUtils.arrayStartsWith(pattern, data);
	}
	
	public static boolean arrayStartsWith(byte [] pattern, byte [] data) {
	    for (int i=0; i<pattern.length; i++) {
	        if (data[i] != pattern[i]) {
	            return false;
	        }
	    }
	    return true;
	}
	
	public static void main (String [] args) {
		byte [] one = new byte[] {1,2,3};
		byte [] two = new byte[] {4};
		byte [] three = new byte[] {};
		byte [] four = new byte[] {5,6,7,8,9};
		
		for (byte b : ByteUtils.joinArray(one,two,three,four)) {
			System.out.println(b);
		}
		
		
		  System.out.println(ByteUtils.lsbMerge(Arrays.asList(new Byte [] {(byte) 0xd0,(byte) 0xaf})));
	}
	
	/*
	 * Utility classes that contain methods to
	 * convert byte arrays to integers and back.
	 * 
	 * Could replace with a more generic implementation based on
	 * BigInteger {@see BigIntUtils}
	 * @author will
	 *
	 */

	/**
	   * Least significant byte first
	   * @param list of bytes to merge into an Integer
	   * @return the merger of the bytes
	   */
	  public static Integer lsbMerge(List<Byte> data) {
	    Integer rtn = 0;
	    byte count = 0;
	    for (Byte b : data) {
	      rtn |= unsignedByteToInt(b) << (count * 8);
	      count ++;
	    }
	    return rtn;
	  }

	/**
	   * Splits a list of bytes ordered with least significant byte first
	   * @param in integer to split
	   * @param numberOfBytes number of bytes to produce
	   * @return
	   */
	  public static List<Byte> lsbSplit(Integer in, int numberOfBytes) {
	    List<Byte> bytes = new ArrayList<Byte>();
	    for (byte i = 0 ; i < numberOfBytes ; i++) {
	      int mask = 0xff << (i*8);
	      bytes.add((byte) ((in & mask) >>> (i*8)) );
	    }
	    return bytes;
	  }

	/**
	   * Splits a list of bytes ordered with most significant byte first
	   * @param in integer to split
	   * @param numberOfBytes number of bytes to produce
	   * @return
	   */
	  public static List<Byte> msbSplit(Integer in, int numberOfBytes) {
	    List<Byte> rtn = lsbSplit(in,numberOfBytes);
	    Collections.reverse(rtn);
	    return rtn;
	  }

	/**
	   * Most significant byte first
	   * @param list of bytes to merge into an Integer
	   * @return the merger of the bytes
	   */
	  public static Integer msbMerge(List<Byte> data) {
	    List<Byte> clone = new ArrayList<Byte>(data);
	    Collections.reverse(clone);
	    return lsbMerge(clone);
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
