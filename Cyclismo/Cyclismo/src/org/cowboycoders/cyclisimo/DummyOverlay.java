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
package org.cowboycoders.cyclisimo;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Doesn't actually update the map
 * @author will
 *
 */
public class DummyOverlay extends MapOverlay {

  public DummyOverlay(Context context) {
    super(context);
  }
  
  
  /**
   * Ensure we don't accidently update map
   * Updates the track, start and end markers, and waypoints.
   * 
   * @param googleMap the google map
   * @param paths the paths
   * @param reload true to reload all points
   */
  public void update(GoogleMap googleMap, ArrayList<Polyline> paths, boolean reload) {
    List<CachedLocation> locations = getLocations();
    BlockingQueue<CachedLocation> pendingLocations = getPendingLocations();
    synchronized (locations) {
      pendingLocations.drainTo(locations);
      }
    }
  
  /**
   * Updates list of locations
   */
  public void update() {
    update(null,null,true);
  }
  
  

}
