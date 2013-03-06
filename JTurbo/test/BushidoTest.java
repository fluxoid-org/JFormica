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

import static org.junit.Assert.*;


import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import org.cowboycoders.ant.AntError;
import org.cowboycoders.ant.Channel;
import org.cowboycoders.ant.NetworkKey;
import org.cowboycoders.ant.Node;
import org.cowboycoders.ant.TransferException;
import org.cowboycoders.ant.events.BroadcastListener;
import org.cowboycoders.ant.events.MessageCondition;
import org.cowboycoders.ant.events.MessageConditionFactory;
import org.cowboycoders.ant.interfaces.AntTransceiver;
import org.cowboycoders.ant.messages.ChannelMessage;
import org.cowboycoders.ant.messages.MasterChannelType;
import org.cowboycoders.ant.messages.SlaveChannelType;
import org.cowboycoders.ant.messages.StandardMessage;
import org.cowboycoders.ant.messages.commands.ChannelRequestMessage;
import org.cowboycoders.ant.messages.commands.ResetMessage;
import org.cowboycoders.ant.messages.data.AcknowledgedDataMessage;
import org.cowboycoders.ant.messages.data.BroadcastDataMessage;
import org.cowboycoders.ant.messages.responses.CapabilityResponse;
import org.cowboycoders.ant.utils.AntLoggerImpl;
import org.cowboycoders.ant.utils.ArrayUtils;
import org.cowboycoders.ant.utils.ByteUtils;
import org.cowboycoders.ant.utils.ChannelMessageSender;
import org.cowboycoders.turbotrainers.Mode;
import org.cowboycoders.turbotrainers.Parameters;
import org.cowboycoders.turbotrainers.TurboTrainerDataListener;
import org.cowboycoders.turbotrainers.bushido.headunit.BushidoBroadcastDataListener;
import org.cowboycoders.turbotrainers.bushido.headunit.BushidoButtonPressDescriptor;
import org.cowboycoders.turbotrainers.bushido.headunit.BushidoButtonPressListener;
import org.cowboycoders.turbotrainers.bushido.headunit.BushidoTargetSlopeModel;
import org.cowboycoders.turbotrainers.bushido.headunit.BushidoHeadunit;
import org.cowboycoders.turbotrainers.bushido.headunit.BushidoButtonPressDescriptor.Button;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;



public class BushidoTest {
  
  private static AntTransceiver antchip = new AntTransceiver(0);
  
  @BeforeClass 
  public static void beforeClass() {
    AntTransceiver.LOGGER.setLevel(Level.SEVERE);
    ConsoleHandler handler = new ConsoleHandler();
    // PUBLISH this level
    handler.setLevel(Level.ALL);
    AntTransceiver.LOGGER.addHandler(handler);
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
  
  @Before
  public void before() throws InterruptedException {
    //Thread.sleep(1000);
  }
  
  Parameters.Builder builder = new Parameters.Builder(65,10);
  
  double oldSlope = 0;
  
  BushidoButtonPressListener buttonPressListener = new BushidoButtonPressListener() {

    @Override
    public void onButtonPressFinished(BushidoButtonPressDescriptor descriptor) {
      System.out.println("Button :" + descriptor.getButton());
      System.out.println("Duration :" + descriptor.getDuration());
      if(descriptor.getButton() == Button.UP) {
    	double slope = 5 + oldSlope;
        b.setParameters(builder.buildTargetSlope(slope));
        oldSlope = slope;
        printSlope();
      } else if (descriptor.getButton() == Button.DOWN) {
    	  double slope = -5 + oldSlope;
          b.setParameters(builder.buildTargetSlope(slope));
          oldSlope = slope;
        printSlope();
      }
      
    }

    @Override
    public void onButtonPressActive(BushidoButtonPressDescriptor descriptor) {
      System.out.println("Duration active :" + descriptor.getDuration());
    }
    
    public void printSlope() {
      System.out.println("Slope: " + oldSlope);
  }

    
  };
  
  TurboTrainerDataListener dataListener = new TurboTrainerDataListener() {

    @Override
    public void onSpeedChange(double speed) {
      System.out.println(speed);
      
    }

    @Override
    public void onPowerChange(double power) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void onCadenceChange(double cadence) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void onDistanceChange(double distance) {
     //System.out.println("Distance: " + distance);
     //System.out.println("Distance real: " + b.getRealDistance());
      
    }

    @Override
    public void onHeartRateChange(double heartRate) {
      // TODO Auto-generated method stub
      
    }
    
    
  };

  BushidoHeadunit b;
  
  AntLoggerImpl antLogger = new AntLoggerImpl();
  
  @Test
  public void testBushido() throws InterruptedException, TimeoutException {
    Node n = new Node(BushidoTest.antchip);
    n.registerAntLogger(antLogger);
    b = new BushidoHeadunit(n);
    b.registerButtonPressListener(buttonPressListener);
    b.registerDataListener(dataListener);
    b.setMode(Mode.TARGET_SLOPE);
    b.startConnection();
    b.resetOdometer();
    b.startCycling();
    
    //Thread.sleep(200);
    //BroadcastDataMessage msg = new BroadcastDataMessage();
    //msg.setData(new byte [] {(byte) 0xac,0x03,0x03,0x00,0x00,0x00,0x00,0x00});
    //b.getMessageSender().sendMessage(msg);
    //b.getMessageSender().sendMessage(msg);
    //b.getMessageSender().sendMessage(msg);
    Thread.sleep(60000);
    b.stop();
    n.stop();
  }
  
  //@Test
  public void testByteShift() {
    Byte [] data = new Byte [] {(byte) 255,(byte) 255,(byte) 255,(byte) 255,(byte) 255,(byte) 255,(byte) 255};
    int [] unsignedData = ByteUtils.unsignedBytesToInts(data);
    double distance = ((long)unsignedData [2] << 24) + (unsignedData [3] << 16) + (unsignedData [4] << 8) + unsignedData [5];
    System.out.println(distance);
    assertTrue(distance > 0);
  }
  
  


}
