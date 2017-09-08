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

import org.cowboycoders.ant.messages.FatalMessageException;

public class IntUtils {

	public static final int BYTES = 4;

	private IntUtils(){};

  /**
   * Converts an int which is being treated as unsigned to a long
   * @param value to document
   * @return to document
   */
  public static Long unsignedIntToLong(int value) {
    return value & 0xffffffffL;
  }

  /**
   * Sets individual bits of given {@code int} specified
   * using a mask and value. The value's left most bit is aligned
   * with the left most bit of the mask. Values must be positive,
   * with a value no greater that the maximum number that can be
   * represented by the masked bits.
   *
   * The mask's bits must be contiguous, or the behaviour
   * is undefined.
   *
   * @param wholeElement the whole element to apply the mask to
   * @param value the value to set in the mask bits
   * @param mask only bits marked in mask are changed
   * @return the mast / to document
   */
  public static int setMaskedBits(int wholeElement,int mask, int value) {
		int clearMask = ~mask;
	    int shift = Integer.numberOfTrailingZeros(mask);
	    value = value << shift;
	    wholeElement &= clearMask;
	    wholeElement |= (mask & value);
		return wholeElement;
  }

  public static int getFromMask(int unmasked, int mask) {
  	int masked = unmasked & mask;
  	int shift  = Integer.numberOfTrailingZeros(mask);
  	//System.out.printf("shift %d\n", shift);
  	int ret = masked >>> shift;
  	return ret;
  }

	/**
	 * Little endian version
	 * @param wholeElement template
	 * @param mask where bytes are place, first byte is aligned to leftmost byte in mask
	 * @param value to put in mask
	 * @return template with mask bits replaced with value
	 */
	public static int setMaskedBitsLE(int wholeElement, int mask, int value) {
		return setMaskedBitsLE(wholeElement, mask, value, Integer.bitCount(mask));
	}

	public static int setMaskedBitsLE(int wholeElement, int mask, int value, int numBits) {

		// can we calc this directly
		int align = getAlignOffset(numBits);

		value = Integer.reverseBytes(value);;
		//int align = 32 - Integer.numberOfTrailingZeros(value);
		//align = align + ((8 - (align % 8)) % 8); // byte level align
		//value = value >>> align;
		//System.out.printf("rev: %x\n", value);
		int clearMask = ~mask;
		//System.out.printf("mask: %x\n", mask);
		int shift = Integer.numberOfLeadingZeros(mask);
		//System.out.printf("align : %x\n", align);
		//align = 0;
		//System.out.println("shift :" + shift);
		value = value >>> shift - align;
		//System.out.printf("val: %x\n", value);
		wholeElement &= clearMask;
		wholeElement |= (mask & value);
		return wholeElement;
	}

	public static int getFromMaskLE(int unmasked, int mask, int numBits) {

		// can we calc this directly
		int align = getAlignOffset(numBits);
		int masked = unmasked & mask;
		//System.out.printf("unmasked: %x\n", unmasked);
		//System.out.printf("masked: %x\n", masked);
		int shift = Integer.numberOfLeadingZeros(mask);
		//System.out.printf("shift: %d\n", shift);
		//System.out.printf("shifted: %x\n", masked << shift - align);
		int rev = Integer.reverseBytes(masked << shift -align);
		//System.out.printf("rev: %x\n", rev);
		return rev;
	}



	private static int getAlignOffset(int numBits) {
		int max = ( 0b1 << numBits) -1;
		int x  = Integer.reverseBytes(max);
		return Integer.numberOfLeadingZeros(x);
	}

	public static int maxValueThatFits(int mask) {
		return (0b1 << Integer.bitCount(mask)) -1;
	}

	public static int maxSigned(int numBits) {
		if (numBits == 0) return 0;
		return (0b1 << (numBits -1)) -1;
	}

	public static int maxUnsigned(int numBits) {
		if (numBits > 32) {
			throw new IllegalArgumentException("integer overflow");
		}
		if (numBits == 0) return 0;
		if (numBits == 32) return 0xffffffff;
		return (0b1 << (numBits)) -1;
	}

}
