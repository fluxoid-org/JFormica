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