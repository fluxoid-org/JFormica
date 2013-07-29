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
package org.cowboycoders.ant.messages;


/**
 * Abstract channel type - combines codes from
 * the Types enum
 * @author will
 *
 */
public abstract class ChannelType {
  
  /**
   * true if shared channel
   */
  private boolean shared = false;

  /**
   * true if oneway transmit/receive
   */
  private boolean oneway = false;
  
  public enum Types {
    SLAVE            (0x00),
    MASTER           (0x10),
    SHARED_RECEIVE   (0x20),
    SHARED_TRANSMIT  (0x30),
    ONEWAY_RECEIVE   (0x40),
    ONEWAY_TRANSMIT  (0x50),
    ;
    
    public int code;
    
    Types (int code) {
      this.code =  code;
    }
  }
  
  private int channelTypeCode = 0;
  
  public int getChannelTypeCode() {
    return channelTypeCode;
  }
  
  /**
   * build ChannelType with the supplied ChannelTypes.Types
   * @param channelTypes
   */
  protected ChannelType(Types [] channelTypes) {
    for (Types c : channelTypes) {
      channelTypeCode |= c.code;
      if (c == Types.SHARED_RECEIVE || c == Types.SHARED_TRANSMIT) {
        shared = true;
      }
      if (c == Types.ONEWAY_RECEIVE || c == Types.ONEWAY_TRANSMIT) {
        oneway = true;
      }
    }
  }
  
  /**
   * @return true, if shared Channel
   */
  public boolean isShared() {
    return shared;
  }

  /**
   * @return true if channel is oneway transmit/receive
   */
  public boolean isOneway() {
    return oneway;
  }
}