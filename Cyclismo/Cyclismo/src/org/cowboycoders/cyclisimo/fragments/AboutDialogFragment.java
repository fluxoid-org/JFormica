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
import android.view.View;
import android.widget.TextView;

import org.cowboycoders.cyclisimo.util.SystemUtils;

/**
 * A DialogFragment to show information about My Tracks.
 * 
 * @author Jimmy Shih
 */
public class AboutDialogFragment extends DialogFragment {

  public static final String ABOUT_DIALOG_TAG = "aboutDialog";

  private FragmentActivity activity;
  
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    activity = getActivity();
    View view = activity.getLayoutInflater().inflate(R.layout.about, null);
    TextView aboutVersion = (TextView) view.findViewById(R.id.about_version);
    aboutVersion.setText(SystemUtils.getMyTracksVersion(activity));
    return new AlertDialog.Builder(activity)
        .setNegativeButton(R.string.about_license, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            EulaDialogFragment.newInstance(true).show(
                activity.getSupportFragmentManager(), EulaDialogFragment.EULA_DIALOG_TAG);
          }
        })
        .setPositiveButton(R.string.generic_ok, null)
        .setTitle(R.string.help_about)
        .setView(view)
        .create();
  }
}