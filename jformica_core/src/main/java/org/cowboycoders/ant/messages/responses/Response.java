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

import org.cowboycoders.ant.messages.ChannelMessage;
import org.cowboycoders.ant.messages.MessageException;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.Constants.DataElement;

/**
 * Dual purpose class that encompasses events and responses
 * @author will
 *
 */
public class Response extends ChannelMessage {
  
  /**
   * The additional elements we are adding to channel message
   */
  private static DataElement [] additionalElements = 
      new DataElement [] {
    DataElement.MESSAGE_ID,
    DataElement.RESPONSE_CODE,
  };
  
  /**
   * Populated with {@code decode()} 
   * @param channelNo channel event occured on
   */
  public Response(Integer channelNo) {
    super(MessageId.RESPONSE_EVENT, channelNo,additionalElements);
  }

  public Response() {
    this(0);
  }

  /* (non-Javadoc)
   * @see org.cowboycoders.ant.messages.ChannelMessage#validate()
   */
  @Override
  public void validate() throws MessageException {
    super.validate();
    if(getStandardPayload().size() < 3) {
      throw new MessageException("insufficent data");
    }
  }
  
  /**
   * @return {@code org.cowboycoders.ant.messages.MessageId} of message that caused
   *         event. This is equal to {@code MessageId.EVENT} if wasn't result of
   *         message sent.
   */
  public MessageId getMessageId() {
    return MessageId.lookUp(getDataElement(DataElement.MESSAGE_ID).byteValue());
  }
  
  public ResponseCode getResponseCode() {
    return ResponseCode.lookUp(getDataElement(DataElement.RESPONSE_CODE).byteValue());
  }
  
  
  
  
 
  

}
