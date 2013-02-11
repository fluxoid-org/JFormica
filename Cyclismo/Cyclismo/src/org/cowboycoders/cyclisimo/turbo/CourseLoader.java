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
package org.cowboycoders.cyclisimo.turbo;

import org.cowboycoders.cyclisimo.content.Track;
import org.cowboycoders.cyclisimo.content.Waypoint;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.cowboycoders.cyclisimo.content.TrackDataHub;
import org.cowboycoders.cyclisimo.content.TrackDataListener;
import org.cowboycoders.cyclisimo.content.TrackDataType;
import org.cowboycoders.location.LatLongAlt;

public class CourseLoader{
  
  public static String TAG = CourseLoader.class.getSimpleName();
  
  private TrackDataHub courseDataHub;
  private ArrayList<LatLongAlt> latLongAlts = new ArrayList<LatLongAlt>();
  private boolean finished = false;
  private Track currentTrack;
  private final long expectedId;
  
  
  CourseLoader(Context context, long trackId) {
    courseDataHub = TrackDataHub.newInstance(context,true);
    expectedId = trackId;
    courseDataHub.start();
    courseDataHub.registerTrackDataListener(trackDataListener, EnumSet.of(TrackDataType.TRACKS_TABLE,
        TrackDataType.SAMPLED_IN_TRACK_POINTS_TABLE));
    loadCourse(trackId);
  }
  
  private void loadCourse(long trackId) {
    //courseDataHub.loadTrack(-1);
    courseDataHub.loadTrack(trackId);
    courseDataHub.reloadDataForListener(trackDataListener);
  }
  
  private TrackDataListener trackDataListener = new TrackDataListener() {
    @Override
    public void onLocationStateChanged(LocationState locationState) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void onLocationChanged(Location location) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void onHeadingChanged(double heading) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void onSelectedTrackChanged(Track track) {
      Log.d(TAG,"selected track changed");
      
    }

    @Override
    public void onTrackUpdated(Track track) {
      Log.d(TAG,"track updated");
      currentTrack = track;
      
    }

    @Override
    public void clearTrackPoints() {
      // TODO Auto-generated method stub
      
    }

    @Override
    public synchronized void onSampledInTrackPoint(Location location) {
      if (!finished) {
      Log.i(TAG,"new track point");
      double lat = location.getLatitude();
      double lng = location.getLongitude();
      double alt = location.getAltitude();
      Log.d(TAG,"alt: " + alt);
      Log.d(TAG,"lat: " + lat);
      Log.d(TAG,"long: " + lng);
      LatLongAlt latLngAlt = new LatLongAlt(lat,lng,alt);
      latLongAlts.add(latLngAlt);
      }
      // TODO Auto-generated method stub
      
    }

    @Override
    public void onSampledOutTrackPoint(Location location) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void onSegmentSplit(Location location) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public synchronized void onNewTrackPointsDone() {
      Log.i(TAG, "track points done");
      if (currentTrack != null && currentTrack.getId() == expectedId ) {
        finished = true;
        courseDataHub.stop();
        courseDataHub.unregisterTrackDataListener(this);
        this.notifyAll();
      } else if (!finished) {
        latLongAlts.clear();
      }
    }

    @Override
    public void clearWaypoints() {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void onNewWaypoint(Waypoint waypoint) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void onNewWaypointsDone() {
      // TODO Auto-generated method stub
      
    }

    @Override
    public boolean onMetricUnitsChanged(boolean metricUnits) {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public boolean onReportSpeedChanged(boolean reportSpeed) {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public boolean onMinRecordingDistanceChanged(int minRecordingDistance) {
      // TODO Auto-generated method stub
      return false;
    }
  };
  
  public List<LatLongAlt> getLatLongAlts() throws InterruptedException {
    
    //Thread.sleep(1000);
    
    synchronized (trackDataListener) {
      while (!finished) {
        trackDataListener.wait();
      }
    }
    
    Log.i(TAG,"lat long length: " + latLongAlts.size());
    
    //courseDataHub.stop();
    //courseDataHub.unregisterTrackDataListener(trackDataListener);
    
    return latLongAlts;
  }



}
