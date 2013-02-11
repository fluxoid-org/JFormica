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
package org.cowboycoders.ant.messages.notifications;

import static org.junit.Assert.*;

import org.cowboycoders.ant.messages.MessageException;
import org.junit.Test;

public class StartupMessageTest {

  @Test
  public void test() {
    StartupMessage msg = new StartupMessage();
    try {
      msg.decode(new byte[]{1,(byte)0x6f,(byte) 255});
    } catch (MessageException e) {
      e.printStackTrace();
    }
    assertTrue(msg.wasCommandReset());
    assertTrue(msg.wasHardwareLineReset());
   // assertTrue(msg.wasPowerOnReset());
    assertTrue(msg.wasSuspendReset());
    assertTrue(msg.wasSynchronousReset());
    assertTrue(msg.wasWatchDogReset());
    
    try {
      msg.decode(new byte[]{1,(byte)0x6f,(byte) (1 << 7)});
    } catch (MessageException e) {
      e.printStackTrace();
    }
    
    assertTrue(msg.wasSuspendReset());
    
    try {
      msg.decode(new byte[]{1,(byte)0x6f,(byte) (1 << 6)});
    } catch (MessageException e) {
      e.printStackTrace();
    }
    
    assertTrue(msg.wasSynchronousReset());
    
    try {
      msg.decode(new byte[]{1,(byte)0x6f,(byte) (1 << 5)});
    } catch (MessageException e) {
      e.printStackTrace();
    }
    
    assertTrue(msg.wasCommandReset());
    
    try {
      msg.decode(new byte[]{1,(byte)0x6f,(byte) (1 << 1)});
    } catch (MessageException e) {
      e.printStackTrace();
    }
    
    assertTrue(msg.wasWatchDogReset());
    
    try {
      msg.decode(new byte[]{1,(byte)0x6f,(byte) (1 << 0)});
    } catch (MessageException e) {
      e.printStackTrace();
    }
    
    assertTrue(msg.wasHardwareLineReset());
    
    try {
      msg.decode(new byte[]{1,(byte)0x6f,(byte) 0});
    } catch (MessageException e) {
      e.printStackTrace();
    }
    
    assertTrue(msg.wasPowerOnReset());
    
    
    
    
  }
  

}
