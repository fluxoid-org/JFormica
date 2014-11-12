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

import org.cowboycoders.ant.messages.ChannelMessage;
import org.cowboycoders.ant.messages.Constants.DataElement;
import org.cowboycoders.ant.messages.FatalMessageException;
import org.cowboycoders.ant.messages.ValidationException;
import org.cowboycoders.ant.messages.MessageExceptionFactory;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.utils.ValidationUtils;

/**
 * Sets up exclude/include list
 * @author will
 *
 */
public class ConfigListIdMessage extends ChannelMessage {

  private static final int MAX_LIST_SIZE = 4;

  /**
   * The additional elements we are adding to channelmessage
   */
  private static DataElement [] additionalElements =
      new DataElement [] {
    DataElement.LIST_SIZE,
    DataElement.INCLUDE_EXCLUDE_FLAG,
  };

  /**
   *
   * @param channelNo of channel to affect
   * @param listSize size of list to use (max 4)
   * @param exclude true for exclusion, false for include
   */
  public ConfigListIdMessage(Integer channelNo, int listSize, boolean exclude) {
    super(MessageId.ID_LIST_CONFIG, channelNo,additionalElements);
    try {
      setListSize(listSize);
      setExcludeInclude(exclude);
    } catch (ValidationException e) {
      throw new FatalMessageException("Error setting values", e);
    }
  }

  /**
   * Channel number set to zero.
   * @param listSize to document
   * @param exclude to document
   */
  public ConfigListIdMessage(int listSize, boolean exclude) {
	  this(0,listSize,exclude);
  }

  private void setExcludeInclude(boolean exclude) {
    int flag = exclude ? 1 : 0;
    setDataElement(DataElement.INCLUDE_EXCLUDE_FLAG,flag);

  }

  /**
   * @param listSize size of list
   * @throws ValidationException if out of bounds
   */
  private void setListSize(int listSize) throws ValidationException {
    ValidationUtils.maxMinValidator(0, MAX_LIST_SIZE, listSize,
        MessageExceptionFactory.createMaxMinExceptionProducable("Transmit power")
        );
    setDataElement(DataElement.LIST_SIZE,listSize);
  }

}
