package org.cowboycoders.ant.messages.notifications;

import static org.junit.Assert.*;

import org.cowboycoders.ant.messages.MessageException;
import org.junit.Test;

public class SerialErrorMessageTest {

  @Test
  public void test() {
    SerialErrorMessage msg = new SerialErrorMessage();
    try {
      msg.decode(new byte[]{1,(byte)0xAE,(byte) 255,1,2,3});
    } catch (MessageException e) {
      e.printStackTrace();
    }
    
    for (Byte b : msg.getOriginalMessage()) {
      System.out.printf("%x ",b);
    }
    System.out.println();
    
    
  }
  

}
