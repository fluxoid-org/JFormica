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

import org.cowboycoders.cyclisimo.content.MyTracksLocation;
import org.cowboycoders.cyclisimo.content.Sensor.SensorDataSet;
import org.cowboycoders.cyclisimo.content.Track;

import android.location.Location;

/**
 * Commons utilities for creating stubs of track, location. The class will be
 * enriched if needs more similar stubs for test.
 * 
 * @author Youtao Liu
 */
public class TrackStubUtils {

  static final String LOCATION_PROVIDER = "gps";
  public static final double INITIAL_LATITUDE = 22;
  public static final double INITIAL_LONGITUDE = 22;
  public static final double INITIAL_ALTITUDE = 22;
  static final float INITIAL_ACCURACY = 5;
  static final float INITIAL_SPEED = 10;
  public static final long INITIAL_TIME = 1000L;
  static final float INITIAL_BEARING = 3.0f;
  // Used to change the value of latitude, longitude, and altitude.
  static final double DIFFERENCE = 0.01;

  /**
   * Gets a a {@link Track} stub with specified number of locations.
   * 
   * @param numberOfLocations the number of locations for the track
   * @return a track stub.
   */
  public static Track createTrack(int numberOfLocations) {
    Track track = new Track();
    for (int i = 0; i < numberOfLocations; i++) {
      track.addLocation(createMyTracksLocation(INITIAL_LATITUDE + i * DIFFERENCE, INITIAL_LONGITUDE
          + i * DIFFERENCE, INITIAL_ALTITUDE + i * DIFFERENCE));
    }

    return track;
  }

  /**
   * Create a MyTracks location with default values.
   * 
   * @return a track stub.
   */
  public static MyTracksLocation createMyTracksLocation() {
    return createMyTracksLocation(INITIAL_LATITUDE, INITIAL_LONGITUDE, INITIAL_ALTITUDE);
  }

  /**
   * Creates a {@link MyTracksLocation} stub with specified values.
   * 
   * @return a MyTracksLocation stub.
   */
  public static MyTracksLocation createMyTracksLocation(double latitude, double longitude,
      double altitude) {
    // Initial Location
    Location loc = new Location(LOCATION_PROVIDER);
    loc.setLatitude(latitude);
    loc.setLongitude(longitude);
    loc.setAltitude(altitude);
    loc.setAccuracy(INITIAL_ACCURACY);
    loc.setSpeed(INITIAL_SPEED);
    loc.setTime(INITIAL_TIME);
    loc.setBearing(INITIAL_BEARING);
    SensorDataSet sd = SensorDataSet.newBuilder().build();
    MyTracksLocation myTracksLocation = new MyTracksLocation(loc, sd);

    return myTracksLocation;
  }

}
