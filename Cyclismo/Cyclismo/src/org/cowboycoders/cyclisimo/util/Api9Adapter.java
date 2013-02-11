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

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import android.annotation.TargetApi;
import android.content.SharedPreferences.Editor;
import android.location.Geocoder;
import android.os.StrictMode;
import android.util.Log;

import java.util.Arrays;

import org.cowboycoders.cyclisimo.Constants;

/**
 * API level 9 specific implementation of the {@link ApiAdapter}.
 *
 * @author Rodrigo Damazio
 */
@TargetApi(9)
public class Api9Adapter extends Api8Adapter {
  
  @Override
  public void applyPreferenceChanges(Editor editor) {
    // Apply asynchronously
    editor.apply();
  }

  @Override
  public void enableStrictMode() {
    Log.d(Constants.TAG, "Enabling strict mode");
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build());
    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build());
  }

  @Override
  public byte[] copyByteArray(byte[] input, int start, int end) {
    return Arrays.copyOfRange(input, start, end);
  }

  @Override
  public HttpTransport getHttpTransport() {
    return new NetHttpTransport();
  }

  @Override
  public boolean isGeoCoderPresent() {
    return Geocoder.isPresent();
  }
}
