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
/**
 * 
 */
package org.cowboycoders.ant.messages.responses;

import org.cowboycoders.ant.messages.Constants.DataElement;
import org.cowboycoders.ant.messages.MessageException;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.StandardMessage;

/**
 * Contains device serial
 * 
 * @author will
 *
 */
public class SerialNumberResponse extends StandardMessage {
  
  /**
   * The additional elements we are adding to channelmessage
   */
  private static DataElement [] additionalElements = 
      new DataElement [] {
    DataElement.SERIAL_NUMBER,
  };

  public SerialNumberResponse() {
    super(MessageId.GET_SERIAL_NUM, additionalElements);
    setAllElementsMustBePresent(true);
  }

  /* (non-Javadoc)
   * @see org.cowboycoders.ant.messages.StandardMessage#validate()
   */
  @Override
  public void validate() throws MessageException {
    // no additional validation

  }
  
  public int getSerialNumber() {
    return getDataElement(DataElement.SERIAL_NUMBER);
  }

}
