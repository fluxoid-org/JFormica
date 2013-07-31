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
package org.cowboycoders.ant.messages.data;

import org.cowboycoders.ant.ChannelId;
import org.cowboycoders.ant.messages.DeviceInfoQueryable;
import org.cowboycoders.ant.messages.DeviceInfoSettable;
import org.cowboycoders.ant.messages.LegacyMessage;
import org.cowboycoders.ant.messages.ValidationException;
import org.cowboycoders.ant.messages.MessageId;

/**
 * Legacy extended broadcast data message
 * @author will
 *
 */
public class LegacyExtendedBroadcastDataMessage extends BroadcastDataMessage
  implements DeviceInfoQueryable, DeviceInfoSettable {
    
  public LegacyExtendedBroadcastDataMessage() {
      this(0);
    }

    public LegacyExtendedBroadcastDataMessage(int channel) {
      super(new LegacyMessage(), MessageId.EXT_BROADCAST_DATA, channel);
    }

    @Override
    public Integer getDeviceNumber() {
      return ((LegacyMessage)getBackendMessage()).getDeviceNumber();
    }

    @Override
    public Byte getDeviceType() {
      return ((LegacyMessage)getBackendMessage()).getDeviceType();
    }

    @Override
    public Byte getTransmissionType() {
      return ((LegacyMessage)getBackendMessage()).getDeviceType();
    }
    
    @Override
    public void setDeviceNumber(int deviceId) throws ValidationException {
      ((LegacyMessage)getBackendMessage()).setDeviceNumber(deviceId);
      
    }

    @Override
    public void setDeviceType(int deviceType) throws ValidationException {
      ((LegacyMessage)getBackendMessage()).setDeviceType(deviceType);
      
    }

    @Override
    public void setTransmissionType(int transmissionType)
        throws ValidationException {
      ((LegacyMessage)getBackendMessage()).setTransmissionType(transmissionType);
      
    }

    @Override
    public void setChannelId(ChannelId id) {
    	setDeviceNumber(id.getDeviceNumber());
    	setDeviceType(id.getDeviceType());
    	setTransmissionType(id.getTransmissonType());
    	setPairingFlag(id.isPairingFlagSet());
    }


    @Override
    public ChannelId getChannelId() {
      	ChannelId id = ChannelId.Builder.newInstance()
          		.setDeviceNumber(getDeviceNumber())
          		.setDeviceType(getDeviceType())
          		.setTransmissonType(getTransmissionType())
          		.setPairingFlag(isPairingFlagSet())
          		.build();
      	return id;
    }

	@Override
	public void setPairingFlag(boolean pair) {
		((LegacyMessage)getBackendMessage()).setPairingFlag(pair);
	}

	@Override
	public Boolean isPairingFlagSet() {
		return ((LegacyMessage)getBackendMessage()).isPairingFlagSet();
	}





}
