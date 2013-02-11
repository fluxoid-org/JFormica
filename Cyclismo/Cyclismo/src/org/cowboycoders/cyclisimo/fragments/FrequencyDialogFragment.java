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

import org.cowboycoders.cyclisimo.util.PreferencesUtils;
import org.cowboycoders.cyclisimo.util.StringUtils;

/**
 * A DialogFragment to configure frequency.
 * 
 * @author Jimmy Shih
 */
public class FrequencyDialogFragment extends DialogFragment {

  public static final String FREQUENCY_DIALOG_TAG = "frequencyDialog";

  private static final String KEY_PREFERENCE_ID = "preferenceId";
  private static final String KEY_DEFAULT_VALUE = "defaultValue";
  private static final String KEY_TITLE_ID = "titleId";

  public static FrequencyDialogFragment newInstance(
      int preferenceId, int defaultValue, int titleId) {
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_PREFERENCE_ID, preferenceId);
    bundle.putInt(KEY_DEFAULT_VALUE, defaultValue);
    bundle.putInt(KEY_TITLE_ID, titleId);

    FrequencyDialogFragment frequencyDialogFragment = new FrequencyDialogFragment();
    frequencyDialogFragment.setArguments(bundle);
    return frequencyDialogFragment;
  }

  private FragmentActivity activity; 
  
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    activity = getActivity();
    
    final int preferenceId = getArguments().getInt(KEY_PREFERENCE_ID);
    int defaultValue = getArguments().getInt(KEY_DEFAULT_VALUE);
    int titleId = getArguments().getInt(KEY_TITLE_ID);
    int frequencyValue = PreferencesUtils.getInt(activity, preferenceId, defaultValue);

    return new AlertDialog.Builder(activity)
        .setPositiveButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            int listIndex = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
            PreferencesUtils.setInt(activity, preferenceId, getFrequencyValue(listIndex));
          }
        })
        .setSingleChoiceItems(getFrequencyDisplayOptions(), getListIndex(frequencyValue), null)
        .setTitle(titleId)
        .create();
  }

  /**
   * Gets the frequency display options.
   */
  private String[] getFrequencyDisplayOptions() {
    boolean metricUnits = PreferencesUtils.getBoolean(
        activity, R.string.metric_units_key, PreferencesUtils.METRIC_UNITS_DEFAULT);
    return StringUtils.getFrequencyOptions(activity, metricUnits);
  }

  /**
   * Gets the list index for a frequency value. Returns 0 if the value is not on
   * the list.
   */
  private int getListIndex(int frequencyValue) {
    String[] values = getResources().getStringArray(R.array.frequency_values);
    for (int i = 0; i < values.length; i++) {
      if (frequencyValue == Integer.parseInt(values[i])) {
        return i;
      }
    }
    return 0;
  }

  /**
   * Gets the frequency value from a list index.
   * 
   * @param listIndex the list index
   */
  private int getFrequencyValue(int listIndex) {
    String[] values = getResources().getStringArray(R.array.frequency_values);
    return Integer.parseInt(values[listIndex]);
  }
}