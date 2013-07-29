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
 * Default, do nothing implementation of {@link ChannelEventHandler}. Override methods
 * Associated with events you care about.
 * @author will
 *
 */
public class DefaultChannelEventHandler extends ChannelEventHandler {

	@Override
	public void onTransferNextDataBlock() {
		// Do nothing
		
	}

	@Override
	public void onTransferTxStart() {
		// Do nothing
	}

	@Override
	public void onTransferTxCompleted() {
		// Do nothing
	}

	@Override
	public void onTransferTxFailed() {
		// Do nothing
		
	}

	@Override
	public void onChannelClosed() {
		// Do nothing
		
	}

	@Override
	public void onRxFailGoToSearch() {
		// Do nothing
		
	}

	@Override
	public void onChannelCollision() {
		// Do nothing
		
	}

	@Override
	public void onTransferRxFailed() {
		// Do nothing
		
	}

	@Override
	public void onRxSearchTimeout() {
		// Do nothing
		
	}

	@Override
	public void onRxFail() {
		// Do nothing
		
	}

	@Override
	public void onTxSuccess() {
		// Do nothing
		
	}
	

}
