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
package org.cowboycoders.ant;

import org.cowboycoders.ant.events.MessageCondition;
import org.cowboycoders.ant.messages.MessageMetaWrapper;
import org.cowboycoders.ant.messages.StandardMessage;
import org.cowboycoders.ant.messages.responses.Response;
import org.cowboycoders.ant.utils.SharedBuffer;

/**
 * Provides acknowledgement buffer and message buffer
 * @author will
 *
 */
@Deprecated
public abstract class BufferedNodeComponent {
  
  private static final int ACK_BUFFER_LENGTH = 20;
  
  private static final int MSG_BUFFER_LENGTH = 20;
  
  private SharedBuffer<MessageMetaWrapper<StandardMessage>> msgBuffer;
  
  private SharedBuffer<MessageMetaWrapper<Response>> ackBuffer;
  
  public BufferedNodeComponent() {
    this(null,null);
  }
  
  public BufferedNodeComponent(MessageCondition msgCondition, MessageCondition ackCondition) {
    msgBuffer = new SharedMetaBuffer<MessageMetaWrapper<StandardMessage>>(MSG_BUFFER_LENGTH, msgCondition);
    ackBuffer = new SharedMetaBuffer<MessageMetaWrapper<Response>>(ACK_BUFFER_LENGTH, ackCondition);
  }

  /**
   * @return the msgBuffer
   */
  public SharedBuffer<MessageMetaWrapper<StandardMessage>> getMsgBuffer() {
    return msgBuffer;
  }

  /**
   * @return the ackBuffer
   */
  public SharedBuffer<MessageMetaWrapper<Response>> getAckBuffer() {
    return ackBuffer;
  }
  
  


}
