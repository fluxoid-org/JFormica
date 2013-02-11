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
 * Copyright 2011 Google Inc.
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

package org.cowboycoders.cyclisimo.content;

import static org.cowboycoders.cyclisimo.Constants.TAG;

import android.util.Log;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manages register/unregister {@link TrackDataListener} and keeping the state
 * for each registered listener.
 * 
 * @author Rodrigo Damazio
 */
public class TrackDataManager {

  // Map of listener to its track data types
  private final Map<TrackDataListener, EnumSet<TrackDataType>>
      listenerToTypesMap = new HashMap<TrackDataListener, EnumSet<TrackDataType>>();

  // Map of track data type to listeners
  private final Map<TrackDataType, Set<TrackDataListener>>
      typeToListenersMap = new EnumMap<TrackDataType, Set<TrackDataListener>>(TrackDataType.class);

  public TrackDataManager() {
    for (TrackDataType trackDataType : TrackDataType.values()) {
      typeToListenersMap.put(trackDataType, new LinkedHashSet<TrackDataListener>());
    }
  }

  /**
   * Registers a listener.
   * 
   * @param listener the listener
   * @param trackDataTypes the track data types the listener is interested
   */
  public void registerListener(
      TrackDataListener listener, EnumSet<TrackDataType> trackDataTypes) {
    if (listenerToTypesMap.containsKey(listener)) {
      Log.w(TAG, "Tried to register a listener that is already registered. Ignore.");
      return;
    }
    listenerToTypesMap.put(listener, trackDataTypes);
    for (TrackDataType trackDataType : trackDataTypes) {
      typeToListenersMap.get(trackDataType).add(listener);
    }
  }

  /**
   * Unregisters a listener.
   * 
   * @param listener the listener
   */
  public void unregisterListener(TrackDataListener listener) {
    EnumSet<TrackDataType> removedTypes = listenerToTypesMap.remove(listener);
    if (removedTypes == null) {
      Log.w(TAG, "Tried to unregister a listener that is not registered. Ignore.");
      return;
    }

    // Remove the listener from the typeToListenersMap
    for (TrackDataType trackDataType : removedTypes) {
      typeToListenersMap.get(trackDataType).remove(listener);
    }
  }

  /**
   * Gets the number of {@link TrackDataListener}.
   */
  public int getNumberOfListeners() {
    return listenerToTypesMap.size();
  }

  /**
   * Gets the track data types for a listener.
   * 
   * @param listener the listener
   */
  public EnumSet<TrackDataType> getTrackDataTypes(TrackDataListener listener) {
    return listenerToTypesMap.get(listener);
  }

  /**
   * Gets the listeners for a {@link TrackDataType}.
   * 
   * @param type the type
   */
  public Set<TrackDataListener> getListeners(TrackDataType type) {
    return typeToListenersMap.get(type);
  }

  /**
   * Gets all the registered {@link TrackDataType}.
   */
  public EnumSet<TrackDataType> getRegisteredTrackDataTypes() {
    EnumSet<TrackDataType> types = EnumSet.noneOf(TrackDataType.class);
    for (EnumSet<TrackDataType> value : listenerToTypesMap.values()) {
      types.addAll(value);
    }
    // Always include preference
    types.add(TrackDataType.PREFERENCE);
    return types;
  }
}
