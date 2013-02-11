/*
*    Copyright (c) 2013, Will Szumski
*    Copyright (c) 2013, Doug Szumski
*
*    This file is part of Cyclismo.
*
*    Cyclismo is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Cyclismo is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Cyclismo.  If not, see <http://www.gnu.org/licenses/>.
*/
/*
 * Copyright 2009 Google Inc. All Rights Reserved.
 */

package org.cowboycoders.cyclisimo.stats;

import org.cowboycoders.cyclisimo.stats.DoubleBuffer;

import junit.framework.TestCase;

/**
 * Test for the DoubleBuffer class.
 *
 * @author Sandor Dornbush
 */
public class DoubleBufferTest extends TestCase {

  /**
   * Tests that the constructor leaves the buffer in a valid state.
   */
  public void testConstructor() {
    DoubleBuffer buffer = new DoubleBuffer(10);
    assertFalse(buffer.isFull());
    assertEquals(0.0, buffer.getAverage());
    double[] averageAndVariance = buffer.getAverageAndVariance();
    assertEquals(0.0, averageAndVariance[0]);
    assertEquals(0.0, averageAndVariance[1]);
  }

  /**
   * Simple test with 10 of the same values.
   */
  public void testBasic() {
    DoubleBuffer buffer = new DoubleBuffer(10);

    for (int i = 0; i < 9; i++) {
      buffer.setNext(1.0);
      assertFalse(buffer.isFull());
      assertEquals(1.0, buffer.getAverage());
      double[] averageAndVariance = buffer.getAverageAndVariance();
      assertEquals(1.0, averageAndVariance[0]);
      assertEquals(0.0, averageAndVariance[1]);
    }
    buffer.setNext(1);
    assertTrue(buffer.isFull());
    assertEquals(1.0, buffer.getAverage());
    double[] averageAndVariance = buffer.getAverageAndVariance();
    assertEquals(1.0, averageAndVariance[0]);
    assertEquals(0.0, averageAndVariance[1]);
  }

  /**
   * Tests with 5 entries of -10 and 5 entries of 10.
   */
  public void testSplit() {
    DoubleBuffer buffer = new DoubleBuffer(10);

    for (int i = 0; i < 5; i++) {
      buffer.setNext(-10);
      assertFalse(buffer.isFull());
      assertEquals(-10.0, buffer.getAverage());
      double[] averageAndVariance = buffer.getAverageAndVariance();
      assertEquals(-10.0, averageAndVariance[0]);
      assertEquals(0.0, averageAndVariance[1]);
    }

    for (int i = 1; i < 5; i++) {
      buffer.setNext(10);
      assertFalse(buffer.isFull());
      double expectedAverage = ((i * 10.0) - 50.0) / (i + 5);
      assertEquals(buffer.toString(),
                   expectedAverage, buffer.getAverage(), 0.01);
      double[] averageAndVariance = buffer.getAverageAndVariance();
      assertEquals(expectedAverage, averageAndVariance[0]);
    }
    buffer.setNext(10);
    assertTrue(buffer.isFull());
    assertEquals(0.0, buffer.getAverage());
    double[] averageAndVariance = buffer.getAverageAndVariance();
    assertEquals(0.0, averageAndVariance[0]);
    assertEquals(100.0, averageAndVariance[1]);
  }

  /**
   * Tests that reset leaves the buffer in a valid state.
   */
  public void testReset() {
    DoubleBuffer buffer = new DoubleBuffer(10);

    for (int i = 0; i < 100; i++) {
      buffer.setNext(i);
    }
    assertTrue(buffer.isFull());

    buffer.reset();
    assertFalse(buffer.isFull());
    assertEquals(0.0, buffer.getAverage());
    double[] averageAndVariance = buffer.getAverageAndVariance();
    assertEquals(0.0, averageAndVariance[0]);
    assertEquals(0.0, averageAndVariance[1]);
  }


  /**
   * Tests that if a lot of items are inserted the smoothing and looping works.
   */
  public void testLoop() {
    DoubleBuffer buffer = new DoubleBuffer(10);

    for (int i = 0; i < 1000; i++) {
      buffer.setNext(i);
      assertEquals(i >= 9, buffer.isFull());
      if (i > 10) {
        assertEquals(i - 4.5, buffer.getAverage());
      }
    }
  }

}
