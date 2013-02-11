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
package org.cowboycoders.cyclisimo.content;

import org.cowboycoders.cyclisimo.content.WaypointCreationRequest;
import org.cowboycoders.cyclisimo.content.WaypointCreationRequest.WaypointType;

import android.os.Parcel;
import android.test.AndroidTestCase;

/**
 * Tests for the WaypointCreationRequest class.
 * {@link WaypointCreationRequest}
 *
 * @author Sandor Dornbush
 */
public class WaypointCreationRequestTest extends AndroidTestCase {

  public void testTypeParceling() {
    WaypointCreationRequest original = WaypointCreationRequest.DEFAULT_WAYPOINT;
    Parcel p = Parcel.obtain();
    original.writeToParcel(p, 0);
    p.setDataPosition(0);
    WaypointCreationRequest copy = WaypointCreationRequest.CREATOR.createFromParcel(p);
    assertEquals(original.getType(), copy.getType());
    assertFalse(copy.isTrackStatistics());
    assertNull(copy.getName());
    assertNull(copy.getDescription());
    assertNull(copy.getIconUrl());
  }

  public void testAllAttributesParceling() {
    WaypointCreationRequest original = new WaypointCreationRequest(
        WaypointType.WAYPOINT, false, "name", "category", "description", "img.png");
    Parcel p = Parcel.obtain();
    original.writeToParcel(p, 0);
    p.setDataPosition(0);
    WaypointCreationRequest copy = WaypointCreationRequest.CREATOR.createFromParcel(p);
    assertEquals(original.getType(), copy.getType());
    assertFalse(copy.isTrackStatistics());
    assertEquals("name", copy.getName());
    assertEquals("category", copy.getCategory());
    assertEquals("description", copy.getDescription());
    assertEquals("img.png", copy.getIconUrl());
  }
}
