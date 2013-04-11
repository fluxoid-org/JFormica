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


import java.io.IOException;
import java.math.BigInteger;
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
import org.cowboycoders.ant.utils.AntUtils;
import org.cowboycoders.ant.utils.ArrayUtils;
import org.cowboycoders.ant.utils.ByteUtils;
import org.cowboycoders.ant.utils.ByteUtils;
import org.cowboycoders.ant.utils.ChannelMessageSender;
import org.cowboycoders.ant.utils.SimplePidLogger;
import org.cowboycoders.pid.GainController;
import org.cowboycoders.pid.GainParameters;
import org.cowboycoders.pid.OutputControlParameters;
import org.cowboycoders.turbotrainers.Mode;
import org.cowboycoders.turbotrainers.TurboTrainerDataListener;
import org.cowboycoders.turbotrainers.bushido.brake.BushidoBrake;
import org.cowboycoders.turbotrainers.bushido.brake.BushidoBrake.CalibrationException;
import org.cowboycoders.turbotrainers.bushido.brake.BushidoBrake.CallibrationCallback;
import org.cowboycoders.turbotrainers.bushido.brake.ConstantResistanceController;
import org.cowboycoders.turbotrainers.bushido.brake.PidBrakeController;
import org.cowboycoders.turbotrainers.bushido.brake.SpeedResistanceMapper;
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



public class BrakeCalibrationHackingArena {
  
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
  

  
  TurboTrainerDataListener dataListener = new TurboTrainerDataListener() {

    @Override
    public void onSpeedChange(double speed) {
      System.out.println("speed: " +speed);
      
    }

    @Override
    public void onPowerChange(double power) {
    	System.out.println("power: " + power);
      
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

  BushidoBrake b;
  
  AntLoggerImpl antLogger = new AntLoggerImpl();
  
  CallibrationCallback callback = new CallibrationCallback() {

	@Override
	public void onRequestStartPedalling() {
		System.out.println("start pedalling");
		
	}

	@Override
	public void onReachedCalibrationSpeed() {
		System.out.println("stop");
		//java.awt.Toolkit.getDefaultToolkit().beep();
		try {
			linuxBeep();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void onRequestResumePedalling() {
		System.out.println("resume");
		
	}

	@Override
	public void onSuccess(double calibrationValue) {
		System.out.println("sucess: "+ calibrationValue);
		
	}

	@Override
	public void onFailure(CalibrationException exception) {
		System.out.println("failure");
		exception.printStackTrace();
		
	}

	@Override
	public void onBelowSpeedReminder(double speed) {
		System.out.println("below speed");
		
	}
	  
  };
  
  byte[] PACKET_START_CALIBRATION = AntUtils.padToDataLength(new int[]{(byte) 0x23, 0x63});
  byte[] PACKET_REQUEST_CALIBRATION_STATUS = AntUtils.padToDataLength(new int[]{(byte) 0x23, 0x58});
  byte[] PACKET_REQUEST_CALIBRATION_VALUE = AntUtils.padToDataLength(new int[]{0x23,0x4d});

  
  @Test
  public void testFixedResistanceCOntroller() throws InterruptedException, TimeoutException {
    Node n = new Node(BrakeCalibrationHackingArena.antchip);
    ConstantResistanceController mapper = new ConstantResistanceController();
    mapper.setAbsoluteResistance(100);
    b = new BushidoBrake(n,mapper);
    b.setMode(Mode.TARGET_SLOPE);
    b.registerDataListener(dataListener);
    b.startConnection();
    
	b.calibrate(callback, 100);
    

    Thread.sleep(45000);
    
    b.stop();
    n.stop();
  }
  
  private void linuxBeep() throws IOException {
	  // from http://marciowb.wordpress.com/2008/07/22/java-sound-at-my-linux-machine-its-ridiculous/
	  String data = "09azaZ09azaZ09azaZ09azaZ";
	  data += "09azaZ09azaZ09azaZ09azaZ09azaZ";
	  data += "09azaZ09azaZ09azaZ09azaZ09azaZ";
	  data += "09azaZ09azaZ09azaZ09azaZ09azaZ";
	  data += "09azaZ09azaZ09azaZ09azaZ09azaZ";
	  data += "09azaZ09azaZ09azaZ09azaZ09azaZ";
	  data += "09azaZ09azaZ09azaZ09azaZ09azaZ";
	  data += "09azaZ09azaZ09azaZ09azaZ09azaZ";
	  data += "09azaZ09azaZ09azaZ09azaZ09azaZ";
	  data += "09azaZ09azaZ09azaZ09azaZ09azAZ";
	  data += "09aZAz09aZAz09aZAz09aZAz09aZAz";
	  data += "09aZAz09aZAz09aZAz09aZAz09aZAz";
	  String[] cmd = {"sh", "-c", "echo " + data + " | aplay -r 4"};
	  Runtime.getRuntime().exec(cmd);
  }
  
  //@Test
  public void a() {
	  byte [] data = new byte[8];
	  data[4] = 0;
	  data[5] = (byte) 0x92;
	  BigInteger val = new BigInteger(new byte[] {0x00,data[4],data[5]});
	  System.out.println(val.doubleValue()/ 10.0);
  }
  

  
  
  


}
