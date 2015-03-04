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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Encapsulates an ant network
 * @author will
 *
 */
public class Network {
	
	/**
	 * Id of network (functions as uuid)
	 */
	private int number;
	
	private AtomicInteger refCount = new AtomicInteger();
	
	/**
	 * Ant network key associated with this network
	 */
	private NetworkKey networkKey;
	
	private NetworkListener networkListener;
	
	/**
	 * 
	 * @param number the internal ant network id
	 * @param networkKey key associated with this network
	 * @param networkListener used to register for notifications
	 */
	protected Network(int number, NetworkKey networkKey,
			NetworkListener networkListener) {
		super();
		this.number = number;
		this.networkKey = networkKey;
		this.networkListener = networkListener;
	}
	
	protected Network(Network network) {
		this(network.getNumber(),network.getNetworkKey(),network.getNetworkListener());
		refCount = network.refCount;
	}
	
	/**
	 * Register that you are using this network and it should not be reassigned.
	 */
	protected void use() {
		refCount.incrementAndGet();
	}
	
	/**
	 * Should be called when you no longer wish to use this network. Should only be called once, so
	 * null your reference after calling.
	 */
	public void free() {
		if (refCount.decrementAndGet() <= 0 && networkListener != null ) {
			networkListener.onFree(this);
		}
	}
	
	/**
	 * Provides the internal ant network
	 * @return internal ant network id
	 */
	public int getNumber() {
		return number;
	}
	
	/**
	 * Current {@link NetworkKey} associated with this network or null if no association exists.
	 * @return
	 */
	public NetworkKey getNetworkKey() {
		return networkKey;
	}

	protected NetworkListener getNetworkListener() {
		return networkListener;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + number;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Network other = (Network) obj;
		if (number != other.number)
			return false;
		return true;
	}
	
	


}
