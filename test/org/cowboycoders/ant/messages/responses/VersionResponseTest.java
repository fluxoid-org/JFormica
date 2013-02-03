package org.cowboycoders.ant.messages.responses;

import static org.junit.Assert.*;

import org.cowboycoders.ant.messages.FatalMessageException;
import org.cowboycoders.ant.messages.MessageException;
import org.cowboycoders.ant.messages.notifications.StartupMessage;
import org.junit.Test;

public class VersionResponseTest {

  @Test (expected=MessageException.class)
  public void testValidationFail() throws MessageException {
    VersionResponse msg = new VersionResponse();
    msg.decode(new byte[]{1,(byte)0x3E,(byte) 255});
  }
  
  @Test
  public void testValidationPass() throws MessageException {
    VersionResponse msg = new VersionResponse();
    msg.decode(new byte[]{1,(byte)0x3E,(byte) 'h',(byte) 'e',(byte) 'l',(byte) 'l',(byte) 'o',(byte) ' ',(byte) 'w',(byte) 'o',(byte) 'r',(byte) 'l',(byte) '\0'});
    System.out.println(msg.getVersionString());
  }

}
