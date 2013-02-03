package org.cowboycoders.ant.messages.notifications;

import static org.junit.Assert.*;

import org.cowboycoders.ant.messages.MessageException;
import org.junit.Test;

public class StartupMessageTest {

  @Test
  public void test() {
    StartupMessage msg = new StartupMessage();
    try {
      msg.decode(new byte[]{1,(byte)0x6f,(byte) 255});
    } catch (MessageException e) {
      e.printStackTrace();
    }
    assertTrue(msg.wasCommandReset());
    assertTrue(msg.wasHardwareLineReset());
   // assertTrue(msg.wasPowerOnReset());
    assertTrue(msg.wasSuspendReset());
    assertTrue(msg.wasSynchronousReset());
    assertTrue(msg.wasWatchDogReset());
    
    try {
      msg.decode(new byte[]{1,(byte)0x6f,(byte) (1 << 7)});
    } catch (MessageException e) {
      e.printStackTrace();
    }
    
    assertTrue(msg.wasSuspendReset());
    
    try {
      msg.decode(new byte[]{1,(byte)0x6f,(byte) (1 << 6)});
    } catch (MessageException e) {
      e.printStackTrace();
    }
    
    assertTrue(msg.wasSynchronousReset());
    
    try {
      msg.decode(new byte[]{1,(byte)0x6f,(byte) (1 << 5)});
    } catch (MessageException e) {
      e.printStackTrace();
    }
    
    assertTrue(msg.wasCommandReset());
    
    try {
      msg.decode(new byte[]{1,(byte)0x6f,(byte) (1 << 1)});
    } catch (MessageException e) {
      e.printStackTrace();
    }
    
    assertTrue(msg.wasWatchDogReset());
    
    try {
      msg.decode(new byte[]{1,(byte)0x6f,(byte) (1 << 0)});
    } catch (MessageException e) {
      e.printStackTrace();
    }
    
    assertTrue(msg.wasHardwareLineReset());
    
    try {
      msg.decode(new byte[]{1,(byte)0x6f,(byte) 0});
    } catch (MessageException e) {
      e.printStackTrace();
    }
    
    assertTrue(msg.wasPowerOnReset());
    
    
    
    
  }
  

}
