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

package org.cowboycoders.cyclisimo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import java.util.List;

import org.cowboycoders.cyclisimo.content.MyTracksCourseProviderUtils;
import org.cowboycoders.cyclisimo.content.MyTracksProviderUtils;
import org.cowboycoders.cyclisimo.content.Track;
import org.cowboycoders.cyclisimo.content.TrackDataHub;
import org.cowboycoders.cyclisimo.content.Waypoint;
import org.cowboycoders.cyclisimo.content.WaypointCreationRequest;
import org.cowboycoders.cyclisimo.fragments.ChartFragment;
import org.cowboycoders.cyclisimo.fragments.ChooseActivityDialogFragment;
import org.cowboycoders.cyclisimo.fragments.ChooseUploadServiceDialogFragment;
import org.cowboycoders.cyclisimo.fragments.ConfirmPlayDialogFragment;
import org.cowboycoders.cyclisimo.fragments.DeleteOneTrackDialogFragment;
import org.cowboycoders.cyclisimo.fragments.DeleteOneTrackDialogFragment.DeleteOneTrackCaller;
import org.cowboycoders.cyclisimo.fragments.FrequencyDialogFragment;
import org.cowboycoders.cyclisimo.fragments.InstallEarthDialogFragment;
import org.cowboycoders.cyclisimo.fragments.MyTracksMapFragment;
import org.cowboycoders.cyclisimo.fragments.StatsFragment;
import org.cowboycoders.cyclisimo.io.file.SaveActivity;
import org.cowboycoders.cyclisimo.io.file.TrackWriterFactory.TrackFileFormat;
import org.cowboycoders.cyclisimo.io.sendtogoogle.SendRequest;
import org.cowboycoders.cyclisimo.services.ITrackRecordingService;
import org.cowboycoders.cyclisimo.services.TrackRecordingServiceConnection;
import org.cowboycoders.cyclisimo.settings.SettingsActivity;
import org.cowboycoders.cyclisimo.util.AnalyticsUtils;
import org.cowboycoders.cyclisimo.util.IntentUtils;
import org.cowboycoders.cyclisimo.util.PreferencesUtils;
import org.cowboycoders.cyclisimo.util.TrackRecordingServiceConnectionUtils;

/**
 * An activity to show the track detail.
 * 
 * @author Leif Hendrik Wilden
 * @author Rodrigo Damazio
 */
public class TrackDetailActivity extends AbstractMyTracksActivity implements DeleteOneTrackCaller {

  public static final String EXTRA_TRACK_ID = "track_id";
  public static final String EXTRA_MARKER_ID = "marker_id";
  public static final String EXTRA_USE_COURSE_PROVIDER = "use_course_provider";
  public static final String EXTRA_COURSE_TRACK_ID = "course_track_id";

  private static final String TAG = TrackDetailActivity.class.getSimpleName();
  private static final String CURRENT_TAB_TAG_KEY = "current_tab_tag_key";

  // The following are set in onCreate
  private SharedPreferences sharedPreferences;
  private TrackRecordingServiceConnection trackRecordingServiceConnection;
  private TrackDataHub trackDataHub;
  private TrackDataHub courseDataHub;
  private TabHost tabHost;
  private TabManager tabManager;
  private TrackController trackController;

  // From intent
  private long trackId;
  private long markerId;
  private boolean useCourseProivder = false;
  private long courseTrackId;
  private boolean courseLoaded = false;

  /**
   * @return the useCourseProivder
   */
  public boolean isUsingCourseProivder() {
    return useCourseProivder;
  }

  // Preferences
  private long recordingTrackId = PreferencesUtils.RECORDING_TRACK_ID_DEFAULT;
  private boolean recordingTrackPaused = PreferencesUtils.RECORDING_TRACK_PAUSED_DEFAULT;

  private MenuItem insertMarkerMenuItem;
  private MenuItem playMenuItem;
  private MenuItem shareMenuItem;
  private MenuItem sendGoogleMenuItem;
  private MenuItem saveMenuItem;
  private MenuItem voiceFrequencyMenuItem;
  private MenuItem splitFrequencyMenuItem;

  private final Runnable bindChangedCallback = new Runnable() {
    @Override
    public void run() {
      // After binding changes (is available), update the total time in
      // trackController.
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          ITrackRecordingService service = trackRecordingServiceConnection.getServiceIfBound();
          try {
            if (service != null) {
              recordingTrackPaused = service.isPaused();
            }
          } catch (RemoteException e) {
            //leave unchanged
          } 
//          synchronized (bindChangedCallback) {
//            if (isCourseMode() && courseLoaded == true && isPaused) {
//              //recordingTrackPaused = true;
//              resume();
//              return;
//            }
//          }
          trackController.update(trackId == recordingTrackId, recordingTrackPaused);
        }
       
      });
    }
  };

  /*
   * Note that sharedPreferenceChangeListener cannot be an anonymous inner
   * class. Anonymous inner class will get garbage collected.
   */
  private final OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
      // Note that key can be null
      if (key == null
          || key.equals(PreferencesUtils.getKey(TrackDetailActivity.this,
              R.string.recording_track_id_key))) {
        recordingTrackId = PreferencesUtils.getLong(TrackDetailActivity.this,
            R.string.recording_track_id_key);
      }
      if (key == null
          || key.equals(PreferencesUtils.getKey(TrackDetailActivity.this,
              R.string.recording_track_paused_key))) {
        recordingTrackPaused = PreferencesUtils.getBoolean(TrackDetailActivity.this,
            R.string.recording_track_paused_key, PreferencesUtils.RECORDING_TRACK_PAUSED_DEFAULT);
      }
      if (key != null) {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            boolean isRecording = trackId == recordingTrackId;
            updateMenuItems(isRecording, recordingTrackPaused);
            trackController.update(isRecording, recordingTrackPaused);
          }
        });
      }
    }
  };

  private final OnClickListener recordListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      if (recordingTrackPaused) {
        // Paused -> Resume
        resume();
      } else {
        // Recording -> Paused
        pause();
      }
    }
  };

  private void resume() {
    recordingTrackPaused = false;
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        AnalyticsUtils.sendPageViews(TrackDetailActivity.this, "/action/resume_track");
        updateMenuItems(true, false);
        TrackRecordingServiceConnectionUtils.resumeTrack(trackRecordingServiceConnection);
        trackController.update(true, false);
      }
    });
  }

  private void pause() {
    recordingTrackPaused = true;
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        AnalyticsUtils.sendPageViews(TrackDetailActivity.this, "/action/pause_track");
        updateMenuItems(true, true);
        TrackRecordingServiceConnectionUtils.pauseTrack(trackRecordingServiceConnection);
        trackController.update(true, true);
      }
    });

  }

  private final OnClickListener stopListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      AnalyticsUtils.sendPageViews(TrackDetailActivity.this, "/action/stop_recording");
      updateMenuItems(false, true);
      TrackRecordingServiceConnectionUtils.stopRecording(TrackDetailActivity.this,
          trackRecordingServiceConnection, true);
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // ActivityManager manager = (ActivityManager) this.getSystemService(
    // ACTIVITY_SERVICE );
    // List<RunningTaskInfo> tasks = manager.getRunningTasks(Integer.MAX_VALUE);
    //
    // for (RunningTaskInfo taskInfo : tasks) {
    // Log.d(TAG,"taskInfo.baseActivity.getClassName() : " +
    // taskInfo.baseActivity.getClassName());
    // Log.d(TAG,"taskInfo.description : " + taskInfo.description);
    //
    // if(taskInfo.baseActivity.getClassName().equals("org.cowboycoders.cyclisimo.TrackListActivity")
    // && (taskInfo.numActivities > 1)){
    // finish();
    // }
    // }

    super.onCreate(savedInstanceState);
    handleIntent(getIntent());

    sharedPreferences = getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);

    trackRecordingServiceConnection = new TrackRecordingServiceConnection(this, bindChangedCallback);
    if (this.useCourseProivder) {
      trackDataHub = TrackDataHub.newInstance(this, true);
    } else {
      trackDataHub = TrackDataHub.newInstance(this);
    }

    courseDataHub = TrackDataHub.newInstance(this, true);

    Bundle extras = new Bundle();

    extras.putLong(TrackDetailActivity.EXTRA_COURSE_TRACK_ID, getCourseTrackId());

    tabHost = (TabHost) findViewById(android.R.id.tabhost);
    tabHost.setup();
    tabManager = new TabManager(this, tabHost, R.id.realtabcontent);
    TabSpec mapTabSpec = tabHost.newTabSpec(MyTracksMapFragment.MAP_FRAGMENT_TAG).setIndicator(
        getString(R.string.track_detail_map_tab), getResources().getDrawable(R.drawable.tab_map));
    tabManager.addTab(mapTabSpec, MyTracksMapFragment.class, extras);
    TabSpec chartTabSpec = tabHost.newTabSpec(ChartFragment.CHART_FRAGMENT_TAG).setIndicator(
        getString(R.string.track_detail_chart_tab),
        getResources().getDrawable(R.drawable.tab_chart));
    tabManager.addTab(chartTabSpec, ChartFragment.class, extras);
    TabSpec statsTabSpec = tabHost.newTabSpec(StatsFragment.STATS_FRAGMENT_TAG).setIndicator(
        getString(R.string.track_detail_stats_tab),
        getResources().getDrawable(R.drawable.tab_stats));
    tabManager.addTab(statsTabSpec, StatsFragment.class, null);
    if (savedInstanceState != null) {
      tabHost.setCurrentTabByTag(savedInstanceState.getString(CURRENT_TAB_TAG_KEY));
    }
    trackController = new TrackController(this, trackRecordingServiceConnection, false,
        recordListener, stopListener);
    showMarker();
  }

  public long getCourseTrackId() {
    return this.courseTrackId;
  }

  @Override
  protected void onStart() {
    super.onStart();

    sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    sharedPreferenceChangeListener.onSharedPreferenceChanged(null, null);

    TrackRecordingServiceConnectionUtils.startConnection(this, trackRecordingServiceConnection);
    trackDataHub.start();
    courseDataHub.start();
    AnalyticsUtils.sendPageViews(this, "/page/track_detail");
  }

  @Override
  protected void onResume() {
    super.onResume();
    registerTurboServiceReceiver();
    Log.d(TAG, "trackId : " + trackId);
    trackDataHub.loadTrack(trackId);
    courseDataHub.loadTrack(courseTrackId);

    // Update UI
    boolean isRecording = trackId == recordingTrackId;
    updateMenuItems(isRecording, recordingTrackPaused);
    trackController.update(isRecording, recordingTrackPaused);
    //pause();
  }

  @Override
  protected void onPause() {
    super.onPause();
    unregisterTurboServiceReceiver();
    trackController.stop();
  }

  @Override
  protected void onStop() {
    super.onStop();
    sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    trackRecordingServiceConnection.unbind();
    trackDataHub.stop();
    courseDataHub.stop();
    AnalyticsUtils.dispatch();
  }

  /**
   * Course turbo trainer mode?
   * 
   * @return
   */
  public boolean isCourseMode() {
    if (!useCourseProivder && getCourseTrackId() != -1L)
      return true;
    return false;
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(CURRENT_TAB_TAG_KEY, tabHost.getCurrentTabTag());
  }

  @Override
  protected int getLayoutResId() {
    return R.layout.track_detail;
  }

  @Override
  protected boolean hideTitle() {
    return true;
  }

  @Override
  protected void onHomeSelected() {
    Intent intent = IntentUtils.newIntent(this, TrackListActivity.class);
    startActivity(intent);
    finish();
  }

  @Override
  public void onNewIntent(Intent intent) {
    setIntent(intent);
    handleIntent(intent);
    showMarker();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.track_detail, menu);
    String fileTypes[] = getResources().getStringArray(R.array.file_types);
    menu.findItem(R.id.track_detail_save_gpx).setTitle(
        getString(R.string.menu_save_format, fileTypes[0]));
    menu.findItem(R.id.track_detail_save_kml).setTitle(
        getString(R.string.menu_save_format, fileTypes[1]));
    menu.findItem(R.id.track_detail_save_csv).setTitle(
        getString(R.string.menu_save_format, fileTypes[2]));
    menu.findItem(R.id.track_detail_save_tcx).setTitle(
        getString(R.string.menu_save_format, fileTypes[3]));

    insertMarkerMenuItem = menu.findItem(R.id.track_detail_insert_marker);
    playMenuItem = menu.findItem(R.id.track_detail_play);
    shareMenuItem = menu.findItem(R.id.track_detail_share);
    sendGoogleMenuItem = menu.findItem(R.id.track_detail_send_google);
    saveMenuItem = menu.findItem(R.id.track_detail_save);
    voiceFrequencyMenuItem = menu.findItem(R.id.track_detail_voice_frequency);
    splitFrequencyMenuItem = menu.findItem(R.id.track_detail_split_frequency);

    updateMenuItems(trackId == recordingTrackId, recordingTrackPaused);
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    boolean showSensorState = !PreferencesUtils.SENSOR_TYPE_DEFAULT.equals(PreferencesUtils
        .getString(this, R.string.sensor_type_key, PreferencesUtils.SENSOR_TYPE_DEFAULT));
    menu.findItem(R.id.track_detail_sensor_state).setVisible(showSensorState);
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Intent intent;
    switch (item.getItemId()) {
      case R.id.track_detail_insert_marker:
        AnalyticsUtils.sendPageViews(this, "/action/insert_marker");
        intent = IntentUtils.newIntent(this, MarkerEditActivity.class).putExtra(
            MarkerEditActivity.EXTRA_TRACK_ID, trackId);
        startActivity(intent);
        return true;
      case R.id.track_detail_play:
        if (isEarthInstalled()) {
          ConfirmPlayDialogFragment.newInstance(trackId).show(getSupportFragmentManager(),
              ConfirmPlayDialogFragment.CONFIRM_PLAY_DIALOG_TAG);
        } else {
          new InstallEarthDialogFragment().show(getSupportFragmentManager(),
              InstallEarthDialogFragment.INSTALL_EARTH_DIALOG_TAG);
        }
        return true;
      case R.id.track_detail_share:
        AnalyticsUtils.sendPageViews(this, "/action/share");
        ChooseActivityDialogFragment.newInstance(trackId, null).show(getSupportFragmentManager(),
            ChooseActivityDialogFragment.CHOOSE_ACTIVITY_DIALOG_TAG);
        return true;
      case R.id.track_detail_markers:
        intent = IntentUtils.newIntent(this, MarkerListActivity.class).putExtra(
            MarkerListActivity.EXTRA_TRACK_ID, trackId);
        startActivity(intent);
        return true;
      case R.id.track_detail_voice_frequency:
        FrequencyDialogFragment.newInstance(R.string.voice_frequency_key,
            PreferencesUtils.VOICE_FREQUENCY_DEFAULT, R.string.menu_voice_frequency).show(
            getSupportFragmentManager(), FrequencyDialogFragment.FREQUENCY_DIALOG_TAG);
        return true;
      case R.id.track_detail_split_frequency:
        FrequencyDialogFragment.newInstance(R.string.split_frequency_key,
            PreferencesUtils.SPLIT_FREQUENCY_DEFAULT, R.string.menu_split_frequency).show(
            getSupportFragmentManager(), FrequencyDialogFragment.FREQUENCY_DIALOG_TAG);
        return true;
      case R.id.track_detail_send_google:
        AnalyticsUtils.sendPageViews(this, "/action/send_google");
        ChooseUploadServiceDialogFragment.newInstance(new SendRequest(trackId)).show(
            getSupportFragmentManager(),
            ChooseUploadServiceDialogFragment.CHOOSE_UPLOAD_SERVICE_DIALOG_TAG);
        return true;
      case R.id.track_detail_save_gpx:
        startSaveActivity(TrackFileFormat.GPX);
        return true;
      case R.id.track_detail_save_kml:
        startSaveActivity(TrackFileFormat.KML);
        return true;
      case R.id.track_detail_save_csv:
        startSaveActivity(TrackFileFormat.CSV);
        return true;
      case R.id.track_detail_save_tcx:
        startSaveActivity(TrackFileFormat.TCX);
        return true;
      case R.id.track_detail_edit:
        intent = IntentUtils.newIntent(this, TrackEditActivity.class)
            .putExtra(TrackEditActivity.EXTRA_TRACK_ID, trackId)
            .putExtra(TrackEditActivity.EXTRA_USE_COURSE_PROVIDER, useCourseProivder);
        startActivity(intent);
        return true;
      case R.id.track_detail_delete:
        DeleteOneTrackDialogFragment.newInstance(trackId, useCourseProivder).show(
            getSupportFragmentManager(), DeleteOneTrackDialogFragment.DELETE_ONE_TRACK_DIALOG_TAG);
        return true;
      case R.id.track_detail_sensor_state:
        intent = IntentUtils.newIntent(this, SensorStateActivity.class);
        startActivity(intent);
        return true;
      case R.id.track_detail_settings:
        intent = IntentUtils.newIntent(this, SettingsActivity.class);
        startActivity(intent);
        return true;
      case R.id.track_detail_help:
        intent = IntentUtils.newIntent(this, HelpActivity.class);
        startActivity(intent);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public boolean onTrackballEvent(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      if (trackId == recordingTrackId && !recordingTrackPaused) {
        TrackRecordingServiceConnectionUtils.addMarker(this, trackRecordingServiceConnection,
            WaypointCreationRequest.DEFAULT_WAYPOINT);
        return true;
      }
    }
    return super.onTrackballEvent(event);
  }

  @Override
  public TrackRecordingServiceConnection getTrackRecordingServiceConnection() {
    return trackRecordingServiceConnection;
  }

  /**
   * Gets the {@link TrackDataHub}.
   */
  public TrackDataHub getTrackDataHub() {
    return trackDataHub;
  }

  public TrackDataHub getCourseDataHub() {
    return courseDataHub;
  }

  /**
   * Handles the data in the intent.
   */
  private void handleIntent(Intent intent) {
    trackId = intent.getLongExtra(EXTRA_TRACK_ID, -1L);
    markerId = intent.getLongExtra(EXTRA_MARKER_ID, -1L);
    useCourseProivder = intent.getBooleanExtra(EXTRA_USE_COURSE_PROVIDER, false);
    courseTrackId = intent.getLongExtra(EXTRA_COURSE_TRACK_ID, -1L);
    if (markerId != -1L) {
      Waypoint waypoint = getProviderUtils().getWaypoint(markerId);
      if (waypoint == null) {
        exit();
        return;
      }
      trackId = waypoint.getTrackId();
    }
    if (trackId == -1L) {
      exit();
      return;
    }
  }

  private MyTracksProviderUtils getProviderUtils() {
    if (this.useCourseProivder) {
      return new MyTracksCourseProviderUtils(this.getContentResolver());
    } else {
      return MyTracksProviderUtils.Factory.get(this);
    }
  }

  /**
   * Exists and returns to {@link TrackListActivity}.
   */
  private void exit() {
    Intent newIntent = IntentUtils.newIntent(this, TrackListActivity.class);
    startActivity(newIntent);
    finish();
  }

  @Override
  public void finish() {
    super.finish();
    Log.d(TAG, "finish");
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "destroy");
  }

  /**
   * Shows marker.
   */
  private void showMarker() {
    if (markerId != -1L) {
      MyTracksMapFragment mapFragmet = (MyTracksMapFragment) getSupportFragmentManager()
          .findFragmentByTag(MyTracksMapFragment.MAP_FRAGMENT_TAG);

      if (mapFragmet != null) {
        tabHost.setCurrentTabByTag(MyTracksMapFragment.MAP_FRAGMENT_TAG);
        mapFragmet.showMarker(trackId, markerId);
      } else {
        Log.e(TAG, "MapFragment is null");
      }
    }
  }

  /**
   * Updates the menu items.
   * 
   * @param isRecording true if recording
   */
  private void updateMenuItems(boolean isRecording, boolean isPaused) {
    if (insertMarkerMenuItem != null) {
      insertMarkerMenuItem.setVisible(isRecording && !isPaused);
    }
    if (playMenuItem != null) {
      playMenuItem.setVisible(!isRecording);
    }
    if (shareMenuItem != null) {
      shareMenuItem.setVisible(!isRecording);
    }
    if (sendGoogleMenuItem != null) {
      sendGoogleMenuItem.setVisible(!isRecording);
    }
    if (saveMenuItem != null) {
      saveMenuItem.setVisible(!isRecording);
    }
    if (voiceFrequencyMenuItem != null) {
      voiceFrequencyMenuItem.setVisible(isRecording);
    }
    if (splitFrequencyMenuItem != null) {
      splitFrequencyMenuItem.setVisible(isRecording);
    }

    String title;
    if (isRecording) {
      title = getString(isPaused ? R.string.generic_paused : R.string.generic_recording);
    } else {
      Track track = getProviderUtils().getTrack(trackId);
      title = track != null ? track.getName() : getString(R.string.my_tracks_app_name);
    }
    setTitle(title);
  }

  /**
   * Starts the {@link SaveActivity} to save a track.
   * 
   * @param trackFileFormat the track file format
   */
  private void startSaveActivity(TrackFileFormat trackFileFormat) {
    AnalyticsUtils.sendPageViews(this, "/action/save");
    Intent intent = IntentUtils.newIntent(this, SaveActivity.class)
        .putExtra(SaveActivity.EXTRA_TRACK_ID, trackId)
        .putExtra(SaveActivity.EXTRA_TRACK_FILE_FORMAT, (Parcelable) trackFileFormat);
    startActivity(intent);
  }

  /**
   * Returns true if Google Earth app is installed.
   */
  private boolean isEarthInstalled() {
    List<ResolveInfo> infos = getPackageManager().queryIntentActivities(
        new Intent().setType(SaveActivity.GOOGLE_EARTH_KML_MIME_TYPE),
        PackageManager.MATCH_DEFAULT_ONLY);
    for (ResolveInfo info : infos) {
      if (info.activityInfo != null && info.activityInfo.packageName != null
          && info.activityInfo.packageName.equals(SaveActivity.GOOGLE_EARTH_PACKAGE)) {
        return true;
      }
    }
    return false;
  }

  private final BroadcastReceiver turboServiceReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (action.equals(TURBO_SERVICE_COURSE_STARTED_ACTION)) {
        synchronized(bindChangedCallback) {
          courseLoaded = true;
        }
        resume();
      } 
      else if (action.equals(TURBO_SERVICE_EXCEPTION_THROWN_ACTION)) {
        Log.v(TAG, "recieved turbo service exception");
        DeleteOneTrackDialogFragment.newInstance(trackId, useCourseProivder,R.string.track_detail_delete_after_turbo_exception).show(
            getSupportFragmentManager(), DeleteOneTrackDialogFragment.DELETE_ONE_TRACK_DIALOG_TAG);
      }
    }
  };
  
  private static String TURBO_SERVICE_EXCEPTION_THROWN_ACTION;

  private static String TURBO_SERVICE_COURSE_STARTED_ACTION;

  public void registerTurboServiceReceiver() {
    if (isCourseMode()) {
      TURBO_SERVICE_COURSE_STARTED_ACTION = this
          .getString(R.string.turbo_service_action_course_start);
      TURBO_SERVICE_EXCEPTION_THROWN_ACTION = this
          .getString(R.string.turbo_service_action_exception_thrown);
      IntentFilter filter = new IntentFilter();
      filter.addAction(TURBO_SERVICE_COURSE_STARTED_ACTION);
      filter.addAction(TURBO_SERVICE_EXCEPTION_THROWN_ACTION);
      registerReceiver(turboServiceReceiver, filter);
    }
  }

  public void unregisterTurboServiceReceiver() {
    if (isCourseMode()) {
      unregisterReceiver(turboServiceReceiver);
    }
  }
}