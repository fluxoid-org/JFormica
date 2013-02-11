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
package org.cowboycoders.cyclisimo;

import static android.content.Intent.ACTION_BOOT_COMPLETED;
import static org.cowboycoders.cyclisimo.Constants.RESUME_TRACK_EXTRA_NAME;
import static org.cowboycoders.cyclisimo.Constants.TAG;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.cowboycoders.cyclisimo.services.RemoveTempFilesService;
import org.cowboycoders.cyclisimo.services.TrackRecordingService;

/**
 * This class handles MyTracks related broadcast messages.
 *
 * One example of a broadcast message that this class is interested in,
 * is notification about the phone boot.  We may want to resume a previously
 * started tracking session if the phone crashed (hopefully not), or the user
 * decided to swap the battery or some external event occurred which forced
 * a phone reboot.
 *
 * This class simply delegates to {@link TrackRecordingService} to make a
 * decision whether to continue with the previous track (if any), or just
 * abandon it.
 * 
 * @author Bartlomiej Niechwiej
 */
public class BootReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d(TAG, "BootReceiver.onReceive: " + intent.getAction());
    if (ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
      Intent startIntent = new Intent(context, TrackRecordingService.class)
          .putExtra(RESUME_TRACK_EXTRA_NAME, true);
      context.startService(startIntent);

      Intent removeTempFilesIntent = new Intent(context, RemoveTempFilesService.class);
      context.startService(removeTempFilesIntent);
    } else {
      Log.w(TAG, "BootReceiver: unsupported action");
    }
  }
}
