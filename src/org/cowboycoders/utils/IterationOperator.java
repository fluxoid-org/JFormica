package org.cowboycoders.utils;


/**
 * perform an operation on each element in an {@code Iterable}
 */
public interface IterationOperator <V> {
  
  void performOperation(V v);

}
