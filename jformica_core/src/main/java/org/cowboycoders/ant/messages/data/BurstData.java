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

import org.cowboycoders.ant.messages.Message;
import org.cowboycoders.ant.messages.ValidationException;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.Constants.DataElement;

/**
 * Common functionality to all burst message types
 * @author will
 *
 */
public abstract class BurstData extends DataMessage {

  public static final int CHANNEL_MASK = 0x1F;
  public static final int BURST_MAX_CHANNEL_NO = 0x1f;
  public static final int SEQUENCE_MASK =  0xe0;
  public static final int SEQUENCE_MAX = 0x07;

  protected BurstData(Message backend, MessageId id, Integer channelNo) {
    super(backend, id, channelNo);
  }

  /* (non-Javadoc)
   * @see org.cowboycoders.ant.messages.ChannelMessage#setChannelNumber(byte)
   */
  @Override
  public void setChannelNumber(int channelNumber) throws ValidationException {
    if (channelNumber > BURST_MAX_CHANNEL_NO || channelNumber < 0) {
      throw new ValidationException("Channel number must be between 0 and " +
          BURST_MAX_CHANNEL_NO);
    }
    setPartialDataElement(DataElement.CHANNEL_ID, (int) channelNumber,(int) CHANNEL_MASK);
  }

  /* (non-Javadoc)
   * @see org.cowboycoders.ant.messages.ChannelMessage#getChannelNumber()
   */
  @Override
  public int getChannelNumber() {
    return getDataElement(DataElement.CHANNEL_ID).byteValue() & CHANNEL_MASK;
  }

  /**
   * Each burst element corresponds to a value in the burst
   * sequence.
   * @param sequence current value
   * @throws ValidationException if sequence is &lt; 0 or &gt; 7
   */
  public void setSequenceNumber(int sequence) throws ValidationException {
    if (sequence > SEQUENCE_MAX || sequence < 0) {
      throw new ValidationException("sequnece number must be between 0 and " +
          SEQUENCE_MAX);
    }
    setPartialDataElement(DataElement.CHANNEL_ID,sequence,SEQUENCE_MASK);
  }

  /**
   * Gets this burst packets sequence number
   * @return sequence number of this burst packet
   */
  public int getSequenceNumber() {
    return (getDataElement(DataElement.CHANNEL_ID) & SEQUENCE_MASK) >>> 5 ;
  }



}
