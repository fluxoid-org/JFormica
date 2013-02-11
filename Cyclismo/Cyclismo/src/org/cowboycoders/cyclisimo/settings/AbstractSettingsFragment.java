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

import org.cowboycoders.cyclisimo.R;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

import org.cowboycoders.cyclisimo.util.PreferencesUtils;
import org.cowboycoders.cyclisimo.util.PreferencesUtils.SettingsSelectionSummarizer;

/**
 * An abstract activity for all the settings activities.
 * 
 * @author Jimmy Shih
 */
public class AbstractSettingsFragment extends PreferenceFragment{

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
  }



  @Override
  public void onDestroy() {
    super.onDestroy();
  }
  
  /**
   * Configures a preference.
   * 
   * @param preference the preference
   * @param options the list of displayed options
   * @param values the list of stored values
   * @param summaryId the summary id
   * @param value the stored value
   * @param listener listener to invoke
   * @param summarizer if you don't want to use toString()
   * @return 
   */
  protected void configurePreference(final Preference preference, final String[] options,
      final String[] values, final int summaryId, Object value,
      final OnPreferenceChangeListener listener, final PreferencesUtils.SettingsSelectionSummarizer summarizer) {
    if (options != null) {
      ((ListPreference) preference).setEntries(options);
    }
    if (values != null) {
      ((ListPreference) preference).setEntryValues(values);
    }
    preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
        @Override
      public boolean onPreferenceChange(Preference pref, Object newValue) {
        updatePreferenceSummary(pref, options, values, summaryId, newValue,summarizer);
        if (listener != null) {
          listener.onPreferenceChange(pref, newValue);
        }
        return true;
      }
    });
    updatePreferenceSummary(preference, options, values, summaryId, value, summarizer);
    if (listener != null) {
      listener.onPreferenceChange(preference, value);
    }
    
    //return new UpdateSummaryCaller(preference, options,
     //  values, summaryId,
       // summarizer);
  }

  /**
   * Updates a preference when a stored value changes.
   * 
   * @param preference the preference
   * @param options the list of displayed options
   * @param values the list of stored values
   * @param summaryId the summary id
   * @param value the stored value
   * @param summarizer 
   */
  private void updatePreferenceSummary(
      Preference preference, String[] options, String[] values, int summaryId, Object value, SettingsSelectionSummarizer summarizer) {
    String summary = getString(summaryId);
    String option;
    if (options != null && values != null) {
      option = getOption(options, values, value);
      if (option == null) {
        option = getString(R.string.value_unknown);
      }
    } else {
      if (summarizer != null) {
        value = summarizer.summarize(value);
      }
      option = value != null && value.toString().length() != 0 ? value.toString() : getString(R.string.value_unknown);
    }
    summary += "\n" + option;
    preference.setSummary(summary);
    
  }

  /**
   * Gets the display option for a stored value.
   * 
   * @param options the list of the display options
   * @param values the list of the stored values
   * @param value the store value
   */
  private String getOption(String[] options, String[] values, Object value) {
    for (int i = 0; i < values.length; i++) {
      if (value.equals(values[i])) {
        return options[i];
      }
    }
    return null;
  }
  
  public class UpdateSummaryCaller {
    
    private Preference preference;
    private String[] options;
    private String[] values;
    private int summaryId ;
    private SettingsSelectionSummarizer summarizer;

    public UpdateSummaryCaller(Preference preference, String[] options, String[] values,
        int summaryId, SettingsSelectionSummarizer summarizer) {
      this.preference = preference;
      this.options = options;
      this.values = values;
      this.summaryId = summaryId;
      this.summarizer = summarizer;
    }

    public void updateSummary(Object value) {
      AbstractSettingsFragment.this.updatePreferenceSummary(preference, options, values, summaryId, value, summarizer);
    }
  }
  

  
}
