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

import java.util.ArrayList;
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
import org.cowboycoders.ant.NetworkKeys;
import org.cowboycoders.ant.Node;
import org.cowboycoders.ant.Receipt;
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
import org.cowboycoders.ant.messages.data.BroadcastDataMessage;
import org.cowboycoders.ant.messages.responses.CapabilityResponse;
import org.cowboycoders.ant.utils.ByteUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;



public class AntTransceiverTest {
  
  private static AntTransceiver antchip = new AntTransceiver(0);
  
  @BeforeClass 
  public static void beforeClass() {
    AntTransceiver.LOGGER.setLevel(Level.ALL);
    ConsoleHandler handler = new ConsoleHandler();
    handler.setLevel(Level.ALL);
    AntTransceiver.LOGGER.addHandler(handler);

  }
  
  @AfterClass
  public static void afterClass() {
    antchip.stop();
  }
  
  @Before
  public void before() throws InterruptedException {
    //Thread.sleep(1000);
  }
  
  //@Test
  public void functionality_runthrough() throws InterruptedException {
    AntTransceiver ant = new AntTransceiver(0);
    ant.start();
    
      StandardMessage msg = new ResetMessage();
      //StandardMessage msg = new BroadcastDataMessage();
      
      ant.send(msg.encode());
      
      
      msg = new ChannelRequestMessage(
          0,ChannelRequestMessage.Request.CAPABILITIES );
      
      ant.send(msg.encode());
      
      int i = 0;
      //while (++i < 10) {
       // msg = new ChannelRequestMessage(
        //    0,ChannelRequestMessage.Request.CAPABILITIES );
      //  ant.send(msg.encode());
     // }

    
    Thread.sleep(10000);
    
    
    ant.stop();
  }
  
 //@Test
  public void basic_node() throws InterruptedException, TimeoutException {
    AntTransceiver ant = antchip;
    
    ant.start();
    
    
    
    StandardMessage msg = new ResetMessage();
    
    //ant.send(msg.encode());
    //ant.send(msg.encode());
    //ant.send(msg.encode());
    
    //Thread.sleep(1000);
    
    
    Node n = new Node(ant);
    n.start();
    
    //n.reset();
    
    MessageCondition condition = new MessageCondition() {

		@Override
		public boolean test(StandardMessage msg) {
			return true;
		}
    	
    };
    
    Receipt receipt = new Receipt();
    
    n.sendAndWaitForMessage(msg, condition, null, null, null, receipt);
    
    System.out.println(receipt.getLastSent().getTimestamp());

    System.out.println(receipt.getLastReceived().getTimestamp());
    
    Thread.sleep(1000);
    
    n.stop();
    
    
    
  }
  
  //@Test
  public void test_init() throws InterruptedException, TimeoutException {
    //Object mBound = bindService(new Intent(MainActivity.getAppContext(), DummyService.class));
    //antchip.start();
    Node n = new Node(antchip);
    //antchip.stop();
    
    //Thread.sleep(1000);
    //EventMachine em = new EventMachine(antchip);
    //em.start();
    try {
    n.start();
    
    
    //Thread.sleep(1000);
    } finally {
      try {
        //n.stop();
      } catch (AntError e) {
        
      }
     
      
    }
    
    //Thread.sleep(3000);
    
    // try expose a race condition or dead lock
    int i = 0;
    while (++i < 1000
    		
    		) {
      StandardMessage capabilitiesMessage = new ChannelRequestMessage(
          0,ChannelRequestMessage.Request.CAPABILITIES );
      StandardMessage capabilitiesResponse;
      MessageCondition condition = MessageConditionFactory.newInstanceOfCondition(CapabilityResponse.class);
      try {
        capabilitiesResponse = n.sendAndWaitForMessage(
            capabilitiesMessage, 
            condition,
            10L,TimeUnit.SECONDS,null,null
            );
        System.out.println(((CapabilityResponse)capabilitiesResponse).getMaxNetworks());
      } catch (InterruptedException e) {
        throw new AntError(e);
      } catch (TimeoutException e) {
        throw new AntError(e);
      }
      //Thread.sleep(100);
    }
    
    
    //Thread.sleep(10000);
    
    n.stop();
  }
  
public static boolean arrayStartsWith(Byte[] pattern, Byte[] data){

	for (int i=0; i<pattern.length; i++) {
		if (data[i] != pattern[i]) {
			return false;
		}
	}
	return true;
}

public static Byte [] iA2bA(int[] intArray){
	Byte [] byteArray = new Byte [intArray.length];
	for (int i=0; i < intArray.length; i++){
		byteArray[i] = (byte)intArray[i];
	}
	return byteArray;
}

class BushidoData {
	
	//TODO cleanup this bodge 
	boolean initState = true;
	
	ArrayList<Double> speedArray = new ArrayList<Double>(); 
	ArrayList<Double> powerArray = new ArrayList<Double>(); 
	ArrayList<Double> cadenceArray = new ArrayList<Double>(); 
	ArrayList<Double> distanceArray = new ArrayList<Double>(); 
	ArrayList<Double> heartRateArray = new ArrayList<Double>(); 
	ArrayList<Double> slopeArray = new ArrayList<Double>(); 
	
	public void logSpeed(double speed) {
		speedArray.add(speed);
	}
	public void logPower(double power) {
		powerArray.add(power);
	}
	public void logCadence(double cadence) {
		cadenceArray.add(cadence);
	}
	public void logDistance(double distance) {
		distanceArray.add(distance);
	}
	public void logHeartRate(double heartRate) {
		heartRateArray.add(heartRate);
	}
	private void logSlope(double slope) {
		slopeArray.add(slope);
	}
	public double getSlope() {
		return slopeArray.get(slopeArray.size()-1);
	}
	public double getDistance() {
		return distanceArray.get(distanceArray.size()-1);
	}
	public Byte[] setSlope(double slope){
		logSlope(slope);
		//DC01 Packet prototype
		Byte[] dc01Packet = {(byte)0xdc, 0x01, 0x00, 0x00, 0x00, 0x4d, 0x00, 0x00};
		if (slope < 0){
			dc01Packet[3] = (byte) 0xFF;
			dc01Packet[4] = (byte) (256 + slope*10);
		} else {
			dc01Packet[4] = (byte) (slope*10);
		}
			
		return dc01Packet;
	}
	public Byte[] keepAlive() {
		Byte[] dc02Packet = {(byte)0xdc, 0x02, 0x00, (byte)0x99, 0x00, 0x00, 0x00, 0x00};
		return dc02Packet;
	}
	
	public Byte[] getByte() {
		if (initState) {
			initState = false;
			return setSlope(getSlope());
		} else {
			initState = true;
			return keepAlive();
		}
	}
	
}
    
//@Test
public void testCompare(){
	int[] packet = {0xad , 0x01 , 0x03 , 0x0a , 0x00 , 0x00, 0x0a, 0x02};
	int[] pattern = {0xad, 0x01, 0x03}; 
	assertTrue(arrayStartsWith(iA2bA(pattern), iA2bA(packet)));
}
  
class Listener implements BroadcastListener<BroadcastDataMessage> {
	Byte[] data;
	double speed;
	double power;
	double cadence;
	double distance;
	double heartRate;
	Byte[] payload = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
	// Packet identifiers
	Byte[] bushidoPaused = {(byte) 0xAD , 0x01 , 0x03 , 0x0a , 0x00 , 0x00, 0x0a, 0x02};
	Byte[] bushidoLogging = {(byte) 0xAD, 0x01, 0x02};
	Byte[] bushidoDataIdentifier = {(byte) 0xDD};
	Byte[] bushidoSPCidentifier = {(byte) 0x01};
	Byte[] bushidoDHidentifier = {(byte) 0x02};
	Byte[] bushidoStatusIdentifier = {(byte) 0xAD};
	Byte[] bushidoResume = {(byte) 0xAC, 0x03, 0x02};
	
	@Override
	public void receiveMessage(BroadcastDataMessage message) {
		data = message.getData();
	    if (arrayStartsWith(bushidoDataIdentifier, data)) {
	    	data = message.getData();
	    	if (arrayStartsWith(bushidoSPCidentifier, data)) {
	    		speed = ((data[2] << 8) + data[3]) / 16.1;
	            power = (data[4] << 8) + data[5];
	            cadence = data[6];
	            System.out.println("Speed: " + speed);
	            System.out.println("Power: " + power);
	            System.out.println("Cadence: " + cadence);
	    	}
	    	if (arrayStartsWith(bushidoDHidentifier, data)) {
	    		distance = (data[2] << 24) + (data[3] << 16) + (data[4] << 8) + data[5];
	    		heartRate = data[6];
	    		System.out.println("Distance: " + distance);
	    		System.out.println("Heart rate: " + heartRate);
	    	}
	    } else if (arrayStartsWith(bushidoStatusIdentifier, data)){
	    	if (arrayStartsWith(bushidoPaused, data)){
	    		//Send un-pause command
	    		payload = bushidoResume;
	    		System.out.println("Bushido Paused");
	    	} else if (arrayStartsWith(bushidoLogging, data)){
	    		//Send keepalive
	    		//payload = bushidoData.getByte()
		    }
		}
	}
}
  
  
  @Test
  public void test_hrm() throws InterruptedException, TimeoutException {
    
    int repeats = 10;
    
    Node n = new Node(antchip);
    
    n.start();
    n.reset();
    
    
    Channel c;
    assertNotNull(c = n.getFreeChannel());
    
    c.setName("C:BUSHIDO");
    
    SlaveChannelType channelType = new SlaveChannelType();
    c.assign(NetworkKeys.ANT_SPORT, channelType);
    
    c.registerRxListener(new Listener(), BroadcastDataMessage.class);
    
    c.setId(0x52, 0, 0, false);
   
    c.setFrequency(60);
    
    c.setPeriod(4096);
    
    c.setSearchTimeout(255);
    
    c.open();
    
    
    Thread.sleep(10000);
    
    c.close();
    c.unassign();
    
    n.freeChannel(c);
    n.stop();
    
  }
  
  
  
  ExecutorService ex = Executors.newSingleThreadExecutor();

  
  class Sender extends Thread {
    
    Channel c;
    
    Sender(Channel c) {
      this.c = c;
    }
    
    @Override
    public void run() {
      ex.execute(new Runnable() {

        @Override
        public void run() {
          BroadcastDataMessage msg = new BroadcastDataMessage();
          msg.setData(new byte[] {(byte) 0xde,(byte) 0xad,(byte) 0xbe,(byte) 0xef,0x00,0x00,0x00,0x00});
          MessageCondition condition = MessageConditionFactory.newResponseCondition(null, null);
          try {
            c.sendAndWaitForMessage(msg, condition, 10L, TimeUnit.SECONDS, null) ;
          } catch (InterruptedException e) {
            e.printStackTrace();
          } catch (TimeoutException e) {
            e.printStackTrace();
          }
        }
        
        
        
      });

    }
    
    public void send(ChannelMessage msg) {
      MessageCondition condition = MessageConditionFactory.newResponseCondition(null, null);
      try {
        c.sendAndWaitForMessage(msg, condition, 10L, TimeUnit.SECONDS, null) ;
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (TimeoutException e) {
        e.printStackTrace();
      }
    }
  }
  
  int count = 0;
  Object lock = new Object();
  

  
  //@Test
  public void test_master() throws InterruptedException, TimeoutException {
    
    int repeats = 10;
    
    Node n = new Node(antchip);
    
    //NetworkKey key = new NetworkKey(0xB9,0xA5,0x21,0xFB,0xBD,0x72,0xC3,0x45);
    //key.setName("N:ANT+");
    
    n.start();
    n.reset();
    
   // n.setNetworkKey(0, key);
    
    
    Channel c;
    assertNotNull(c = n.getFreeChannel());
    
    c.setName("C:HRM");
    
    MasterChannelType channelType = new MasterChannelType();
    c.assign(NetworkKeys.ANT_SPORT, channelType);
    
    
    c.setId(33, 1, 1, false);
   
    c.setFrequency(66);
    
    c.setPeriod(8192);
    
    c.setSearchTimeout(255);
    
    c.open();
    
    Sender s = new Sender(c);
    
    for (int i = 0 ; i < repeats ; i++) {
      BroadcastDataMessage msg = new BroadcastDataMessage();
      List<Byte> bytes = ByteUtils.lsbSplit(i, 4);
      msg.setData(new byte[] {(byte) 0xde,(byte) 0xad,(byte) 0xbe,(byte) 0xef,bytes.get(0),bytes.get(1),bytes.get(2),bytes.get(3)});
      MessageCondition condition = MessageConditionFactory.newResponseCondition(null, null);
      //c.enqueue(msg, condition, null, null);
    }
    
    //ex.awaitTermination(10, TimeUnit.SECONDS);
    
    Thread.sleep(5000);
    
    int length = 255;
    byte [] test = new byte[length];
    for (int i = 0 ; i < length ; i ++) {
     test[i] = (byte) i;
   }
    
    try {
      c.sendBurst(test, 10L, TimeUnit.SECONDS);
      System.out.println("transfer completed");
    } catch (TransferException e) {
      System.out.println(e);
    }
    
    
    
    Thread.sleep(10000);
    
    c.close();
    c.unassign();
    
    n.freeChannel(c);
    n.stop();
    
  }
  
  

}
