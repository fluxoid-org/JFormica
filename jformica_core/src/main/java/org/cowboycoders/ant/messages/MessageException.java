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
package org.cowboycoders.ant.messages;

/**
 * 
 * Thrown when there is an error processing a method of
 * {@link Message} or one of its descendants.
 * 
 * @author will
 *
 */
public class MessageException extends Exception {

  /**
   * UID for serialisation
   */
  private static final long serialVersionUID = -327114350153625390L;

  /**
   * {@inheritDoc}
   */
  public MessageException() {
  }

  /**
   * {@inheritDoc}
   */
  public MessageException(String detailMessage) {
    super(detailMessage);
  }

  /**
   * {@inheritDoc}
   */
  public MessageException(Throwable throwable) {
    super(throwable);
  }

  /**
   * {@inheritDoc}
   */
  public MessageException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);

  }

}
