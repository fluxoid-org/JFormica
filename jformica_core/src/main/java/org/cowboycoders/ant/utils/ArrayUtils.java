/**
 *     Copyright (c) 2013, Will Szumski
 *
 *     This file is part of formicidae.
 *
 *     formicidae is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     formicidae is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with formicidae.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cowboycoders.ant.utils;

import java.lang.reflect.Array;

public class ArrayUtils {

  public static <V> boolean arrayStartsWith(V[] pattern, V[] data){

	    for (int i=0; i<pattern.length; i++) {
	        if (data[i] != pattern[i]) {
	            return false;
	        }
	    }
	    return true;
	}
  
	public static <V> V [] joinArray(V [] ... arrays) {
		int totalLength = 0;
		for (V [] array : arrays) {
			totalLength += array.length;
		}
		Class<?> clazz = arrays[0].getClass().getComponentType();
		// allocate
		@SuppressWarnings("unchecked")
		V [] joined = (V []) Array.newInstance(clazz, totalLength);
				
		int nextIndex = 0;
		for (V [] array: arrays ) {
			for (int i = nextIndex ; i < array.length + nextIndex ; i++) {
				joined[i] = array [i - nextIndex];
			}
			nextIndex += array.length;
		}
		
		return joined;
	}
	
	public static void main(String [] args) {
		String [] one = new String[] {"one", "two"};
		String [] two = new String[] {"three"};
		for (String s : ArrayUtils.joinArray(one,two,two,one)) {
			System.out.println(s);
		}
	}



}
