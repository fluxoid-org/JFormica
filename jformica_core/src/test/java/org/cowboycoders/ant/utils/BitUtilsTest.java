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

import org.junit.Test;

public class BitUtilsTest {

  @Test
  public void testGetMaxBitIndex() {
    assertEquals(1,BitUtils.getMaxBitIndex(1));
    assertEquals(8,BitUtils.getMaxBitIndex(255));
    org.junit.Assert.assertNotSame(8, BitUtils.getMaxBitIndex(256));
  }

}
