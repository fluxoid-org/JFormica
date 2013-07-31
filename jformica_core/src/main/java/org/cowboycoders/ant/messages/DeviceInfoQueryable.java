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

public interface DeviceInfoQueryable {

  /**
   * Gets the currently set device number
   * @return the {@code Integer} value or null if not set
   */
  public abstract Integer getDeviceNumber();

  /**
   * Gets the currently set device type
   * @return the {@code Integer} value or null if not set
   */
  public abstract Byte getDeviceType();

  /**
   * Gets the currently set transmission type
   * @return the {@code Integer} value or null if not set
   */
  public abstract Byte getTransmissionType();
  
  /**
   * Gets device info in form of a {@link ChannelId}
   * @return the {@link ChannelId} containing the device settings
   */
  
  /**
   * Pairing flag setting
   * @return true if pairing flag is set, false if it is not, null if not available
   */
  public abstract Boolean isPairingFlagSet();
  
  /**
   * ChannelID info associated with this {@code Object}
   * @return the {@link ChannelId} info associated with this {@code Object}
   */
  public abstract ChannelId getChannelId();

}