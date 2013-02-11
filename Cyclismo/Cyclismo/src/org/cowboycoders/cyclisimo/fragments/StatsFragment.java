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
 * Copyright 2008 Google Inc.
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

package org.cowboycoders.cyclisimo.fragments;

import org.cowboycoders.cyclisimo.content.Track;
import org.cowboycoders.cyclisimo.content.Waypoint;
import org.cowboycoders.cyclisimo.stats.TripStatistics;
import org.cowboycoders.cyclisimo.R;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.EnumSet;

import org.cowboycoders.cyclisimo.TrackDetailActivity;
import org.cowboycoders.cyclisimo.content.TrackDataHub;
import org.cowboycoders.cyclisimo.content.TrackDataListener;
import org.cowboycoders.cyclisimo.content.TrackDataType;
import org.cowboycoders.cyclisimo.util.StatsUtils;

/**
 * A fragment to display track statistics to the user.
 * 
 * @author Sandor Dornbush
 * @author Rodrigo Damazio
 */
public class StatsFragment extends Fragment implements TrackDataListener {

  public static final String STATS_FRAGMENT_TAG = "statsFragment";

  private static final int ONE_SECOND = 1000;
  
  private TrackDataHub trackDataHub;
  private Handler handler;

  private Location lastLocation = null;
  private TripStatistics lastTripStatistics = null;

  // A runnable to update the total time field.
  private final Runnable updateTotalTime = new Runnable() {
    public void run() {
      if (isResumed() && isSelectedTrackRecording()) {
        if (!isSelectedTrackPaused() && lastTripStatistics != null) {
          StatsUtils.setTotalTimeValue(getActivity(), System.currentTimeMillis()
              - lastTripStatistics.getStopTime() + lastTripStatistics.getTotalTime());
        }
        handler.postDelayed(this, ONE_SECOND);
      }
    }
  };

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.stats, container, false);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    handler = new Handler();
    updateUi(getActivity());
  }

  @Override
  public void onResume() {
    super.onResume();
    resumeTrackDataHub();
  }

  @Override
  public void onPause() {
    super.onPause();
    pauseTrackDataHub();
    handler.removeCallbacks(updateTotalTime);
  }

  @Override
  public void onLocationStateChanged(LocationState state) {
    if (isResumed() && state != LocationState.GOOD_FIX) {
      getActivity().runOnUiThread(new Runnable() {
          @Override
        public void run() {
          if (isResumed()) {
            lastLocation = null;
            StatsUtils.setLocationValues(getActivity(), lastLocation, true);
          }
        }
      });
    }
  }

  @Override
  public void onLocationChanged(final Location location) {
    if (isResumed()) {
      getActivity().runOnUiThread(new Runnable() {
          @Override
        public void run() {
          if (isResumed()) {
            if (isSelectedTrackRecording() && !isSelectedTrackPaused()) {
              lastLocation = location;
              StatsUtils.setLocationValues(getActivity(), location, true);
            } else {
              if (lastLocation != null) {
                lastLocation = null;
                StatsUtils.setLocationValues(getActivity(), lastLocation, true);
              }
            }
          }
        }
      });
    }
  }

  @Override
  public void onHeadingChanged(double heading) {
    // We don't care.
  }

  @Override
  public void onSelectedTrackChanged(Track track) {
    if (isResumed()) {
      handler.removeCallbacks(updateTotalTime);
      if (isSelectedTrackRecording()) {
        handler.post(updateTotalTime);
      }
    }
  }

  @Override
  public void onTrackUpdated(final Track track) {
    if (isResumed()) {
      getActivity().runOnUiThread(new Runnable() {
          @Override
        public void run() {
          if (isResumed()) {
            lastTripStatistics = track != null ? track.getTripStatistics() : null;
            updateUi(getActivity());
          }
        }
      });
    }
  }

  @Override
  public void clearTrackPoints() {
    // We don't care.
  }

  @Override
  public void onSampledInTrackPoint(Location location) {
    // We don't care.
  }

  @Override
  public void onSampledOutTrackPoint(Location location) {
    // We don't care.
  }

  @Override
  public void onSegmentSplit(Location location) {
    // We don't care.
  }

  @Override
  public void onNewTrackPointsDone() {
    // We don't care.
  }

  @Override
  public void clearWaypoints() {
    // We don't care.
  }

  @Override
  public void onNewWaypoint(Waypoint wpt) {
    // We don't care.
  }

  @Override
  public void onNewWaypointsDone() {
    // We don't care.
  }

  @Override
  public boolean onMetricUnitsChanged(final boolean metric) {
    if (isResumed()) {
      getActivity().runOnUiThread(new Runnable() {
          @Override
        public void run() {
          if (isResumed()) {
            updateUi(getActivity());
          }
        }
      });
    }
    return true;
  }

  @Override
  public boolean onReportSpeedChanged(final boolean speed) {
    if (isResumed()) {
      getActivity().runOnUiThread(new Runnable() {
          @Override
        public void run() {
          if (isResumed()) {
            updateUi(getActivity());
          }
        }
      });
    }
    return true;
  }

  @Override
  public boolean onMinRecordingDistanceChanged(int minRecordingDistance) {
    // We don't care.
    return false;
  }

  /**
   * Resumes the trackDataHub. Needs to be synchronized because trackDataHub can
   * be accessed by multiple threads.
   */
  private synchronized void resumeTrackDataHub() {
    trackDataHub = ((TrackDetailActivity) getActivity()).getTrackDataHub();
    trackDataHub.registerTrackDataListener(this, EnumSet.of(TrackDataType.SELECTED_TRACK,
        TrackDataType.TRACKS_TABLE, TrackDataType.LOCATION, TrackDataType.PREFERENCE));
  }

  /**
   * Pauses the trackDataHub. Needs to be synchronized because trackDataHub can
   * be accessed by multiple threads.
   */
  private synchronized void pauseTrackDataHub() {
    trackDataHub.unregisterTrackDataListener(this);
    trackDataHub = null;
  }

  /**
   * Returns true if the selected track is recording. Needs to be synchronized
   * because trackDataHub can be accessed by multiple threads.
   */
  private synchronized boolean isSelectedTrackRecording() {
    return trackDataHub != null && trackDataHub.isSelectedTrackRecording();
  }

  /**
   * Returns true if the selected track is paused. Needs to be synchronized
   * because trackDataHub can be accessed by multiple threads.
   */
  private synchronized boolean isSelectedTrackPaused() {
    return trackDataHub != null && trackDataHub.isSelectedTrackPaused();
  }

  /**
   * Updates the UI.
   */
  private void updateUi(FragmentActivity activity) {
    StatsUtils.setTripStatisticsValues(activity, lastTripStatistics);
    StatsUtils.setLocationValues(activity, lastLocation, true);
  }
}
