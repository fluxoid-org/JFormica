package org.cowboycoders.ant.messages;

import static org.junit.Assert.*;

import org.cowboycoders.ant.ChannelId;
import org.cowboycoders.ant.messages.Constants.DataElement;
import org.junit.Test;

public class ExtendedMessageTest {
	
	private static final int SAMPLE_RSSI_VALUE = 67;
	private static final int SAMPLE_RSSI_MEASUREMENT = 23;
	private static final int SAMPLE_RSSI_THRESH = 9; 
	private static final int SAMPLE_TIMESTAMP = 9999;
	
	private static ChannelId TEST_ID = ChannelId.Builder.newInstance()
    		.setDeviceNumber(31769)
    		.setDeviceType(120)
    		.setTransmissonType(0)
    		.setPairingFlag(true)
    		.build();

	@Test
	public void shouldReturnSameValuesAsSet() {
    	ExtendedMessage msg = new ExtendedMessage();
    	msg.setChannelId(TEST_ID);
    	
    	// tweak some values (these change the number of extended bytes)
    	setSampleRssi(msg);
    	msg.setDataElement(DataElement.RX_TIMESTAMP, SAMPLE_TIMESTAMP);
    	
    	// this will remove some extended bytes
    	msg.setDataElement(DataElement.RX_TIMESTAMP, null);
    	
    	assertChannelIdCorrectlySet(msg);
    	assertRssiCorrectlySet(msg);
	}
	
	public void setSampleRssi(ExtendedMessage msg) {
		msg.setDataElement(DataElement.RSSI_VALUE, SAMPLE_RSSI_VALUE);
		msg.setDataElement(DataElement.RSSI_THRESHOLD_CONFIG, SAMPLE_RSSI_THRESH);
		msg.setDataElement(DataElement.RSSI_MEASUREMENT_TYPE, SAMPLE_RSSI_MEASUREMENT);
	}
	
	
	public void assertChannelIdCorrectlySet(ExtendedMessage msg) {
    	assertEquals(TEST_ID.getDeviceNumber(),msg.getDeviceNumber().intValue());
    	assertEquals(TEST_ID.getDeviceType(),msg.getDeviceType().intValue());
    	assertEquals(TEST_ID.getTransmissonType(),msg.getTransmissionType().intValue());
    	assertEquals(TEST_ID.isPairingFlagSet(),msg.isPairingFlagSet());
	}
	
	public void assertRssiCorrectlySet(ExtendedMessage msg) {
		assertEquals(SAMPLE_RSSI_VALUE,msg.getRssiValue().intValue());
		assertEquals(SAMPLE_RSSI_THRESH,msg.getRssiThresholdConfig().intValue());
		assertEquals(SAMPLE_RSSI_MEASUREMENT,msg.getRssiMeasurementType().intValue());
	}
	
	public void assertChannelIdCorrectlyCleared(ExtendedMessage msg) {
    	assertEquals(null,msg.getDeviceNumber());
    	assertEquals(null,msg.getDeviceType());
    	assertEquals(null,msg.getTransmissionType());
    	assertEquals(null,msg.isPairingFlagSet());
	}
	
	@Test
	public void shouldSetCorrectBytesinPacket() {
		ExtendedMessage msg = new ExtendedMessage();
		msg.setDataElement(DataElement.RX_TIMESTAMP, SAMPLE_TIMESTAMP);
		msg.setChannelId(TEST_ID);
		setSampleRssi(msg);
		
		// this hasn't been verified but is assumed to be correct
		byte [] expectedBytes = new byte [] {19,0,0,0,0,0,0,0,0,0,0,-32,15,39,23,9,67,25,124,-8,0};
		assertArrayEquals(expectedBytes, msg.encode());
	}
	
	@Test
	public void shouldClearChannelId() {
    	ExtendedMessage msg = new ExtendedMessage();
    	msg.setChannelId(TEST_ID);
    	msg.setDataElement(DataElement.RX_TIMESTAMP, SAMPLE_TIMESTAMP);
    	msg.setChannelId(null);
    	setSampleRssi(msg);
    	assertChannelIdCorrectlyCleared(msg);
	}
	
    
   

}
