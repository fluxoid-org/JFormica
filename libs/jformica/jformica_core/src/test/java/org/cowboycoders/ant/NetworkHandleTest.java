/**
 *     Copyright (c) 2013, Will Szumski
 *
 *     This file is part of formicidae.
 *
 *     formicidae is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     formicidae is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with formicidae.  If not, see <http://www.gnu.org/licenses/>.
 */
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
