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
