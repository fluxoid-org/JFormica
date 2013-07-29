package org.cowboycoders.ant.messages.responses.exceptions;

import org.cowboycoders.ant.AntError;

/**
 * Thrown when when attempting to transmit on ANT channel 0 in scan
 * mode.
 * @author will
 *
 */
public class InvalidListIdException extends AntError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -767937266435117460L;

	public InvalidListIdException() {
		super();
	}

	public InvalidListIdException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public InvalidListIdException(String detailMessage) {
		super(detailMessage);
	}

	public InvalidListIdException(Throwable throwable) {
		super(throwable);
	}
	
	

}
