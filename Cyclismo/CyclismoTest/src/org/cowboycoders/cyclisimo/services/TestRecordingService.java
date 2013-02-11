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
// Copyright 2012 Google Inc. All Rights Reserved.

package org.cowboycoders.cyclisimo.services;

import android.app.Notification;
import android.app.Service;
import android.test.ServiceTestCase;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.cowboycoders.cyclisimo.services.TrackRecordingService;

/**
 * A {@link TrackRecordingService} that can be used with {@link ServiceTestCase}. 
 * {@link ServiceTestCase} throws a null pointer exception when the service
 * calls {@link Service#startForeground(int, android.app.Notification)} and
 * {@link Service#stopForeground(boolean)}.
 * <p>
 * See http://code.google.com/p/android/issues/detail?id=12122
 * <p>
 * Wrap these two methods in wrappers and override them.
 *
 * @author Jimmy Shih
 */
public class TestRecordingService extends TrackRecordingService {

  private static final String TAG = TestRecordingService.class.getSimpleName();

  @Override
  protected void startForegroundService(Notification notification) {
    try {
      Method setForegroundMethod = Service.class.getMethod("setForeground", boolean.class);
      setForegroundMethod.invoke(this, true);
    } catch (SecurityException e) {
      Log.e(TAG, "Unable to start a service in foreground", e);
    } catch (NoSuchMethodException e) {
      Log.e(TAG, "Unable to start a service in foreground", e);
    } catch (IllegalArgumentException e) {
      Log.e(TAG, "Unable to start a service in foreground", e);
    } catch (IllegalAccessException e) {
      Log.e(TAG, "Unable to start a service in foreground", e);
    } catch (InvocationTargetException e) {
      Log.e(TAG, "Unable to start a service in foreground", e);
    }
  }

  @Override
  protected void stopForegroundService() {
    try {
      Method setForegroundMethod = Service.class.getMethod("setForeground", boolean.class);
      setForegroundMethod.invoke(this, false);
    } catch (SecurityException e) {
      Log.e(TAG, "Unable to start a service in foreground", e);
    } catch (NoSuchMethodException e) {
      Log.e(TAG, "Unable to start a service in foreground", e);
    } catch (IllegalArgumentException e) {
      Log.e(TAG, "Unable to start a service in foreground", e);
    } catch (IllegalAccessException e) {
      Log.e(TAG, "Unable to start a service in foreground", e);
    } catch (InvocationTargetException e) {
      Log.e(TAG, "Unable to start a service in foreground", e);
    }
  }
}
