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
import org.cowboycoders.ant.messages.ValidationException;
import org.cowboycoders.ant.messages.MessageId;

/**
 * ant.ChannelUnassignMessage
 * @author will
 *
 */
public class ChannelUnassignMessage extends ChannelMessage {
  
  /**
   * Unassigns an ant channel
   * @param channelNo channel number to unassign
   * @throws ValidationException if channelNo out of bounds
   */
  public ChannelUnassignMessage(Integer channelNo) throws ValidationException {
    super(MessageId.UNASSIGN_CHANNEL, channelNo);
    if (channelNo == null) {
      channelNo = new Integer(0);
    }
    setChannelNumber(channelNo);
  }
  
}
