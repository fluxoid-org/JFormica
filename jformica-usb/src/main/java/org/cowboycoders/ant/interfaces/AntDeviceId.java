package org.cowboycoders.ant.interfaces;

/**
 * Supported devices for {@link AntTransceiver}
 * @author will
 *
 */
public enum AntDeviceId {
	
	ANTUSB_2(new DeviceDescriptor(0x0fcf,0x1008)),
	ANTUSB_M(new DeviceDescriptor(0x0fcf,0x1009)),
	;
	
	private DeviceDescriptor usbDescriptor;

	public DeviceDescriptor getUsbDescriptor() {
		return usbDescriptor;
	}
	
	AntDeviceId(DeviceDescriptor usbDescriptor) {
		if (usbDescriptor == null) throw new IllegalArgumentException("you must provide a usb descriptor");
		this.usbDescriptor = usbDescriptor;
	}
	
}