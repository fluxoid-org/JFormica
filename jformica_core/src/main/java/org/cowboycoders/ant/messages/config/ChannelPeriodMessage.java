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
 * ChannelPeriodMessage
 * @author will
 *
 */
public class ChannelPeriodMessage extends ChannelMessage {
  
  private static final int MAX_PERIOD = 65535;
  
  /**
   * The additional elements we are adding to channelmessage
   */
  private static DataElement [] additionalElements = 
      new DataElement [] {
    DataElement.CHANNEL_PERIOD,
  };
  
  /**
   * creates channel message
   * 
   * Messaging period = channel period time (s) * 32768.
   * E.g.: To send or receive a message at 4Hz, set the channel period to 32768/4 = 8192.
   * 
   * @param channelNo target channel number
   * @param period period to set
   */
  public ChannelPeriodMessage(Integer channelNo, int period) {
    super(MessageId.CHANNEL_PERIOD, channelNo,additionalElements);
    try {
      setPeriod(period);
    } catch (ValidationException e) {
      throw new FatalMessageException("Error setting values", e);
    }
  }
  
  /**
   * Sets period
   * @param period to set
   * @throws ValidationException if out of limits
   */
  private void setPeriod(int period) throws ValidationException {
    ValidationUtils.maxMinValidator(0, MAX_PERIOD, period, 
        MessageExceptionFactory.createMaxMinExceptionProducable("period")
        );
    setDataElement(DataElement.CHANNEL_PERIOD,period);
    
  }

}
