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

import org.cowboycoders.ant.messages.ChannelIdCompanion;
import org.cowboycoders.ant.messages.ChannelMessage;
import org.cowboycoders.ant.messages.FatalMessageException;
import org.cowboycoders.ant.messages.ValidationException;
import org.cowboycoders.ant.messages.MessageExceptionFactory;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.Constants.DataElement;
import org.cowboycoders.ant.utils.ValidationUtils;

/**
 * Adds devices to channel exclude/include list
 * @author will
 *
 */
public class AddChannelIdMessage extends ChannelMessage {
  
  private static final int MAX_LIST_INDEX = 3;
  
  /**
   * The additional elements we are adding to channelmessage
   */
  private static DataElement [] additionalElements = 
      new DataElement [] {
    DataElement.DEVICE_NUMBER,
    DataElement.DEVICE_TYPE,
    DataElement.TRANSMISSION_TYPE,
    DataElement.LIST_ID,
  };
  
  /** companion object we delegate shared methods to */
  private ChannelIdCompanion companion;
  
  
  public AddChannelIdMessage(Integer channelNo, int deviceNumber, int deviceType,
      int transmissionType, int listIndex) {
    super(MessageId.ID_LIST_ADD , channelNo, additionalElements);
    this.companion = new ChannelIdCompanion(this);
    try {
      setDeviceNumber(deviceNumber);
      setDeviceType(deviceType);
      setTransmissionType(transmissionType);
      setListIndex(listIndex);
    } catch (ValidationException e) {
      throw new FatalMessageException("Error setting values",e);
    }
  }
  
  public AddChannelIdMessage(int deviceNumber, int deviceType,
	      int transmissionType, int listIndex) {
	  this(0,deviceNumber, deviceType,
	      transmissionType, listIndex);
	  }
  
  /**
   * Sets list index
   * @param listIndex min:0 , max:3
   * @throws ValidationException if out of bounds
   */
  private void setListIndex(int listIndex) throws ValidationException {
    ValidationUtils.maxMinValidator(0, MAX_LIST_INDEX, listIndex, 
        MessageExceptionFactory.createMaxMinExceptionProducable("List index")
        );
    setDataElement(DataElement.LIST_ID,listIndex);
  }


  /**
  * @param deviceType to set
  * @throws ValidationException if out of limit
  */
 private void setDeviceType(int deviceType) throws ValidationException {
   companion.setDeviceType(deviceType);
 }
 

/**
* 
* @param transmissionType  to set
* @throws ValidationException if out of limit
* 
* */
 private void setTransmissionType(int transmissionType) throws ValidationException {
   companion.setTransmissionType(transmissionType);
 }

 /**
  * 
  * @param deviceNumber to set
  * @throws ValidationException if out of limit
  */
 private void setDeviceNumber(int deviceNumber) throws ValidationException {
   companion.setDeviceNumber(deviceNumber);
 }

}
