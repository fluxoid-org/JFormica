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
package org.cowboycoders.cyclisimo.util;

import android.os.Build;

/**
 * A factory to get the {@link ApiAdapter} for the current device.
 *
 * @author Rodrigo Damazio
 */
public class ApiAdapterFactory {

  private static ApiAdapter apiAdapter;

  /**
   * Gets the {@link ApiAdapter} for the current device.
   */
  public static ApiAdapter getApiAdapter() {
    if (apiAdapter == null) {
      if (Build.VERSION.SDK_INT >= 14) {
        apiAdapter = new Api14Adapter();
        return apiAdapter;
      } else if (Build.VERSION.SDK_INT >= 11) {
        apiAdapter = new Api11Adapter();
        return apiAdapter;
      } else if (Build.VERSION.SDK_INT >= 10) {
        apiAdapter = new Api10Adapter();
        return apiAdapter;
      } else if (Build.VERSION.SDK_INT >= 9) {
        apiAdapter = new Api9Adapter();
        return apiAdapter;
      } else {
        apiAdapter = new Api8Adapter();
        return apiAdapter;
      }
    }
    return apiAdapter;
  }
}
