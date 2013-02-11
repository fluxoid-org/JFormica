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
package org.cowboycoders.cyclisimo.services.tasks;

import org.cowboycoders.cyclisimo.stats.TripStatistics;

import android.util.Log;

import org.cowboycoders.cyclisimo.services.TrackRecordingService;
import org.cowboycoders.cyclisimo.util.PreferencesUtils;
import org.cowboycoders.cyclisimo.util.UnitConversions;

/**
 * Execute a periodic task on a time or distance schedule.
 * 
 * @author Sandor Dornbush
 */
public class PeriodicTaskExecutor {

  private static final String TAG = PeriodicTaskExecutor.class.getSimpleName();
  private static final long MINUTE_TO_MILLISECONDS = 60000L;

  private final TrackRecordingService trackRecordingService;
  private final PeriodicTaskFactory periodicTaskFactory;

  /**
   * The task frequency. A positive value is a time frequency (minutes). A
   * negative value is a distance frequency (km or mi). A zero value is to turn
   * off periodic task.
   */
  private int taskFrequency = PreferencesUtils.FREQUENCY_OFF;

  private PeriodicTask periodicTask;

  // Time periodic task executor
  private TimerTaskExecutor timerTaskExecutor = null;

  private boolean metricUnits;

  // The next distance for the distance periodic task
  private double nextTaskDistance = Double.MAX_VALUE;

  public PeriodicTaskExecutor(
      TrackRecordingService trackRecordingService, PeriodicTaskFactory periodicTaskFactory) {
    this.trackRecordingService = trackRecordingService;
    this.periodicTaskFactory = periodicTaskFactory;
  }

  /**
   * Restores the executor.
   */
  public void restore() {
    if (!trackRecordingService.isRecording() || trackRecordingService.isPaused()) {
      Log.d(TAG, "Not recording or paused.");
      return;
    }

    if (!isTimeFrequency()) {
      if (timerTaskExecutor != null) {
        timerTaskExecutor.shutdown();
        timerTaskExecutor = null;
      }
    }
    if (taskFrequency == PreferencesUtils.FREQUENCY_OFF) {
      Log.d(TAG, "Task frequency is off.");
      return;
    }

    periodicTask = periodicTaskFactory.create(trackRecordingService);

    // Returning null is ok
    if (periodicTask == null) {
      Log.d(TAG, "Peridoic task is null.");
      return;
    }
    periodicTask.start();

    if (isTimeFrequency()) {
      if (timerTaskExecutor == null) {
        timerTaskExecutor = new TimerTaskExecutor(periodicTask, trackRecordingService);
      }
      timerTaskExecutor.scheduleTask(taskFrequency * MINUTE_TO_MILLISECONDS);
    } else {
      // For distance periodic task
      calculateNextTaskDistance();
    }
  }

  /**
   * Shuts down the executor.
   */
  public void shutdown() {
    if (periodicTask != null) {
      periodicTask.shutdown();
      periodicTask = null;
    }
    if (timerTaskExecutor != null) {
      timerTaskExecutor.shutdown();
      timerTaskExecutor = null;
    }
  }

  /**
   * Updates the executor.
   */
  public void update() {
    if (!isDistanceFrequency() || periodicTask == null) {
      return;
    }
    TripStatistics tripStatistics = trackRecordingService.getTripStatistics();
    if (tripStatistics == null) {
      return;
    }
    double distance = tripStatistics.getTotalDistance()
        * UnitConversions.M_TO_KM;
    if (!metricUnits) {
      distance *= UnitConversions.KM_TO_MI;
    }

    if (distance > nextTaskDistance) {
      periodicTask.run(trackRecordingService);
      calculateNextTaskDistance();
    }
  }

  /**
   * Sets task frequency.
   * 
   * @param taskFrequency the task frequency
   */
  public void setTaskFrequency(int taskFrequency) {
    this.taskFrequency = taskFrequency;
    restore();
  }

  /**
   * Sets metricUnits.
   * 
   * @param metricUnits true to use metric units
   */
  public void setMetricUnits(boolean metricUnits) {
    this.metricUnits = metricUnits;
    calculateNextTaskDistance();
  }

  /**
   * Calculates the next distance for the distance periodic task.
   */
  private void calculateNextTaskDistance() {
    if (!trackRecordingService.isRecording() || trackRecordingService.isPaused()
        || periodicTask == null) {
      return;
    }

    TripStatistics tripStatistics = trackRecordingService.getTripStatistics();
    if (tripStatistics == null) {
      return;
    }
    
    if (!isDistanceFrequency()) {
      nextTaskDistance = Double.MAX_VALUE;
      Log.d(TAG, "SplitManager: Distance splits disabled.");
      return;
    }

    double distance = tripStatistics.getTotalDistance()
        * UnitConversions.M_TO_KM;
    if (!metricUnits) {
      distance *= UnitConversions.KM_TO_MI;
    }
    // The index will be negative since the frequency is negative.
    int index = (int) (distance / taskFrequency);
    index -= 1;
    nextTaskDistance = taskFrequency * index;
  }

  /**
   * True if time frequency.
   */
  private boolean isTimeFrequency() {
    return taskFrequency > 0;
  }

  /**
   * True if distance frequency.
   */
  private boolean isDistanceFrequency() {
    return taskFrequency < 0;
  }
}
