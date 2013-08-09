package org.cowboycoders.ant;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import net.vidageek.mirror.dsl.Mirror;

import static org.junit.Assert.*;

import org.junit.Test;

public class NetworkHandleTest {

	@Test (expected=IllegalStateException.class) 
	public void checkFreeCanOnlyBeCalledOnce() {
		NetworkKey key = new NetworkKey(1,2,3,4,5,6,7,8);
		Network n = new Network(0, key, null);
		NetworkHandle h = new NetworkHandle(n);
		assertEquals(key,h.getNetworkKey());
		h.free();
		h.getNetworkKey();
	}
	
	@Test 
	public void checkRefCountIncremented() {
		NetworkKey key = new NetworkKey(1,2,3,4,5,6,7,8);
		Network n = new Network(0, key, null);
		ArrayList<NetworkHandle> handles = new ArrayList<NetworkHandle>();
		for (int i = 0 ; i < 10 ; i++) {
			NetworkHandle h = new NetworkHandle(n);
			handles.add(h);
		}
		AtomicInteger count = (AtomicInteger) new Mirror().on(n).get().field("refCount");
		assertEquals(10, count.get());
		for (NetworkHandle h : handles) {
			h.free();
		}
		assertEquals(0, count.get());
	}
	

}
