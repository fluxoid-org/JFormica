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
package org.cowboycoders.cyclisimo.io.gdata;

import com.google.wireless.gdata.client.GDataClient;

import android.content.Context;
import android.util.Log;

import org.cowboycoders.cyclisimo.Constants;

/**
 * This factory will fetch the right class for the platform.
 * 
 * @author Sandor Dornbush
 */
public class GDataClientFactory {

  private GDataClientFactory() { }

  /**
   * Creates a new GData client.
   * This factory will fetch the right class for the platform.
   * @return A GDataClient appropriate for this platform
   */
  public static GDataClient getGDataClient(Context context) {
    // TODO This should be moved into ApiAdapter
    try {
      // Try to use the official unbundled gdata client implementation.
      // This should work on Froyo and beyond.
      return new com.google.android.common.gdata.AndroidGDataClient(context);
    } catch (LinkageError e) {
      // On all other platforms use the client implementation packaged in the
      // apk.
      Log.i(Constants.TAG, "Using mytracks AndroidGDataClient.", e);
      return new org.cowboycoders.cyclisimo.io.gdata.AndroidGDataClient();
    }
  }
}
