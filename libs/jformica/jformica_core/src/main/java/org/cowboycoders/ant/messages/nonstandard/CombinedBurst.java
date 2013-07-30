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
package org.cowboycoders.ant.messages.nonstandard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.cowboycoders.ant.ChannelId;
import org.cowboycoders.ant.messages.DeviceInfoQueryable;
import org.cowboycoders.ant.messages.RssiInfoQueryable;
import org.cowboycoders.ant.messages.TimestampInfoQueryable;
import org.cowboycoders.ant.messages.data.BurstDataMessage;
import org.cowboycoders.ant.utils.BurstMessageSequenceGenerator;
import org.cowboycoders.ant.utils.ByteUtils;

/**
 * Combines individual BurstDataMessage to produce a single array of data
 * @author will
 *
 */
public class CombinedBurst implements DeviceInfoQueryable, RssiInfoQueryable, TimestampInfoQueryable {
	
	public static Logger LOGGER = Logger.getLogger(CombinedBurst.class.getSimpleName());
	
	/**
	 * Diagnostic flags
	 * @author will
	 *
	 */
	public enum StatusFlag {
		ERROR_SEQUENCE_INVALID,
		ERROR_TIMEOUT,
	}
	
	private final Byte [] data;
	
	private final List<StatusFlag> statusFlags = new ArrayList<StatusFlag>();

	private final boolean complete;
	
	private DeviceInfoQueryable deviceInfo;
	
	private RssiInfoQueryable rssiInfo;
	
	private TimestampInfoQueryable timestampInfo;

	private CombinedBurst(Byte [] data, boolean complete, 
			DeviceInfoQueryable deviceInfo,
			RssiInfoQueryable rssiInfo,
			TimestampInfoQueryable timestampInfo,
			StatusFlag ...statusFlags) {
		this.data = data;
		this.complete = complete;
		this.deviceInfo = deviceInfo;
		this.rssiInfo = rssiInfo;
		this.timestampInfo = timestampInfo;
		for (StatusFlag flag : statusFlags){
			this.statusFlags.add(flag);
		}
	}
	
	/**
	 * Get the partial/ complete burst data packet depending on {@link Combinded#isComplete()}
	 * @return
	 */
	public Byte[] getData() {
		return data;
	}
	
	/**
	 * Convert all bytes to integer representation. Assumes bytes are unsigned.
	 * @return data
	 */
	public int [] getUnsignedData() {
		return ByteUtils.unsignedBytesToInts(getData());
	}
	
	/**
	 * Status/Error flags 
	 * @return list of diagnostic flags
	 */
	public List<StatusFlag> getStatusFlags() {
		return statusFlags;
	}
	
	/**
	 * Status of combination
	 * @return true if combination complete, false otherwise/
	 */
	public boolean isComplete() {
		return complete;
	}
	
	@Override
	public Integer getRxTimeStamp() {
		if (timestampInfo == null) {
			return null;
		}
		return timestampInfo.getRxTimeStamp();
	}

	@Override
	public Byte getRssiMeasurementType() {
		if (rssiInfo == null) {
			return null;
		}
		return rssiInfo.getRssiMeasurementType();
	}

	@Override
	public Byte getRssiThresholdConfig() {
		if (rssiInfo == null) {
			return null;
		}
		return rssiInfo.getRssiThresholdConfig();
	}

	@Override
	public Byte getRssiValue() {
		if (rssiInfo == null) {
			return null;
		}
		return rssiInfo.getRssiValue();
	}

	@Override
	public Integer getDeviceNumber() {
		if (deviceInfo == null) {
			return null;
		}
		return deviceInfo.getDeviceNumber();
	}

	@Override
	public Byte getDeviceType() {
		if (deviceInfo == null) {
			return null;
		}
		return deviceInfo.getDeviceType();
	}

	@Override
	public Byte getTransmissionType() {
		if (deviceInfo == null) {
			return null;
		}
		return deviceInfo.getTransmissionType();
	}
	
	/**
	 * Indicates whether or not extended information is available
	 * @return true if available
	 */
	public boolean isExtended() {
		return (deviceInfo != null || rssiInfo != null || timestampInfo != null);
	}
	
	public static class Builder {
		
		private List<Byte> combinedData;
		private List<StatusFlag> statusFlags;
		private BurstMessageSequenceGenerator sequenceGenerator;
		private int expectedSequenceNumber;
		private boolean complete;
		private boolean building;
		private DeviceInfoQueryable deviceInfo;
		private RssiInfoQueryable rssiInfo;
		private TimestampInfoQueryable timestampInfo;
		
		public Builder() {
			reset();
		}
		
		/**
		 * Combines the individual messages
		 * @return the finished combination or null if still being generated
		 */
		public CombinedBurst addMessage(BurstDataMessage message) {
			
			// case: finished
			if ((message.getSequenceNumber() & BurstMessageSequenceGenerator.FINISH_MASK) != 0) {
				expectedSequenceNumber = sequenceGenerator.finish();
				complete = true;
			}
			// case : sequence error
			else if (message.getSequenceNumber() != expectedSequenceNumber) {
				statusFlags.add(StatusFlag.ERROR_SEQUENCE_INVALID);
				return generateCombinedBurst();
			// case : not finished, but passes validation
			} else {
				expectedSequenceNumber = sequenceGenerator.next();
			}
			
			// we now consider the message under construction
			building = true;
			combinedData.addAll(Arrays.asList(message.getData()));
			
			if (message instanceof TimestampInfoQueryable) {
				timestampInfo = (TimestampInfoQueryable) message;
			}
			if (message instanceof RssiInfoQueryable) {
				rssiInfo = (RssiInfoQueryable) message;
			}
			if (message instanceof DeviceInfoQueryable) {
				deviceInfo = (DeviceInfoQueryable) message;
			}
			
			// only return upon completion
			if(!complete) return null;

			return generateCombinedBurst();
		}
		
		private CombinedBurst generateCombinedBurst() {
			CombinedBurst rtn = null;
			// if building return what we have, else null
			if (building) {
				rtn =  new CombinedBurst(
						combinedData.toArray(new Byte[0]),
						complete,
						deviceInfo,
						rssiInfo,
						timestampInfo,
						statusFlags.toArray(new StatusFlag[0])
						);
			}
			reset();
			return rtn;
		}
		
		/**
		 * Returns combination of what we have received so far and sets timeout status flag
		 * @return the combination
		 */
		public CombinedBurst timeout() {
			statusFlags.add(StatusFlag.ERROR_TIMEOUT);
			return generateCombinedBurst();
		}
		
		/**
		 * Discards all stored data
		 */
		public void reset() {
			combinedData = new ArrayList<Byte>();
			statusFlags = new ArrayList<StatusFlag>();
			sequenceGenerator = new BurstMessageSequenceGenerator();
			expectedSequenceNumber = sequenceGenerator.next();
			complete = false;
			building = false;
		}
		
		/**
		 * Returns whether or not a {@link CombinedBurst} is under construction
		 * @return
		 */
		public boolean isBuilding() {
			return building;
		}
	
		
		
	}

	@Override
	public ChannelId getChannelId() {
    	ChannelId id = ChannelId.Builder.newInstance()
        		.setDeviceNumber(getDeviceNumber())
        		.setDeviceType(getDeviceType())
        		.setTransmissonType(getTransmissionType())
        		.setPairingFlag(this.isPairingFlagSet())
        		.build();
    	return id;
	}

	@Override
	public Boolean isPairingFlagSet() {
		if (deviceInfo == null) {
			return null;
		}
		return deviceInfo.isPairingFlagSet();
	}



	
	
	

}
