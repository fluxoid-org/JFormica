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

package org.cowboycoders.cyclisimo.maps;

import static org.cowboycoders.cyclisimo.Constants.TAG;

import org.cowboycoders.cyclisimo.content.MyTracksProviderUtils;
import org.cowboycoders.cyclisimo.content.Track;
import org.cowboycoders.cyclisimo.stats.TripStatistics;
import org.cowboycoders.cyclisimo.R;
import com.google.common.annotations.VisibleForTesting;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;

import org.cowboycoders.cyclisimo.Constants;
import org.cowboycoders.cyclisimo.util.PreferencesUtils;
import org.cowboycoders.cyclisimo.util.UnitConversions;

/**
 * A dynamic speed path descriptor.
 * 
 * @author Vangelis S.
 */
public class DynamicSpeedTrackPathDescriptor implements TrackPathDescriptor {

  private final OnSharedPreferenceChangeListener
      sharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
          @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
          if (key == null || key.equals(PreferencesUtils.getKey(context, R.string.track_color_mode_percentage_key))) {
            speedMargin = PreferencesUtils.getInt(context, R.string.track_color_mode_percentage_key,
                PreferencesUtils.TRACK_COLOR_MODE_PERCENTAGE_DEFAULT);
          }
        }
      };

  private final Context context;
  private int speedMargin;
  private int slowSpeed;
  private int normalSpeed;
  private double averageMovingSpeed;

  @VisibleForTesting
  static final int CRITICAL_DIFFERENCE_PERCENTAGE = 20;

  public DynamicSpeedTrackPathDescriptor(Context context) {
    this.context = context;
    context.getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE)
        .registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    sharedPreferenceChangeListener.onSharedPreferenceChanged(null, null);
  }

  @Override
  public int getSlowSpeed() {
    slowSpeed = (int) (averageMovingSpeed - (averageMovingSpeed * speedMargin / 100.0));
    return slowSpeed;
  }

  @Override
  public int getNormalSpeed() {
    normalSpeed = (int) (averageMovingSpeed + (averageMovingSpeed * speedMargin / 100.0));
    return normalSpeed;
  }

  @Override
  public boolean updateState() {
    long selectedTrackId = PreferencesUtils.getLong(context, R.string.selected_track_id_key);
    if (selectedTrackId == PreferencesUtils.SELECTED_TRACK_ID_DEFAULT) {
      Log.d(TAG, "No selected track id.");
      return false;
    }
    Track track = MyTracksProviderUtils.Factory.get(context).getTrack(selectedTrackId);
    if (track == null) {
      Log.d(TAG, "No track for " + selectedTrackId);
      return false;
    }
    TripStatistics tripStatistics = track.getTripStatistics();
    double newAverageMovingSpeed = (int) Math.floor(
        tripStatistics.getAverageMovingSpeed() * UnitConversions.MS_TO_KMH);

    if (isDifferenceSignificant(averageMovingSpeed, newAverageMovingSpeed)) {
      averageMovingSpeed = newAverageMovingSpeed;
      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns true if the average moving speed and the new average moving speed
   * are significantly different.
   * 
   * @param oldAverageMovingSpeed
   * @param newAverageMovingSpeed
   */
  @VisibleForTesting
  boolean isDifferenceSignificant(double oldAverageMovingSpeed, double newAverageMovingSpeed) {
    if (oldAverageMovingSpeed == 0) {
      return newAverageMovingSpeed != 0;
    }
    
    // Here, both oldAverageMovingSpeed and newAverageMovingSpeed are not zero.
    double maxValue = Math.max(oldAverageMovingSpeed, newAverageMovingSpeed);
    double differencePercentage = Math.abs(oldAverageMovingSpeed - newAverageMovingSpeed) / maxValue
        * 100.0;
    return differencePercentage >= CRITICAL_DIFFERENCE_PERCENTAGE;
  }

  /**
   * Gets the speed margin.
   */
  @VisibleForTesting
  int getSpeedMargin() {
    return speedMargin;
  }

  /**
   * Gets the average moving speed.
   */
  @VisibleForTesting
  double getAverageMovingSpeed() {
    return averageMovingSpeed;
  }

  /**
   * Sets the average moving speed.
   * 
   * @param value the value
   */
  @VisibleForTesting
  void setAverageMovingSpeed(double value) {
    averageMovingSpeed = value;
  }
}