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
package org.cowboycoders.location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LocationUtils {
  
  private LocationUtils () {
    
  }
  
  public static int EARTH_RADIUS = 6371000; //m
  
  public static LatLong midPoint(LatLong point1, LatLong point2){
    double lat1 = point1.getLatitude();
    double lon1 = point1.getLongitude();
    double lat2 = point2.getLatitude();
    double lon2 = point2.getLongitude();
    
    double dLon = Math.toRadians(lon2 - lon1);

    lat1 = Math.toRadians(lat1);
    lat2 = Math.toRadians(lat2);
    lon1 = Math.toRadians(lon1);

    double Bx = Math.cos(lat2) * Math.cos(dLon);
    double By = Math.cos(lat2) * Math.sin(dLon);
    double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
    double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

    return new LatLong(Math.toDegrees(lat3),Math.toDegrees(lon3));
}
  
  public static LatLongAlt midPoint(LatLongAlt point1, LatLongAlt point2){
    LatLong midpoint = LocationUtils.midPoint((LatLong)point1,(LatLong)point2);
    double averageHeight = (point2.getAltitude() + point1.getAltitude()) /2;
    return new LatLongAlt(midpoint.getLatitude(),midpoint.getLongitude(),averageHeight);
}
  
  /**
   * see http://www.movable-type.co.uk/scripts/latlong.html
   * @param point1
   * @param point2
   * @return distance in m
   */
  public static double distance(LatLong point1, LatLong point2) {
    double lat1 = point1.getLatitude();
    double lon1 = point1.getLongitude();
    double lat2 = point2.getLatitude();
    double lon2 = point2.getLongitude();

    double dLat = Math.toRadians(lat2-lat1);
    double dLon = Math.toRadians(lon2-lon1);
    
    lat1 = Math.toRadians(lat1);
    lat2 = Math.toRadians(lat2);
    

    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2); 
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
    double d = EARTH_RADIUS  * c;
    return d;
  }
  
  public static double gradientCorrectedDistance(LatLongAlt point1, LatLongAlt point2) {
	    double horizontalDistance = distance(point1,point2);
	    double heightDifference = point2.getAltitude() - point1.getAltitude();
	    return Math.sqrt(Math.pow(heightDifference,2) + Math.pow(horizontalDistance, 2));
  }
  

  public static List<LatLongAlt> interpolatePoints(List<LatLongAlt> locations, double maximumDistance) {
    List<LatLongAlt> rtn = new ArrayList<LatLongAlt>();
    for (int i = 0 ; i< locations.size() -1 ; i++) {
      // add points to the left
      rtn.add(locations.get(i));
      rtn.addAll(interpolateBetweenPoints(locations.get(i),locations.get(i + 1), maximumDistance));
    }
    // add right most
    rtn.add(locations.get(locations.size() -1));
    return rtn;
  }
  
  /**
   * Does no return original points
   * @param point1
   * @param point2
   * @param maximumDistance max distance between two points in m
   */
  public static List<LatLongAlt> interpolateBetweenPoints(LatLongAlt point1, LatLongAlt point2, final double maximumDistance ) {
    if (distance(point1,point2) < maximumDistance) {
      return Collections.<LatLongAlt>emptyList();
    }
    List<LatLongAlt> left = new ArrayList<LatLongAlt>();
    List<LatLongAlt> right = new ArrayList<LatLongAlt>();
    LatLongAlt midpoint = midPoint(point1,point2);
    left = interpolateBetweenPoints(point1,midpoint,maximumDistance);
    right = interpolateBetweenPoints(midpoint,point2,maximumDistance);
    List<LatLongAlt> rtn = new ArrayList<LatLongAlt>(left.size() + 1 + right.size());
    rtn.addAll(left);
    rtn.add(midpoint);
    rtn.addAll(right);
    return rtn;
  }
  
  /**
   * Gradient in percent between two points
   * @param point1
   * @param point2
   * @return gradient in percent
   */
  public static double getLocalisedGradient(LatLongAlt point1, LatLongAlt point2) {
    double horizontalDistance = distance(point1,point2);
    double heightDifference = point2.getAltitude() - point1.getAltitude();
    return (heightDifference / horizontalDistance) * 100;
  }
  
  public static void main(String[] args) {
    LatLongAlt l1 = new LatLongAlt(50.066389,5.715, 1000);
    LatLongAlt l2 = new LatLongAlt(58.643889,3.07, 4000);
    LatLongAlt l3 = LocationUtils.midPoint(l1, l2);
    System.out.println("Lat: " + l3.getLatitude());
    System.out.println("Long: " + l3.getLongitude());
    System.out.println("Alt: " + l3.getAltitude());
    System.out.println(distance(l1,l2));
    System.out.println(interpolateBetweenPoints(l1,l2,1000).size());
    List<LatLongAlt> locations = interpolateBetweenPoints(l1,l2,1000);
    for (int i = 0 ; i< locations.size() -2 ; i++) {
      System.out.println(distance(locations.get(i),locations.get(i+1)));
    }
    List<LatLongAlt> rtn = new ArrayList<LatLongAlt>();
    rtn.add(l1);
    rtn.add(l2);
    System.out.println("locations.size(): " + locations.size());
    locations = interpolatePoints(rtn,1000);
    System.out.println("locations.size(): " + locations.size());
  }
  
  
  
  

}
