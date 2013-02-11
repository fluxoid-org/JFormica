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
package org.cowboycoders.cyclisimo.maps;

import org.cowboycoders.cyclisimo.R;

import android.content.Context;
import android.test.AndroidTestCase;

import org.cowboycoders.cyclisimo.maps.DynamicSpeedTrackPathDescriptor;
import org.cowboycoders.cyclisimo.maps.FixedSpeedTrackPathDescriptor;
import org.cowboycoders.cyclisimo.util.PreferencesUtils;

/**
 * Tests for the {@link DynamicSpeedTrackPathDescriptor}.
 * 
 * @author Youtao Liu
 */
public class FixedSpeedTrackPathDescriptorTest extends AndroidTestCase {

  private Context context;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    context = getContext();
  }

  /**
   * Tests the initialization of slowSpeed and normalSpeed in {@link DynamicSpeedTrackPathDescriptor#DynamicSpeedTrackPathDescriptor(Context)}
   * .
   */
  public void testConstructor() {
    int[] slowSpeedExpectations = { 0, 1, 99, PreferencesUtils.TRACK_COLOR_MODE_SLOW_DEFAULT };
    int[] normalSpeedExpectations = { 0, 1, 99, PreferencesUtils.TRACK_COLOR_MODE_MEDIUM_DEFAULT };
    for (int i = 0; i < slowSpeedExpectations.length; i++) {
      PreferencesUtils.setInt(
          context, R.string.track_color_mode_slow_key, slowSpeedExpectations[i]);
      PreferencesUtils.setInt(
          context, R.string.track_color_mode_medium_key, normalSpeedExpectations[i]);
      FixedSpeedTrackPathDescriptor fixedSpeedTrackPathDescriptor = new FixedSpeedTrackPathDescriptor(
          context);
      assertEquals(slowSpeedExpectations[i], fixedSpeedTrackPathDescriptor.getSlowSpeed());
      assertEquals(normalSpeedExpectations[i], fixedSpeedTrackPathDescriptor.getNormalSpeed());
    }
  }

  /**
   * Tests {@link FixedSpeedTrackPathDescriptor#getSlowSpeed()} and
   * {@link FixedSpeedTrackPathDescriptor#getNormalSpeed()}.
   */
  public void testGetSpeed() {
    FixedSpeedTrackPathDescriptor fixedSpeedTrackPathDescriptor = new FixedSpeedTrackPathDescriptor(
        context);
    int slowSpeed = fixedSpeedTrackPathDescriptor.getSlowSpeed();
    int normalSpeed = fixedSpeedTrackPathDescriptor.getNormalSpeed();
    // Change value in shared preferences
    PreferencesUtils.setInt(context, R.string.track_color_mode_slow_key, slowSpeed + 2);
    PreferencesUtils.setInt(context, R.string.track_color_mode_medium_key, normalSpeed + 2);
    assertEquals(slowSpeed, fixedSpeedTrackPathDescriptor.getSlowSpeed());
    assertEquals(normalSpeed, fixedSpeedTrackPathDescriptor.getNormalSpeed());
  }
}