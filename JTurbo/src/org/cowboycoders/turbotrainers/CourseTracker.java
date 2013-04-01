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
package org.cowboycoders.turbotrainers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cowboycoders.location.LatLongAlt;
import org.cowboycoders.location.LocationUtils;

public class CourseTracker {
  
  private static final double ACCURACY = 0.001;
  
  private Map<Double,LatLongAlt> distanceLocationMap = new HashMap<Double,LatLongAlt>();
  
  private Double[] distanceMarkers;
  
  private int lastKnownDistanceMarkerIndex = 0; 
  
  public CourseTracker(List<LatLongAlt> coursePoints) {
    double totalDistance = 0.0;
    distanceLocationMap.put(totalDistance,coursePoints.get(0));
    for (int i=1 ; i< coursePoints.size() ; i++) {
      double distanceBetweenPoints = org.cowboycoders.location.LocationUtils.gradientCorrectedDistance(coursePoints.get(i-1), coursePoints.get(i));
      if (distanceBetweenPoints < ACCURACY) {
        // assume the same location
        continue;
      }
      assert distanceBetweenPoints > 0;
      totalDistance += distanceBetweenPoints;
      distanceLocationMap.put(totalDistance, coursePoints.get(i));
    }
    distanceMarkers = distanceLocationMap.keySet().toArray(new Double[0]);
    Arrays.sort(distanceMarkers);
  }
  
  public LatLongAlt getNearestLocation(final double distance) {
    Double key = null;
    for (int i = lastKnownDistanceMarkerIndex ; i < distanceMarkers.length ; i++) {
      key = distanceMarkers[i];
      lastKnownDistanceMarkerIndex = i;
      if ((distance - key) < ACCURACY) {
        break;
      }
  }
    if (key == null) { // must have reached end
      key = distanceMarkers[distanceMarkers.length -1];
      return distanceLocationMap.get(key);
    }
    
    return distanceLocationMap.get(key);
}
  
  public boolean hasFinished() {
    if (lastKnownDistanceMarkerIndex < distanceMarkers.length -1){
      return false;
    }
    return true;
  }
  
  public double getCurrentGradient() {
    if (hasFinished()) {
      return 0.0;
    }
    final Double currentLocationKey = distanceMarkers[lastKnownDistanceMarkerIndex];
    final Double nextLocationKey = distanceMarkers[lastKnownDistanceMarkerIndex +1];
    double gradient = LocationUtils
        .getLocalisedGradient(distanceLocationMap.get(currentLocationKey), distanceLocationMap.get(nextLocationKey));
    return gradient;
  }
  
  public static void main(String [] args) {
    LatLongAlt l1 = new LatLongAlt(50.066389,5.715, 1000);
    LatLongAlt l2 = new LatLongAlt(58.643889,3.07, 4000);
    List<LatLongAlt> locations = LocationUtils.interpolateBetweenPoints(l1,l2,1000);
    locations.addAll(LocationUtils.interpolateBetweenPoints(l2,l1,1000));
    CourseTracker ct = new CourseTracker(locations);
    
    for (Double marker : ct.distanceMarkers) {
      System.out.println(marker);
    }
    double distance = 0;
    while (!ct.hasFinished()) {
      ct.getNearestLocation(distance += 1000);
      System.out.println("lastKnownDistanceMarkerIndex " + ct.lastKnownDistanceMarkerIndex);
    }
    System.out.println("finished");
  }
  
}