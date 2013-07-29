package org.cowboycoders.ant.messages.responses.exceptions;

import org.cowboycoders.ant.AntError;

/**
 * Thrown when trying to open a channel in rx scan mode and other channels are still open.
 * These channels must be closed before a channel can be opened in this mode.
 * @author will
 *
 */
public class ChannelsStillOpenException extends AntError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -767937266435117460L;

	public ChannelsStillOpenException() {
		super();
	}

	public ChannelsStillOpenException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ChannelsStillOpenException(String detailMessage) {
		super(detailMessage);
	}

	public ChannelsStillOpenException(Throwable throwable) {
		super(throwable);
	}
	
	

}
