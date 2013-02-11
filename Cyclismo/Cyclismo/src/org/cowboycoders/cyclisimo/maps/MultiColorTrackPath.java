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
 * Copyright 2011 Google Inc.
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
 * A path painter that varies the path colors based on fixed speeds or average
 * speed margin depending of the TrackPathDescriptor passed to its constructor.
 * 
 * @author Vangelis S.
 */
public class MultiColorTrackPath implements TrackPath {
  private final TrackPathDescriptor trackPathDescriptor;
  private final int slowColor;
  private final int normalColor;
  private final int fastColor;
  
  public MultiColorTrackPath(Context context, TrackPathDescriptor trackPathDescriptor) {
    this.trackPathDescriptor = trackPathDescriptor;
    slowColor = context.getResources().getColor(R.color.slow_path);
    normalColor = context.getResources().getColor(R.color.normal_path);
    fastColor = context.getResources().getColor(R.color.fast_path);
  }

  @Override
  public boolean updateState() {
    return trackPathDescriptor.updateState();
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
    LatLng lastLatLng = startIndex != 0 ? locations.get(startIndex -1).getLatLng() : null;
    
    ArrayList<LatLng> lastSegmentPoints = new ArrayList<LatLng>();
    int lastSegmentColor = paths.size() != 0  ? paths.get(paths.size() - 1).getColor() : slowColor;
    boolean useLastPolyline = true;

    for (int i = startIndex; i < locations.size(); ++i) {
      CachedLocation cachedLocation = locations.get(i);

      // If not valid, start a new segment
      if (!cachedLocation.isValid()) {
        newSegment = true;
        lastLatLng = null;
        continue;
      }
      LatLng latLng = cachedLocation.getLatLng();
      int color = getColor(cachedLocation.getSpeed());
      
      // Either update point or draw a line from the last point
      if (newSegment) {
        TrackPathUtils.addPath(googleMap, paths, lastSegmentPoints, lastSegmentColor, useLastPolyline);
        useLastPolyline = false;
        lastSegmentColor = color;
        newSegment = false;
      }
      if (lastSegmentColor == color) {
        lastSegmentPoints.add(latLng);
      } else {
        TrackPathUtils.addPath(googleMap, paths, lastSegmentPoints, lastSegmentColor, useLastPolyline);
        useLastPolyline = false;
        if (lastLatLng != null) {
          lastSegmentPoints.add(lastLatLng);
        }
        lastSegmentPoints.add(latLng);
        lastSegmentColor = color;
      }
      lastLatLng = latLng;
    }
    TrackPathUtils.addPath(googleMap, paths, lastSegmentPoints, lastSegmentColor, useLastPolyline);
  }

  private int getColor(int speed) {
    if (speed <= trackPathDescriptor.getSlowSpeed()) {
      return slowColor;
    } else if (speed <= trackPathDescriptor.getNormalSpeed()) {
      return normalColor;
    } else {
      return fastColor;
    }
  }
}