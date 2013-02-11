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

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

/**
 * A list preference which persists its values as integers instead of strings.
 * Code reading the values should use
 * {@link android.content.SharedPreferences#getInt}.
 * When using XML-declared arrays for entry values, the arrays should be regular
 * string arrays containing valid integer values.
 *
 * @author Rodrigo Damazio
 */
public class IntegerListPreference extends ListPreference {

  public IntegerListPreference(Context context) {
    super(context);

    verifyEntryValues(null);
  }

  public IntegerListPreference(Context context, AttributeSet attrs) {
    super(context, attrs);

    verifyEntryValues(null);
  }

  @Override
  public void setEntryValues(CharSequence[] entryValues) {
    CharSequence[] oldValues = getEntryValues();
    super.setEntryValues(entryValues);
    verifyEntryValues(oldValues);
  }

  @Override
  public void setEntryValues(int entryValuesResId) {
    CharSequence[] oldValues = getEntryValues();
    super.setEntryValues(entryValuesResId);
    verifyEntryValues(oldValues);
  }

  @Override
  protected String getPersistedString(String defaultReturnValue) {
    // During initial load, there's no known default value
    int defaultIntegerValue = Integer.MIN_VALUE;
    if (defaultReturnValue != null) {
      defaultIntegerValue = Integer.parseInt(defaultReturnValue);
    }

    // When the list preference asks us to read a string, instead read an
    // integer.
    int value = getPersistedInt(defaultIntegerValue);
    return Integer.toString(value);
  }

  @Override
  protected boolean persistString(String value) {
    // When asked to save a string, instead save an integer
    return persistInt(Integer.parseInt(value));
  }

  private void verifyEntryValues(CharSequence[] oldValues) {
    CharSequence[] entryValues = getEntryValues();
    if (entryValues == null) {
      return;
    }

    for (CharSequence entryValue : entryValues) {
      try {
        Integer.parseInt(entryValue.toString());
      } catch (NumberFormatException nfe) {
        super.setEntryValues(oldValues);
        throw nfe;
      }
    }
  }
}
