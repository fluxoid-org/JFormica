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

import java.util.ArrayList;
import java.util.List;

public interface Messageable {

  /**
   * Gets the message id of the ANT message that this class encapsulates
   * @return the {@code MessageType} this {@code Message}
   *            corresponds to.
   */
  public abstract MessageId getId();

  /**
   * Returns the size of {@code Message.payload}
   * @return the size (number of bytes)
   */
  public abstract byte getPayloadSize();

  /**
   * returns the raw bytes to be written to the ant chip,
   * excluding checksum and sync byte
   * @return TODO: document this
   */
  public abstract byte[] encode();

  /**
   *  Returns the payload actually sent to the ant chip. Override
   *  if sent message is in different format to that received.
   * @return the payload to send to the ant chip
   */
  public abstract List<Byte> getPayloadToSend();

  /**
   * Returns the payload in the standard (non extended) form.
   * @return the payload in standard form
   */
  public abstract ArrayList<Byte> getStandardPayload();

  /**
   * Sets the payload in the standard (non extended) form.
   * @param payload the payload in standard form
   * @throws ValidationException if payload is malformed
   */
  public abstract void setStandardPayload(ArrayList<Byte> payload)
      throws ValidationException;

  /**
   * see  TODO : fix this code Message.decode(byte[], boolean)
   * @param buffer message to debug
   * @throws MessageException when error occurs
   */
  public abstract void decode(byte[] buffer) throws MessageException;

  /**
   * Returns all data held in form suitable for calling decode with.
   * In comparison with encode, this should not lose data.
   * @return the data
   */
  public byte [] toArray();


}
