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
import org.cowboycoders.ant.messages.StandardMessage;
import org.cowboycoders.ant.utils.Containable;
import org.cowboycoders.ant.utils.FixedSizeLifo;

public class MessageBufferContainer<T extends Containable<? extends StandardMessage>> extends FixedSizeLifo<T> {

  private MessageCondition condition;

  public MessageBufferContainer(int maxSize, MessageCondition condition) {
    super(maxSize);
    this.condition = condition;
  }

  /* (non-Javadoc)
   * @see org.cowboycoders.ant.events.FixedSizeBuffer#offer(java.lang.Object)
   */
  @Override
  public boolean offer(T e) {
    if (condition != null && !condition.test(e.unwrap()) ) {
      return false;
    }
    return super.offer(e);
  }
  
  

}
