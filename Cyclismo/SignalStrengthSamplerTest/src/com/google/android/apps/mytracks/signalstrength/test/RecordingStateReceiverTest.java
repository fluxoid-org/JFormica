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
 * Copyright 2010 Google Inc.
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
package org.cowboycoders.cyclisimo.signalstrength.test;

import org.cowboycoders.cyclisimo.signalstrength.R;
import org.cowboycoders.cyclisimo.signalstrength.RecordingStateReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

/**
 * Tests for {@link RecordingStateReceiver}.
 *
 * @author Rodrigo Damazio
 */
public class RecordingStateReceiverTest extends AndroidTestCase {

  private RecordingStateReceiver receiver;
  private RenamingDelegatingContext context;
  private SharedPreferences preferences;
  private int serviceStopCalls;
  private int serviceStartCalls;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    context = new RenamingDelegatingContext(getContext(), "test.");
    setContext(context);

    receiver = new RecordingStateReceiver() {
      @Override
      protected void stopService(Context ctx) {
        serviceStopCalls++;
      }

      @Override
      protected void startService(Context ctx) {
        serviceStartCalls++;
      }
    };

    preferences = PreferenceManager.getDefaultSharedPreferences(context);
    preferences
        .edit()
        .putBoolean(context.getString(R.string.settings_auto_start_key), true)
        .putBoolean(context.getString(R.string.settings_auto_stop_key), true)
        .commit();
  }

  public void testOnReceive_start() {
    receiver.onReceive(context,
        new Intent(context.getString(R.string.track_started_broadcast_action)));
    assertServiceCalls(1, 0);
  }

  public void testOnReceive_stop() {
    receiver.onReceive(context,
        new Intent(context.getString(R.string.track_stopped_broadcast_action)));
    assertServiceCalls(0, 1);
  }

  public void testOnReceive_noAutoStart() {
    preferences
        .edit()
        .putBoolean(context.getString(R.string.settings_auto_start_key), false)
        .commit();
    receiver.onReceive(context,
        new Intent(context.getString(R.string.track_started_broadcast_action)));
    assertServiceCalls(0, 0);
  }

  public void testOnReceive_noAutoStop() {
    preferences
        .edit()
        .putBoolean(context.getString(R.string.settings_auto_stop_key), false)
        .commit();
    receiver.onReceive(context,
        new Intent(context.getString(R.string.track_stopped_broadcast_action)));
    assertServiceCalls(0, 0);
  }

  public void testOnReceive_other() {
    receiver.onReceive(context, new Intent("bla"));
    assertServiceCalls(0, 0);
  }

  private void assertServiceCalls(int startCalls, int stopCalls) {
    assertEquals(startCalls, serviceStartCalls);
    assertEquals(stopCalls, serviceStopCalls);
  }
}
