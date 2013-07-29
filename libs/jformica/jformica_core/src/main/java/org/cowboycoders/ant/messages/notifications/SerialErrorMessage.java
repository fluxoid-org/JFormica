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

import java.util.List;

import org.cowboycoders.ant.messages.MessageException;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.StandardMessage;
import org.cowboycoders.ant.messages.Constants.DataElement;

/**
 * Sent if ant chip detects an error
 * @author will
 *
 */
public class SerialErrorMessage extends StandardMessage {
  
  /**
   * The additional elements we are adding to channelmessage
   */
  private static DataElement [] additionalElements = 
      new DataElement [] {
    DataElement.ERROR_CODE,
    // additional elements contain original message
  };
  
  
  public SerialErrorMessage() {
    super(MessageId.SERIAL_ERROR,additionalElements);
  }

  @Override
  public void validate() throws MessageException {
    if ( getStandardPayload().size() < 1) {
      throw new MessageException("insufficent data");
    }
  }

  
  /**
   * @return Error byte (unadulterated)
   */
  public byte getErrorNumber() {
    return getStandardPayload().get(0);
  }
  
  /**
   * @return true, if error caused by sync byte not being first in
   * packet
   */
  public boolean wasSyncByteMissing() {
    if(getErrorNumber() == 0x00) return true;
    return false;
  }
  
  /**
   * @return true, if checksum incorrect
   */
  public boolean wasChecksumIncorrect() {
    if(getErrorNumber() == 0x02) return true;
    return false;
  }
  
  /**
   * @return true, if too much data
   */
  public boolean wasPayloadBloated() {
    if(getErrorNumber() == 0x03) return true;
    return false;
  }
  
  /**
   * @return original message as raw bytes
   */
  public byte [] getOriginalMessage() {
    List<Byte> payload = getStandardPayload();
    //strip off error code
    payload = payload.subList(1, payload.size());
   byte [] rtn = new byte[payload.size()];
   for (int i = 0 ; i< payload.size() ; i++) {
     rtn[i] = payload.get(i);
   }
   return rtn;
  }

}
