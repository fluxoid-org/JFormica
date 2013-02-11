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

package org.cowboycoders.cyclisimo.content;

import static org.cowboycoders.cyclisimo.Constants.MAX_LOCATION_AGE_MS;

import org.cowboycoders.cyclisimo.R;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.cowboycoders.cyclisimo.Constants;
import org.cowboycoders.cyclisimo.services.MyTracksLocationManager;
import org.cowboycoders.cyclisimo.util.GoogleLocationUtils;

/**
 * Data source on the phone.
 * 
 * @author Rodrigo Damazio
 */
public class DataSource {

  private static final int NETWORK_PROVIDER_MIN_TIME = 5 * 60 * 1000; // 5 minutes
  private static final String TAG = DataSource.class.getSimpleName();

  private final Context context;
  private final ContentResolver contentResolver;
  private final MyTracksLocationManager myTracksLocationManager;
  private final SensorManager sensorManager;
  private final SharedPreferences sharedPreferences;
  
  public DataSource(Context context) {
    this.context = context;
    contentResolver = context.getContentResolver();
    myTracksLocationManager = new MyTracksLocationManager(context);
    sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    sharedPreferences = context.getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
  }

  public void close() {
    myTracksLocationManager.close();
  }

  public boolean isAllowed() {
    return myTracksLocationManager.isAllowed();
  }

  public boolean isGpsProviderEnabled() {
    return myTracksLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  }

  /**
   * Registers a content observer.
   * 
   * @param uri the uri
   * @param observer the observer
   */
  public void registerContentObserver(Uri uri, ContentObserver observer) {
    contentResolver.registerContentObserver(uri, false, observer);
  }

  /**
   * Unregisters a content observer.
   * 
   * @param observer the observer
   */
  public void unregisterContentObserver(ContentObserver observer) {
    contentResolver.unregisterContentObserver(observer);
  }

  /**
   * Registers a location listener.
   * 
   * @param listener the listener
   */
  public void registerLocationListener(LocationListener listener) {
    // Check if the GPS provider exists
    if (myTracksLocationManager.getProvider(LocationManager.GPS_PROVIDER) == null) {
      listener.onProviderDisabled(LocationManager.GPS_PROVIDER);
      unregisterLocationListener(listener);
      return;
    }

    // Listen for GPS location
    myTracksLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);

    // Update the listener with the current provider state
    if (myTracksLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      listener.onProviderEnabled(LocationManager.GPS_PROVIDER);
    } else {
      listener.onProviderDisabled(LocationManager.GPS_PROVIDER);
    }

    // Listen for network location
    try {
      myTracksLocationManager.requestLocationUpdates(
          LocationManager.NETWORK_PROVIDER, NETWORK_PROVIDER_MIN_TIME, 0, listener);
    } catch (RuntimeException e) {
      // Network location is optional, so just log the exception
      Log.w(TAG, "Could not register for network location.", e);
    }
  }

  /**
   * Unregisters a location listener.
   * 
   * @param listener the listener
   */
  public void unregisterLocationListener(LocationListener listener) {
    myTracksLocationManager.removeUpdates(listener);
  }

  /**
   * Gets the last known location.
   */
  public Location getLastKnownLocation() {
    Location location = myTracksLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    if (!isLocationRecent(location)) {
      // Try network location
      location = myTracksLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
      String toast;
      if (isLocationRecent(location)) {
        toast = context.getString(R.string.my_location_approximate_location);
      } else {
        String setting = context.getString(
            GoogleLocationUtils.isAvailable(context) ? R.string.gps_google_location_settings
                : R.string.gps_location_access);
        toast = context.getString(R.string.my_location_no_gps, setting);
      }
      Toast.makeText(context, toast, Toast.LENGTH_LONG).show();
    }
    return location;
  }

  /**
   * Returns true if the location is recent.
   * 
   * @param location the location
   */
  private boolean isLocationRecent(Location location) {
    if (location == null) {
      return false;
    }
    return location.getTime() > System.currentTimeMillis() - MAX_LOCATION_AGE_MS;
  }

  /**
   * Registers a heading listener.
   * 
   * @param listener the listener
   */
  public void registerHeadingListener(SensorEventListener listener) {
    Sensor heading = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    if (heading == null) {
      Log.d(TAG, "No heading sensor.");
      return;
    }
    sensorManager.registerListener(listener, heading, SensorManager.SENSOR_DELAY_UI);
  }

  /**
   * Unregisters a heading listener.
   * 
   * @param listener the listener
   */
  public void unregisterHeadingListener(SensorEventListener listener) {
    sensorManager.unregisterListener(listener);
  }

  /**
   * Registers a shared preference change listener.
   * 
   * @param listener the listener
   */
  public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
    sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
  }

  /**
   * Unregisters a shared preference change listener.
   * 
   * @param listener the listener
   */
  public void unregisterOnSharedPreferenceChangeListener(
      OnSharedPreferenceChangeListener listener) {
    sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
  }
}