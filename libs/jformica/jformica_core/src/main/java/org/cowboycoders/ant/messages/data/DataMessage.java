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
package org.cowboycoders.ant.messages.data;

import java.util.ArrayList;
import java.util.List;

import org.cowboycoders.ant.messages.ChannelMessage;
import org.cowboycoders.ant.messages.FatalMessageException;
import org.cowboycoders.ant.messages.Message;
import org.cowboycoders.ant.messages.ValidationException;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.Constants.DataElement;
import org.cowboycoders.ant.utils.ByteUtils;

/**
 * Common functionality for all data messages
 * @author will
 *
 */
public abstract class DataMessage extends ChannelMessage {
  
  /**
   * The additional elements we are adding to channelmessage
   */
  private static DataElement [] additionalElements;
  
  private static final byte DATA_LENGTH = 8;
  
  static {
    additionalElements = new DataElement[DATA_LENGTH];
    for (int i = 0 ; i< additionalElements.length; i++) {
      additionalElements[i] = DataElement.DATA_BYTE;
    }
  }
  
  protected DataMessage(Message backend, MessageId id,
      Integer channelNo) {
    super(backend,id, channelNo,additionalElements);
  }

  /**
   * {@see setData(Byte[])}
   * @param data
   */
  public void setData(byte[] data) {
    Byte [] boxed = new Byte[data.length];
    for(int i =0 ; i < data.length ; i++) {
      boxed[i] = data[i];
    }
    setData(boxed);
  }
  
  /**
   * 
   * @param data to set as 'data' section in payload (must be exactly 8 bytes)
   * @throws FatalMessageException on error setting payload
   */
  public void setData(Byte[] data) {
    if (data.length != DATA_LENGTH) {
      throw new FatalMessageException("data array incorrect length");
    }
    ArrayList <Byte> payload = new ArrayList<Byte>();
    payload.add((byte)getChannelNumber());
    for(int i =0 ; i < data.length ; i++) {
      payload.add(data[i]);
    }
    try {
      setStandardPayload(payload);
    } catch (ValidationException e) {
        throw new FatalMessageException("Error setting data",e);
    }
  }
   
  
  /**
   * {@see getData()}
   */
  public byte [] getPrimitiveData() {
    Byte [] boxedData = getData();
    byte [] rtn = new byte[boxedData.length];
    for(int i =0 ; i < boxedData.length ; i++) {
      rtn[i] = boxedData[i];
    }
    return rtn;
  }
  
  /**
   * returns 'data' section of payload
   * @return data contained in payload (8 bytes)
   */
  public Byte [] getData() {
    List <Byte> payload = getStandardPayload();
    payload = payload.subList(1, payload.size());
    return payload.toArray(new Byte[0]);
  }
  
  /**
   * Returns 'data' section of payload converted to ints.
   * Takes into account that the bytes should be unsigned and
   * thus negative byte values are mapped to positive ints.
   * @return the payload as an int []
   */
  public int[] getUnsignedData() {
    return ByteUtils.unsignedBytesToInts(getData());
  }
  

}
