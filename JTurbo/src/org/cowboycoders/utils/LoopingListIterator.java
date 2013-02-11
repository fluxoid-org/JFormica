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
import java.util.Iterator;
import java.util.List;



public class LoopingListIterator <V> implements Iterator<V>{
  
  private List<V> list;
  private int currentIndex = 0;
  
  private int getCurrentIndex() {
    int index = currentIndex;
    currentIndex++;
    if (currentIndex >= list.size()) {
      currentIndex = 0;
    }
    return index;
  }
  
  /**
   * 
   * @param list to iterate on
   */
  public LoopingListIterator(List<V> list) {
    this.list = list;
  }

  @Override
  public boolean hasNext() {
    return true;
  }

  @Override
  public V next() {
    return list.get(getCurrentIndex());
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
    
  }

}
