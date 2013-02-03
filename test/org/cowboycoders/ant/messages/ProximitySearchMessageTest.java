package org.cowboycoders.ant.messages;

import static org.junit.Assert.*;

import org.cowboycoders.ant.messages.config.ProximitySearchMessage;
import org.junit.Test;

public class ProximitySearchMessageTest {

  @Test
  public void test() {
    ProximitySearchMessage msg = new ProximitySearchMessage(0,10);
    for (Byte b : msg.encode()) {
      System.out.printf("%x ",b);
    }
    System.out.println();
    
    assertEquals(new Byte((byte) 10),msg.getStandardPayload().get(1));
  }
  
  @Test(expected=FatalMessageException.class)
  public void test_validationUpper() {
    new ProximitySearchMessage(0,11);
  }
  
  @Test(expected=FatalMessageException.class)
  public void test_validationLower() {
    new ProximitySearchMessage(0,-1);
  }

}
