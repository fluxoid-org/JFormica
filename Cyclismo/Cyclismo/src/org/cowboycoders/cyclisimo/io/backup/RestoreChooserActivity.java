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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import org.cowboycoders.cyclisimo.util.FileUtils;
import org.cowboycoders.cyclisimo.util.IntentUtils;
import org.cowboycoders.cyclisimo.util.StringUtils;

/**
 * An activity to choose a date to restore from.
 *
 * @author Jimmy Shih
 */
public class RestoreChooserActivity extends Activity {

  private static final Comparator<Date> REVERSE_DATE_ORDER = new Comparator<Date>() {
    @Override
    public int compare(Date s1, Date s2) {
      return s2.compareTo(s1);
    }
  };
  private static final int DIALOG_CHOOSER_ID = 0;

  private Date[] backupDates;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ExternalFileBackup externalFileBackup = new ExternalFileBackup(this);

    // Get the list of existing backups
    if (!FileUtils.isSdCardAvailable()) {
      Toast.makeText(this, R.string.external_storage_error_no_storage, Toast.LENGTH_LONG).show();
      finish();
      return;
    }

    if (!externalFileBackup.isBackupsDirectoryAvailable(false)) {
      Toast.makeText(this, R.string.settings_backup_restore_no_backup, Toast.LENGTH_LONG).show();
      finish();
      return;
    }

    backupDates = externalFileBackup.getAvailableBackups();
    if (backupDates == null || backupDates.length == 0) {
      Toast.makeText(this, R.string.settings_backup_restore_no_backup, Toast.LENGTH_LONG).show();
      finish();
      return;
    }

    if (backupDates.length == 1) {
      Intent intent = IntentUtils.newIntent(this, RestoreActivity.class)
          .putExtra(RestoreActivity.EXTRA_DATE, backupDates[0].getTime());
      startActivity(intent);
      finish();
      return;
    }

    Arrays.sort(backupDates, REVERSE_DATE_ORDER);
    showDialog(DIALOG_CHOOSER_ID);
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    if (id != DIALOG_CHOOSER_ID) {
      return null;
    }
    String items[] = new String[backupDates.length];
    for (int i = 0; i < backupDates.length; i++) {
      items[i] = StringUtils.formatDateTime(this, backupDates[i].getTime());
    }
    return new AlertDialog.Builder(this)
        .setCancelable(true)
        .setItems(items, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Intent intent = IntentUtils.newIntent(
                RestoreChooserActivity.this, RestoreActivity.class)
                .putExtra(RestoreActivity.EXTRA_DATE, backupDates[which].getTime());
            startActivity(intent);
            finish();
          }
        })
        .setOnCancelListener(new DialogInterface.OnCancelListener() {
          @Override
          public void onCancel(DialogInterface dialog) {
            finish();
          }
        })
        .setTitle(R.string.settings_backup_restore_select_title)
        .create();
  }
}
