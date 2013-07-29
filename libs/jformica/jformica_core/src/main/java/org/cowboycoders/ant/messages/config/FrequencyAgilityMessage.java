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
package org.cowboycoders.ant.messages.config;

import org.cowboycoders.ant.messages.ChannelMessage;
import org.cowboycoders.ant.messages.Constants.DataElement;
import org.cowboycoders.ant.messages.FatalMessageException;
import org.cowboycoders.ant.messages.ValidationException;
import org.cowboycoders.ant.messages.MessageExceptionFactory;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.utils.ValidationUtils;

/**
 * Frequency hopping - device must have capability
 * @author will
 *
 */
public class FrequencyAgilityMessage extends ChannelMessage {
  
  private static final int MAX_FREQUENCY = 124;
  
  /**
   * The additional elements we are adding to channelmessage
   */
  private static DataElement [] additionalElements = 
      new DataElement [] {
    DataElement.CHANNEL_FREQUENCY,
    DataElement.CHANNEL_FREQUENCY,
    DataElement.CHANNEL_FREQUENCY,
  };
  
  /**
   * 2400Mhz + frequency for 3 bands to switch between. All frequencies
   * have a max value of 124.
   * @param channelNo channel to use frequency hopping with
   * @param frequency1 first band
   * @param frequency2 second band
   * @param frequency3 third band
   */
  public FrequencyAgilityMessage(Integer channelNo, 
      Integer frequency1, Integer frequency2,  Integer frequency3) {
    super(MessageId.AUTO_FREQ_CONFIG, channelNo, additionalElements);
    if (frequency1 == null) {
      frequency1=  Integer.valueOf(3);
    }
    if (frequency2 == null) {
      frequency1= Integer.valueOf(39);
    }
    if (frequency3 == null) {
      frequency1= Integer.valueOf(75);
    }
    try {
      setFrequency(frequency1,1);
      setFrequency(frequency2,2);
      setFrequency(frequency3,3);
    } catch (ValidationException e) {
      throw new FatalMessageException("Error setting values", e);
    }
    
  }
  
  public FrequencyAgilityMessage( 
	      Integer frequency1, Integer frequency2,  Integer frequency3) {
	  	this(0,frequency1, frequency2,  frequency3);
	    
	  }

  private void setFrequency(Integer frequency, int i) throws ValidationException {
    ValidationUtils.maxMinValidator(0, MAX_FREQUENCY, frequency, 
        MessageExceptionFactory.createMaxMinExceptionProducable("Search timeout")
        );
    setDataElement(DataElement.CHANNEL_FREQUENCY,frequency,i);
  }

}
