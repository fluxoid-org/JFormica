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

/**
 * Unit conversion constants.
 *
 * @author Sandor Dornbush
 */
public class UnitConversions {

  private UnitConversions() {}
  
  // multiplication factor to convert kilometers to miles
  public static final double KM_TO_MI = 0.621371192;

  // multiplication factor to convert miles to kilometers
  public static final double MI_TO_KM = 1 / KM_TO_MI;

  // multiplication factor to convert miles to feet
  public static final double MI_TO_FT = 5280.0;

  // multiplication factor to convert feet to miles
  public static final double FT_TO_MI = 1 / MI_TO_FT;

  // multiplication factor to convert meters to kilometers
  public static final double M_TO_KM = 1 / 1000.0;

  // multiplication factor to convert meters per second to kilometers per hour
  public static final double MS_TO_KMH = 3.6;

  // multiplication factor to convert meters to miles
  public static final double M_TO_MI = M_TO_KM * KM_TO_MI;

  // multiplication factor to convert meters to feet
  public static final double M_TO_FT = M_TO_MI * MI_TO_FT;

  // multiplication factor to convert degrees to radians
  public static final double DEG_TO_RAD = Math.PI / 180.0;
}
