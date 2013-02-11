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
package org.cowboycoders.cyclisimo;


import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.test.AndroidTestCase;

import java.util.List;

import org.cowboycoders.cyclisimo.BootReceiver;
import org.cowboycoders.cyclisimo.services.TrackRecordingService;

/**
 * Tests for the BootReceiver.
 * 
 * @author Youtao Liu
 */
public class BootReceiverTest extends AndroidTestCase {
  private static final String SERVICE_NAME = "org.cowboycoders.cyclisimo.services.TrackRecordingService";

  /**
   * Tests the behavior when receive notification which is the phone boot.
   */
  public void testOnReceive_startService() {
    // Make sure no TrackRecordingService
    Intent stopIntent = new Intent(getContext(), TrackRecordingService.class);
    getContext().stopService(stopIntent);
    assertFalse(isServiceExisted(getContext(), SERVICE_NAME));

    BootReceiver bootReceiver = new BootReceiver();
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_BOOT_COMPLETED);
    bootReceiver.onReceive(getContext(), intent);
    // Check if the service is started
    assertTrue(isServiceExisted(getContext(), SERVICE_NAME));
  }

  /**
   * Tests the behavior when receive notification which is not the phone boot.
   */
  public void testOnReceive_noStartService() {
    // Make sure no TrackRecordingService
    Intent stopIntent = new Intent(getContext(), TrackRecordingService.class);
    getContext().stopService(stopIntent);
    assertFalse(isServiceExisted(getContext(), SERVICE_NAME));

    BootReceiver bootReceiver = new BootReceiver();
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_BUG_REPORT);
    bootReceiver.onReceive(getContext(), intent);
    // Check if the service is not started
    assertFalse(isServiceExisted(getContext(), SERVICE_NAME));
  }

  /**
   * Checks if a service is started in a context.
   * 
   * @param context the context for checking a service
   * @param serviceName the service name to find if existed
   */
  private boolean isServiceExisted(Context context, String serviceName) {
    ActivityManager activityManager = (ActivityManager) context
        .getSystemService(Context.ACTIVITY_SERVICE);
    List<ActivityManager.RunningServiceInfo> serviceList = activityManager
        .getRunningServices(Integer.MAX_VALUE);
    for (int i = 0; i < serviceList.size(); i++) {
      RunningServiceInfo serviceInfo = serviceList.get(i);
      ComponentName componentName = serviceInfo.service;
      if (componentName.getClassName().equals(serviceName)) { 
        return true; 
      }
    }
    return false;
  }
}