package org.cowboycoders.ant;

/**
 * Ensures a network reference can only be freed once
 * @author will
 *
 */
public class NetworkHandle extends Network {

	private boolean freed = false;

	protected NetworkHandle(Network network) {
		super(network);
		// increment reference count
		use();
	}
	

	@Override
	public synchronized void free() {
		// if has been freed
		checkState();
		super.free();
		freed = true;
	}


	private synchronized void checkState() {
		if (freed) {
			throw new IllegalStateException("Trying to use a network which has been freed");
		}
	}


	@Override
	protected synchronized void use() {
		checkState();
		super.use();
	}


	@Override
	public synchronized int getNumber() {
		checkState();
		return super.getNumber();
	}


	@Override
	public synchronized NetworkKey getNetworkKey() {
		checkState();
		return super.getNetworkKey();
	}


	@Override
	protected synchronized NetworkListener getNetworkListener() {
		checkState();
		return super.getNetworkListener();
	}
	
	

	
	
	

}
