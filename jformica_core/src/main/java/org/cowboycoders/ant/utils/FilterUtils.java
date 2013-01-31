package org.cowboycoders.ant.utils;

import java.util.ArrayList;
import java.util.List;

public class FilterUtils {
  private FilterUtils() {}
  
  public static <V> List<V> filter(Iterable<V> iterable, Filter<V> filter) {
    List <V> filteredObjects = new ArrayList<V>();
    for (V object : iterable) {
      if (filter.isWanted(object)) {
        filteredObjects.add(object);
      }
    }
    
    return filteredObjects;
  }
  

}
