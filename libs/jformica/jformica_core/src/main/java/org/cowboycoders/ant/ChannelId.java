package org.cowboycoders.ant;

/**
 * Encapsulates an ant ChannelId. Immutable.
 * @author will
 *
 */
public class ChannelId {
	
	public static final int WILDCARD = 0;
	public static final int MAX_DEVICE_TYPE = 127;
	public static final int MAX_TRANSMISSION_TYPE = 255;
	public static final int MAX_DEVICE_NUMBER = 65535;
	
	private boolean pair;
	private int transmissonType;
	private int deviceType;
	private int deviceNumber;

	private ChannelId(int deviceNumber, int deviceType, int transmissionType,
			boolean pair) {
		this.deviceNumber = deviceNumber;
		this.deviceType = deviceType;
		this.transmissonType = transmissionType;
		this.pair = pair;
	}
	
	
	public boolean isPairingFlagSet() {
		return pair;
	}



	private void setPairingFlag(boolean pair) {
		this.pair = pair;
	}



	public int getTransmissonType() {
		return transmissonType;
	}



	private void setTransmissonType(int transmissonType) {
		this.transmissonType = transmissonType;
	}



	public int getDeviceType() {
		return deviceType;
	}



	private void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}



	public int getDeviceNumber() {
		return deviceNumber;
	}



	private void setDeviceNumber(int deviceNumber) {
		this.deviceNumber = deviceNumber;
	}
	
	private void cloneExisting(ChannelId model) {
		setDeviceNumber(model.getDeviceNumber());
		setDeviceType(model.getDeviceType());
		setTransmissonType(model.getTransmissonType());
		setPairingFlag(model.isPairingFlagSet());
	}


	/**
	 * Builds ChannelIds
	 * @author will
	 *
	 */
	public static class Builder {
		
		
		private final ChannelId channelId = new ChannelId(0,0,0,false);
		
		private Builder() {
			
		}
		
		protected ChannelId getInternalChannelId() {
			return channelId;
		}

		public static Builder newInstance() {
			return new Builder();
		}
		
		public static Builder newfromChannelId(ChannelId channelId) {
			Builder rtn = newInstance();
			rtn.channelId.cloneExisting(channelId);
			return rtn;
		}
		
		public static Builder newFromBuilder(Builder model) {
			Builder rtn = newInstance();
			rtn.channelId.cloneExisting(model.getInternalChannelId());
			return rtn;
		}
		

		public Builder setPairingFlag(boolean flag) {
			channelId.setPairingFlag(flag);
			return this;
		}
		
		public Builder setTransmissonType(int transmissonType) {
			channelId.transmissonType = transmissonType;
			return this;
		}
		
		public Builder setDeviceType(int deviceType) {
			channelId.deviceType = deviceType;
			return this;
		}
		
		public Builder setDeviceNumber(int deviceNumber) {
			channelId.deviceNumber = deviceNumber;
			return this;
		}
		
		public ChannelId build() {
			return new ChannelId(channelId.getDeviceNumber(),
					channelId.getDeviceType(), 
					channelId.getTransmissonType(), 
					channelId.isPairingFlagSet());
		}
		
		
		
	}
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + deviceNumber;
		result = prime * result + deviceType;
		result = prime * result + (pair ? 1231 : 1237);
		result = prime * result + transmissonType;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChannelId other = (ChannelId) obj;
		if (deviceNumber != other.deviceNumber)
			return false;
		if (deviceType != other.deviceType)
			return false;
		if (pair != other.pair)
			return false;
		if (transmissonType != other.transmissonType)
			return false;
		return true;
	}


	public static void main(String [] args) {
		ChannelId id = ChannelId.Builder.newInstance()
				.setDeviceNumber(1)
				.setDeviceType(2)
				.setTransmissonType(1)
				.setPairingFlag(false)
				.build();
		System.out.println(id.getDeviceType());
	}

}
