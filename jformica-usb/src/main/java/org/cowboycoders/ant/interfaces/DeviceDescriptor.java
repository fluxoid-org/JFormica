package org.cowboycoders.ant.interfaces;

public class DeviceDescriptor {
	
	private final short deviceId;
	private final short vendorId;
	
	private static void checkShort(int in) {
		if (in > Short.MAX_VALUE) {
			throw new IllegalArgumentException("id must be a short");
		}
	}

	public DeviceDescriptor(int vendorId,int deviceId) {
		checkShort(vendorId);
		checkShort(deviceId);
		this.deviceId = (short) deviceId;
		this.vendorId = (short) vendorId;
	}

	public short getDeviceId() {
		return deviceId;
	}

	public short getVendorId() {
		return vendorId;
	}
	
}