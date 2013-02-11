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
package org.cowboycoders.ant.messages;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.cowboycoders.ant.messages.config.ChannelSearchPriorityMessage;
import org.junit.Test;

public class ChannelSearchPriorityMessageTest {

  @Test
  public void test() {
    StandardMessage msg = new ChannelSearchPriorityMessage(1,null);
    assertEquals(msg.getPayloadToSend(),Arrays.asList(new Byte[] {1,0}));
    msg = new ChannelSearchPriorityMessage(1,10);
    assertEquals(msg.getPayloadToSend(),Arrays.asList(new Byte[] {1,10}));

    for (Byte b : msg.encode()) {
      System.out.printf("%x ",b);
    }
    System.out.println();
    
  }
  
  @Test(expected=FatalMessageException.class)
  public void test_validationUpper() {
    new ChannelSearchPriorityMessage(0,256);
  }
  
  @Test(expected=FatalMessageException.class)
  public void test_validationLower() {
    new ChannelSearchPriorityMessage(0,-1);
  }

}
