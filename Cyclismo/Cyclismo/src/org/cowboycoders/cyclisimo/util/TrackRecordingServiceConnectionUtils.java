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

package org.cowboycoders.cyclisimo.util;

import org.cowboycoders.cyclisimo.content.WaypointCreationRequest;
import org.cowboycoders.cyclisimo.services.ITrackRecordingService;
import org.cowboycoders.cyclisimo.R;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import org.cowboycoders.cyclisimo.TrackEditActivity;
import org.cowboycoders.cyclisimo.services.TrackRecordingService;
import org.cowboycoders.cyclisimo.services.TrackRecordingServiceConnection;

/**
 * Utilities for {@link TrackRecordingServiceConnection}.
 * 
 * @author Rodrigo Damazio
 */
public class TrackRecordingServiceConnectionUtils {

  private static final String TAG = TrackRecordingServiceConnectionUtils.class.getSimpleName();

  private TrackRecordingServiceConnectionUtils() {}

  /**
   * Returns true if the recording service is running.
   * 
   * @param context the current context
   */
  public static boolean isRecordingServiceRunning(Context context) {
    ActivityManager activityManager = (ActivityManager) context.getSystemService(
        Context.ACTIVITY_SERVICE);
    List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

    for (RunningServiceInfo serviceInfo : services) {
      ComponentName componentName = serviceInfo.service;
      String serviceName = componentName.getClassName();
      if (TrackRecordingService.class.getName().equals(serviceName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Resumes the recording track.
   * 
   * @param trackRecordingServiceConnection the track recording service
   */
  public static void resumeTrack(TrackRecordingServiceConnection trackRecordingServiceConnection) {
    try {
      ITrackRecordingService service = trackRecordingServiceConnection.getServiceIfBound();
      if (service != null) {
        service.resumeCurrentTrack();
      }
    } catch (RemoteException e) {
      Log.e(TAG, "Unable to resume track.", e);
    }
  }

  /**
   * Pauses the recording track.
   * 
   * @param trackRecordingServiceConnection the track recording service
   *          connection
   */
  public static void pauseTrack(TrackRecordingServiceConnection trackRecordingServiceConnection) {
    try {
      ITrackRecordingService service = trackRecordingServiceConnection.getServiceIfBound();
      if (service != null) {
        service.pauseCurrentTrack();
      }
    } catch (RemoteException e) {
      Log.e(TAG, "Unable to resume track.", e);
    }
  }

  /**
   * Stops the recording.
   * 
   * @param context the context
   * @param trackRecordingServiceConnection the track recording service
   *          connection
   * @param showEditor true to show the editor
   */
  public static void stopRecording(Context context,
      TrackRecordingServiceConnection trackRecordingServiceConnection, boolean showEditor) {
    ITrackRecordingService trackRecordingService = trackRecordingServiceConnection
        .getServiceIfBound();
    if (trackRecordingService != null) {
      try {
        if (showEditor) {
          /*
           * Need to remember the recordingTrackId before calling
           * endCurrentTrack. endCurrentTrack sets the value to -1L.
           */
          long recordingTrackId = PreferencesUtils.getLong(
              context, R.string.recording_track_id_key);
          trackRecordingService.endCurrentTrack();
          if (recordingTrackId != PreferencesUtils.RECORDING_TRACK_ID_DEFAULT) {
            Intent intent = IntentUtils.newIntent(context, TrackEditActivity.class)
                .putExtra(TrackEditActivity.EXTRA_TRACK_ID, recordingTrackId)
                .putExtra(TrackEditActivity.EXTRA_NEW_TRACK, true);
            context.startActivity(intent);
          }
        } else {
          trackRecordingService.endCurrentTrack();
        }
      } catch (Exception e) {
        Log.e(TAG, "Unable to stop recording.", e);
      }
    } else {
      resetRecordingState(context);
    }
    trackRecordingServiceConnection.unbindAndStop();
  }

  /**
   * Resumes the track recording service connection.
   *
   * @param context the context
   * @param trackRecordingServiceConnection the track recording service
   *          connection
   */
  public static void startConnection(
      Context context, TrackRecordingServiceConnection trackRecordingServiceConnection) {
    trackRecordingServiceConnection.bindIfStarted();
    if (!isRecordingServiceRunning(context)) {
      resetRecordingState(context);
    }
  }

  private static void resetRecordingState(Context context) {
    long recordingTrackId = PreferencesUtils.getLong(context, R.string.recording_track_id_key);
    if (recordingTrackId != PreferencesUtils.RECORDING_TRACK_ID_DEFAULT) {
      PreferencesUtils.setLong(
          context, R.string.recording_track_id_key, PreferencesUtils.RECORDING_TRACK_ID_DEFAULT);
    }
    boolean recordingTrackPaused = PreferencesUtils.getBoolean(context,
        R.string.recording_track_paused_key, PreferencesUtils.RECORDING_TRACK_PAUSED_DEFAULT);
    if (!recordingTrackPaused) {
      PreferencesUtils.setBoolean(context, R.string.recording_track_paused_key,
          PreferencesUtils.RECORDING_TRACK_PAUSED_DEFAULT);
    }
  }

  /**
   * Adds a marker.
   */
  public static void addMarker(Context context,
      TrackRecordingServiceConnection trackRecordingServiceConnection,
      WaypointCreationRequest waypointCreationRequest) {
    ITrackRecordingService trackRecordingService = trackRecordingServiceConnection
        .getServiceIfBound();
    if (trackRecordingService == null) {
      Log.d(TAG, "Unable to add marker, no track recording service");
    } else {
      try {
        if (trackRecordingService.insertWaypoint(waypointCreationRequest) != -1L) {
          Toast.makeText(context, R.string.marker_add_success, Toast.LENGTH_SHORT).show();
          return;
        }
      } catch (RemoteException e) {
        Log.e(TAG, "Unable to add marker", e);
      } catch (IllegalStateException e) {
        Log.e(TAG, "Unable to add marker.", e);
      }
    }
    Toast.makeText(context, R.string.marker_add_error, Toast.LENGTH_LONG).show();
  }
}
