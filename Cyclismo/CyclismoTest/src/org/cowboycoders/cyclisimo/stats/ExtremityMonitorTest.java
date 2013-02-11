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
/**
 * Copyright 2009 Google Inc. All Rights Reserved.
 */
package org.cowboycoders.cyclisimo.stats;

import org.cowboycoders.cyclisimo.stats.ExtremityMonitor;

import junit.framework.TestCase;
import java.util.Random;

/**
 * This class test the ExtremityMonitor class.
 *
 * @author Sandor Dornbush
 */
public class ExtremityMonitorTest extends TestCase {

  public ExtremityMonitorTest(String name) {
    super(name);
  }

  public void testInitialize() {
    ExtremityMonitor monitor = new ExtremityMonitor();
    assertEquals(Double.POSITIVE_INFINITY, monitor.getMin());
    assertEquals(Double.NEGATIVE_INFINITY, monitor.getMax());
  }

  public void testSimple() {
    ExtremityMonitor monitor = new ExtremityMonitor();
    assertTrue(monitor.update(0));
    assertTrue(monitor.update(1));
    assertEquals(0.0, monitor.getMin());
    assertEquals(1.0, monitor.getMax());
    assertFalse(monitor.update(1));
    assertFalse(monitor.update(0.5));
  }

  /**
   * Throws a bunch of random numbers between [0,1] at the monitor.
   */
  public void testRandom() {
    ExtremityMonitor monitor = new ExtremityMonitor();
    Random random = new Random(42);
    for (int i = 0; i < 1000; i++) {
      monitor.update(random.nextDouble());
    }
    assertTrue(monitor.getMin() < 0.1);
    assertTrue(monitor.getMax() < 1.0);
    assertTrue(monitor.getMin() >= 0.0);
    assertTrue(monitor.getMax() > 0.9);
  }

  public void testReset() {
    ExtremityMonitor monitor = new ExtremityMonitor();
    assertTrue(monitor.update(0));
    assertTrue(monitor.update(1));
    monitor.reset();
    assertEquals(Double.POSITIVE_INFINITY, monitor.getMin());
    assertEquals(Double.NEGATIVE_INFINITY, monitor.getMax());
    assertTrue(monitor.update(0));
    assertTrue(monitor.update(1));
    assertEquals(0.0, monitor.getMin());
    assertEquals(1.0, monitor.getMax());
  }
}
