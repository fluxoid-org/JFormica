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

package org.cowboycoders.cyclisimo.io.backup;

import org.cowboycoders.cyclisimo.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

import org.cowboycoders.cyclisimo.TrackListActivity;
import org.cowboycoders.cyclisimo.util.DialogUtils;
import org.cowboycoders.cyclisimo.util.IntentUtils;

/**
 * An activity to restore data from the SD card.
 * 
 * @author Jimmy Shih
 */
public class RestoreActivity extends Activity {

  public static final String EXTRA_DATE = "date";
  
  private static final String TAG = RestoreActivity.class.getSimpleName();
  private static final int DIALOG_PROGRESS_ID = 0;
  
  private RestoreAsyncTask restoreAsyncTask;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Object retained = getLastNonConfigurationInstance();
    if (retained instanceof RestoreAsyncTask) {
      restoreAsyncTask = (RestoreAsyncTask) retained;
      restoreAsyncTask.setActivity(this);
    } else {
      long date = getIntent().getLongExtra(EXTRA_DATE, -1L);
      if (date == -1L) {
        Log.d(TAG, "Invalid date");
        finish();
        return;
      }
      restoreAsyncTask = new RestoreAsyncTask(this, new Date(date));
      restoreAsyncTask.execute();
    }
  }

  @Override
  public Object onRetainNonConfigurationInstance() {
    restoreAsyncTask.setActivity(null);
    return restoreAsyncTask;
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    if (id != DIALOG_PROGRESS_ID) {
      return null;
    }
    return DialogUtils.createSpinnerProgressDialog(this,
        R.string.settings_backup_restore_progress_message, new DialogInterface.OnCancelListener() {
          @Override
          public void onCancel(DialogInterface dialog) {
            finish();
          }
        });
  }

  /**
   * Invokes when the associated AsyncTask completes.
   *
   * @param success true if the AsyncTask is successful
   * @param messageId message id to display to user
   */
  public void onAsyncTaskCompleted(boolean success, int messageId) {
    removeDialog(DIALOG_PROGRESS_ID);
    Toast.makeText(this, messageId, success ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    Intent intent = IntentUtils.newIntent(this, TrackListActivity.class);
    startActivity(intent);
  }

  /**
   * Shows the progress dialog.
   */
  public void showProgressDialog() {
    showDialog(DIALOG_PROGRESS_ID);
  }
}
