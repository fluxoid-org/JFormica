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
package org.cowboycoders.ant.messages.commands;

import org.cowboycoders.ant.messages.ChannelMessage;
import org.cowboycoders.ant.messages.MessageId;

/**
 * Opens channel in continuous scanning mode
 * @author will
 *
 */
public class ChannelOpenRxScanMode extends ChannelMessage {
  
  
  /**
   * Should set up channel 0 as slave before sending this
   */
  public ChannelOpenRxScanMode() {
    super(MessageId.OPEN_RX_SCAN, 0);
  }

}