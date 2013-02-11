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
 * Copyright 2012 Google Inc.
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


import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

import org.cowboycoders.cyclisimo.Constants;
import org.cowboycoders.cyclisimo.services.sensors.BluetoothConnectionManager;

/**
 * API level 10 specific implementation of the {@link ApiAdapter}.
 *
 * @author Jimmy Shih
 */
@TargetApi(10)
public class Api10Adapter extends Api9Adapter {

  @Override
  public BluetoothSocket getBluetoothSocket(BluetoothDevice bluetoothDevice) throws IOException {
    try {
      return bluetoothDevice.createInsecureRfcommSocketToServiceRecord(
          BluetoothConnectionManager.MY_TRACKS_UUID);
    } catch (IOException e) {
      Log.d(Constants.TAG, "Unable to create insecure connection", e);
    }
    return bluetoothDevice.createRfcommSocketToServiceRecord(BluetoothConnectionManager.MY_TRACKS_UUID);
  };
}
