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
 * Shared constants
 * @author will
 *
 */
public class Constants {
  
  public enum DataElement {
  
  CHANNEL_ID (1),
  
  DEVICE_NUMBER  (2),
  DEVICE_TYPE  (1),
  TRANSMISSION_TYPE  (1),
  
  RSSI_MEASUREMENT_TYPE (1),
  RSSI_VALUE  (1),
  RSSI_THRESHOLD_CONFIG  (1),
  RX_TIMESTAMP  (2),
  
  FILLER_BYTE (1),
  EXTENDED_MESSAGE_FLAG(1),
  
  CHANNEL_TYPE(1),
  NETWORK_NUMBER(1),
  EXTENDED_ASSIGNMENT(1),
  
  CHANNEL_PERIOD(2,65535),
  
  SEARCH_TIMEOUT(1),
  
  CHANNEL_FREQUENCY(1,124),
  
  CHANNEL_TX_POWER(1,4),
  
  NETWORK_KEY(1),
  
  // non channel specific tx power
  TX_POWER(1,4),
  
  // include/exclude list
  LIST_ID(1,3),
  
  LIST_SIZE(1,4),
  INCLUDE_EXCLUDE_FLAG(1,1),
  
  DATA(1),
  
  SEARCH_THRESHOLD(1,10),
  
  SEARCH_PRIORITY(1),
  
  STARTUP_INFO(1),
  
  ERROR_CODE(1),
  
  CHANNEL_REQUEST_MSG_ID(1),
  
  MESSAGE_ID(1),
  RESPONSE_CODE(1),
  
  CHANNEL_STATUS(1),
  
  VERSION_STRING_BYTE(1),
  
  SERIAL_NUMBER(4),
  
  MAX_CHANNELS(1),
  MAX_NETWORKS(1),
  STANDARD_CAPABILITIES(1),
  ADVANCED_CAPABILITIES(1),
  
  DATA_BYTE(1),
  
  
  
  ;
  private byte length;
  
  private Integer maxValue;
  
  private Integer minValue;
  
  /**
   * DataElement with length
   * @param length of data element
   */
  DataElement(int length) {
    this(length,null,null);
  }
  
  DataElement(int length, Integer maxValue) {
    this(length,maxValue,null);
    
  }
  
  DataElement(int length, Integer maxValue, Integer minValue) {
    setLength((byte) length);
    setMaxValue(maxValue);
    setMinValue(minValue);
    
  }

  public byte getLength() {
    return length;
  }

  private void setLength(byte length) {
    this.length = length;
  }

  public Integer getMaxValue() {
    return maxValue;
  }

  public void setMaxValue(Integer maxValue) {
    if(maxValue == null) {
      maxValue = (int) (1 << (getLength() * 8) ) -1;
    }
    this.maxValue = maxValue;
  }

  public Integer getMinValue() {
    return minValue;
  }

  public void setMinValue(Integer minValue) {
    if(minValue == null) {
      minValue = 0;
    }
    this.minValue = minValue;
  }
  }
  
  /**
   * Stop instantiation
   */
  private Constants() {
    
  }
 
 
}
