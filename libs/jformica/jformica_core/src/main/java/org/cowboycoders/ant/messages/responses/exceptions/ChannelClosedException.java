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
