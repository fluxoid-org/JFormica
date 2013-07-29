package org.cowboycoders.ant.messages.responses.exceptions;

import org.cowboycoders.ant.ChannelError;

/**
 * Thrown when trying to open a channel without first setting the id.
 * @author will
 *
 */
public class ChannelIdNotSetException extends ChannelError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -767937266435117460L;

	public ChannelIdNotSetException() {
		super();
	}

	public ChannelIdNotSetException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ChannelIdNotSetException(String detailMessage) {
		super(detailMessage);
	}

	public ChannelIdNotSetException(Throwable throwable) {
		super(throwable);
	}
	
	

}
