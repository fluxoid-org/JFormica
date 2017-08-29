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

import org.cowboycoders.ant.messages.ChannelMessage;
import org.cowboycoders.ant.messages.Constants.DataElement;
import org.cowboycoders.ant.messages.FatalMessageException;
import org.cowboycoders.ant.messages.ValidationException;
import org.cowboycoders.ant.messages.MessageExceptionFactory;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.utils.ValidationUtils;

/**
 * ChannelSearchTimeoutMessage
 * @author will
 *
 */
public class ChannelSearchTimeoutMessage extends ChannelMessage {

  private static final int MAX_SEARCH_TIMEOUT = 255;

  /**
   * The additional elements we are adding to channelmessage
   */
  private static DataElement [] additionalElements =
      new DataElement [] {
    DataElement.SEARCH_TIMEOUT,
  };

  /**
   * Search timeout before channel stops looking for master
   *
   * @param channelNo target channel number
   * @param searchTimeout timeout each count equivalent to 2.5s, 255 infinite
   */
  public ChannelSearchTimeoutMessage(Integer channelNo, int searchTimeout) {
    super(MessageId.CHANNEL_SEARCH_TIMEOUT, channelNo,additionalElements);
    try {
      setSearchTimeout(searchTimeout);
    } catch (ValidationException e) {
      throw new FatalMessageException("Error setting values", e);
    }
  }

  /**
   * Sets timeout
   * @param timeout to set
   * @throws ValidationException if out of limits
   */
  private void setSearchTimeout(int timeout) throws ValidationException {
    ValidationUtils.maxMinValidator(0, MAX_SEARCH_TIMEOUT, timeout,
        MessageExceptionFactory.createMaxMinExceptionProducable("Search timeout")
        );
    setDataElement(DataElement.SEARCH_TIMEOUT,timeout);

  }

}
