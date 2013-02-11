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

package org.cowboycoders.cyclisimo.settings;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;

import org.cowboycoders.cyclisimo.R;
import org.cowboycoders.cyclisimo.util.DialogUtils;

/**
 * An activity for accessing the sharing settings.
 * 
 * @author Jimmy Shih
 */
public class AntSettingsActivity extends AbstractSettingsActivity {

  private static final int DIALOG_CONFIRM_ALLOW_LOGGING_ID = 0;

  private CheckBoxPreference antLoggingCheckBoxPreference;

  @SuppressWarnings("deprecation")
  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    addPreferencesFromResource(R.xml.ant_settings);

    antLoggingCheckBoxPreference = (CheckBoxPreference) findPreference(
        getString(R.string.settings_ant_diagnostic_logging_state_key));
    antLoggingCheckBoxPreference.setSummaryOn(getString(
        R.string.settings_ant_diagnostic_logging_summary_on));
    antLoggingCheckBoxPreference.setSummaryOff(getString(
        R.string.settings_ant_diagnostic_logging_summary_off));
    antLoggingCheckBoxPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
        @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        if ((Boolean) newValue) {
          showDialog(DIALOG_CONFIRM_ALLOW_LOGGING_ID);
          return false;
        } else {
          return true;
        }
      }
    });
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    if (id != DIALOG_CONFIRM_ALLOW_LOGGING_ID) {
      return null;
    }
    return DialogUtils.createConfirmationDialog(this,
        R.string.settings_ant_allow_logging_confirm_message,
        new DialogInterface.OnClickListener() {
            @Override
          public void onClick(DialogInterface dialog, int button) {
              antLoggingCheckBoxPreference.setChecked(true);
          }
        });
  }
}
