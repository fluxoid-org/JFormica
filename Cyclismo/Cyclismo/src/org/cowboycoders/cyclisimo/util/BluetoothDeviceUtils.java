/*
*    Copyright (c) 2013, Will Szumski
*    Copyright (c) 2013, Doug Szumski
*
*    This file is part of Cyclismo.
*
*    Cyclismo is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Cyclismo is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Cyclismo.  If not, see <http://www.gnu.org/licenses/>.
*/
/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cowboycoders.cyclisimo.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

import java.util.List;
import java.util.Set;

/**
 * Utilities for dealing with bluetooth devices.
 * 
 * @author Rodrigo Damazio
 */
public class BluetoothDeviceUtils {

  private BluetoothDeviceUtils() {}

  /**
   * Populates the device names and the device addresses with all the suitable
   * bluetooth devices.
   * 
   * @param bluetoothAdapter the bluetooth adapter
   * @param deviceNames list of device names
   * @param deviceAddresses list of device addresses
   */
  public static void populateDeviceLists(
      BluetoothAdapter bluetoothAdapter, List<String> deviceNames, List<String> deviceAddresses) {
    // Ensure the bluetooth adapter is not in discovery mode.
    bluetoothAdapter.cancelDiscovery();

    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
    for (BluetoothDevice device : pairedDevices) {
      BluetoothClass bluetoothClass = device.getBluetoothClass();
      if (bluetoothClass != null) {
        // Not really sure what we want, but I know what we don't want.
        switch (bluetoothClass.getMajorDeviceClass()) {
          case BluetoothClass.Device.Major.COMPUTER:
          case BluetoothClass.Device.Major.PHONE:
            break;
          default:
            deviceAddresses.add(device.getAddress());
            deviceNames.add(device.getName());
        }
      }
    }
  }
}
