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

import org.cowboycoders.cyclisimo.content.Track;
import org.cowboycoders.cyclisimo.stats.TripStatistics;

import java.util.Vector;

import org.cowboycoders.cyclisimo.util.ChartURLGenerator;

import junit.framework.TestCase;

/**
 * Tests for the Chart URL generator.
 *
 * @author Sandor Dornbush
 */
public class ChartURLGeneratorTest extends TestCase {

  public void testgetChartUrl() {
    Vector<Double> distances = new Vector<Double>();
    Vector<Double> elevations = new Vector<Double>();
    Track t = new Track();
    TripStatistics stats = t.getTripStatistics();
    stats.setMinElevation(0);
    stats.setMaxElevation(2000);
    stats.setTotalDistance(100);

    distances.add(0.0);
    elevations.add(10.0);

    distances.add(10.0);
    elevations.add(300.0);

    distances.add(20.0);
    elevations.add(800.0);

    distances.add(50.0);
    elevations.add(1900.0);

    distances.add(75.0);
    elevations.add(1200.0);

    distances.add(90.0);
    elevations.add(700.0);

    distances.add(100.0);
    elevations.add(70.0);

    String chart = ChartURLGenerator.getChartUrl(distances,
                                                 elevations,
                                                 t,
                                                 "Title",
                                                 true);

    assertEquals(
        "http://chart.apis.google.com/chart?&chs=600x350&cht=lxy&"
        + "chtt=Title&chxt=x,y&chxr=0,0,0,0|1,0.0,2100.0,300&chco=009A00&"
        + "chm=B,00AA00,0,0,0&chg=100000,14.285714285714286,1,0&"
        + "chd=e:AAGZMzf.v.5l..,ATJJYY55kkVVCI",
        chart);
  }
}
