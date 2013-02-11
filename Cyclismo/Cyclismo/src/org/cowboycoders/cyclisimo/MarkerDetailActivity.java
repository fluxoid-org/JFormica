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
 * Copyright 2009 Google Inc.
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

import org.cowboycoders.cyclisimo.content.MyTracksProviderUtils;
import org.cowboycoders.cyclisimo.content.Waypoint;
import org.cowboycoders.cyclisimo.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.cowboycoders.cyclisimo.fragments.DeleteOneMarkerDialogFragment;
import org.cowboycoders.cyclisimo.util.IntentUtils;
import org.cowboycoders.cyclisimo.util.StatsUtils;

/**
 * An activity to display marker detail info.
 *
 * @author Leif Hendrik Wilden
 */
public class MarkerDetailActivity extends AbstractMyTracksActivity {

  public static final String EXTRA_MARKER_ID = "marker_id";
  private static final String TAG = MarkerDetailActivity.class.getSimpleName();

  private long markerId;
  private Waypoint waypoint;

  private TextView name;
  private View waypointSection;
  private View statisticsSection;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);

    markerId = getIntent().getLongExtra(EXTRA_MARKER_ID, -1L);
    if (markerId == -1L) {
      Log.d(TAG, "invalid marker id");
      finish();
      return;
    }
    name = (TextView) findViewById(R.id.marker_detail_name);
    waypointSection = findViewById(R.id.marker_detail_waypoint_section);
    statisticsSection = findViewById(R.id.marker_detail_statistics_section);
  }

  @Override
  protected int getLayoutResId() {
    return R.layout.marker_detail;
  }

  @Override
  protected void onResume() {
    super.onResume();
    waypoint = MyTracksProviderUtils.Factory.get(this).getWaypoint(markerId);
    if (waypoint == null) {
      Log.d(TAG, "waypoint is null");
      finish();
      return;
    }
    name.setText(getString(R.string.generic_name_line, waypoint.getName()));
    if (waypoint.getType() == Waypoint.TYPE_WAYPOINT) {
      waypointSection.setVisibility(View.VISIBLE);
      statisticsSection.setVisibility(View.GONE);

      TextView markerType = (TextView) findViewById(R.id.marker_detail_waypoint_marker_type);
      markerType.setText(getString(
          R.string.marker_detail_waypoint_marker_type, waypoint.getCategory()));
      TextView description = (TextView) findViewById(R.id.marker_detail_waypoint_description);
      description.setText(getString(R.string.generic_description_line, waypoint.getDescription()));
    } else {
      waypointSection.setVisibility(View.GONE);
      statisticsSection.setVisibility(View.VISIBLE);
      StatsUtils.setTripStatisticsValues(this, waypoint.getTripStatistics());
      StatsUtils.setLocationValues(this, waypoint.getLocation(), false);
    }
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.marker_detail, menu);
    return true;
  }

  @Override
  protected void onHomeSelected() {
    Intent intent = IntentUtils.newIntent(this, MarkerListActivity.class)
        .putExtra(MarkerListActivity.EXTRA_TRACK_ID, waypoint.getTrackId());
    startActivity(intent);
    finish();
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Intent intent;
    switch (item.getItemId()) {
      case R.id.marker_detail_show_on_map:
        intent = IntentUtils.newIntent(this, TrackDetailActivity.class)
            .putExtra(TrackDetailActivity.EXTRA_MARKER_ID, markerId);
        startActivity(intent);
        return true;
      case R.id.marker_detail_edit:
        intent = IntentUtils.newIntent(this, MarkerEditActivity.class)
            .putExtra(MarkerEditActivity.EXTRA_MARKER_ID, markerId);
        startActivity(intent);
        return true;
      case R.id.marker_detail_delete:
        DeleteOneMarkerDialogFragment.newInstance(markerId, waypoint.getTrackId()).show(
            getSupportFragmentManager(),
            DeleteOneMarkerDialogFragment.DELETE_ONE_MARKER_DIALOG_TAG);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
