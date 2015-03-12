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
package org.cowboycoders.cyclisimo.endtoendtest;


import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;

import org.cowboycoders.cyclisimo.TrackListActivity;

/**
 * Tests sending a track to SD card.
 * 
 * @author Youtao Liu
 */
public class SaveToSdCardTest extends ActivityInstrumentationTestCase2<TrackListActivity> {

  private Instrumentation instrumentation;
  private TrackListActivity activityMyTracks;

  public SaveToSdCardTest() {
    super(TrackListActivity.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    instrumentation = getInstrumentation();
    activityMyTracks = getActivity();
    EndToEndTestUtils.setupForAllTest(instrumentation, activityMyTracks);
    EndToEndTestUtils.createTrackIfEmpty(1, false);
  }

  /**
   * Tests saving a track to SD card as a GPX file.
   */
  public void testSaveToSdCard_GPX() {
    EndToEndTestUtils.saveTrackToSdCard(EndToEndTestUtils.GPX);
    assertEquals(1, EndToEndTestUtils.getExportedFiles(EndToEndTestUtils.GPX).length);
  }

  /**
   * Tests saving a track to SD card as a KML file.
   */
  public void testSaveToSdCard_KML() {
    EndToEndTestUtils.saveTrackToSdCard(EndToEndTestUtils.KML);
    assertEquals(1, EndToEndTestUtils.getExportedFiles(EndToEndTestUtils.KML).length);
  }

  /**
   * Tests saving a track to SD card as a CSV file.
   */
  public void testSaveToSdCard_CSV() {
    EndToEndTestUtils.saveTrackToSdCard(EndToEndTestUtils.CSV);
    assertEquals(1, EndToEndTestUtils.getExportedFiles(EndToEndTestUtils.CSV).length);
  }

  /**
   * Tests saving a track to SD card as a TCX file.
   */
  public void testSaveToSdCard_TCX() {
    EndToEndTestUtils.saveTrackToSdCard(EndToEndTestUtils.TCX);
    assertEquals(1, EndToEndTestUtils.getExportedFiles(EndToEndTestUtils.TCX).length);
  }

  @Override
  protected void tearDown() throws Exception {
    EndToEndTestUtils.SOLO.finishOpenedActivities();
    super.tearDown();
  }

}
