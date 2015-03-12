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

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

import org.cowboycoders.cyclisimo.R;

/**
 * An activity for accessing turbo trainer settings.
 * 
 * @author Will Szumski
 */
public class TurboSettingsActivity extends AbstractSettingsActivity {
  
  private static String TAG = TurboSettingsActivity.class.getSimpleName();

  private EditTextPreference scaleFactor;

  @SuppressWarnings("deprecation")
  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    addPreferencesFromResource(R.xml.turbotrainer_generic_settings);

    scaleFactor = (EditTextPreference) findPreference(getString(R.string.settings_turbotrainer_generic_scale_factor_key));
    scaleFactor.setSummary(getString(R.string.settings_turbotrainer_generic_scale_factor_summary));
    scaleFactor.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        Context context = TurboSettingsActivity.this;
        SharedPreferences preferences = PreferenceManager
            .getDefaultSharedPreferences(TurboSettingsActivity.this);
        
        Float newFloat;
        try {
          newFloat = Float.valueOf((String) newValue);
        } catch (Exception e) {
          Log.e(TAG,"unable to parse value : using default");
          newFloat = 1.0f;
        }
        preferences.edit().putFloat(context.getString(R.string.settings_turbotrainer_generic_scale_factor_key), newFloat).apply();
        Log.d(TAG, "new scalefactor: " + newFloat);
        return true;
        //PreferencesUtils.set(Double) newValue
      }
    });
    
    scaleFactor.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean invalid = false;
        
        Float newFloat;
        try {
          newFloat = Float.valueOf((String) newValue);
          if (newFloat < 0. || newFloat > 1.) invalid = true;
        } catch (Exception e) {
          newFloat = null;
        }
        
        if (newFloat == null || invalid) {
          final AlertDialog.Builder builder = new AlertDialog.Builder(TurboSettingsActivity.this);
          builder.setTitle(R.string.settings_turbo_scale_factor_invalid);
          builder.setMessage(R.string.settings_turbo_scale_factor_help);
          builder.setPositiveButton(android.R.string.ok, null);
          builder.show();
          return false;
        }
        
        return true;
      }});
      
    }
  
}



