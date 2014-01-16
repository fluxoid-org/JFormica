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
package org.cowboycoders.ant.messages.responses;

import static org.junit.Assert.*;

import org.cowboycoders.ant.messages.FatalMessageException;
import org.cowboycoders.ant.messages.MessageException;
import org.cowboycoders.ant.messages.notifications.StartupMessage;
import org.junit.Test;

public class VersionResponseTest {

  @Test (expected=MessageException.class)
  public void testValidationFail() throws MessageException {
    VersionResponse msg = new VersionResponse();
    msg.decode(new byte[]{1,(byte)0x3E,(byte) 255});
  }
  
  @Test
  public void testValidationPass() throws MessageException {
    VersionResponse msg = new VersionResponse();
    msg.decode(new byte[]{1,(byte)0x3E,(byte) 'h',(byte) 'e',(byte) 'l',(byte) 'l',(byte) 'o',(byte) ' ',(byte) 'w',(byte) 'o',(byte) 'r',(byte) 'l',(byte) '\0'});
    System.out.println(msg.getVersionString());
  }

}
