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
 * Copyright 2011 Google Inc.
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


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import org.cowboycoders.cyclisimo.Constants;

/**
 * Utility class for acessing basic Android functionality.
 *
 * @author Rodrigo Damazio
 */
public class SystemUtils {

  /**
   * Get the My Tracks version from the manifest.
   *
   * @return the version, or an empty string in case of failure.
   */
  public static String getMyTracksVersion(Context context) {
    try {
      PackageInfo pi = context.getPackageManager().getPackageInfo(
          "org.cowboycoders.cyclisimo",
          PackageManager.GET_META_DATA);
      return pi.versionName;
    } catch (NameNotFoundException e)  {
      Log.w(Constants.TAG, "Failed to get version info.", e);
      return "";
    }
  }

  /**
   * Tries to acquire a partial wake lock if not already acquired. Logs errors
   * and gives up trying in case the wake lock cannot be acquired.
   */
  public static WakeLock acquireWakeLock(Activity activity, WakeLock wakeLock) {
    Log.i(Constants.TAG, "LocationUtils: Acquiring wake lock.");
    try {
      PowerManager pm = (PowerManager) activity
          .getSystemService(Context.POWER_SERVICE);
      if (pm == null) {
        Log.e(Constants.TAG, "LocationUtils: Power manager not found!");
        return wakeLock;
      }
      if (wakeLock == null) {
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
            Constants.TAG);
        if (wakeLock == null) {
          Log.e(Constants.TAG,
              "LocationUtils: Could not create wake lock (null).");
        }
        return wakeLock;
      }
      if (!wakeLock.isHeld()) {
        wakeLock.acquire();
        if (!wakeLock.isHeld()) {
          Log.e(Constants.TAG,
              "LocationUtils: Could not acquire wake lock.");
        }
      }
    } catch (RuntimeException e) {
      Log.e(Constants.TAG,
          "LocationUtils: Caught unexpected exception: " + e.getMessage(), e);
    }
    return wakeLock;
  }

  private SystemUtils() {}
}