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

public interface DeviceInfoSettable {

  /**
   * Sets device number
   * @param deviceId the new device number
   * @throws ValidationException if not within expected range
   */
  public abstract void setDeviceNumber(int deviceId) throws ValidationException;

  /**
   * Sets device type
   * @param deviceType the new device type
   * @throws ValidationException if not within expected range
   */
  public abstract void setDeviceType(int deviceType) throws ValidationException;
  
  /**
   * Sets transmission type
   * @param transmissionType the new transmission type
   * @throws ValidationException if not within expected range
   */
  public void setTransmissionType(int transmissionType) throws  ValidationException;
  
  /**
   * Set all channel info at once.
   * @param id {@link ChannelId} object encapsulating the channel id
   */
  public void setChannelId(ChannelId id);
  
  /**
   * Pairing flag setting
   * @param pair true to enable pairing flag, false otherwise.
   */
  public abstract void setPairingFlag(boolean pair);

}