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
		int clearMask = mask ^ (~0);
	    int shift = BitUtils.getMaxZeroBitIndex(mask) + 1;
	    if (shift < 0) {
	      throw new IllegalArgumentException("value cannot be larger than mask");
	    }
	    value = value << shift;
	    wholeElement &= clearMask;
	    wholeElement |= (mask & value);
		return wholeElement;
  }

}
