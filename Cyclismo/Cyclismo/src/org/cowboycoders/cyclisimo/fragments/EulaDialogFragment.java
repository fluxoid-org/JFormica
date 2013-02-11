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

package org.cowboycoders.cyclisimo.fragments;

import org.cowboycoders.cyclisimo.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import java.util.Locale;

import org.cowboycoders.cyclisimo.TrackListActivity;
import org.cowboycoders.cyclisimo.util.EulaUtils;
import org.cowboycoders.cyclisimo.util.PreferencesUtils;

/**
 * A DialogFragment to show EULA.
 * 
 * @author Jimmy Shih
 */
public class EulaDialogFragment extends DialogFragment {

  public static final String EULA_DIALOG_TAG = "eulaDialog";
  private static final String KEY_HAS_ACCEPTED = "hasAccepted";
  private static final String GOOGLE_URL = "m.google.com";
  private static final String KOREAN = "ko";

  /**
   * Creates a new instance of {@link EulaDialogFragment}.
   * 
   * @param hasAccepted true if the user has accepted the eula.
   */
  public static EulaDialogFragment newInstance(boolean hasAccepted) {
    Bundle bundle = new Bundle();
    bundle.putBoolean(KEY_HAS_ACCEPTED, hasAccepted);

    EulaDialogFragment eulaDialogFragment = new EulaDialogFragment();
    eulaDialogFragment.setArguments(bundle);
    return eulaDialogFragment;
  }

  private FragmentActivity activity;
  
  @Override
  public void onCancel(DialogInterface arg0) {
    if (!getArguments().getBoolean(KEY_HAS_ACCEPTED)) {
      exitApp();
    }
  }
  
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    activity = getActivity();
    
    boolean hasAccepted = getArguments().getBoolean(KEY_HAS_ACCEPTED);
    AlertDialog.Builder builder = new AlertDialog.Builder(activity)
        .setMessage(getEulaText())
        .setTitle(R.string.eula_title);

    if (hasAccepted) {
      builder.setPositiveButton(R.string.generic_ok, null);
    } else {
      builder.setNegativeButton(R.string.eula_decline, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          exitApp();
        }
      })
      .setPositiveButton(R.string.eula_accept, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          EulaUtils.setAcceptEula(activity);
          PreferencesUtils.setBoolean(
              activity, R.string.metric_units_key, !Locale.US.equals(Locale.getDefault()));
          TrackListActivity trackListActivity = (TrackListActivity) activity;
          trackListActivity.showStartupDialogs();
        }
      });
    }
    return builder.create();
  }

  /**
   * Exits the application.
   */
  private void exitApp() {
    activity.finish();
  }

  /**
   * Gets the EULA text.
   * 
   */
  private String getEulaText() {
    String tos = getString(R.string.eula_date) 
        + "\n\n"
        + getString(R.string.eula_body, GOOGLE_URL) 
        + "\n\n" 
        + getString(R.string.eula_footer, GOOGLE_URL) 
        + "\n\n" 
        + getString(R.string.eula_copyright_year);
    boolean isKorean = getResources().getConfiguration().locale.getLanguage().equals(KOREAN);
    if (isKorean) {
      tos += "\n\n" + getString(R.string.eula_korean);
    }
    return tos;
  }
}