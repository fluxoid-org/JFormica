package org.cowboycoders.ant.utils;

import java.util.ArrayList;

public class TestUtils {

  /**
   * @param args
   */
  public static void main(String[] args) {
    ArrayList<Integer> test = new ArrayList<Integer>();
    ArrayList<Integer> test2 = new ArrayList<Integer>();
    test.add(3);
    test.add(3);
    test.add(3);
    System.out.println(test);
    test2.add(9);
    ListUtils.prefixList(test, new Integer [] {1,2,4,5});
    ListUtils.prefixList(test, test2, Integer.class);
    System.out.println(test);

  }

}
