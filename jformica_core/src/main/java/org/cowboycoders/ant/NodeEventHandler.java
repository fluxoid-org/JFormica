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
 * Handler for events not associated with an ant channel ant node. All methods need to be implemented.
 * @author will
 *
 */
public abstract class NodeEventHandler implements EventHandler {

	@Override
	public final void receiveMessage(Response msg) {
		// Ensure we only are dealing with events
		if(!MessageConditionFactory.newEventCondition().test(msg)) return;
		ResponseCode code = msg.getResponseCode();
		
		switch(code) {
		case EVENT_SERIAL_QUE_OVERFLOW:
			onSerialQueOverflow();
			break;
		case EVENT_QUE_OVERFLOW:
			onQueOverflow();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Slow/busy serial port has led to one or more lost events
	 */
	public abstract void onQueOverflow();
	
	/**
	 * Serial buffer of usb chip has overflow. Chip is generating messages but they are not being
	 * collected by the pc application.
	 */
	public abstract void onSerialQueOverflow();

}
