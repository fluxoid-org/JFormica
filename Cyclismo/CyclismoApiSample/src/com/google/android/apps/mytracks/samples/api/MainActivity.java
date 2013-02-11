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
package org.cowboycoders.cyclisimo.samples.api;

import org.cowboycoders.cyclisimo.content.MyTracksProviderUtils;
import org.cowboycoders.cyclisimo.content.Track;
import org.cowboycoders.cyclisimo.content.Waypoint;
import org.cowboycoders.cyclisimo.services.ITrackRecordingService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

/**
 * An activity to access MyTracks content provider and service.
 *
 * Note you must first install MyTracks before installing this app.
 *
 * You also need to enable third party application access inside MyTracks
 * MyTracks -> menu -> Settings -> Sharing -> Allow access
 *
 * @author Jimmy Shih
 */
public class MainActivity extends Activity {

  private static final String TAG = MainActivity.class.getSimpleName();

  // utils to access the MyTracks content provider
  private MyTracksProviderUtils myTracksProviderUtils;

  // display output from the MyTracks content provider
  private TextView outputTextView;

  // MyTracks service
  private ITrackRecordingService myTracksService;
  
  // intent to access the MyTracks service
  private Intent intent;

  // connection to the MyTracks service
  private ServiceConnection serviceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
      myTracksService = ITrackRecordingService.Stub.asInterface(service);
    }

    @Override
    public void onServiceDisconnected(ComponentName className) {
      myTracksService = null;
    }
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    // for the MyTracks content provider
    myTracksProviderUtils = MyTracksProviderUtils.Factory.get(this);
    outputTextView = (TextView) findViewById(R.id.output);

    Button addWaypointsButton = (Button) findViewById(R.id.add_waypoints_button);
    addWaypointsButton.setOnClickListener(new View.OnClickListener() {
	@Override
        public void onClick(View v) {
	    List<Track> tracks = myTracksProviderUtils.getAllTracks();
	    Calendar now = Calendar.getInstance();   
	    for (Track track : tracks) {
	      Waypoint waypoint = new Waypoint();
	      waypoint.setTrackId(track.getId());
	      waypoint.setName(now.getTime().toLocaleString());
	      waypoint.setDescription(now.getTime().toLocaleString());
	      myTracksProviderUtils.insertWaypoint(waypoint);
	    }   	
        }
    });
    
    // for the MyTracks service
    intent = new Intent();
    ComponentName componentName = new ComponentName(
        getString(R.string.mytracks_service_package), getString(R.string.mytracks_service_class));
    intent.setComponent(componentName);

    Button startRecordingButton = (Button) findViewById(R.id.start_recording_button);
    startRecordingButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (myTracksService != null) {
          try {
            myTracksService.startNewTrack();
          } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
          }
        }
      }
    });

    Button stopRecordingButton = (Button) findViewById(R.id.stop_recording_button);
    stopRecordingButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (myTracksService != null) {
          try {
            myTracksService.endCurrentTrack();
          } catch (RemoteException e) {
            Log.e(TAG, "RemoteException", e);
          }
        }
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();

    // use the MyTracks content provider to get all the tracks
    List<Track> tracks = myTracksProviderUtils.getAllTracks();
    for (Track track : tracks) {
      outputTextView.append(track.getId() + " ");
    }

    // start and bind the MyTracks service
    startService(intent);
    bindService(intent, serviceConnection, 0);
  }

  @Override
  protected void onStop() {
    super.onStop();
    
    // unbind and stop the MyTracks service
    if (myTracksService != null) {
      unbindService(serviceConnection);
    }
    stopService(intent);
  }
}
