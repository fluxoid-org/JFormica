package org.cowboycoders.ant.messages.responses;

import static org.junit.Assert.*;

import org.junit.Test;

public class CapabilityTest {

  @Test
  public void test() {
    System.out.println(Capability.getCapabilitiesInCategory(CapabilityCategory.STANDARD));
  }

}
