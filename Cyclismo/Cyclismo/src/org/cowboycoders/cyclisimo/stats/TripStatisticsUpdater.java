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

package org.cowboycoders.cyclisimo.stats;

import static org.cowboycoders.cyclisimo.Constants.TAG;

import com.google.common.annotations.VisibleForTesting;

import android.location.Location;
import android.util.Log;

import org.cowboycoders.cyclisimo.Constants;
import org.cowboycoders.cyclisimo.services.TrackRecordingService;
import org.cowboycoders.cyclisimo.util.LocationUtils;

/**
 * Updater for {@link TripStatistics}. For updating track trip statistics as new
 * locations are added. Note that some of the locations represent pause/resume
 * separator.
 * 
 * @author Sandor Dornbush
 * @author Rodrigo Damazio
 */
public class TripStatisticsUpdater {

  // The track's trip statistics
  private final TripStatistics tripStatistics;

  // The current segment's trip statistics
  private TripStatistics currentSegment;

  // Current segment's last location.
  private Location lastLocation;

  // Current segment's last moving location
  private Location lastMovingLocation;

  // A buffer of the recent speed readings (m/s) for calculating max speed
  private final DoubleBuffer speedBuffer = new DoubleBuffer(Constants.SPEED_SMOOTHING_FACTOR);

  // A buffer of the recent elevation readings (m)
  private final DoubleBuffer elevationBuffer = new DoubleBuffer(
      Constants.ELEVATION_SMOOTHING_FACTOR);

  // A buffer of the recent distance readings (m) for calculating grade
  private final DoubleBuffer distanceBuffer = new DoubleBuffer(Constants.DISTANCE_SMOOTHING_FACTOR);

  // A buffer of the recent grade calculations (%)
  private final DoubleBuffer gradeBuffer = new DoubleBuffer(Constants.GRADE_SMOOTHING_FACTOR);

  /**
   * Creates a new trip statistics updater.
   * 
   * @param startTime the start time
   */
  public TripStatisticsUpdater(long startTime) {
    tripStatistics = init(startTime);
    currentSegment = init(startTime);
  }

  public void updateTime(long time) {
    currentSegment.setStopTime(time);
    currentSegment.setTotalTime(time - currentSegment.getStartTime());
  }

  /**
   * Gets the track's trip statistics.
   */
  public TripStatistics getTripStatistics() {
    // Take a snapshot - we don't want anyone messing with our tripStatistics
    TripStatistics stats = new TripStatistics(tripStatistics);
    stats.merge(currentSegment);
    return stats;
  }

  /**
   * Adds a location. TODO: This assume location has a valid time.
   * 
   * @param location the location
   * @param minRecordingDistance the min recording distance
   */
  public void addLocation(Location location, int minRecordingDistance) {
    if (!LocationUtils.isValidLocation(location)) {
      updateTime(location.getTime());
      if (location.getLatitude() == TrackRecordingService.PAUSE_LATITUDE) {
        if (lastLocation != null && lastMovingLocation != null
            && lastLocation != lastMovingLocation) {
          currentSegment.addTotalDistance(lastMovingLocation.distanceTo(lastLocation));
        }
        tripStatistics.merge(currentSegment);
      }
      currentSegment = init(location.getTime());
      lastLocation = null;
      lastMovingLocation = null;
      speedBuffer.reset();
      elevationBuffer.reset();
      distanceBuffer.reset();
      gradeBuffer.reset();
      return;
    }
    double elevationDifference = updateElevation(location.getAltitude());
    currentSegment.updateLatitudeExtremities(location.getLatitude());
    currentSegment.updateLongitudeExtremities(location.getLongitude());

    if (lastLocation == null || lastMovingLocation == null) {
      updateTime(location.getTime());
      lastLocation = location;
      lastMovingLocation = location;
      return;
    }
    double movingDistance = lastMovingLocation.distanceTo(location);
    if (movingDistance < minRecordingDistance - Constants.RECORDING_DISTANCE_ACCURACY 
        && location.getSpeed() < Constants.MAX_NO_MOVEMENT_SPEED) { //TODO: Make this into a preference
      updateTime(location.getTime());
      lastLocation = location;
      return;
    }
    long movingTime = location.getTime() - lastLocation.getTime();
    if (movingTime < 0) {
      updateTime(location.getTime());
      lastLocation = location;
      return;
    }
    currentSegment.addTotalDistance(movingDistance);
    currentSegment.addMovingTime(movingTime);
    updateSpeed(
        location.getTime(), location.getSpeed(), lastLocation.getTime(), lastLocation.getSpeed());
    updateGrade(lastLocation.distanceTo(location), elevationDifference);
    updateTime(location.getTime());
    lastLocation = location;
    lastMovingLocation = location;
  }

  /**
   * Gets the smoothed elevation over several readings. The elevation readings
   * is noisy so the smoothed elevation is better than the raw elevation for
   * many tasks.
   */
  public double getSmoothedElevation() {
    return elevationBuffer.getAverage();
  }

  public double getSmoothedSpeed() {
    return speedBuffer.getAverage();
  }
  
  public double getSpeed() {
    if (lastLocation == null) return 0.;
    return lastLocation.getSpeed();
  }

  /**
   * Updates a speed reading. Assumes the user is moving.
   * 
   * @param time the time
   * @param speed the speed
   * @param lastLocationTime the last location time
   * @param lastLocationSpeed the last location speed
   */
  @VisibleForTesting
  void updateSpeed(long time, double speed, long lastLocationTime, double lastLocationSpeed) {
    if (!isValidSpeed(time, speed, lastLocationTime, lastLocationSpeed)) {
      Log.d(TAG, "Invalid speed. speed: " + speed + " lastLocationSpeed: " + lastLocationSpeed);
      return;
    }
    speedBuffer.setNext(speed);
    if (speed > currentSegment.getMaxSpeed()) {
      currentSegment.setMaxSpeed(speed);
    }
  }

  /**
   * Updates an elevation reading.
   * 
   * @param elevation the elevation
   */
  @VisibleForTesting
  double updateElevation(double elevation) {
    double oldAverage = elevationBuffer.getAverage();
    elevationBuffer.setNext(elevation);
    double newAverage = elevationBuffer.getAverage();
    currentSegment.updateElevationExtremities(newAverage);
    double elevationDifference = elevationBuffer.isFull() ? newAverage - oldAverage : 0.0;
    if (elevationDifference > 0) {
      currentSegment.addTotalElevationGain(elevationDifference);
    }
    return elevationDifference;
  }

  /**
   * Updates a grade reading.
   * 
   * @param distance the distance the user just traveled
   * @param elevationDifference the elevation difference between the current
   *          reading and the previous reading
   */
  @VisibleForTesting
  void updateGrade(double distance, double elevationDifference) {
    distanceBuffer.setNext(distance);
    double smoothedDistance = distanceBuffer.getAverage();

    /*
     * With the error in the altitude measurement it is dangerous to divide by
     * anything less than 5.
     */
    if (!elevationBuffer.isFull() || !distanceBuffer.isFull() || smoothedDistance < 5.0) {
      return;
    }
    gradeBuffer.setNext(elevationDifference / smoothedDistance);
    currentSegment.updateGradeExtremities(gradeBuffer.getAverage());
  }

  private TripStatistics init(long time) {
    TripStatistics stats = new TripStatistics();
    stats.setStartTime(time);
    stats.setStopTime(time);
    return stats;
  }

  /**
   * Returns true if the speed is valid.
   * 
   * @param time the time
   * @param speed the speed
   * @param lastLocationTime the last location time
   * @param lastLocationSpeed the last location speed
   */
  private boolean isValidSpeed(
      long time, double speed, long lastLocationTime, double lastLocationSpeed) {

    /*
     * There are a lot of noisy speed readings. Do the cheapest checks first,
     * most expensive last.
     */
    if (speed < 0) {
      return false;
    }

    /*
     * The following code will ignore unlikely readings. 128 m/s seems to be an
     * internal android error code.
     */
    if (Math.abs(speed - 128) < 1) {
      return false;
    }

    /*
     * See if the speed seems physically likely. Ignore any speeds that imply
     * acceleration greater than 2g.
     */
    long timeDifference = time - lastLocationTime;
    double speedDifference = Math.abs(lastLocationSpeed - speed);
    if (speedDifference > Constants.MAX_ACCELERATION * timeDifference) {
      return false;
    }

    /*
     * Only check if the speed buffer is full. Check that the speed is less than
     * 10X the smoothed average and the speed difference doesn't imply 2g
     * acceleration.
     */
    if (!speedBuffer.isFull()) {
      return true;
    }
    double average = speedBuffer.getAverage();
    double diff = Math.abs(average - speed);
    return (speed < average * 10) && (diff < Constants.MAX_ACCELERATION * timeDifference);
  }
}
