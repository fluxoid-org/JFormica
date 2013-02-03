package org.cowboycoders.ant.messages.data;

import static org.junit.Assert.*;

import org.cowboycoders.ant.messages.MessageException;
import org.junit.Test;

public class BroadcastDataMessageTest {

  @Test
  public void test() throws MessageException {
    BroadcastDataMessage msg = new BroadcastDataMessage();
    msg.setData(new byte[] {0,1,2,3,4,5,6,7});
    msg.setChannelNumber((byte) 5);
    
    for (Byte b : msg.encode()) {
      System.out.printf("%x ",b);
    }
    System.out.println();
  }

}
