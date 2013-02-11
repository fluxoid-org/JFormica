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
package org.cowboycoders.cyclisimo.content;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A request for the service to create a waypoint at the current location.
 * 
 * @author Sandor Dornbush
 */
public class WaypointCreationRequest implements Parcelable {

  public static enum WaypointType {
    WAYPOINT, STATISTICS;
  }

  private WaypointType type;
  // true if this marker contains the track statistics
  private boolean isTrackStatistics;
  private String name;
  private String category;
  private String description;
  private String iconUrl;

  public final static WaypointCreationRequest DEFAULT_WAYPOINT = new WaypointCreationRequest(
      WaypointType.WAYPOINT, false);
  public final static WaypointCreationRequest DEFAULT_STATISTICS = new WaypointCreationRequest(
      WaypointType.STATISTICS, false);
  public final static WaypointCreationRequest DEFAULT_START_TRACK = new WaypointCreationRequest(
      WaypointType.STATISTICS, true);

  private WaypointCreationRequest(WaypointType type, boolean isTrackStatistics) {
    this(type, isTrackStatistics, null, null, null, null);
  }

  public WaypointCreationRequest(WaypointType type, boolean isTrackStatistics, String name,
      String category, String description, String iconUrl) {
    this.type = type;
    this.isTrackStatistics = isTrackStatistics;
    this.name = name;
    this.category = category;
    this.description = description;
    this.iconUrl = iconUrl;
  }

  public static class Creator implements Parcelable.Creator<WaypointCreationRequest> {

    @Override
    public WaypointCreationRequest createFromParcel(Parcel source) {
      WaypointCreationRequest request = new WaypointCreationRequest(
          WaypointType.values()[source.readInt()],
          source.readByte() == 1,
          source.readString(),
          source.readString(),
          source.readString(),
          source.readString());
      return request;
    }

    public WaypointCreationRequest[] newArray(int size) {
      return new WaypointCreationRequest[size];
    }
  }

  public static final Creator CREATOR = new Creator();

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int arg1) {
    parcel.writeInt(type.ordinal());
    parcel.writeByte((byte) (isTrackStatistics ? 1 : 0));
    parcel.writeString(name);
    parcel.writeString(category);
    parcel.writeString(description);
    parcel.writeString(iconUrl);
  }

  public WaypointType getType() {
    return type;
  }

  public boolean isTrackStatistics() {
    return isTrackStatistics;
  }

  public String getName() {
    return name;
  }

  public String getCategory() {
    return category;
  }

  public String getDescription() {
    return description;
  }

  public String getIconUrl() {
    return iconUrl;
  }
}