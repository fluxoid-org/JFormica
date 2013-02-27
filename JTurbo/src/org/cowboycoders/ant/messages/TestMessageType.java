/*
*    Copyright (c) 2013, Will Szumski
*    Copyright (c) 2013, Doug Szumski
*
*    This file is part of Cyclismo.
*
*    Cyclismo is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Cyclismo is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Cyclismo.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.cowboycoders.ant.messages;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.cowboycoders.ant.messages.Constants.DataElements;
import org.cowboycoders.ant.messages.config.ChannelAssignMessage;
import org.cowboycoders.ant.messages.config.ChannelIdMessage;
import org.cowboycoders.ant.messages.config.NetworkKeyMessage;
import org.cowboycoders.ant.messages.ExtendedMessage;
import org.cowboycoders.ant.messages.LegacyMessage;
import org.cowboycoders.ant.messages.MessageException;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.utils.ByteUtils;

public class TestMessageType {

  
  public static long func(Long i) {
    return i;
  }
  /**
   * @param args
   * 
   * 
   * 
   */
  public static void main(String[] args) {
    MessageId type = MessageId.lookUp((byte)1);
    
    Message msg = new Message();
    ExtendedMessage msg4 = new ExtendedMessage();
    ExtendedMessage msg8 = new ExtendedMessage();
    LegacyMessage msg2 = new LegacyMessage();
    ChannelMessage msg3 = null;
    ChannelType t = new MasterChannelType(true,false);
    Set<ChannelAssignMessage.ExtendedAssignment> options = new HashSet<ChannelAssignMessage.ExtendedAssignment>();
    //options.add(ChannelAssignMessage.ExtendedAssignment.BACKGROUND_SCANNING_ENABLE);
    //options.add(ChannelAssignMessage.ExtendedAssignment.BACKGROUND_SCANNING_ENABLE);
    options.add(ChannelAssignMessage.ExtendedAssignment.FREQUENCY_AGILITY_ENABLE);
    ChannelAssignMessage msg5 = new ChannelAssignMessage (0,t);
    
    ChannelIdMessage msg6 = new ChannelIdMessage(1,33,127,1,true);
    
    NetworkKeyMessage msg7 = new NetworkKeyMessage(0, new int[] {0,255,2,3,4,5,6,255});
    
    BigInteger big = new BigInteger(BigInteger.ZERO.toByteArray());
    
    //big.add(18);
    for (int i=0 ; i <8 ; i++) {
      msg6.addOptionalDataElement(DataElements.DATA);
    }
    
    msg6.setDataElement(DataElements.DATA, 0x22,7);
    
    System.out.printf("%x",msg6.getDataElement(DataElements.DATA, 7));

   System.out.printf(" Integer %x",new Long(0x9fffffffffffffffl));
    System.out.println();
   
    
    //try {
    //  msg3 = new ChannelMessage(msg2,MessageId.ASSIGN_CHANNEL);
    //} catch (MessageException e) {
    //  // TODO Auto-generated catch block
    //  e.printStackTrace();
    //}
    
    
    
    try {
      msg4.decode(new byte[] {(byte)0x9,0x4e,0x00,(byte) 0x84,0x00,0x00,(byte) 0x96,(byte) 0x9a,(byte) 0x99,(byte) 0xd3,0x3e,(byte) 0xA0,0x0,0x01,0x00,0x00,0x00,0x01});
      msg.decode(new byte[] {(byte)0x9,0x4e,0x00,(byte) 0x05,0x00,0x00,(byte) 0x96,(byte) 0x9a,(byte) 0x99,(byte) 0xd3,0x3e});
      msg2.decode(new byte[] {(byte)0x9,0x5d,0x05,0x01,0x01,0x01,(byte) 0x08,0x00,0x00,(byte) 0x96,(byte) 0x9a,(byte) 0x99,(byte) 0xd3,0x00,0x00});
      msg8.decode(new byte[] {(byte)0x9,0x4e,0x00,(byte) 0x05,0x00,0x00,(byte) 0x96,(byte) 0x9a,(byte) 0x99,(byte) 0xd3,0x3e,(byte) 0xA0,0x0,0x01,0x00,0x00,0x00,0x01});
      //msg3.decode(new byte[] {(byte)0x9,0x4e,0x07,0x01,0x01,0x01,(byte) 0x84,0x00,0x00,(byte) 0x96,(byte) 0x9a,(byte) 0x99,(byte) 0xd3,0x3e});
    } catch (MessageException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    //for (Byte b : msg.encode()) {
    //  System.out.printf("%x ",b);
    //}
    //System.out.println();
    
    for (Byte b : msg4.getStandardPayload()) {
      System.out.printf("%x ",b);
    }
    
    System.out.println();
    System.out.println("msg5");
    
    for (Byte b : msg5.getStandardPayload()) {
      System.out.printf("%x ",b);
    }
    
    System.out.println();
    System.out.println("msg6");
    
    for (Byte b : msg6.getStandardPayload()) {
      System.out.printf("%x ",b);
    }
    
    System.out.println();
    System.out.println("msg7");
    
    for (Byte b : msg7.getStandardPayload()) {
      System.out.printf("%x ",b);
    }
    
    System.out.println();
    System.out.println("msg");
    
    for (Byte b : msg.encode()) {
      System.out.printf("%x ",b);
    }
    
    System.out.println();
    System.out.println("msg8");
    
    for (Byte b : msg8.clone().encode()) {
      System.out.printf("%x ",b);
    }
    
    
    System.out.println();
    System.out.println();
    
      System.out.println(msg4.getExtendedData(DataElements.TRANSMISSION_TYPE));
      msg2.setTransmissionType(127);
      System.out.println(msg2.getExtendedData(DataElements.TRANSMISSION_TYPE));
      System.out.println(msg4.getTransmissionType());

    
    //for (Byte b : msg2.encode()) {
    //  System.out.printf("%x ",b);
    //}
    
    System.out.println();
    
    //for (Byte b : msg.getStandardPayload()) {
    //  System.out.printf("%x ",b);
    //}
    
    System.out.println();
    
    System.out.println(DataElements.DEVICE_NUMBER.getMaxValue());
    //try {
    //  msg3.setChannelNumber((byte) 10);
    //} catch (MessageException e) {
      // TODO Auto-generated catch block
    // e.printStackTrace();
   // }
    //System.out.println(msg3.getChannelNumber());
    //System.out.print(msg3.getBackendMessage() instanceof LegacyMessage);
   
    System.out.println();
    
    System.out.println(ByteUtils.lsbSplit(16384, 2));
    
    
    
  }

}

