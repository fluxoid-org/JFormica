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

import org.cowboycoders.ant.ChannelId;
import org.cowboycoders.ant.messages.Constants.DataElement;
import org.cowboycoders.ant.utils.ValidationUtils;

/**
 * Channel id common functionality shared between classes
 * @author will
 *
 */
public class ChannelIdCompanion {
  

  private StandardMessage message;
  
  public ChannelIdCompanion(StandardMessage message) {
    this.setMessage(message);
  }

  public StandardMessage getMessage() {
    return message;
  }

  private void setMessage(StandardMessage message) {
    this.message = message;
  }
  
  /**
  * @param deviceType to set
  * @throws ValidationException if out of limit
  */
 public void setDeviceType(int deviceType) throws ValidationException {
   ValidationUtils.maxMinValidator(0, ChannelId.MAX_DEVICE_TYPE, deviceType, 
       MessageExceptionFactory.createMaxMinExceptionProducable("deviceType")
       );
   StandardMessage message = getMessage(); 
   message.setPartialDataElement(DataElement.DEVICE_TYPE,deviceType,ChannelId.DEVICE_TYPE_MASK);
 }
 

/**
* @param setPairingFlag  to set
*/
 public void setPairingFlag(boolean setPairingFlag) {
   int flag = setPairingFlag ? 1 : 0;
   StandardMessage message = getMessage(); 
   message.setPartialDataElement(DataElement.DEVICE_TYPE,flag,ChannelId.PAIRING_FLAG_MASK);
 }



/**
* 
* @param transmissionType  to set
* @throws ValidationException if out of limit
* 
* */
 public void setTransmissionType(int transmissionType) throws ValidationException {
   ValidationUtils.maxMinValidator(0, ChannelId.MAX_TRANSMISSION_TYPE, transmissionType, 
       MessageExceptionFactory.createMaxMinExceptionProducable("transmissionType")
       );
   StandardMessage message = getMessage(); 
   message.setDataElement(DataElement.TRANSMISSION_TYPE, transmissionType);
   
 }

 /**
  * 
  * @param deviceNumber to set
  * @throws ValidationException if out of limit
  */
 public void setDeviceNumber(int deviceNumber) throws ValidationException {
   ValidationUtils.maxMinValidator(0, ChannelId.MAX_DEVICE_NUMBER, deviceNumber, 
       MessageExceptionFactory.createMaxMinExceptionProducable("deviceNumber")
       );
   StandardMessage message = getMessage(); 
   message.setDataElement(DataElement.DEVICE_NUMBER,deviceNumber);
 }
 
 public int getDeviceNumber() {
	 StandardMessage message = getMessage(); 
	 return message.getDataElement(DataElement.DEVICE_NUMBER);
 }
 
 public int getTransmissionType() {
	 StandardMessage message = getMessage(); 
	 return message.getDataElement(DataElement.TRANSMISSION_TYPE);
 }
 
 public boolean isPairingFlagSet() {
	 StandardMessage message = getMessage();
	 int unmasked = message.getDataElement(DataElement.DEVICE_TYPE);
	 return (unmasked & ChannelId.PAIRING_FLAG_MASK) > 0 ? true : false ;
 }
 
 public int getDeviceType() {
	 StandardMessage message = getMessage();
	 int unmasked = message.getDataElement(DataElement.DEVICE_TYPE);
	 return (unmasked &  ChannelId.DEVICE_TYPE_MASK);
 }
  

}
