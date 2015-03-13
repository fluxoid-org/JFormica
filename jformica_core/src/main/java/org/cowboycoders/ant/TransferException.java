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
package org.cowboycoders.ant;

import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.responses.ResponseCode;

/**
 * @author will
 *
 */
public class TransferException extends ChannelError {


  private static final long serialVersionUID = 1L;
  private MessageId messageId;
  private ResponseCode responseCode;


 /**
   * @return the messageId
   */
  public MessageId getMessageId() {
    return messageId;
  }


  /**
   * @param messageId the messageId to set
   */
  public void setMessageId(MessageId messageId) {
    this.messageId = messageId;
  }


  /**
   * @return the responseCode
   */
  public ResponseCode getResponseCode() {
    return responseCode;
  }


  /**
   * @param responseCode the responseCode to set
   */
  public void setResponseCode(ResponseCode responseCode) {
    this.responseCode = responseCode;
  }


/**
  * Ant msg id and response code of event that caused this eception to be thrown
  * @param id TODO : document this
  * @param response TODO : document this
  */
  public TransferException(MessageId id, ResponseCode response) {
    this.messageId = id;
    this.responseCode = response;
  }

  public TransferException(MessageId id, ResponseCode response, String detail) {
    super(detail);
    this.messageId = id;
    this.responseCode = response;
  }



}
