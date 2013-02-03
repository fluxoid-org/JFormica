package org.cowboycoders.ant.messages;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.cowboycoders.ant.messages.config.ChannelSearchPriorityMessage;
import org.junit.Test;

public class ChannelSearchPriorityMessageTest {

  @Test
  public void test() {
    StandardMessage msg = new ChannelSearchPriorityMessage(1,null);
    assertEquals(msg.getPayloadToSend(),Arrays.asList(new Byte[] {1,0}));
    msg = new ChannelSearchPriorityMessage(1,10);
    assertEquals(msg.getPayloadToSend(),Arrays.asList(new Byte[] {1,10}));

    for (Byte b : msg.encode()) {
      System.out.printf("%x ",b);
    }
    System.out.println();
    
  }
  
  @Test(expected=FatalMessageException.class)
  public void test_validationUpper() {
    new ChannelSearchPriorityMessage(0,256);
  }
  
  @Test(expected=FatalMessageException.class)
  public void test_validationLower() {
    new ChannelSearchPriorityMessage(0,-1);
  }

}
