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

import org.cowboycoders.ant.messages.ChannelMessage;
import org.cowboycoders.ant.messages.FatalMessageException;
import org.cowboycoders.ant.messages.ValidationException;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.Constants.DataElement;

/**
 * Request channel / ant info
 * @author will
 *
 */
public class ChannelRequestMessage extends ChannelMessage {
  
  /**
   * The additional elements we are adding to channel message
   */
  private static DataElement [] additionalElements = 
      new DataElement [] {
    DataElement.CHANNEL_REQUEST_MSG_ID,
  };
  
  /**
   * Supported request messages
   * @author will
   *
   */
  public enum Request {
	CHANNEL_STATUS(0x52),
    CHANNEL_ID(0x51),
    ANT_VERSION(0x3E),
    CAPABILITIES(0x54),
    SERIAL_NUMBER(0x61),
    ;
    
    byte msgId;
    
    /**
     * @return the msgId
     */
    public byte getMsgId() {
      return msgId;
    }

    /**
     * @param msgId the msgId to set
     */
    private void setMsgId(byte msgId) {
      this.msgId = msgId;
    }

    Request(int msgId) {
     setMsgId((byte)msgId);
    }

  }

  public ChannelRequestMessage(Integer channelNo, Request request) {
    super(MessageId.REQUEST, channelNo, additionalElements );
    try {
      setRequest(request);
    } catch (ValidationException e) {
      throw new FatalMessageException("Error setting values",e);
    }
  }
  
  /**
   * Use default channel (zero). If sent though {@link org.cowycoders.ant.Channel} this
   * will be set to the corresponding channel number associated with that instance.
   * @param request
   */
  public ChannelRequestMessage(Request request) {
	  this(0,request);
  }
  
  /**
   * @param request to set
   * @throws ValidationException if fails validation
   */
  private void setRequest(Request request) throws ValidationException {
    setAndValidateDataElement(DataElement.CHANNEL_REQUEST_MSG_ID, request.getMsgId());
  }

}
