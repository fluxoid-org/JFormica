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
 * Copyright 2009 Google Inc.
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

package org.cowboycoders.cyclisimo.services;

/**
 * A {@link LocationListenerPolicy} that will change based on how long the user
 * has been stationary. This policy will dictate a policy based on a min, max
 * and idle time. The policy will dictate an interval bounded by min and max,
 * and is half of the idle time.
 * 
 * @author Sandor Dornbush
 */
public class AdaptiveLocationListenerPolicy implements LocationListenerPolicy {

  private final long minInterval;
  private final long maxInterval;
  private final int minDistance;

  // The time the user has been idle at the current location, in milliseconds.
  private long idleTime;

  /**
   * Creates a policy that will be bounded by the given minInterval and
   * maxInterval.
   * 
   * @param minInterval the smallest interval this policy will dictate, in
   *          milliseconds
   * @param maxInterval the largest interval this policy will dictate, in
   *          milliseconds
   * @param minDistance the minimum distance in meters
   */
  public AdaptiveLocationListenerPolicy(long minInterval, long maxInterval, int minDistance) {
    this.minInterval = minInterval;
    this.maxInterval = maxInterval;
    this.minDistance = minDistance;
  }

  /*
   * Returns an interval half of the idle time, but bounded by minInteval and
   * maxInterval.
   */
  @Override
  public long getDesiredPollingInterval() {
    long desiredInterval = idleTime / 2;
    // Round to second to avoid setting the interval too often
    desiredInterval = (desiredInterval / 1000) * 1000;
    return Math.max(Math.min(maxInterval, desiredInterval), minInterval);
  }

  @Override
  public int getMinDistance() {
    return minDistance;
  }

  @Override
  public void updateIdleTime(long newIdleTime) {
    idleTime = newIdleTime;
  }
}
