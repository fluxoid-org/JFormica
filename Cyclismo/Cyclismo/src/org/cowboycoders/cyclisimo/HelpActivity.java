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

import org.cowboycoders.cyclisimo.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import org.cowboycoders.cyclisimo.fragments.AboutDialogFragment;
import org.cowboycoders.cyclisimo.util.StringUtils;

/**
 * An activity that displays the help page.
 * 
 * @author Sandor Dornbush
 */
public class HelpActivity extends AbstractMyTracksActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    TextView mapsPublicUnlisted = (TextView) findViewById(R.id.help_maps_public_unlisted_answer);
    mapsPublicUnlisted.setText(StringUtils.getHtml(
        this, R.string.help_maps_public_unlisted_answer, R.string.maps_public_unlisted_url));

    TextView sendTrack = (TextView) findViewById(R.id.help_send_track_answer);
    sendTrack.setText(StringUtils.getHtml(
        this, R.string.help_send_track_answer, R.string.send_google_maps_url,
        R.string.send_google_fusion_tables_url, R.string.send_google_docs_url));

    findViewById(R.id.help_ok).setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        finish();
      }
    });
    findViewById(R.id.help_about).setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        new AboutDialogFragment().show(
            getSupportFragmentManager(), AboutDialogFragment.ABOUT_DIALOG_TAG);
      }
    });
  }

  @Override
  protected int getLayoutResId() {
    return R.layout.help;
  }
}
