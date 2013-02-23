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
package org.cowboycoders.utils;

public class TrapezoidIntegrator {
  
  Double lastY;
  Double lastX;
  
  double integral = 0;
  
  public double add(double x, double y) {
    if (lastY == null) {
      lastY = y;
      lastX = x;
      return 0;
    }
    integral += 0.5 * (x - lastX) * (y + lastY);
    lastY = y;
    lastX = x;
    return integral;
  }
  

  /**
   * @return the integral
   */
  public double getIntegral() {
    return integral;
  }
  
  /**
   * Adjust integral to a new value
   * @param newValue
   */
  public void setIntegral(double newValue) {
	  integral = newValue;
  }
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    TrapezoidIntegrator I = new TrapezoidIntegrator();
    for (int i = 0 ; i <= 100 ; i++) {
      I.add(i, i);
    }
    System.out.println(I.getIntegral());

  }

}
