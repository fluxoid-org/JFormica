package org.cowboycoders.ant.messages.data;

import static org.junit.Assert.*;

import org.junit.Test;

public class BurstDataMessageTest {

  @Test
  public void testSetSequenceNumber() {
    BurstDataMessage msg = new BurstDataMessage();
    msg.setSequenceNumber(7);
  }

}
