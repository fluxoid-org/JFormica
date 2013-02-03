package org.cowboycoders.utils;

public class IterationUtils {
  private IterationUtils() {}
  
  public static <V> void operateOnAll(Iterable<V> iterable, IterationOperator<V> operator) {
    for (V element : iterable) {
      operator.performOperation(element);
    }
  }
  

}
