/**
 *     Copyright (c) 2012, Will Szumski
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
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.Constants.DataElements;

/**
 * Sets search priority for channels.
 * Not available on all ant chips.
 * @author will
 *
 */
public class ChannelSearchPriorityMessage extends ChannelMessage {
  
  /**
   * The additional elements we are adding to channelmessage
   */
  private static DataElements [] additionalElements = 
      new DataElements [] {
    DataElements.SEARCH_PRIORITY,
  };
  
  /**
   * Channels with higher priority take preference
   * @param channelNo target
   * @param priority 0-255
   */
  public ChannelSearchPriorityMessage(Integer channelNo,Integer priority) {
    super(MessageId.CHANNEL_SEARCH_PRIORITY, channelNo,additionalElements);
    try {
      setPriority(priority);
    } catch (ValidationException e) {
      throw new FatalMessageException("Error setting values", e);
    }
    
  }
  
  /**
   * 
   * @param priority
   * @throws ValidationException if out of bounds
   */
  private void setPriority(Integer priority) throws ValidationException {
    if (priority == null) {
      priority =0;
    }
    setAndValidateDataElement(DataElements.SEARCH_PRIORITY, priority);
    
  }

}
