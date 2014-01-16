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
package org.cowboycoders.ant.utils;

import java.util.ArrayList;
import java.util.List;

import javax.usb.UsbDevice;
import javax.usb.UsbHub;


public class UsbUtils {
  
  /**
   * This forms an inclusive list of all UsbDevices connected to this UsbDevice.
   * <p>
   * The list includes the provided device.  If the device is also a hub,
   * the list will include all devices connected to it, recursively.
   * @param usbDevice The UsbDevice to use.
   * @return An inclusive List of all connected UsbDevices.
   */
  public static List<UsbDevice> getAllUsbDevices(UsbDevice usbDevice)
  {
      List<UsbDevice> list = new java.util.ArrayList<UsbDevice>();

      list.add(usbDevice);

      /* this is just normal recursion.  Nothing special. */
      if (usbDevice.isUsbHub()) {
          @SuppressWarnings("unchecked")
          List<UsbDevice> devices = ((UsbHub)usbDevice).getAttachedUsbDevices();
          for (int i=0; i<devices.size(); i++)
              list.addAll(getAllUsbDevices((UsbDevice)devices.get(i)));
      }

      return list;
  }
  
  /**
   * Get a List of all devices that match the specified vendor and product id.
   * @param usbDevice The UsbDevice to check.
   * @param vendorId The vendor id to match.
   * @param productId The product id to match.
   * @param A List of any matching UsbDevice(s).
   */
  public static List<UsbDevice> getUsbDevicesWithId(UsbDevice usbDevice, short vendorId, short productId)
  {
    List<UsbDevice> list = new ArrayList<UsbDevice>();

      /* A device's descriptor is always available.  All descriptor
       * field names and types match exactly what is in the USB specification.
       * Note that Java does not have unsigned numbers, so if you are 
       * comparing 'magic' numbers to the fields, you need to handle it correctly.
       * For example if you were checking for Intel (vendor id 0x8086) devices,
       *   if (0x8086 == descriptor.idVendor())
       * will NOT work.  The 'magic' number 0x8086 is a positive integer, while
       * the _short_ vendor id 0x8086 is a negative number!  So you need to do either
       *   if ((short)0x8086 == descriptor.idVendor())
       * or
       *   if (0x8086 == UsbUtil.unsignedInt(descriptor.idVendor()))
       * or
       *   short intelVendorId = (short)0x8086;
       *   if (intelVendorId == descriptor.idVendor())
       * Note the last one, if you don't cast 0x8086 into a short,
       * the compiler will fail because there is a loss of precision;
       * you can't represent positive 0x8086 as a short; the max value
       * of a signed short is 0x7fff (see Short.MAX_VALUE).
       *
       * See javax.usb.util.UsbUtil.unsignedInt() for some more information.
       */
      if (vendorId == usbDevice.getUsbDeviceDescriptor().idVendor() &&
          productId == usbDevice.getUsbDeviceDescriptor().idProduct())
          list.add(usbDevice);

      /* this is just normal recursion.  Nothing special. */
      if (usbDevice.isUsbHub()) {
          @SuppressWarnings("unchecked")
          List<UsbDevice> devices = ((UsbHub)usbDevice).getAttachedUsbDevices();
          for (int i=0; i<devices.size(); i++)
              list.addAll(getUsbDevicesWithId((UsbDevice)devices.get(i), vendorId, productId));
      }

      return list;
  }

}
