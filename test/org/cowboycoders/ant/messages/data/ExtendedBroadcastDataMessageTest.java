package org.cowboycoders.ant.messages.data;

import static org.junit.Assert.*;

import org.cowboycoders.ant.messages.ExtendedMessage;
import org.junit.Test;

public class ExtendedBroadcastDataMessageTest {

  @Test
  public void test() {
    ExtendedBroadcastDataMessage msg = new ExtendedBroadcastDataMessage();
    
    for (Byte b : msg.encode()) {
      System.out.printf("%x ",b);
    }
    System.out.println();
    
    ExtendedMessage msg2 = (ExtendedMessage) msg.getBackendMessage();
    System.out.println(msg2.getDeviceNumber());
    
    
  }

}
