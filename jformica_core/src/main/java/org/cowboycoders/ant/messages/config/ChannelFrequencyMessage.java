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

import org.cowboycoders.ant.messages.ChannelMessage;
import org.cowboycoders.ant.messages.FatalMessageException;
import org.cowboycoders.ant.messages.ValidationException;
import org.cowboycoders.ant.messages.MessageExceptionFactory;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.Constants.DataElement;
import org.cowboycoders.ant.utils.ValidationUtils;

/**
 * ChannelFrequencyMessage
 * @author will
 *
 */
public class ChannelFrequencyMessage extends ChannelMessage {
  
  private static final int MAX_CHANNEL_FREQUENCY = 124;
  
  /**
   * The additional elements we are adding to channelmessage
   */
  private static DataElement [] additionalElements = 
      new DataElement [] {
    DataElement.CHANNEL_FREQUENCY,
  };
  
  /**
   * Radio frequency 
   * 
   * @param channelNo target channel number
   * @param channelFrequency 2400 MHz + this value (max: 124)
   */
  public ChannelFrequencyMessage(Integer channelNo, int channelFrequency) {
    super(MessageId.CHANNEL_RADIO_FREQ, channelNo,additionalElements);
    try {
      setChannelFrequency(channelFrequency);
    } catch (ValidationException e) {
      throw new FatalMessageException("Error setting values", e);
    }
  }
  
  /**
   * Sets timeout
   * @param timeout to set
   * @throws ValidationException if out of limits
   */
  private void setChannelFrequency(int frequency) throws ValidationException {
    ValidationUtils.maxMinValidator(0, MAX_CHANNEL_FREQUENCY, frequency, 
        MessageExceptionFactory.createMaxMinExceptionProducable("Channel frequency")
        );
    setDataElement(DataElement.CHANNEL_FREQUENCY,frequency);
    
  }

}
