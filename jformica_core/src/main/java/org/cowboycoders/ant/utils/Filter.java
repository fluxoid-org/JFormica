package org.cowboycoders.ant.utils;

/**
 * Filter elements added to new structures
 * @author will
 *
 * @param <V>
 */
public interface Filter <V> {
  
  boolean isWanted(V value);

}
