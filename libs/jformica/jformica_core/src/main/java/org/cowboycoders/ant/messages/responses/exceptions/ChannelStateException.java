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
