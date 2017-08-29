package org.cowboycoders.ant.interfaces;

import static org.junit.Assert.*;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import org.cowboycoders.ant.events.BroadcastListener;
import org.cowboycoders.ant.events.BroadcastMessenger;
import org.cowboycoders.ant.messages.StandardMessage;
import org.cowboycoders.ant.utils.ByteUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class AntTransceiverTest {
	
	  @BeforeClass 
	  public static void beforeClass() {
	    AntTransceiver.LOGGER.setLevel(Level.ALL);
	    ConsoleHandler handler = new ConsoleHandler();
	    // PUBLISH this level
	    handler.setLevel(Level.ALL);
	    //AntTransceiver.LOGGER.addHandler(handler);
	  }
	  
	  byte [] message = new byte [] {(byte)0x20, (byte)0xae, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0};
	  
	  int count = 0;
	  BroadcastListener<byte[]> listener = new BroadcastListener<byte[]>() {


		@Override
		public void receiveMessage(byte[] message) {
			for (byte b : message) {
				System.out.printf("%02x, ", b);
			}
			System.out.println();
			assertEquals(true,ByteUtils.arrayStartsWith(AntTransceiverTest.this.message, message));
			count++;
		}
		  
	  };

	@Test
	public void test_usbreader() throws InterruptedException {
		AntTransceiver ant = new AntTransceiver();
		AntTransceiver.UsbReader reader = (ant). new UsbReader(); 
		BroadcastMessenger<byte[]> mess = new BroadcastMessenger<byte[]>();
		mess.addBroadcastListener(listener);
		ant.registerRxMesenger(mess);
		byte [] buff = new byte [] {(byte)0xa4, (byte)0x20, (byte)0xae, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x2a, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0,(byte)0xa4, (byte)0x20, (byte)0xae, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x2a, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0, (byte)0x0};
		reader.processBuffer(buff,5);
		// hope its done within a second
		//Thread.sleep(1000);
		//assertEquals(2,count);
	}

}
