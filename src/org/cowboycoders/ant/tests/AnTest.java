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
package org.cowboycoders.ant.tests;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


public class AnTest {
  
  @Target(value={ElementType.METHOD, ElementType.FIELD})
  @Retention(RetentionPolicy.RUNTIME)
  public @interface WaitForMessageOpts  {
      boolean clearBuffer() default true;
  }
  
  @WaitForMessageOpts 
  static void print() throws SecurityException, NoSuchMethodException {
    
    System.out.println((AnTest.class.getMethod("print").getAnnotation(WaitForMessageOpts.class)).clearBuffer());
  }
  
  @WaitForMessageOpts(clearBuffer=false)
  static void printFalse() throws SecurityException, NoSuchMethodException {
    print();
  }
  
  public static void main(String [] args) throws SecurityException, NoSuchMethodException {
    
    print();
    
    
    printFalse();
    
    
    
  }

}
