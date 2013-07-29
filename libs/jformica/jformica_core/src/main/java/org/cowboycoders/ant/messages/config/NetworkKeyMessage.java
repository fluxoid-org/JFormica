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

import org.cowboycoders.ant.messages.Constants.DataElement;
import org.cowboycoders.ant.messages.FatalMessageException;
import org.cowboycoders.ant.messages.MessageException;
import org.cowboycoders.ant.messages.MessageExceptionFactory;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.StandardMessage;
import org.cowboycoders.ant.messages.ValidationException;
import org.cowboycoders.ant.utils.ValidationUtils;

/**
 * @author will
 *
 */
public class NetworkKeyMessage extends StandardMessage {
  
  private static final int MAX_NETWORK_NUMBER = 255;
  
  /**
   * Max value for individual bytes
   */
  private static final int MAX_NETWORK_KEY_ELEMENT = 255;
  
  private static final int MAX_NETWORK_KEY_LENGTH = 8;
  
  private static final int MIN_NETWORK_KEY_LENGTH = 8;
  
  
  
  
  /**
   * The additional elements we are adding to channelmessage
   */
  private static DataElement [] additionalElements = 
      new DataElement [9];
  
  static {
    int index = 0;
    additionalElements[index] = DataElement.NETWORK_NUMBER;
    index ++;
    for (int i = index ; i< additionalElements.length ; i++) {
      additionalElements[i] = DataElement.NETWORK_KEY;
      index = i;
    }
  }

  /**
   * 
   * @param networkNumber 0 to max net number (check capabilities)
   * @param networkKey values contained cannot be larger than 255
   */
  public NetworkKeyMessage(int networkNumber, int [] networkKey) {
    super(MessageId.NETWORK_KEY, additionalElements);
    try {
      setNetworkNumber(networkNumber);
      setNetworkKey(networkKey);
    } catch (ValidationException e) {
      throw new FatalMessageException("Error setting values", e);
    }
  }

  /**
   * Sets network key
   * @param networkKey 8 individual bytes
   * @throws ValidationException if key too long, or an element is too large
   */
  private void setNetworkKey(int[] networkKey) throws ValidationException {
    ValidationUtils.maxMinValidator(MIN_NETWORK_KEY_LENGTH, MAX_NETWORK_KEY_LENGTH, networkKey.length, 
        MessageExceptionFactory.createMaxMinExceptionProducable("Number of network key elements")
        );
    for (int i = 0 ; i < networkKey.length ; i++) {
      ValidationUtils.maxMinValidator(0, MAX_NETWORK_KEY_ELEMENT, networkKey[i], 
          MessageExceptionFactory.createMaxMinExceptionProducable("element " + i + " value")
          );
      setDataElement(DataElement.NETWORK_KEY, networkKey[i],i);
    }
    
  }
  /**
   * Sets network number
   * @param networkNumber
   * @throws ValidationException if out of bounds
   */
  private void setNetworkNumber(int networkNumber) throws ValidationException {
    ValidationUtils.maxMinValidator(0, MAX_NETWORK_NUMBER, networkNumber, 
        MessageExceptionFactory.createMaxMinExceptionProducable("Network number")
        );
    setDataElement(DataElement.NETWORK_NUMBER, networkNumber);
  }

  /* (non-Javadoc)
   * @see org.cowboycoders.ant.messages.StandardMessage#validate()
   */
  @Override
  public void validate() throws MessageException {
    // This message is not supposed to be decoded
    throw new FatalMessageException("decoding not supported");

  }

}
