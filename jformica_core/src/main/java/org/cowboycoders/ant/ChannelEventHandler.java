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

import org.cowboycoders.ant.events.MessageConditionFactory;
import org.cowboycoders.ant.messages.responses.Response;
import org.cowboycoders.ant.messages.responses.ResponseCode;

/**
 * Handler for events on ant channel. All methods need to be implemented.
 * @author will
 *
 */
public abstract class ChannelEventHandler implements EventHandler {

	@Override
	public final void receiveMessage(Response msg) {
		// Ensure we only are dealing with events
		if(!MessageConditionFactory.newEventCondition().test(msg)) return;
		ResponseCode code = msg.getResponseCode();
		
		switch(code) {
		case EVENT_RX_SEARCH_TIMEOUT:
			onRxSearchTimeout();
			break;
		case EVENT_RX_FAIL:
			onRxFail();
			break;
		case   EVENT_TX:
			onTxSuccess();
			break;
		case   EVENT_TRANSFER_RX_FAILED:
			onTransferRxFailed();
			break;
		case   EVENT_TRANSFER_TX_COMPLETED:
			onTransferTxCompleted();
			break;
		case   EVENT_TRANSFER_TX_FAILED:
			onTransferTxFailed();
			break;
		case   EVENT_CHANNEL_CLOSED:
			onChannelClosed();
			break;
		case   EVENT_RX_FAIL_GO_TO_SEARCH:
			onRxFailGoToSearch();
			break;
		case   EVENT_CHANNEL_COLLISION:
			onChannelCollision();
			break;
		case EVENT_TRANSFER_TX_START:
			onTransferTxStart();
			break;
		// these are equivalent. Current implementation doesn't guarantee which it will generate.
		case EVENT_TRANSFER_NEXT_DATA_BLOCK:
		case EVENT_TRANSFER_TX_NEXT_MESSAGE:
			onTransferNextDataBlock();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Only sent on FIT1. Indicates a "data block release on burst buffer"
	 */
	
	public abstract void onTransferNextDataBlock();

	/**
	 * Indicates a burst has begun. This will be sent on next channel period
	 * after successful transmission
	 *
	 */
	public abstract void onTransferTxStart();
	
	/**
	 * An acknowledged or burst packet has successfully be transmitted. These messages
	 * do not produce an EVENT_TX event.
	 */
	public abstract void onTransferTxCompleted();
	
	/**
	 * An acknowledged or burst packet has failed to be transmitted
	 */
	public abstract void onTransferTxFailed();
	
	/**
	 * Channel has been successfully closed
	 */
	public abstract void onChannelClosed();
	
	/**
	 * Channel has reentered search mode after missing too many messages
	 */
	public abstract void onRxFailGoToSearch();	
	
	/**
	 * Channel periods have overlapped and as a result this channel is blocked.
	 */
	public abstract void onChannelCollision();

	/**
	 * This occurs when a burst transfer is incorrectly received - badly constructed
	 * or corrupt.
	 */
	public abstract void onTransferRxFailed();

	/**
	 * Called when a channel has timeout out searching for a master. The channel is automatically closed
	 * and {@link Channel#open()} must be called again.
	 */
	public abstract void onRxSearchTimeout();
	
	/**
	 * A message that was expected was not received. A message is expected
	 * once per channel period - this indicates one was lost. Check for reception issues
	 * or correct your channel period.
	 */
	public abstract  void onRxFail();
	
	/**
	 * This indicates that a data message has been transmitted. A new data message sent
	 * to the channel will transmitted on the next channel period, else the same message
	 * will be resent.
	 */
	public abstract  void onTxSuccess();
	

}
