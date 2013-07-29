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
package org.cowboycoders.ant.messages.commands;

import org.cowboycoders.ant.messages.Constants.DataElement;
import org.cowboycoders.ant.messages.MessageException;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.StandardMessage;

/**
 * resets the ant chip
 * @author will
 *
 */
public class ResetMessage extends StandardMessage {

  /**
   * The additional elements we are adding to channel message
   */
  private static DataElement [] additionalElements = 
      new DataElement [] {
    DataElement.FILLER_BYTE,
  };
  
  public ResetMessage() {
    super(MessageId.SYSTEM_RESET, additionalElements);
  }

  @Override
  public void validate() throws MessageException {
    // no validation needed
  }
  
  

}
