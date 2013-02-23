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
package org.cowboycoders.ant.temp;

import static org.junit.Assert.*;

import static org.cowboycoders.ant.utils.ArrayUtils.*;

import java.lang.Thread.State;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import org.cowboycoders.ant.Channel;
import org.cowboycoders.ant.NetworkKey;
import org.cowboycoders.ant.Node;
import org.cowboycoders.ant.events.BroadcastListener;
import org.cowboycoders.ant.events.MessageCondition;
import org.cowboycoders.ant.events.MessageConditionFactory;
import org.cowboycoders.ant.interfaces.AntTransceiver;
import org.cowboycoders.ant.messages.MasterChannelType;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.SlaveChannelType;
import org.cowboycoders.ant.messages.StandardMessage;
import org.cowboycoders.ant.messages.commands.ResetMessage;
import org.cowboycoders.ant.messages.data.BroadcastDataMessage;
import org.cowboycoders.ant.messages.responses.ResponseCode;
import org.cowboycoders.ant.temp.BushidoBrakeModel.CalibrationState;
import org.cowboycoders.ant.utils.ArrayUtils;
import org.cowboycoders.ant.utils.ByteUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class BushidoBrakeTest {
  
  private static AntTransceiver antchip = new AntTransceiver(0);
  
  @BeforeClass 
  public static void beforeClass() {
    //AntTransceiver.LOGGER.setLevel(Level.ALL);
    //ConsoleHandler handler = new ConsoleHandler();
    // PUBLISH this level
    //handler.setLevel(Level.ALL);
    //AntTransceiver.LOGGER.addHandler(handler);
    //Node.LOGGER.setLevel(Level.ALL);
    //Node.LOGGER.addHandler(handler);
    StandardMessage msg = new ResetMessage();
    //StandardMessage msg = new BroadcastDataMessage();
    //antchip.start();
    //antchip.send(msg.encode());
    //antchip.send(msg.encode());
    //antchip.stop();
  }
  
  @AfterClass
  public static void afterClass() {
    antchip.stop();
    //antchip.stop();
  }
  
  BushidoBrakeModel model = new BushidoBrakeModel();
  
  Thread speedUp = new Thread() {
    
    public void run() {
      model.setCalibrationState(CalibrationState.BELOW_SPEED);
      doSpeedUp();
      
      model.setCalibrationState(CalibrationState.UP_TO_SPEED);
      
      slowDown.start();
    }

      
    
  };
  
  private void doSpeedUp() {
    for (int i = 0 ; i<= 42 ; i++) {
      model.setWheelSpeed(i);
      System.out.println(i);
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  
  
  Thread slowDown = new Thread() {
    
    public void run() {
      model.setCalibrationState(CalibrationState.SLOWING_DOWN);
      for (int i = 40 ; i >= 0 ; i--) {
        model.setWheelSpeed(i);
        System.out.println(i);
        if (i == 20) {
          model.setCalibrationState(CalibrationState.NO_ERROR);
        }
        try {
          Thread.sleep(200);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      
      synchronized(sendData) {
        send = false;
      }
      
      doShutDown();
      
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
      doStartUp();
      
      synchronized(sendData) {
        send = true;
      }
      
      doSpeedUp();
      
    }
    
  };
  
  
  boolean gotRequest = false;
  boolean doneRequest = false;
  
  Long calibratedTimestamp;

  class Listener implements BroadcastListener<BroadcastDataMessage> {

    // Packet identifiers
    Byte[] startCalibration = {(byte) 0x23, 0x63};
    Byte[] requestCalibrationStatus = {(byte) 0x23, 0x58};
    Byte[] requestCalibrationData = {0x23,0x4d};
    @Override
    public void receiveMessage(BroadcastDataMessage message) {
        Byte[] data = message.getData();
        if (arrayStartsWith(startCalibration, data)) {
          if (!gotRequest) {
            model.setCalibrationState(CalibrationState.CALIBRATION_REQUESTED);
            gotRequest = true;
            speedUp.start();
          }
        }
        if (arrayStartsWith(requestCalibrationStatus, data)) {
          model.setCalibrationState(CalibrationState.CALIBRATION_VALUE_READY);
        }
        if (arrayStartsWith(requestCalibrationData, data)) {
          model.setCalibrationState(CalibrationState.CALIBRATED);
          calibratedTimestamp = System.nanoTime();
        }
        
    }
}
  
  
  public void doSending() {
    BroadcastDataMessage msg = new BroadcastDataMessage();
    msg.setData(model.getDataPacket());
    try {
      c.sendAndWaitForMessage(msg, CONDITION_CHANNEL_TX , 10l, TimeUnit.SECONDS, null);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (TimeoutException e) {
      e.printStackTrace();
    }
  }
  
  boolean send = true;
  
  Thread sendData = new Thread() {
    
    public void run() {
      
      while(true) {
        if (Thread.interrupted()) {
          break;
        }
        if (calibratedTimestamp != null) {
          long timeLeft = TimeUnit.SECONDS.toNanos(2) - (System.nanoTime() - calibratedTimestamp);
          if (timeLeft < 0) {
            break;
          }
        }
        Thread.yield();
        synchronized(this) {
          if (send == false) {
            continue;
          }
          doSending();
        }

      }
    }
  };
  
  
  private static final MessageCondition CONDITION_CHANNEL_TX = 
      MessageConditionFactory.newResponseCondition(MessageId.EVENT, ResponseCode.EVENT_TX);
  
  Channel c;
  Node n = new Node(antchip);
  Listener listener = new Listener();
  
  
  public void doStartUp() {
    int repeats = 10;
      
    NetworkKey key = new NetworkKey(0,0,0,0,0,0,0,0);
    key.setName("N:ANT+");
    
    n.start();
    n.reset();
    
    n.setNetworkKey(0, key);
    
    assertNotNull(c = n.getFreeChannel());
    
    c.setName("C:BUSHIDO");
    
    MasterChannelType channelType = new MasterChannelType();
    c.assign("N:ANT+", channelType);
    
    c.registerRxListener(listener, BroadcastDataMessage.class);
    
    c.setId(0x1223, 0x51, 1, false);
   
    c.setFrequency(60);
    
    c.setPeriod(4096);
    
    c.setSearchTimeout(255);
    
    c.open();
  }
  
  
  public void doShutDown() {
    c.removeRxListener(listener);
    c.close();
    c.unassign();
    
    n.freeChannel(c);
    n.stop();
  }
  
  
  private final static double CALIBRATION_VALUE = 4.0;
  
  
  //@Test
  public void test_calibration() throws InterruptedException, TimeoutException {
    
    model.setCalibrationValue(CALIBRATION_VALUE);
    
    doStartUp();
    
    sendData.start();
    
    //doSpeedUp();
    
    sendData.join();
    
    
    
  }
  
  Thread changeSpeedOnly = new Thread() {
    public void run() {
      while(true) {
        
        for (int i = 0 ; i<= 42 ; i++) {
          model.setWheelSpeed(i);
          System.out.println(i);
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        
        for (int i = 40 ; i >= 0 ; i--) {
          model.setWheelSpeed(i);
          System.out.println(i);
          if (i == 20) {
            model.setCalibrationState(CalibrationState.NO_ERROR);
          }
          try {
            Thread.sleep(200);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        
      }
      
    }
  };
  
  //@Test
  public void test_calibration_val() throws InterruptedException, TimeoutException {
    
    model.setCalibrationValue(CALIBRATION_VALUE);
    model.setPower(200);
    model.setCadence(50);
    model.setBalance((byte) 50);
    
    doStartUp();
    
    sendData.start();
    
    changeSpeedOnly.start();
    
    //doSpeedUp();
    
    sendData.join();
    
    
    
  }
  
  private final BroadcastListener<BroadcastDataMessage> versionListener = new BroadcastListener<BroadcastDataMessage>() {

	private  Byte[] PARTIAL_PACKET_REQUEST_VERSION = {(byte) 0xAc, 0x02};  
	
	@Override
	public void receiveMessage(BroadcastDataMessage message) {
		Byte [] data = message.getData();
		if (ArrayUtils.arrayStartsWith(PARTIAL_PACKET_REQUEST_VERSION, data)) {
	        synchronized(sendData) {
	        		send = false;
	            }
			BroadcastDataMessage version = new BroadcastDataMessage();
			// format a.b.c in this case a = 0xde , b =0xad, and c is made up of 0x02,0x03 combined (big endian, 2 bytes)
			version.setData(new Byte[]{(byte) 0xad,0x02,(byte) 0xde,(byte) 0xad,0x02,0x03,0x00,0x00});
			c.send(version);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        synchronized(sendData) {
        		send = true;
            }
		}
	}
	  
  };
  
  @Test
  public void test_calibration_soft_version() throws InterruptedException, TimeoutException {
    
    model.setCalibrationValue(CALIBRATION_VALUE);
    model.setPower(200);
    model.setCadence(50);
    model.setBalance((byte) 50);
    
    doStartUp();
    
    c.registerRxListener(versionListener, BroadcastDataMessage.class);
    
    sendData.start();
    
    //changeSpeedOnly.start();
    
    //doSpeedUp();
    
    sendData.join();
    
    
    
  }
  
  //@Test
  public void startStop() throws InterruptedException, TimeoutException {
    n.start();
    n.stop();
    n.start();
    n.stop();
    n.start();
    n.stop();
    
    
    
  }


  
  
  

}
