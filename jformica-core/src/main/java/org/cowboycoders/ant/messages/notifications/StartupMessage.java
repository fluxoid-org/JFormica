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
package org.cowboycoders.ant.messages.notifications;

import org.cowboycoders.ant.messages.Constants.DataElement;
import org.cowboycoders.ant.messages.MessageException;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.StandardMessage;

/**
 * Sent after system reset
 * @author will
 *
 */
public class StartupMessage extends StandardMessage {
  
  
  /**
   * The additional elements we are adding to channelmessage
   */
  private static DataElement [] additionalElements = 
      new DataElement [] {
    DataElement.STARTUP_INFO,
  };
  
  /**
   * designed to be populated with decode
   */
  public StartupMessage() {
    super(MessageId.STARTUP, additionalElements);
  }

  @Override
  public void validate() throws MessageException {
    if ( getStandardPayload().size() < 1) {
      throw new MessageException("insufficent data");
    }
  }
  
  /**
   * @return true, if caused by power on
   */
  public boolean wasPowerOnReset() {
    if (getStandardPayload().get(0) == 0) return true;
    return false;
  }
  
  /**
   * 
   * @return true if hardware line was reset
   */
  public boolean wasHardwareLineReset() {
    if ((getStandardPayload().get(0) & (1 << 0)) != 0) return true;
    return false;
  }
  
  /**
   * @return true if reset by watchdog timer
   */
  public boolean wasWatchDogReset() {
    if ((getStandardPayload().get(0) & (1 << 1)) != 0) return true;
    return false;
  }
  
  /**
   * @return true if reset result of 
   * {@code org.cowboycoders.org.ant.messages.control.ResetMessage}
   */
  public boolean wasCommandReset() {
    if ((getStandardPayload().get(0) & (1 << 5)) != 0) return true;
    return false;
  }
  
  /**
   * @return true if synchronous
   */
  public boolean wasSynchronousReset() {
    if ((getStandardPayload().get(0) & (1 << 6)) != 0) return true;
    return false;
  }
  
  /**
   * @return true if result of powersaving
   */
  public boolean wasSuspendReset() {
    if ((getStandardPayload().get(0) & (1 << 7)) != 0) return true;
    return false;
  }
  
  
  
  
  
  
  
  
  
  
  
  
  

}
