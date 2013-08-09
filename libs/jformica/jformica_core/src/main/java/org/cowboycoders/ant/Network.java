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

	protected void use() {
		refCount.incrementAndGet();
	}
	
	public void free() {
		if (refCount.decrementAndGet() <= 0 && networkListener != null ) {
			networkListener.onFree(this);
		}
	}

	public int getNumber() {
		return number;
	}

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
