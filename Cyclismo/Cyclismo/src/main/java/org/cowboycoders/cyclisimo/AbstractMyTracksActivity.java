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
 * Copyright 2012 Google Inc.
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


import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;

import org.cowboycoders.cyclisimo.util.ApiAdapterFactory;

/**
 * An abstract class for all My Tracks activities.
 * 
 * @author Jimmy Shih
 */
public abstract class AbstractMyTracksActivity extends FragmentActivity {
  
  public static String TAG = "AbstractMyTracksActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setVolumeControlStream(TextToSpeech.Engine.DEFAULT_STREAM);

    // Hide title must be before setContentView
    if (hideTitle()) {
      ApiAdapterFactory.getApiAdapter().hideTitle(this);
    }

    setContentView(getLayoutResId());

    // Configure action bar must be after setContentView
    ApiAdapterFactory.getApiAdapter().configureActionBarHomeAsUp(this);
  }

  /**
   * Gets the layout resource id.
   */
  protected abstract int getLayoutResId();

  /**
   * Returns true to hide the title. Be default, do not hide the title.
   */
  protected boolean hideTitle() {
    return false;
  }

  /**
   * Callback when the home menu item is selected.
   */
  protected void onHomeSelected() {
    Log.d(TAG,"home selected: finishing");
    finish();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() != android.R.id.home) {
      return super.onOptionsItemSelected(item);
    }
    onHomeSelected();
    return true;
  }
}
