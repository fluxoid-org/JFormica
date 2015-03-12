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
package org.cowboycoders.cyclisimo.util;

/**
 * The singleton class representing the extended encoding of chart data.
 */
public class ChartsExtendedEncoder {

  // ChartServer data encoding in extended mode
  private static final String CHARTSERVER_EXTENDED_ENCODING_SEPARATOR = ",";
  private static final String CHARTSERVER_EXTENDED_ENCODING =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-.";
  private static final int CHARTSERVER_EXTENDED_SINGLE_CHARACTER_VALUES =
      CHARTSERVER_EXTENDED_ENCODING.length();
  private static final String MISSING_POINT_EXTENDED_ENCODING = "__";

  private ChartsExtendedEncoder() { }

  public static String getEncodedValue(int scaled) {
    int index1 = scaled / CHARTSERVER_EXTENDED_SINGLE_CHARACTER_VALUES;
    if (index1 < 0 || index1 >= CHARTSERVER_EXTENDED_SINGLE_CHARACTER_VALUES) {
      return MISSING_POINT_EXTENDED_ENCODING;
    }

    int index2 = scaled % CHARTSERVER_EXTENDED_SINGLE_CHARACTER_VALUES;
    if (index2 < 0 || index2 >= CHARTSERVER_EXTENDED_SINGLE_CHARACTER_VALUES) {
      return MISSING_POINT_EXTENDED_ENCODING;
    }

    return String.valueOf(CHARTSERVER_EXTENDED_ENCODING.charAt(index1))
         + String.valueOf(CHARTSERVER_EXTENDED_ENCODING.charAt(index2));
  }

  public static String getSeparator() {
    return CHARTSERVER_EXTENDED_ENCODING_SEPARATOR;
  }
}
