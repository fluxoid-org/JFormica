package org.cowboycoders.ant;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.vidageek.mirror.dsl.Mirror;

import org.cowboycoders.ant.defines.AntDefine;
import org.cowboycoders.ant.events.BroadcastListener;
import org.cowboycoders.ant.interfaces.AbstractAntTransceiver;
import org.cowboycoders.ant.messages.data.BurstDataMessage;
import org.cowboycoders.ant.messages.nonstandard.CombinedBurst;
import org.cowboycoders.ant.messages.nonstandard.CombinedBurst.StatusFlag;
import org.cowboycoders.ant.utils.BurstMessageSequenceGenerator;
import org.cowboycoders.ant.utils.ByteUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class ChannelTest {

    Channel channel;


    @Before
    public void setup() {
        Node node = new Node(mock(AbstractAntTransceiver.class));
        channel = new Channel(node, 0);
    }
    
    public List<BurstDataMessage> genBurst(byte [] data) {
		final List<byte[]> list = ByteUtils.splitByteArray(data,
				AntDefine.ANT_STANDARD_DATA_PAYLOAD_SIZE);
		final BurstMessageSequenceGenerator generator = new BurstMessageSequenceGenerator();
		final List<BurstDataMessage> rtn = new ArrayList<BurstDataMessage>();
		
		for (int i = 0; i < list.size() - 1; i++) {
			BurstDataMessage msg = new BurstDataMessage();
			msg.setData(list.get(i));
			msg.setSequenceNumber(generator.next());
			rtn.add(msg);

		}

		BurstDataMessage msg = new BurstDataMessage();
		msg.setData(list.get(list.size() - 1));
		msg.setSequenceNumber(generator.finish());
		rtn.add(msg);
		
		return rtn;
		
    }
    
    @Test
    public void shouldNotDiscardMessageCausingSequenceError() throws InterruptedException {
        final List<CombinedBurst> bursts = new ArrayList<CombinedBurst>();
        final Lock syncLock = new ReentrantLock();
        final Condition allBurstsArrived = syncLock.newCondition();
        final int burstsSent = 2;
    	
    	channel.registerBurstListener(new BroadcastListener<CombinedBurst>() {

			@Override
			public void receiveMessage(CombinedBurst message) {
				bursts.add(message);
				
				if (bursts.size() >= burstsSent) {
					try {
						syncLock.lock();
						allBurstsArrived.signalAll();
					} finally {
						syncLock.unlock();
					}
				}
				
			}
    		
    	});
    	@SuppressWarnings("unchecked")
		BroadcastListener<BurstDataMessage> listener = (BroadcastListener<BurstDataMessage>) new Mirror().on(channel).get().field("burstListener");
    	
    	// send a partial sequence ...
    	
    	byte [] data = new byte[100];
    	byte magicValue1 = 0x41;
    	// make sure this is within the number of packets we send x 8 bytes
    	int magicIndex1 = 7;
		data[magicIndex1] = magicValue1;
		
    	
    	int count = 0;
    	for (BurstDataMessage msg : genBurst(data)) {
    		listener.receiveMessage(msg);
    		if (++count > 2) {
    			break;
    		}
    	}
    	
    	// followed by complete burst ...
    	
    	// insert a magic value to identify this packet
    	byte [] data2 = new byte[100];
    	byte magicValue2 = 0x29;
    	int magicIndex2 = 23;
		data2[magicIndex2] = magicValue2;
		
    	for (BurstDataMessage msg : genBurst(data2)){
    		listener.receiveMessage(msg);
    	}
    	
    	try {
    		syncLock.lock();
    		
    		while (bursts.size() < burstsSent) {
    
    			if (!allBurstsArrived.await(100, TimeUnit.MILLISECONDS)) {
    				throw new RuntimeException("timeout waiting for bursts to arrive");
    			}
    		}
    		
		} finally {
			syncLock.unlock();
		}
    	
    	assertTrue(bursts.get(0).getStatusFlags().contains(StatusFlag.ERROR_SEQUENCE_INVALID));
    	assertTrue(bursts.get(1).isComplete());
    	
    	// check magic packets
    	count = 0;
    	for (byte b : bursts.get(0).getData()) {
    		//System.out.print(b + " ");
    		//System.out.println(data[count]);
    		assertEquals(data[count], b);
    		count++;
    	}
    	
    	//System.out.println("next ...");
    	
    	count = 0;
    	for (byte b : bursts.get(1).getData()) {
    		//System.out.print(b + " ");
    		//System.out.println(data2[count]);
    		
    		// we have reached the padded bytes
    		if (count >= data2.length) {
    			break;
    		}
    		
    		assertEquals(data2[count], b);
    		count++;
    	}
    	
    	boolean receivedAtLeastAsLongSent = (bursts.get(1).getData().length >= data2.length);
    	assertTrue(receivedAtLeastAsLongSent);
    }


}
