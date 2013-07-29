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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListUtils {
  
  private ListUtils() {
    
  }
  
  /**
   * prefixes a list with elements given in an array
   * @param list that will be prefixed (must be mutable)
   * @param prefix the prefix to added to @{code ArrayList}
   * @return the List with prefix attached
   */
  public static <X extends List<V>,V> X prefixList(X list, V [] prefix) {
    List<V> prefixList = Arrays.asList(prefix);
    @SuppressWarnings("unchecked")
    Class<V> clazz = (Class<V>)prefix.getClass().getComponentType();
    prefixList(list,prefixList,clazz);
    return list;
  }
  
  /**
   * prefixes a list with elements given in an List 
   * @param list that will be prefixed (must be mutable)
   * @param prefix the prefix to added to @{code ArrayList}
   * @param clazz the class of Objects held in the two lists
   * @return the List with prefix attached
   */
  @SuppressWarnings("unchecked")
  public static <X extends List<V>, V> X prefixList(X list, X prefix, Class<V> clazz) {
    if (list == null) {
      list = (X) new ArrayList<V>();
    }
    List<V> temp =  new ArrayList<V>(list);
    list.removeAll(temp);
    list.addAll(prefix);
    list.addAll(temp);
    return list;
  }

}
