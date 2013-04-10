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
import java.math.BigInteger;
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
import org.cowboycoders.ant.utils.AntLoggerImpl;
import org.cowboycoders.ant.utils.ArrayUtils;
import org.cowboycoders.ant.utils.ByteUtils;
import org.cowboycoders.turbotrainers.Parameters;
import org.cowboycoders.turbotrainers.TurboTrainerDataListener;
import org.cowboycoders.turbotrainers.bushido.brake.CalibrationState;
import org.cowboycoders.turbotrainers.bushido.headunit.BushidoHeadunit;
import org.cowboycoders.utils.SimpleCsvLogger;
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
      model.setCalibrationState(CalibrationState.CALIBRATION_MODE);
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
  
  
  public void doSending() throws InterruptedException, TimeoutException {
    BroadcastDataMessage msg = new BroadcastDataMessage();
    msg.setData(model.getDataPacket());
    c.sendAndWaitForMessage(msg, CONDITION_CHANNEL_TX , 10l, TimeUnit.SECONDS, null);

  }
  
  boolean send = true;
  
  Thread sendData = new Thread() {
    
    public void run() {
      
      while(true) {
        if (Thread.interrupted()) {
          return;
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
          try {
			doSending();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		} catch (TimeoutException e) {
			e.printStackTrace();
          	return;
		}
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
    
    model.setCalibrationValue(15);
    
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
  
  //@Test
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
    
    Thread.sleep (10000);
    
    sendData.interrupt();
    
    System.out.println(sendData.isAlive());
    
    //sendData.join();
    
    
    
  }
  
  boolean f = true;
  
 
  
  private final BroadcastListener<BroadcastDataMessage> resistanceListener = new BroadcastListener<BroadcastDataMessage>() {

	private  Byte[] PARTIAL_PACKET_RESISTANCE = {(byte) 0x01};  
	
	@Override
	public void receiveMessage(BroadcastDataMessage message) {
		Byte [] data = message.getData();
		if (ArrayUtils.arrayStartsWith(PARTIAL_PACKET_RESISTANCE, data)) {
			int resistance = (int) (new BigInteger(new byte[]{data[1],data[2]})).longValue();
			System.out.println("wheel speed: " + wheelSpeed);
			System.out.println("resistance: " + resistance);
//			if (f) {
//				model.setPower(200);
//				f = false;
//			} else {
//				model.setPower(500);
//				f = true;
//			}
			
			
			
			if (resistance == lastRes) {
//
//				if (weight > 100){
//				main.interrupt();
//			}				
//			log.update(weight,wheelSpeed,resistance);
//			if (wheelSpeed > 60) {
//				wheelSpeed = 0;
//				weight += 10;
//			} else {
//				wheelSpeed += 0.5;
//			}
//			b.setWeight(weight);
//			model.setWheelSpeed(wheelSpeed);
//			
//							
//				
//		

				//
				if (weight > 120){
				main.interrupt();
			}				
			log.update(weight,slope,wheelSpeed,resistance);
			if (wheelSpeed > 60)
			{
				wheelSpeed = 0;
				if (slope > 20) {
					slope = -5;
					weight += 5;
				} else {
					slope += 0.5;
				}
				
			} else {
				wheelSpeed += 0.5;
			}
			Parameters.Builder builder = new Parameters.Builder(weight,0);
			b.setParameters(builder.buildTargetSlope(slope));
			model.setWheelSpeed(wheelSpeed);
			
							
		
				
				
				
				
//				if (weight > 0.1){
//					main.interrupt();
//				}				
//				log.update(weight,slope,resistance);
//				if (slope > 20) {
//					slope = -5;
//					weight += 5;
//				} else {
//					slope += 0.1;
//				}
//				b.setWeight(weight);
//				b.setSlope(slope);
				
				
//				log.update(weight,resistance);
//				slope += 0.1;
//				if (slope > 20) {
//					main.interrupt();
//				}
			}
			
			lastRes = resistance;
			

			//b.setWeight(weight += 10);
			//model.setWheelSpeed(wheelSpeed += 1);
			//if (power <= 300) model.setPower(power += 10);
			//else model.setPower(0);
		}
	}
	  
  };
  
  int lastRes = Integer.MIN_VALUE;
  double wheelSpeed = 0;
  double power =0;
  double weight = 50;
  double slope = -5;
  double virtualSpeed = 0;
  double startPower = 200;
  double startCadence = 50;
  byte startBalance = 50;
  double startWheelSpeed = 0.00;
  Thread main;
  
  
private SimpleCsvLogger log;
  
  //@Test
  public void test_actual_speed_brake_ressitance() throws InterruptedException, TimeoutException {
    
    model.setCalibrationValue(CALIBRATION_VALUE);
    //model.setCalibrationValue(10);
    model.setPower(startPower);
    model.setCadence(startCadence);
    model.setBalance(startBalance);
    model.setWheelSpeed(startWheelSpeed);
    model.setLeftPower(900);
    model.setRightPower(100);
    //log = new SimpleCsvLogger("logs", "slope_resistance_constant_speed.log","weight", "slope", "resistance");
    log = new SimpleCsvLogger("logs", "weight_slope_speed_resistance.log","weight","slope", "speed", "resistance");
	//log = new SimpleCsvLogger("logs", "speed_resistance_constant_slope.log","weight","speed", "resistance");
	log.addTime(false);
	log.append(true);
	log.setComment("power: " + startPower 
			+ " speed: " + startWheelSpeed 
			+ " balance: " + startBalance
			+ " cadence: " + startCadence
			+ " weight: " + weight
			+ " slope: " + slope
			);
    
    startHeadunit();
    
    main = Thread.currentThread();
    
	Parameters.Builder builder = new Parameters.Builder(weight,0);
	b.setParameters(builder.buildTargetSlope(slope));
    
    doStartUp();
    
    c.registerRxListener(resistanceListener, BroadcastDataMessage.class);
    
    sendData.start();
    

    
    while(true) {
    	if (Thread.interrupted()) {
    		break;
    	}
        try {
        	Thread.sleep (6000000);
        } catch (Exception e) {
        	break;
        }
    }
    
    sendData.interrupt();
    
    System.out.println(sendData.isAlive());
    
    stopHeadUnit();
    
    //sendData.join();
    
    
    
  }
  
  double lastVirtualSpeed = Integer.MIN_VALUE;
  
  @Test
  public void test_get_virt_speed() throws InterruptedException, TimeoutException {
	  
	  lastRes = Integer.MIN_VALUE;
	  wheelSpeed = 10;
	  power =1750;
	  weight = 60;
	  slope = 17.5;
	  virtualSpeed = 0;
	  startPower = 1750;
	  startCadence = 50;
	  startBalance = 50;
	  startWheelSpeed = 10;
	    
	    model.setCalibrationValue(CALIBRATION_VALUE);
	    //model.setCalibrationValue(10);
	    model.setPower(startPower);
	    model.setCadence(startCadence);
	    model.setBalance(startBalance);
	    model.setWheelSpeed(startWheelSpeed);
	    model.setLeftPower(900);
	    model.setRightPower(100);
	    //log = new SimpleCsvLogger("logs", "slope_resistance_constant_speed.log","weight", "slope", "resistance");
	    log = new SimpleCsvLogger("logs", "virtual_speed.log","weight","slope","power" ,"virtual speed", "resistance");
		//log = new SimpleCsvLogger("logs", "speed_resistance_constant_slope.log","weight","speed", "resistance");
		log.addTime(false);
		log.append(true);
		log.setComment("power: " + startPower 
				+ " speed: " + startWheelSpeed 
				+ " balance: " + startBalance
				+ " cadence: " + startCadence
				+ " weight: " + weight
				+ " slope: " + slope
				);
	    
		// bodge
		dataListener = dataListener2;
		
	    startHeadunit();
	    
	    main = Thread.currentThread();
	    
		Parameters.Builder builder = new Parameters.Builder(weight,0);
		b.setParameters(builder.buildTargetSlope(slope));
	    
	    doStartUp();
	    
	    c.registerRxListener(resistanceListener2, BroadcastDataMessage.class);
	    
	    sendData.start();
	    

	    
	    while(true) {
	    	if (Thread.interrupted()) {
	    		break;
	    	}
	        try {
	        	Thread.sleep (6000000);
	        } catch (Exception e) {
	        	break;
	        }
	    }
	    
	    sendData.interrupt();
	    
	    System.out.println(sendData.isAlive());
	    
	    stopHeadUnit();
	    
	    //sendData.join();
	    
	    
	    
	  }
	  
  double resistance;
  
  private final BroadcastListener<BroadcastDataMessage> resistanceListener2 = new BroadcastListener<BroadcastDataMessage>() {

	private  Byte[] PARTIAL_PACKET_RESISTANCE = {(byte) 0x01};  
	
	@Override
	public void receiveMessage(BroadcastDataMessage message) {
		Byte [] data = message.getData();
		if (ArrayUtils.arrayStartsWith(PARTIAL_PACKET_RESISTANCE, data)) {
			int resistance = (int) (new BigInteger(new byte[]{data[1],data[2]})).longValue();
			//System.out.println("wheel speed: " + wheelSpeed);
			//System.out.println("resistance: " + resistance);
			BushidoBrakeTest.this.resistance = resistance;
//			if (f) {
//				model.setPower(200);
//				f = false;
//			} else {
//				model.setPower(500);
//				f = true;
//			}
			
			
			
			

		}
	}
	  
  };
  
  //@Test
  public void startStop() throws InterruptedException, TimeoutException {
    n.start();
    n.stop();
    n.start();
    n.stop();
    n.start();
    n.stop();
    
    
    
  }


  
  TurboTrainerDataListener dataListener = new TurboTrainerDataListener() {

	    @Override
	    public void onSpeedChange(double speed) {
	    	
	      virtualSpeed = speed;
	      
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
	  
	  TurboTrainerDataListener dataListener2 = new TurboTrainerDataListener() {

		    @Override
		    public void onSpeedChange(double speed) {
		    	
		    //FIXME: make sure ppower has actualy been sent ie check last val recieved is what we set
		      virtualSpeed = speed;
		      System.out.println("speed from headunit:" +speed);
				if (speed == lastVirtualSpeed) {
					if (weight > 120){
					main.interrupt();
				}				
				log.update(weight,slope,power,speed,resistance);
				if (power > 2000)
				{
					power = 0;
					if (slope > 20) {
						slope = -5;
						weight += 5;
					} else {
						slope += 0.5;
					}
					
				} else {
					power += 10;
				}
				Parameters.Builder builder = new Parameters.Builder(weight,0);
				b.setParameters(builder.buildTargetSlope(slope));
				model.setPower(power);
				//model.setWheelSpeed(wheelSpeed);
				
								
			
					
					
			
				}
				
				lastVirtualSpeed = speed;
		      
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
	  Node headunit;
	  
	  AntLoggerImpl antLogger = new AntLoggerImpl();
	  
	  public void startHeadunit() throws InterruptedException, TimeoutException {
	    headunit = new Node(new AntTransceiver(1));
	    n.registerAntLogger(antLogger);
	    b = new BushidoHeadunit(headunit);
	    b.registerDataListener(dataListener);
	    b.startConnection();
	    b.resetOdometer();
	    b.startCycling();
	  }
	  
	  public void stopHeadUnit() throws InterruptedException, TimeoutException {
		  b.stop();
		  headunit.stop();
	  }
  

}
