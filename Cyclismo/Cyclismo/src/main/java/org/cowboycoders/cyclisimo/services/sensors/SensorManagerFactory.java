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
 * Copyright 2009 Google Inc.
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

package org.cowboycoders.cyclisimo.services.sensors;

import android.content.Context;

/**
 * A factory of {@link SensorManager}.
 *
 * @author Sandor Dornbush
 */
public class SensorManagerFactory {

  private static SensorManager systemSensorManager = null;
  private static SensorManager tempSensorManager = null;

  private SensorManagerFactory() {}

  /**
   * Gets the system sensor manager.
   *
   * @param context the context
   */
  public static SensorManager getSystemSensorManager(Context context) {
    releaseTempSensorManager();
    releaseSystemSensorManager();
    systemSensorManager = getSensorManager(context, true);
    if (systemSensorManager != null) {
      systemSensorManager.startSensor();
    }
    return systemSensorManager;
  }

  /**
   * Releases the system sensor manager.
   */
  public static void releaseSystemSensorManager() {
    if (systemSensorManager != null) {
      systemSensorManager.stopSensor();
    }
    systemSensorManager = null;
  }

  /**
   * Gets the temp sensor manager.
   *
   * @param context
   */
  public static SensorManager getTempSensorManager(Context context) {
    releaseTempSensorManager();
    if (systemSensorManager != null) {
      return null;
    }
    tempSensorManager = getSensorManager(context, false);
    if (tempSensorManager != null) {
      tempSensorManager.startSensor();
    }
    return tempSensorManager;
  }

  /**
   * Releases the temp sensor manager.
   */
  public static void releaseTempSensorManager() {
    if (tempSensorManager != null) {
      tempSensorManager.stopSensor();
    }
    tempSensorManager = null;
  }

  /**
   * Gets the sensor manager.
   *
   * @param context the context
   */
  private static SensorManager getSensorManager(Context context, boolean sendPageViews) {
//    String sensorType = PreferencesUtils.getString(
//        context, R.string.sensor_type_key, PreferencesUtils.SENSOR_TYPE_DEFAULT);
//
//    if (sensorType.equals(context.getString(R.string.sensor_type_value_ant))) {
//      if (sendPageViews) {
//        AnalyticsUtils.sendPageViews(context, "/sensor/ant");
//      }
//      //return new AntSensorManager(context);
//    } else if (sensorType.equals(context.getString(R.string.sensor_type_value_zephyr))) {
//      if (sendPageViews) {
//        AnalyticsUtils.sendPageViews(context, "/sensor/zephyr");
//      }
//      return new ZephyrSensorManager(context);
//    } else if (sensorType.equals(context.getString(R.string.sensor_type_value_polar))) {
//      if (sendPageViews) {
//        AnalyticsUtils.sendPageViews(context, "/sensor/polar");
//      }
//      return new PolarSensorManager(context);
//    }
//    return null;
    return new TurboSensorManager(context);
  }
}
