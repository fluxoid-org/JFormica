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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import org.cowboycoders.cyclisimo.R;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import org.cowboycoders.cyclisimo.MapOverlay.CachedLocation;

/**
 * A single color track path.
 * 
 * @author Jimmy Shih
 */
public class SingleColorTrackPath implements TrackPath {

  final int color;
  
  /**
   * @return the color
   */
  public int getColor() {
    return color;
  }

  public SingleColorTrackPath(Context context) {
    color = context.getResources().getColor(R.color.fast_path);
  }

  @Override
  public boolean updateState() {
    return false;
  }

  @Override
  public void updatePath(GoogleMap googleMap, ArrayList<Polyline> paths, int startIndex,
      List<CachedLocation> locations) {
    if (googleMap == null) {
      return;
    }
    if (startIndex >= locations.size()) {
      return;
    }

    boolean newSegment = startIndex == 0 || !locations.get(startIndex - 1).isValid();
    ArrayList<LatLng> lastSegmentPoints = new ArrayList<LatLng>();
    boolean useLastPolyline = true;
    for (int i = startIndex; i < locations.size(); i++) {
      CachedLocation cachedLocation = locations.get(i);

      // If not valid, start a new segment
      if (!cachedLocation.isValid()) {
        newSegment = true;
        continue;
      }
      LatLng latLng = cachedLocation.getLatLng();
      if (newSegment) {
        TrackPathUtils.addPath(googleMap, paths, lastSegmentPoints, getColor(), useLastPolyline);
        useLastPolyline = false;
        newSegment = false;
      }
      lastSegmentPoints.add(latLng);
    }
    TrackPathUtils.addPath(googleMap, paths, lastSegmentPoints, getColor(), useLastPolyline);
  }
}