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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.cowboycoders.cyclisimo.util.PreferencesUtils;

/**
 * An activity displaying a list of turbo trainers.
 * 
 * @author Will Szumski
 */
@Deprecated // use preferences instead
public class TurboTrainerListActivity extends FragmentActivity {

  public static final String TAG = TurboTrainerListActivity.class.getSimpleName();


  // protected static final int COURSE_SETUP_RESPONSE_CODE = 0;

  /*
   * Note that sharedPreferenceChangeListenr cannot be an anonymous inner class.
   * Anonymous inner class will get garbage collected.
   */
  private final OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
      if (key == null
          || key.equals(PreferencesUtils.getKey(TurboTrainerListActivity.this,
              R.string.recording_track_id_key))) {
        recordingTrackId = PreferencesUtils.getLong(TurboTrainerListActivity.this,
            R.string.recording_track_id_key);
        
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            boolean isRecording = recordingTrackId != PreferencesUtils.RECORDING_TRACK_ID_DEFAULT;
            updateUi(isRecording);
            resourceCursorAdapter.notifyDataSetChanged();
          }
        });
      }
    }
  };

  private ListView listView;
  private ArrayAdapter<String> resourceCursorAdapter;

  private long recordingTrackId = PreferencesUtils.RECORDING_TRACK_ID_DEFAULT;


  private Handler mHandler;

  private SharedPreferences sharedPreferences;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.generic_list_select);
    
    mHandler = new Handler();

    sharedPreferences = getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);

    listView = (ListView) findViewById(R.id.generic_list);

    this.setTitle(this.getString(R.string.turbotrainer_select_title));
    
    LinearLayout bottomButtons = (LinearLayout) this.findViewById(R.id.generic_list_buttons_container);
    bottomButtons.setVisibility(View.GONE);
    
    Resources res = getResources();
    String[] trainers = res.getStringArray(R.array.turbotrainer_opions); 
    
    resourceCursorAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,trainers);

    listView.setAdapter(resourceCursorAdapter);
    
    
  }



  private void doFinish() {
    TurboTrainerListActivity.this.finish();
  }

  

  @Override
  protected void onStart() {
    super.onStart();

    // Register shared preferences listener
    sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

    // Update shared preferences
    sharedPreferenceChangeListener.onSharedPreferenceChanged(null, null);

  }

  @Override
  protected void onResume() {
    super.onResume();

    // Update UI
    boolean isRecording = recordingTrackId != PreferencesUtils.RECORDING_TRACK_ID_DEFAULT;
    updateUi(isRecording);
    resourceCursorAdapter.notifyDataSetChanged();
  }

  @Override
  protected void onPause() {
    super.onPause();

  }

  @Override
  protected void onStop() {
    super.onStop();

    // Unregister shared preferences listener
    sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  
  private boolean isRecording() {
    return recordingTrackId != PreferencesUtils.RECORDING_TRACK_ID_DEFAULT;
  }


  /**
   * Updates the menu items.
   * 
   * @param isRecording true if recording
   */
  private void updateUi(boolean isRecording) {
    
  }



}
