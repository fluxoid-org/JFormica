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
package org.cowboycoders.ant.messages.config;

import org.cowboycoders.ant.messages.Constants.DataElement;
import org.cowboycoders.ant.messages.MessageException;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.StandardMessage;

/**
 * @author will
 *
 */
public class EnableExtendedMessagesMessage extends StandardMessage {
  
  /**
   * Enables extended Messages. 
   * @param enable true to enable
   */
  public EnableExtendedMessagesMessage(boolean enable) {
    super(MessageId.RX_EXT_MESGS_ENABLE, new DataElement [] {
        DataElement.FILLER_BYTE,
        DataElement.EXTENDED_MESSAGE_FLAG
    } );
    
    byte flag = generateFlag(enable);
    setFlag(flag);
  }
  
  
  private void setFlag(byte flag) {
    setDataElement(DataElement.EXTENDED_MESSAGE_FLAG,(int) flag);
  }

  /**
   * Generates payload
   * @param enable if extended messages are required
   * @return a suitable payload
   */
  private static byte generateFlag(boolean enable) {
    byte flag = 0;
    if (enable) {
      flag = 1;
    }
    return flag;
  }

  /* (non-Javadoc)
   * @see org.cowboycoders.ant.messages.StandardMessage#validate()
   */
  @Override
  public void validate() throws MessageException {
    // not need as we do not recieve

  }

}
