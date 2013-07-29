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
package org.cowboycoders.ant.messages.data;

import org.cowboycoders.ant.messages.Message;
import org.cowboycoders.ant.messages.MessageId;

/**
 * Standard burst message
 * @author will
 *
 */
public class BurstDataMessage extends BurstData {

  public BurstDataMessage(Integer channelNo) {
    super(new Message(), MessageId.BURST_DATA, channelNo);
  }
  
  public BurstDataMessage() {
    super(new Message(), MessageId.BURST_DATA, 0);
  }

  protected BurstDataMessage(Message backend, MessageId id, Integer channelNo) {
    super(backend, id, channelNo);
  }
  

}
