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

import org.cowboycoders.cyclisimo.content.MyTracksCourseProviderUtils;
import org.cowboycoders.cyclisimo.content.MyTracksProviderUtils;
import org.cowboycoders.cyclisimo.content.Track;
import org.cowboycoders.cyclisimo.R;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.cowboycoders.cyclisimo.util.TrackIconUtils;
import org.cowboycoders.cyclisimo.util.TrackNameUtils;

/**
 * An activity that let's the user see and edit the user editable track meta
 * data such as track name, activity type, and track description.
 * 
 * @author Leif Hendrik Wilden
 */
public class TrackEditActivity extends AbstractMyTracksActivity {

  public static final String EXTRA_TRACK_ID = "track_id";
  public static final String EXTRA_NEW_TRACK = "new_track";
  public static final String EXTRA_USE_COURSE_PROVIDER = "use_course_provider";

  private static final String TAG = TrackEditActivity.class.getSimpleName();

  private Long trackId;
  private MyTracksProviderUtils myTracksProviderUtils;
  private Track track;
  private boolean useCourseProvider = false;

  private EditText name;
  private TextView activityTypeLabel;
  private AutoCompleteTextView activityType;
  private EditText description;
  
  

  private MyTracksProviderUtils getProviderUtils() {
    if (this.useCourseProvider) {
      return new MyTracksCourseProviderUtils(this.getContentResolver());
    } else {
      return MyTracksProviderUtils.Factory.get(this);
    }
  }
  
  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);

    trackId = getIntent().getLongExtra(EXTRA_TRACK_ID, -1L);
    useCourseProvider = getIntent().getBooleanExtra(EXTRA_USE_COURSE_PROVIDER, false);
    if (trackId == -1L) {
      Log.e(TAG, "invalid trackId");
      finish();
      return;
    }

    myTracksProviderUtils = getProviderUtils();
    track = myTracksProviderUtils.getTrack(trackId);
    if (track == null) {
      Log.e(TAG, "No track for " + trackId);
      finish();
      return;
    }
    

    name = (EditText) findViewById(R.id.track_edit_name);
    name.setText(track.getName());

    activityType = (AutoCompleteTextView) findViewById(R.id.track_edit_activity_type);
    activityType.setText(track.getCategory());

    activityTypeLabel = (TextView) findViewById(R.id.track_edit_activity_type_label);
    setActivityTypeIcon(track.getIcon());
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
        this, R.array.activity_types, android.R.layout.simple_dropdown_item_1line);
    activityType.setAdapter(adapter);
    activityType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String iconValue = TrackIconUtils.getIconValue(
            TrackEditActivity.this, (String) activityType.getAdapter().getItem(position));
        setActivityTypeIcon(iconValue);
      }
    });
    activityType.setOnFocusChangeListener(new View.OnFocusChangeListener() {

        @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
          String iconValue = TrackIconUtils.getIconValue(
              TrackEditActivity.this, activityType.getText().toString());
          setActivityTypeIcon(iconValue);
        }

      }
    });
    description = (EditText) findViewById(R.id.track_edit_description);
    description.setText(track.getDescription());

    Button save = (Button) findViewById(R.id.track_edit_save);
    save.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        track.setName(name.getText().toString());
        String category = activityType.getText().toString();
        track.setCategory(category);
        track.setIcon(TrackIconUtils.getIconValue(TrackEditActivity.this, category));
        track.setDescription(description.getText().toString());
        myTracksProviderUtils.updateTrack(track);
        finish();
      }
    });

    Button cancel = (Button) findViewById(R.id.track_edit_cancel);
    if (getIntent().getBooleanExtra(EXTRA_NEW_TRACK, false)) {
      String trackName = TrackNameUtils.getTrackName(
          this, -1L, -1L, myTracksProviderUtils.getLastValidTrackPoint(trackId));
      if (trackName != null) {
        name.setText(trackName);
      }
      setTitle(R.string.track_edit_new_track_title);
      cancel.setVisibility(View.GONE);
    } else {
      setTitle(R.string.menu_edit);
      cancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          finish();
        }
      });
      cancel.setVisibility(View.VISIBLE);
    }
  }

  @Override
  protected int getLayoutResId() {
    return R.layout.track_edit;
  }

  /**
   * Sets the activity type icon.
   * 
   * @param iconValue the icon value
   */
  private void setActivityTypeIcon(String iconValue) {
    activityTypeLabel.setCompoundDrawablesWithIntrinsicBounds(
        TrackIconUtils.getIconDrawable(iconValue), 0, 0, 0);
  }
}
