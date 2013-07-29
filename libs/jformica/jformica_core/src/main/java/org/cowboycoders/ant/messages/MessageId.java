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

import java.util.HashMap;

/**
 * Encapsulates ant message types
 * @author will
 *
 */
public enum MessageId {
  INVALID                      ((byte)0x00),
  EVENT                        ((byte)0x01),

  VERSION                      ((byte)0x3E),
  RESPONSE_EVENT               ((byte)0x40),

  UNASSIGN_CHANNEL             ((byte)0x41),
  ASSIGN_CHANNEL               ((byte)0x42),
  CHANNEL_PERIOD               ((byte)0x43),
  CHANNEL_SEARCH_TIMEOUT       ((byte)0x44),
  CHANNEL_RADIO_FREQ           ((byte)0x45),
  NETWORK_KEY                  ((byte)0x46),
  RADIO_TX_POWER               ((byte)0x47),
  RADIO_CW_MODE                ((byte)0x48),
      
  SYSTEM_RESET                 ((byte)0x4A),
  OPEN_CHANNEL                 ((byte)0x4B),
  CLOSE_CHANNEL                ((byte)0x4C),
  REQUEST                      ((byte)0x4D),

  BROADCAST_DATA               ((byte)0x4E),
  ACKNOWLEDGED_DATA            ((byte)0x4F),
  BURST_DATA                   ((byte)0x50),

  CHANNEL                      ((byte)0x51),
  CHANNEL_STATUS               ((byte)0x52),
  RADIO_CW_INIT                ((byte)0x53),
  CAPABILITIES                 ((byte)0x54),

  STACKLIMIT                   ((byte)0x55),

  SCRIPT_DATA                  ((byte)0x56),
  SCRIPT_CMD                   ((byte)0x57),

  ID_LIST_ADD                  ((byte)0x59),
  ID_LIST_CONFIG               ((byte)0x5A),
  OPEN_RX_SCAN                 ((byte)0x5B),

      /**
       *  OBSOLETE: ((byte)for 905 radio)
       *  @deprecated
       */
  EXT_CHANNEL_RADIO_FREQ       ((byte)0x5C),
  EXT_BROADCAST_DATA           ((byte)0x5D),
  EXT_ACKNOWLEDGED_DATA        ((byte)0x5E),
  EXT_BURST_DATA               ((byte)0x5F),

  CHANNEL_RADIO_TX_POWER       ((byte)0x60),
  GET_SERIAL_NUM               ((byte)0x61),
  GET_TEMP_CAL                 ((byte)0x62),
  SET_LP_SEARCH_TIMEOUT        ((byte)0x63),
  SET_TX_SEARCH_ON_NEXT        ((byte)0x64),
  SERIAL_NUM_SET_CHANNEL       ((byte)0x65),
  RX_EXT_MESGS_ENABLE          ((byte)0x66), 
  RADIO_CONFIG_ALWAYS          ((byte)0x67),
  ENABLE_LED_FLASH             ((byte)0x68),
      
  XTAL_ENABLE                  ((byte)0x6D),
      
  STARTUP                      ((byte)0x6F),
  AUTO_FREQ_CONFIG             ((byte)0x70),
  PROX_SEARCH_CONFIG           ((byte)0x71),
  EVENT_BUFFERING_CONFIG       ((byte)0x74),

      
  CUBE_CMD                     ((byte)0x80),

  GET_PIN_DIODE_CONTROL        ((byte)0x8D),
  PIN_DIODE_CONTROL            ((byte)0x8E),
  FIT1_SET_AGC                 ((byte)0x8F),
  
  //adds
  
  LIB_CONFIG                   ((byte)0x6E),
  CHANNEL_SEARCH_PRIORITY      ((byte)0x75),
  SERIAL_ERROR                 ((byte)0xAE),
  
  
  
  ;
  
  private static HashMap<Byte, MessageId> iDTypeMap = 
      new HashMap<Byte, MessageId>();
  
  static {
    for( MessageId type : MessageId.values() ) {
      iDTypeMap.put(type.getMessageID(), type);
    }
  }
  /**
   * Returns the message type for a given message ID
   * @param messageID byte value of the message ID
   * @return the type mapped to {@code messageID} or null
   *            if mapping doesn't exist   
   */
  public static MessageId lookUp(byte messageID) {
    return iDTypeMap.get(messageID);  
  }
  
  private final byte messageID;

  
  MessageId(byte messageID) {
    this.messageID = messageID;
  }
  
  /** 
   * Gets the the byte value for a give {@code MessageID}
   * @return The corresponding message ID
   */
  public byte getMessageID() {
    return messageID;
  }

    
  
}
