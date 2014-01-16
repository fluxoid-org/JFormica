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
 * Thrown when trying to transmit data to a channel which is closed. Make sure you open the channel first.
 * @author will
 *
 */
public class ChannelClosedException extends ChannelError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -767937266435117460L;

	public ChannelClosedException() {
		super();
	}

	public ChannelClosedException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ChannelClosedException(String detailMessage) {
		super(detailMessage);
	}

	public ChannelClosedException(Throwable throwable) {
		super(throwable);
	}
	
	

}
