package org.cowboycoders.ant.messages.data;

import static org.junit.Assert.*;

import org.cowboycoders.ant.messages.LegacyMessage;
import org.cowboycoders.ant.messages.MessageException;
import org.junit.Test;

public class LegacyExtendedBroadcastDataMessageTest {

  @Test
  public void test() {
    LegacyExtendedBroadcastDataMessage msg = new LegacyExtendedBroadcastDataMessage();
    
    msg.setDeviceNumber(5);

    
    for (Byte b : msg.encode()) {
      System.out.printf("%x ",b);
    }
    System.out.println();
    

    System.out.println(msg.getDeviceNumber());
    
    assertEquals((int)msg.getDeviceNumber(),5);
  }

}
