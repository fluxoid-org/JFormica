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
package org.cowboycoders.turbotrainers.bushido.headunit;
import static org.junit.Assert.*;

import org.cowboycoders.turbotrainers.bushido.headunit.BushidoButtonPressDescriptor.Button;
import org.cowboycoders.turbotrainers.bushido.headunit.BushidoButtonPressDescriptor.Duration;
import org.junit.Test;


public class BushidoButtonDescriptorTest {

  @Test
  public void test_button_lookup() {
    Button b = BushidoButtonPressDescriptor.Button.fromId(1);
    assertEquals(Button.LEFT, b);
    b = BushidoButtonPressDescriptor.Button.fromId(2);
    assertEquals(Button.UP, b);
    b = BushidoButtonPressDescriptor.Button.fromByte((byte) 0xc3);
    assertEquals(Button.OK, b);
  }
  
  @Test
  public void test_duration_lookup() {
    Duration d = BushidoButtonPressDescriptor.Duration.fromByte((byte) 0xc3);
    assertEquals(Duration.LONG, d);
    d = BushidoButtonPressDescriptor.Duration.fromByte((byte) 0x03);
    assertEquals(Duration.SHORT, d);
  }
  
  @Test
  public void descriptor_build() {
    assertNotNull(BushidoButtonPressDescriptor.fromByte((byte)0xc5));
    // there is is no button 6
    assertNull(BushidoButtonPressDescriptor.fromByte((byte)0xc6));
    // not a current duration
    assertNull(BushidoButtonPressDescriptor.fromByte((byte)0xf3));
  }

}
