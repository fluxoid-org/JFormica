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

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.cowboycoders.cyclisimo.Constants;
import org.cowboycoders.cyclisimo.ContextualActionModeCallback;
import org.cowboycoders.cyclisimo.services.sensors.BluetoothConnectionManager;

/**
 * API level 8 specific implementation of the {@link ApiAdapter}.
 *
 * @author Bartlomiej Niechwiej
 */
public class Api8Adapter implements ApiAdapter {

  @Override
  public void applyPreferenceChanges(Editor editor) {
    editor.commit();
  }

  @Override
  public void enableStrictMode() {
    // Not supported
  }

  @Override
  public byte[] copyByteArray(byte[] input, int start, int end) {
    int length = end - start;
    byte[] output = new byte[length];
    System.arraycopy(input, start, output, 0, length);
    return output;
  }

  @Override
  public HttpTransport getHttpTransport() {
    return new ApacheHttpTransport();
  }

  @Override
  public boolean isGeoCoderPresent() {
    return true;
  }

  @Override
  public BluetoothSocket getBluetoothSocket(BluetoothDevice bluetoothDevice) throws IOException {
    try {
      Class<? extends BluetoothDevice> c = bluetoothDevice.getClass();
      Method insecure = c.getMethod("createInsecureRfcommSocket", Integer.class);
      insecure.setAccessible(true);
      return (BluetoothSocket) insecure.invoke(bluetoothDevice, 1);
    } catch (SecurityException e) {
      Log.d(Constants.TAG, "Unable to create insecure connection", e);
    } catch (NoSuchMethodException e) {
      Log.d(Constants.TAG, "Unable to create insecure connection", e);
    } catch (IllegalArgumentException e) {
      Log.d(Constants.TAG, "Unable to create insecure connection", e);
    } catch (IllegalAccessException e) {
      Log.d(Constants.TAG, "Unable to create insecure connection", e);
    } catch (InvocationTargetException e) {
      Log.d(Constants.TAG, "Unable to create insecure connection", e);
    }
    return bluetoothDevice.createRfcommSocketToServiceRecord(BluetoothConnectionManager.MY_TRACKS_UUID);
  }

  @Override
  public void hideTitle(Activity activity) {
    activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
  }

  @Override
  public void configureActionBarHomeAsUp(Activity activity) {
    // Do nothing
  }

  @Override
  public void configureListViewContextualMenu(Activity activity, ListView listView,
      ContextualActionModeCallback contextualActionModeCallback) {
    activity.registerForContextMenu(listView);
  }

  @Override
  public void configureSearchWidget(Activity activity, MenuItem menuItem) {
    // Do nothing
  }

  @Override
  public boolean handleSearchMenuSelection(Activity activity) {
    activity.onSearchRequested();
    return true;
  }

  @Override
  public <T> void addAllToArrayAdapter(ArrayAdapter<T> arrayAdapter, List<T> items) {
    for (T item : items) {
      arrayAdapter.add(item);
    }
  }

  @Override
  public void invalidMenu(Activity activity) {
    // Do nothing
  }

  @Override
  public boolean handleSearchKey(MenuItem menuItem) {
    // Return false and allow the framework to handle the search key.
    return false;
  }
}
