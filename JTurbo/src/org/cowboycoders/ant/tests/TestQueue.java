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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;

import org.cowboycoders.ant.utils.FixedSizeFifo;
import org.cowboycoders.ant.utils.FixedSizeLifo;

class Dave {
  String name;
  
  Dave(String name) {
    this.name = name;
  }
}

public class TestQueue {
  

  
  public static void main(String [] args) throws InterruptedException {
	FixedSizeLifo<String> list = new FixedSizeLifo<String>(3);
    list.offer("hi");
    System.out.println(list.size());
    list.offer("bye");
    list.offer("smells");
    list.offer("crufts");
    System.out.println(list.displace("strange"));
//    ArrayList<String> reverse = new ArrayList<String>(list);
//    Collections.reverse(reverse);
//    list.removeAll(list);
//    for (String s : reverse ) {
//      System.out.println(s);
//    }
    for (String s : list ) {
      System.out.println(s);
    }
    
    System.out.println(list.poll());
    
    Set<Dave> strings = Collections.newSetFromMap(new WeakHashMap<Dave,Boolean>());
    
    Dave d = new Dave("simon");
    strings.add(d);
    
    d = new Dave("luke");
    strings.add(d);
    
    d = new Dave("mark");
    strings.add(d);
    
    d = new Dave("tom");
    strings.add(d);
    
    strings.add(new Dave("bexley") {});
    
    int count = 0;
    
//    while(true) {
//    count++;
//    System.out.println(count);
//    for (Dave s : strings ) {
//      System.out.println(s.name);
//    }
//    System.gc();
//    Thread.sleep(1000);
//    }
    
  }
  


}
