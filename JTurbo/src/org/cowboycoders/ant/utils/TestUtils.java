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
package org.cowboycoders.ant.utils;

import java.util.ArrayList;

public class TestUtils {

  /**
   * @param args
   */
  public static void main(String[] args) {
    ArrayList<Integer> test = new ArrayList<Integer>();
    ArrayList<Integer> test2 = new ArrayList<Integer>();
    test.add(3);
    test.add(3);
    test.add(3);
    System.out.println(test);
    test2.add(9);
    ListUtils.prefixList(test, new Integer [] {1,2,4,5});
    ListUtils.prefixList(test, test2, Integer.class);
    System.out.println(test);

  }

}
