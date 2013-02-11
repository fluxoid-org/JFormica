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

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.Date;

/**
 * AsyncTask to restore data from the SD card.
 *
 * @author Jimmy Shih
 */
public class RestoreAsyncTask extends AsyncTask<Void, Integer, Boolean> {

  private static final String TAG = RestoreAsyncTask.class.getSimpleName();

  private RestoreActivity restoreActivity;
  private final Date date;
  private final ExternalFileBackup externalFileBackup;

  // true if the AsyncTask result is success
  private boolean success;

  // true if the AsyncTask has completed
  private boolean completed;

  // message id to return to the activity
  private int messageId;

  /**
   * Creates an AsyncTask.
   *
   * @param restoreActivity the activity currently associated with this
   *          AsyncTask
   * @param date the date to retore from
   */
  public RestoreAsyncTask(RestoreActivity restoreActivity, Date date) {
    this.restoreActivity = restoreActivity;
    this.date = date;
    this.externalFileBackup = new ExternalFileBackup(restoreActivity);
    success = false;
    completed = false;
    messageId = R.string.external_storage_import_error;
  }

  /**
   * Sets the current {@link RestoreActivity} associated with this AyncTask.
   *
   * @param activity the current {@link RestoreActivity}, can be null
   */
  public void setActivity(RestoreActivity activity) {
    this.restoreActivity = activity;
    if (completed && restoreActivity != null) {
      restoreActivity.onAsyncTaskCompleted(success, messageId);
    }
  }

  @Override
  protected void onPreExecute() {
    if (restoreActivity != null) {
      restoreActivity.showProgressDialog();
    }
  }

  @Override
  protected Boolean doInBackground(Void... params) {
    try {
      externalFileBackup.restoreFromDate(date);
      messageId = R.string.external_storage_import_success;
      return true;
    } catch (IOException e) {
      Log.d(TAG, "IO exception", e);
      return false;
    }
  }

  @Override
  protected void onPostExecute(Boolean result) {
    success = result;
    completed = true;
    if (restoreActivity != null) {
      restoreActivity.onAsyncTaskCompleted(success, messageId);
    }
  }
}
