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

package org.cowboycoders.cyclisimo.util;

import org.cowboycoders.cyclisimo.R;

import android.test.AndroidTestCase;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.cowboycoders.cyclisimo.util.PreferencesUtils;
import org.cowboycoders.cyclisimo.util.StringUtils;
import org.cowboycoders.cyclisimo.util.TrackNameUtils;

/**
 * Tests {@link TrackNameUtils}.
 * 
 * @author Matthew Simmons
 */
public class TrackNameUtilsTest extends AndroidTestCase {

  private static final long TRACK_ID = 1L;
  private static final long START_TIME = 1288213406000L;

  /**
   * Tests when the track_name_key is
   * settings_recording_track_name_date_local_value.
   */
  public void testTrackName_date_local() {
    PreferencesUtils.setString(getContext(), R.string.track_name_key,
        getContext().getString(R.string.settings_recording_track_name_date_local_value));
    assertEquals(StringUtils.formatDateTime(getContext(), START_TIME),
        TrackNameUtils.getTrackName(getContext(), TRACK_ID, START_TIME, null));
  }

  /**
   * Tests when the track_name_key is
   * settings_recording_track_name_date_iso_8601_value.
   */
  public void testTrackName_date_iso_8601() {
    PreferencesUtils.setString(getContext(), R.string.track_name_key,
        getContext().getString(R.string.settings_recording_track_name_date_iso_8601_value));
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TrackNameUtils.ISO_8601_FORMAT);
    assertEquals(simpleDateFormat.format(new Date(START_TIME)),
        TrackNameUtils.getTrackName(getContext(), TRACK_ID, START_TIME, null));
  }

  /**
   * Tests when the track_name_key is
   * settings_recording_track_name_number_value.
   */
  public void testTrackName_number() {
    PreferencesUtils.setString(getContext(), R.string.track_name_key,
        getContext().getString(R.string.settings_recording_track_name_number_value));
    assertEquals(
        "Track " + TRACK_ID, TrackNameUtils.getTrackName(getContext(), TRACK_ID, START_TIME, null));
  }
}
