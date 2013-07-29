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
package org.cowboycoders.ant.messages.responses.exceptions;

import org.cowboycoders.ant.ChannelError;

/**
 *  Thrown when a channel is the wrong state for the requested operation e.g trying to 
 *  assign an already assigned channel
 * @author will
 *
 */
public class ChannelStateException extends ChannelError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -767937266435117460L;

	public ChannelStateException() {
		super();
	}

	public ChannelStateException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ChannelStateException(String detailMessage) {
		super(detailMessage);
	}

	public ChannelStateException(Throwable throwable) {
		super(throwable);
	}
	
	

}
