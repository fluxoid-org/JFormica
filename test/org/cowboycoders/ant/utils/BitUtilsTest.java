package org.cowboycoders.ant.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class BitUtilsTest {

  @Test
  public void testGetMaxBitIndex() {
    assertEquals(1,BitUtils.getMaxBitIndex(1));
    assertEquals(8,BitUtils.getMaxBitIndex(255));
    org.junit.Assert.assertNotSame(8, BitUtils.getMaxBitIndex(256));
  }

}
