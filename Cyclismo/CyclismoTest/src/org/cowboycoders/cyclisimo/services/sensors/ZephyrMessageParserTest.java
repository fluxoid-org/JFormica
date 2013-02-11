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
package org.cowboycoders.cyclisimo.services.sensors;

import org.cowboycoders.cyclisimo.content.Sensor;

import org.cowboycoders.cyclisimo.services.sensors.ZephyrMessageParser;

import junit.framework.TestCase;

public class ZephyrMessageParserTest extends TestCase {

  ZephyrMessageParser parser = new ZephyrMessageParser();

  public void testIsValid() {
    byte[] smallBuf = new byte[59];
    assertFalse(parser.isValid(smallBuf));
    // A complete and valid Zephyr HxM packet
    byte[] buf = { 2,38,55,26,0,49,101,80,0,49,98,100,42,113,120,-53,-24,-60,-123,-61,117,-69,42,-75,74,-78,51,-79,27,-83,28,-88,28,-93,29,-98,25,-103,26,-108,26,-113,59,-118,0,0,0,0,0,0,-22,3,125,1,48,0,96,4,30,0 };
    // Make buffer invalid
    buf[0] = buf[58] = buf[59] = 0;
    assertFalse(parser.isValid(buf));
    buf[0] = 0x02;
    assertFalse(parser.isValid(buf));
    buf[58] = 0x1E;
    assertFalse(parser.isValid(buf));
    buf[59] = 0x03;
    assertTrue(parser.isValid(buf));
  }

  public void testParseBuffer() {
    byte[] buf = new byte[60];
    // Heart Rate (-1 =^ 255 unsigned byte)
    buf[12] = -1;
    // Battery Level
    buf[11] = 51;
    // Cadence (=^ 255*16 strides/min)
    buf[56] = -1;
    buf[57] = 15;
    Sensor.SensorDataSet sds = parser.parseBuffer(buf);
    assertTrue(sds.hasHeartRate());
    assertTrue(sds.getHeartRate().getState() == Sensor.SensorState.SENDING);
    assertEquals(255, sds.getHeartRate().getValue());
    assertTrue(sds.hasBatteryLevel());
    assertTrue(sds.getBatteryLevel().getState() == Sensor.SensorState.SENDING);
    assertEquals(51, sds.getBatteryLevel().getValue());
    assertTrue(sds.hasCadence());
    assertTrue(sds.getCadence().getState() == Sensor.SensorState.SENDING);
    assertEquals(255, sds.getCadence().getValue());
  }

  public void testFindNextAlignment() {
    byte[] buf = new byte[60];
    assertEquals(-1, parser.findNextAlignment(buf));
    buf[10] = 0x03;
    buf[11] = 0x02;
    assertEquals(10, parser.findNextAlignment(buf));
  }
}
