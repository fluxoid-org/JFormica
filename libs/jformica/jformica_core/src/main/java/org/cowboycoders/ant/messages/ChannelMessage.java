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
package org.cowboycoders.ant.messages;

import java.util.ArrayList;
import java.util.Arrays;

import org.cowboycoders.ant.messages.Constants.DataElement;
import org.cowboycoders.ant.utils.ListUtils;

public abstract class ChannelMessage extends StandardMessage {

  public static final byte MAX_CHANNEL_NO = 127;
  public static final byte CHANNEL_NO_OFFSET = 0;
  
  /**
   * Creates a ChannelMessage using the a {@code Message}
   * as the backend. See @{code ChannelMessage(message, id, payload)}
   * for param explanations.
   * @throws ValidationException 
   */
  public ChannelMessage(MessageId id, Integer channelNo,
      ArrayList<DataElement> messageElements) {
    this(null,id, channelNo,messageElements);
  }
  
  public ChannelMessage(MessageId id, Integer channelNo,
      DataElement [] messageElements) {
    this(null,id, channelNo,
        messageElements);
  }
  
  public ChannelMessage(Message backend,MessageId id, Integer channelNo,
      DataElement [] messageElements) {
    this(backend,id, channelNo,
        new ArrayList<DataElement>(Arrays.asList(messageElements)));
  }
  
  /**
   * Creates a ChannelMessage using the a {@code Message}
   * as the backend without any additional data elements. 
   * See @{code ChannelMessage(message, id, payload)}
   * for param explanations.
   * @throws ValidationException 
   */
  public ChannelMessage(MessageId id, Integer channelNo) {
    this(null,id, channelNo,new DataElement[0]);
  }
  
  /**
   * Creates a ChannelMessage using {@code message} as its
   * backend
   * @param message the message to use as the backend
   * @param id the id of the message
   * @param messageElements additional message elements - this is appended
   *        to the the standard channel message list of elements
   * @throws FatalMessageException on error creating the message
   */
  public ChannelMessage(Message message, MessageId id, Integer channelNo,
      ArrayList<DataElement> messageElements) {
    super(message, id, ListUtils.prefixList(
        messageElements, 
        new DataElement [] {DataElement.CHANNEL_ID})
        );
        try {
          setChannelNumber(channelNo.byteValue());
        } catch(ValidationException e) {
          throw new FatalMessageException("invalid channel no",e);
        }
  }
  
   
  /**
   * Sets the channel to transmit on
   * 
   * @param channelNumber channel number (id)
   * @throws ValidationException if cahnnelNumber out of range
   */
  public void setChannelNumber(int channelNumber) throws ValidationException {
    if (channelNumber > MAX_CHANNEL_NO || channelNumber < 0) {
      throw new ValidationException("Channel number must be between 0 and " +
          MAX_CHANNEL_NO);
    }
    //ArrayList<Byte >payload = getStandardPayload();
    //payload.set(CHANNEL_NO_OFFSET, channelNumber);
   //setStandardPayload(payload);
    setDataElement(DataElement.CHANNEL_ID, channelNumber);
  }
  
  /**
   * Gets the channel number
   * @return the channel number
   */
  public int getChannelNumber(){
    //ArrayList<Byte >payload = getStandardPayload();
    //return payload.get(CHANNEL_NO_OFFSET);
    return getDataElement(DataElement.CHANNEL_ID);
  }
  
  // TODO: validate that message id is a suitable value
  @Override
  public void validate() throws MessageException {
    try {
      setChannelNumber(getChannelNumber());
    } catch (IndexOutOfBoundsException e) {
      throw new MessageException("Payload too small", e);
    }
    
    
  }
  
  
 
}
