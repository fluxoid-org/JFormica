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
package org.cowboycoders.ant.messages.responses;

import org.cowboycoders.ant.messages.Constants.DataElement;
import org.cowboycoders.ant.messages.MessageException;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.StandardMessage;

/**
 * contains version string
 * @author will
 *
 */
public class VersionResponse extends StandardMessage{
  
  /**
   * The additional elements we are adding to channelmessage
   */
  private static DataElement [] additionalElements;
  
  private static final byte VERSION_STRING_LENGTH = 11;
  
  static {
    additionalElements = new DataElement[VERSION_STRING_LENGTH];
    for (int i = 0 ; i< additionalElements.length; i++) {
      additionalElements[i] = DataElement.VERSION_STRING_BYTE;
    }
    
  }
  
  /**
   * creates version response message
   */
  public VersionResponse() {
    super(MessageId.VERSION, additionalElements);
    setAllElementsMustBePresent(true);
  }
  
  /**
   * Returns the version string as a {@code java.lang.String}
   * @return the version string
   */
  public String getVersionString() {
    char [] string = new char[VERSION_STRING_LENGTH];
    for (int i = 0 ; i< VERSION_STRING_LENGTH; i++) {
      string[i] = (char)getDataElement(DataElement.VERSION_STRING_BYTE,i).intValue();
    }
    return new String(string);
  }

  @Override
  public void validate() throws MessageException {
    // no additional validation
  }

}
