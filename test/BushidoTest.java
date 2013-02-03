
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
import org.cowboycoders.ant.utils.ArrayUtils;
import org.cowboycoders.ant.utils.ByteMerger;
import org.cowboycoders.ant.utils.ByteUtils;
import org.cowboycoders.turbotrainers.ChannelMessageSender;
import org.cowboycoders.turbotrainers.TurboTrainerDataListener;
import org.cowboycoders.turbotrainers.bushido.headunit.BushidoBroadcastDataListener;
import org.cowboycoders.turbotrainers.bushido.headunit.BushidoButtonPressDescriptor;
import org.cowboycoders.turbotrainers.bushido.headunit.BushidoButtonPressListener;
import org.cowboycoders.turbotrainers.bushido.headunit.BushidoData;
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
  
  BushidoButtonPressListener buttonPressListener = new BushidoButtonPressListener() {

    @Override
    public void onButtonPressFinished(BushidoButtonPressDescriptor descriptor) {
      System.out.println("Button :" + descriptor.getButton());
      System.out.println("Duration :" + descriptor.getDuration());
      if(descriptor.getButton() == Button.UP) {
        b.incrementSlope(5);
        printSlope();
      } else if (descriptor.getButton() == Button.DOWN) {
        b.decrementSlope(5);
        printSlope();
      }
      
    }

    @Override
    public void onButtonPressActive(BushidoButtonPressDescriptor descriptor) {
      System.out.println("Duration active :" + descriptor.getDuration());
    }
    
    public void printSlope() {
      System.out.println("Slope: " + b.getSlope());
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
  
  @Test
  public void testBushido() throws InterruptedException, TimeoutException {
    Node n = new Node(BushidoTest.antchip);
    b = new BushidoHeadunit(n);
    b.registerButtonPressListener(buttonPressListener);
    b.registerDataListener(dataListener);
    b.startConnection();
    b.resetOdometer();
    b.startCycling();
    
    //Thread.sleep(200);
    //BroadcastDataMessage msg = new BroadcastDataMessage();
    //msg.setData(new byte [] {(byte) 0xac,0x03,0x03,0x00,0x00,0x00,0x00,0x00});
    //b.getMessageSender().sendMessage(msg);
    //b.getMessageSender().sendMessage(msg);
    //b.getMessageSender().sendMessage(msg);
    Thread.sleep(30000);
    b.stop();
    n.stop();
  }
  
  //@Test
  public void testByteShift() {
    Byte [] data = new Byte [] {(byte) 255,(byte) 255,(byte) 255,(byte) 255,(byte) 255,(byte) 255,(byte) 255};
    int [] unsignedData = ArrayUtils.unsignedBytesToInts(data);
    double distance = ((long)unsignedData [2] << 24) + (unsignedData [3] << 16) + (unsignedData [4] << 8) + unsignedData [5];
    System.out.println(distance);
    assertTrue(distance > 0);
  }
  
  


}
