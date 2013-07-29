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
import org.cowboycoders.ant.messages.Constants.DataElement;
import org.cowboycoders.ant.messages.FatalMessageException;
import org.cowboycoders.ant.messages.ValidationException;
import org.cowboycoders.ant.messages.MessageId;

/**
 * Search of devices based on signal strength bands
 * 
 * @author will
 *
 */
public class ProximitySearchMessage extends ChannelMessage {
  
  /**
   * The additional elements we are adding to channelmessage
   */
  private static DataElement [] additionalElements = 
      new DataElement [] {
    DataElement.SEARCH_THRESHOLD,
  };
  
  /**
   * Filter using signal strength
   * @param channelNo target
   * @param threshold 0-10 , 0 disabled, 1-10 signal strength bands
   */
  public ProximitySearchMessage(Integer channelNo, int threshold) {
    super(MessageId.PROX_SEARCH_CONFIG, channelNo,additionalElements);
    try {
      setThreshold(threshold);
    } catch (ValidationException e) {
      throw new FatalMessageException("Error setting values", e);
    }
  }
  
  public ProximitySearchMessage(int threshold) {
	  this(0,threshold);
  }
  
  
  private void setThreshold(int threshold) throws ValidationException {
    setAndValidateDataElement(DataElement.SEARCH_THRESHOLD, threshold);
  }

}
