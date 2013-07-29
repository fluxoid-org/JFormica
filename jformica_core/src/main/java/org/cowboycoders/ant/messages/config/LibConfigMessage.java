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

import org.cowboycoders.ant.messages.Constants.DataElement;
import org.cowboycoders.ant.messages.ExtendedMessage;
import org.cowboycoders.ant.messages.MessageException;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.StandardMessage;


public class LibConfigMessage extends StandardMessage {
  
  /**
   * Generate libconfig message
   * @param enableChannelId true if channel id info required
   * @param enableRssi true if rssi info is required
   * @param enableTimestamps true if timestamps are required
   */
  public LibConfigMessage(boolean enableChannelId, boolean enableRssi,
      boolean enableTimestamps) {
      super(MessageId.LIB_CONFIG, new DataElement [] {
          DataElement.FILLER_BYTE, DataElement.EXTENDED_MESSAGE_FLAG}
          );
      byte flag = 
      generateFlag(
          enableChannelId,enableRssi, enableTimestamps);
      setFlag(flag);
  }
  
  private void setFlag(byte flag) {
    setDataElement(DataElement.EXTENDED_MESSAGE_FLAG, (int)flag);
  }

  /**
   * Useful for some chips which do not support rssi
   * @param enableChannelId true if channel id info required
   * @param enableTimestamps true if timestamps are required
   */
  public LibConfigMessage(boolean enableChannelId,
      boolean enableTimestamps) {
      this(enableChannelId,false, enableTimestamps);  
  }
  
  /**
   * Generates a libconfig extended message flag
   * @param enableChannelId if require channel id info
   * @param enableRssi if require rssi info
   * @param enableTimestamps if require timestamp info
   * @return extended message flag
   */
  private static byte generateFlag(boolean enableChannelId,
      boolean enableRssi, boolean enableTimestamps) {
    byte value = ExtendedMessage.ExtendedFlag.DISABLE.getMask();
    if (enableChannelId) {
      value |= ExtendedMessage.ExtendedFlag.ENABLE_CHANNEL_ID.getMask();
    }
    if (enableRssi) {
      value |= ExtendedMessage.ExtendedFlag.ENABLE_RSSI_OUTPUT.getMask();
    }
    if (enableTimestamps) {
      value |= ExtendedMessage.ExtendedFlag.ENABLE_RX_TIMESTAMP.getMask();
    }
    return value; 
  }


  @Override
  public void validate() throws MessageException {
    // not needed as we do not receive these messages
  }

  
  

}
