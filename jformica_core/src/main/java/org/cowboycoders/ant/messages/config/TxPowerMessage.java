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
 * TxPowerMessage
 * @author will
 *
 */
public class TxPowerMessage extends StandardMessage {

  private static final int MAX_TX_POWER = 4;

  /**
   * The additional elements we are adding to channelmessage
   */
  private static DataElement [] additionalElements =
      new DataElement [] {
    DataElement.FILLER_BYTE,
    DataElement.TX_POWER,
  };

  /**
   * Transmit power - see output power table (9.4.3)
   * Affects all channels.p
   * @param  txPower power (max : 4)
   */
  public TxPowerMessage(int txPower) {
    super(MessageId.RADIO_TX_POWER,additionalElements);
    try {
      setTxPower(txPower);
    } catch (ValidationException e) {
      throw new FatalMessageException("Error setting values", e);
    }
  }

  /**
   * Sets transmit power on all channels
   * @param power to set
   * @throws ValidationException if out of limits
   */
  private void setTxPower(int power) throws ValidationException {
    ValidationUtils.maxMinValidator(0, MAX_TX_POWER, power,
        MessageExceptionFactory.createMaxMinExceptionProducable("Transmit power")
        );
    setDataElement(DataElement.TX_POWER,power);

  }

  @Override
  public void validate() throws MessageException {
    throw new MessageException("not designed to be used with decode");
  }

}
