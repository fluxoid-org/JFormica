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

package org.cowboycoders.cyclisimo.util;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.content.Context;

/**
 * Utitlites for sending pageviews to Google Analytics.
 * 
 * @author Jimmy Shih
 */
public class AnalyticsUtils {

  private static final String UA = "UA-7222692-2";
  private static final String PRODUCT_NAME = "android-mytracks";
  private static GoogleAnalyticsTracker tracker;

  private AnalyticsUtils() {}

  /**
   * Sends a page view.
   * 
   * @param context the context
   * @param page the page
   */
  public static void sendPageViews(Context context, String page) {
//    if (tracker == null) {
//      tracker = GoogleAnalyticsTracker.getInstance();
//      tracker.startNewSession(UA, context);
//      tracker.setProductVersion(PRODUCT_NAME, SystemUtils.getMyTracksVersion(context));
//    }
//    tracker.trackPageView(page);
  }

  public static void dispatch() {
//    if (tracker != null) {
//      tracker.dispatch();
//    }
  }
}
