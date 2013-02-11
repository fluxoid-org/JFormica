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


import android.location.Location;

import org.cowboycoders.cyclisimo.content.Sensor.SensorDataSet;

/**
 * This class extends the standard Android location with extra information.
 *
 * @author Sandor Dornbush
 */
public class MyTracksLocation extends Location {

  private SensorDataSet sensorDataSet = null;

  /**
   * The id of this location from the provider.
   */
  private int id = -1;

  public MyTracksLocation(Location location, Sensor.SensorDataSet sd) {
    super(location);
    this.sensorDataSet = sd;
  }

  public MyTracksLocation(String provider) {
    super(provider);
  }

  public SensorDataSet getSensorDataSet() {
    return sensorDataSet;
  }

  public void setSensorDataSet(SensorDataSet sensorDataSet) {
    this.sensorDataSet = sensorDataSet;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
  
  public void reset() {
    super.reset();
    sensorDataSet = null;
    id = -1;
  }
}
