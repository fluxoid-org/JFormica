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

package org.cowboycoders.cyclisimo.services.tasks;

import org.cowboycoders.cyclisimo.stats.TripStatistics;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.cowboycoders.cyclisimo.services.TrackRecordingService;

/**
 * This class will periodically perform a task.
 * 
 * @author Sandor Dornbush
 */
public class TimerTaskExecutor {

  private final PeriodicTask periodicTask;
  private final TrackRecordingService trackRecordingService;

  /**
   * A timer to schedule the announcements. This is non-null if the task is in
   * started (scheduled) state.
   */
  private Timer timer;

  public TimerTaskExecutor(PeriodicTask periodicTask, TrackRecordingService trackRecordingService) {
    this.periodicTask = periodicTask;
    this.trackRecordingService = trackRecordingService;
  }

  /**
   * Schedules the periodic task at an interval.
   * 
   * @param interval the interval in milliseconds
   */
  public void scheduleTask(long interval) {
    if (!trackRecordingService.isRecording() || trackRecordingService.isPaused()) {
      return;
    }

    TripStatistics tripStatistics = trackRecordingService.getTripStatistics();
    if (tripStatistics == null) {
      return;
    }

    if (timer != null) {
      timer.cancel();
      timer.purge();
    } else {
      // First start, or we were previously shut down.
      periodicTask.start();
    }

    timer = new Timer();
    if (interval <= 0) {
      return;
    }

    long next = System.currentTimeMillis() + interval - (tripStatistics.getTotalTime() % interval);
    timer.scheduleAtFixedRate(new PeriodicTimerTask(), new Date(next), interval);
  }

  /**
   * Shuts down.
   */
  public void shutdown() {
    if (timer != null) {
      timer.cancel();
      timer.purge();
      timer = null;
      periodicTask.shutdown();
    }
  }

  /**
   * The timer task to announce the trip status.
   */
  private class PeriodicTimerTask extends TimerTask {

    @Override
    public void run() {
      periodicTask.run(trackRecordingService);
    }
  }
}
