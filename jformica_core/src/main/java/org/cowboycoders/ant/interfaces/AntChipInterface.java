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
package org.cowboycoders.ant.interfaces;

import org.cowboycoders.ant.events.BroadcastMessenger;

public interface AntChipInterface {

  /**
   * Initialises chip ready for send/receiving
   *
   * @return true if successful or already started
   */
  boolean start();

  /**
   * Shuts down ant chip or does nothing if already stopped
   */
  void stop();

  /**
   * Adds a messenger to send received messages to
   *
   * @param rxMessenger to send messages to
   */
  void registerRxMesenger(BroadcastMessenger<byte[]> rxMessenger);

  /**
   * Adds a messenger which will be informed when the chip status changes
   *
   * @param statusMessenger to send messages to
   */
  void registerStatusMessenger(
      BroadcastMessenger<AntStatusUpdate> statusMessenger);

  /**
   * send a bytes array directly to the antchip. You should not use this method
   * directly. Use
   * {@link org.cowboycoders.ant.Node#send(org.cowboycoders.ant.messages.StandardMessage)}
   *
   * @param message to document
   * @throws AntCommunicationException if message could not be sent
   */
  void send(byte[] message) throws AntCommunicationException;

  /**
   * Check the ant status
   * @return the last ant status update
   */
  AntStatusUpdate getStatus();

  /**
   * Does the ant chip think its running?
   * @return true if running, else false.
   */
  boolean isRunning();

}
