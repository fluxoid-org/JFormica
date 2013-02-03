package org.cowboycoders.ant.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;

import org.cowboycoders.ant.events.FixedSizeBuffer;

class Dave {
  String name;
  
  Dave(String name) {
    this.name = name;
  }
}

public class TestQueue {
  

  
  public static void main(String [] args) throws InterruptedException {
    Queue<String> list = new FixedSizeBuffer<String>(3);
    list.offer("hi");
    list.offer("bye");
    list.offer("smells");
    list.offer("crufts");
    ArrayList<String> reverse = new ArrayList<String>(list);
    Collections.reverse(reverse);
    list.removeAll(list);
    for (String s : reverse ) {
      System.out.println(s);
    }
    for (String s : list ) {
      System.out.println(s);
    }
    
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
    
    while(true) {
    count++;
    System.out.println(count);
    for (Dave s : strings ) {
      System.out.println(s.name);
    }
    System.gc();
    Thread.sleep(1000);
    }
    
  }
  


}
