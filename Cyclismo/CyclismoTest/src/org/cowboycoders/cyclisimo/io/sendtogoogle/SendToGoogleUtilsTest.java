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
package org.cowboycoders.cyclisimo.io.sendtogoogle;

/**
 * Tests the {@link SendToGoogleUtils}.
 * 
 * @author Youtao Liu
 */
import org.cowboycoders.cyclisimo.content.MyTracksLocation;
import org.cowboycoders.cyclisimo.content.Track;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

import org.cowboycoders.cyclisimo.TrackStubUtils;
import org.cowboycoders.cyclisimo.io.sendtogoogle.SendToGoogleUtils;

import junit.framework.TestCase;

public class SendToGoogleUtilsTest extends TestCase {

  private final static long TRACK_ID = 1L;
  private final static String TRACK_CATEGORY = "trackCategory";
  private final static String TRACK_NAME = "trackName";
  private final static double DIFFERENCE = 0.02;
  private final static double INVALID_LATITUDE = 91;

  /**
   * Tests the method
   * {@link SendToGoogleUtils#endSegment(Track, long, ArrayList)}
   * when there is only one location in a segment.
   */
  public void testPrepareTrackSegment_onlyOneLocation() {
    Track segment = TrackStubUtils.createTrack(1);
    assertFalse(SendToGoogleUtils.endSegment(segment, -1L, null));
  }

  /**
   * Tests the method
   * {@link SendToGoogleUtils#endSegment(Track, long, ArrayList)} when there
   * is no stop time.
   */
  public void testPrepareTrackSegment_noStopTime() {
    Track segment = TrackStubUtils.createTrack(2);
    assertEquals(-1L, segment.getTripStatistics().getStopTime());

    ArrayList<Track> tracksArray = new ArrayList<Track>();
    assertTrue(SendToGoogleUtils.endSegment(
        segment, segment.getLocations().get(1).getTime(), tracksArray));
    assertEquals(segment, tracksArray.get(0));
    // The stop time should be the time of last location
    assertEquals(
        segment.getLocations().get(1).getTime(), segment.getTripStatistics().getStopTime());
  }

  /**
   * Tests the method
   * {@link SendToGoogleUtils#prepareLocations(Track, java.util.List)} .
   */
  public void testPrepareLocations() {
    Track trackStub = TrackStubUtils.createTrack(2);
    trackStub.setId(TRACK_ID);
    trackStub.setName(TRACK_NAME);
    trackStub.setCategory(TRACK_CATEGORY);

    List<Location> locationsArray = new ArrayList<Location>();
    // Adds 100 location to List.
    for (int i = 0; i < 100; i++) {
      // Use this variable as a flag to make all points in the track can be kept
      // after run the LocationUtils#decimate(Track, double) with
      // Ramer–Douglas–Peucker algorithm.
      double latitude = TrackStubUtils.INITIAL_LATITUDE;
      if (i % 2 == 0) {
        latitude -= DIFFERENCE * (i % 10);
      }
      // Inserts 9 points which have wrong latitude, so would have 10 segments.
      if (i % 10 == 0 && i > 0) {
        MyTracksLocation location = TrackStubUtils.createMyTracksLocation();
        location.setLatitude(INVALID_LATITUDE);
        locationsArray.add(location);
      } else {
        locationsArray.add(TrackStubUtils.createMyTracksLocation(latitude,
            TrackStubUtils.INITIAL_LONGITUDE + DIFFERENCE * (i % 10),
            TrackStubUtils.INITIAL_ALTITUDE + DIFFERENCE * (i % 10)));
      }
    }
    ArrayList<Track> result = SendToGoogleUtils.prepareLocations(trackStub, locationsArray);
    assertEquals(10, result.size());
    // Checks all segments.
    for (int i = 0; i < 10; i++) {
      Track segmentOne = result.get(i);
      assertEquals(TRACK_ID, segmentOne.getId());
      assertEquals(TRACK_NAME, segmentOne.getName());
      assertEquals(TRACK_CATEGORY, segmentOne.getCategory());
    }
    // Checks all locations in the first segment.
    for (int j = 0; j < 9; j++) {
      double latitude = TrackStubUtils.INITIAL_LATITUDE;
      if (j % 2 == 0) {
        latitude -= DIFFERENCE * (j % 10);
      }
      Location oneLocation = result.get(0).getLocations().get(j);
      assertEquals(TrackStubUtils.createMyTracksLocation().getAltitude() + DIFFERENCE * (j % 10),
          oneLocation.getAltitude());
      assertEquals(latitude, oneLocation.getLatitude());
      assertEquals(TrackStubUtils.createMyTracksLocation().getLongitude() + DIFFERENCE * (j % 10),
          oneLocation.getLongitude());
    }
  }
}
