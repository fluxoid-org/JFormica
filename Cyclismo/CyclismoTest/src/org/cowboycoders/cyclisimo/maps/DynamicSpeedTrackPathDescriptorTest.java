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
import org.cowboycoders.cyclisimo.util.PreferencesUtils;

/**
 * Tests for the {@link DynamicSpeedTrackPathDescriptor}.
 * 
 * @author Youtao Liu
 */
public class DynamicSpeedTrackPathDescriptorTest extends AndroidTestCase {

  private Context context;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    context = getContext();
  }

  /**
   * Tests the method {@link DynamicSpeedTrackPathDescriptor#getSpeedMargin()}
   * with zero, normal and illegal value.
   */
  public void testGetSpeedMargin() {
    // The default value of speedMargin is 25.
    int[] expectations = { 0, 50, 99, 25 };
    // Test
    for (int i = 0; i < expectations.length; i++) {
      PreferencesUtils.setInt(context, R.string.track_color_mode_percentage_key, expectations[i]);
      DynamicSpeedTrackPathDescriptor dynamicSpeedTrackPathDescriptor = new DynamicSpeedTrackPathDescriptor(
          context);
      assertEquals(expectations[i], dynamicSpeedTrackPathDescriptor.getSpeedMargin());
    }
  }

  /**
   * Tests {@link DynamicSpeedTrackPathDescriptor#updateState()} by wrong track
   * id.
   */
  public void testNeedsRedraw_WrongTrackId() {
    PreferencesUtils.setLong(
        context, R.string.selected_track_id_key, PreferencesUtils.SELECTED_TRACK_ID_DEFAULT);
    DynamicSpeedTrackPathDescriptor dynamicSpeedTrackPathDescriptor = new DynamicSpeedTrackPathDescriptor(
        context);
    assertEquals(false, dynamicSpeedTrackPathDescriptor.updateState());
  }

  /**
   * Tests {@link DynamicSpeedTrackPathDescriptor#updateState()} by different
   * averageMovingSpeed.
   */
  public void testIsDiffereceSignificant() {
    DynamicSpeedTrackPathDescriptor dynamicSpeedTrackPathDescriptor = new DynamicSpeedTrackPathDescriptor(
        context);
    double[] averageMovingSpeeds = { 0, 30, 30, 30 };
    double[] newAverageMovingSpeed = { 20, 30,
    // Difference is less than CRITICAL_DIFFERENCE_PERCENTAGE
        30 * (1 + (DynamicSpeedTrackPathDescriptor.CRITICAL_DIFFERENCE_PERCENTAGE / 100) / 2),
    // Difference is more than CRITICAL_DIFFERENCE_PERCENTAGE
        30 * (1 + (DynamicSpeedTrackPathDescriptor.CRITICAL_DIFFERENCE_PERCENTAGE / 100.00) * 2) };
    boolean[] expectedValues = { true, false, false, true };
    double[] expectedAverageMovingSpeed = { 0, 30, 30, 30 };
    // Test
    for (int i = 0; i < newAverageMovingSpeed.length; i++) {
      dynamicSpeedTrackPathDescriptor.setAverageMovingSpeed(averageMovingSpeeds[i]);
      assertEquals(expectedValues[i], dynamicSpeedTrackPathDescriptor.isDifferenceSignificant(
          averageMovingSpeeds[i], newAverageMovingSpeed[i]));
      assertEquals(
          expectedAverageMovingSpeed[i], dynamicSpeedTrackPathDescriptor.getAverageMovingSpeed());
    }
  }
}