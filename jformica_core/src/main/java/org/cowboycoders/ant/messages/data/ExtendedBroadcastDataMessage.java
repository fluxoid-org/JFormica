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
import org.cowboycoders.ant.messages.Constants.DataElement;
import org.cowboycoders.ant.messages.DeviceInfoQueryable;
import org.cowboycoders.ant.messages.DeviceInfoSettable;
import org.cowboycoders.ant.messages.ExtendedMessage;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.RssiInfoQueryable;
import org.cowboycoders.ant.messages.TimestampInfoQueryable;
import org.cowboycoders.ant.messages.ValidationException;

/**
 * Extended broadcast data message
 * @author will
 *
 */
public class ExtendedBroadcastDataMessage extends BroadcastDataMessage
  implements DeviceInfoQueryable, DeviceInfoSettable, RssiInfoQueryable, TimestampInfoQueryable {
    
  public ExtendedBroadcastDataMessage() {
      this(0);
    }

    public ExtendedBroadcastDataMessage(int channel) {
      super(new ExtendedMessage(), MessageId.BROADCAST_DATA, channel);
    }

    @Override
    public Integer getRxTimeStamp() {
      return ((ExtendedMessage)getBackendMessage()).getRxTimeStamp();
    }

    @Override
    public Byte getRssiMeasurementType() {
      return ((ExtendedMessage)getBackendMessage()).getRssiMeasurementType();
    }

    @Override
    public Byte getRssiThresholdConfig() {
      return ((ExtendedMessage)getBackendMessage()).getRssiThresholdConfig();
    }

    @Override
    public Byte getRssiValue() {
      return ((ExtendedMessage)getBackendMessage()).getRssiValue();
    }

    @Override
    public Integer getDeviceNumber() {
      return ((ExtendedMessage)getBackendMessage()).getDeviceNumber();
    }

    @Override
    public Byte getDeviceType() {
      return ((ExtendedMessage)getBackendMessage()).getDeviceType();
    }

    @Override
    public Byte getTransmissionType() {
      return ((ExtendedMessage)getBackendMessage()).getTransmissionType();
    }
    
    public void setChannelId(ChannelId id) {
    	((ExtendedMessage)getBackendMessage()).setChannelId(id);
    }
    
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
	public void setDeviceNumber(int deviceId) throws ValidationException {
		   ((ExtendedMessage)getBackendMessage()).setDeviceNumber(deviceId);
	}

	@Override
	public void setDeviceType(int deviceType) throws ValidationException {
	   ((ExtendedMessage)getBackendMessage()).setDeviceType(deviceType);
		
	}

	@Override
	public void setTransmissionType(int transmissionType)
			throws ValidationException {
	   ((ExtendedMessage)getBackendMessage()).setTransmissionType(transmissionType);
	}
	
	@Override
	public void setPairingFlag(boolean pair) {
		((ExtendedMessage)getBackendMessage()).setPairingFlag(pair);
		
	}

	@Override
	public Boolean isPairingFlagSet() {
		return ((ExtendedMessage)getBackendMessage()).isPairingFlagSet();
	}
     
}
