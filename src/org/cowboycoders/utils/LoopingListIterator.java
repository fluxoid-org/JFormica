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
