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
 * Copyright 2008 Google Inc.
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
package org.cowboycoders.cyclisimo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Utilities for EULA.
 * 
 * @author Jimmy Shih
 */
public class EulaUtils {

  private static final String EULA_PREFERENCE_FILE = "eula";

  // Accepting Google mobile terms of service
  private static final String ACCEPT_EULA_PREFERENCE_KEY = "eula.google_mobile_tos_accepted";
  private static final String SHOW_WELCOME_PREFERENCE_KEY = "showWelcome";
  
  private EulaUtils() {}

  public static boolean getAcceptEula(Context context) {
    return true;
    //return getValue(context, ACCEPT_EULA_PREFERENCE_KEY, false);
  }

  public static void setAcceptEula(Context context) {
    setValue(context, ACCEPT_EULA_PREFERENCE_KEY, true);
  }

  public static boolean getShowWelcome(Context context) {
      return false;
    //return getValue(context, SHOW_WELCOME_PREFERENCE_KEY, true);
  }

  public static void setShowWelcome(Context context) {
    setValue(context, SHOW_WELCOME_PREFERENCE_KEY, false);
  }
 
  private static boolean getValue(Context context, String key, boolean defaultValue) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(
        EULA_PREFERENCE_FILE, Context.MODE_PRIVATE);
    return sharedPreferences.getBoolean(key, defaultValue);
  }

  private static void setValue(Context context, String key, boolean value) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(
        EULA_PREFERENCE_FILE, Context.MODE_PRIVATE);
    Editor editor = sharedPreferences.edit();
    editor.putBoolean(key, value);
    ApiAdapterFactory.getApiAdapter().applyPreferenceChanges(editor);
  }
}
