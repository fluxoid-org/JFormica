package org.cowboycoders.ant.messages.responses.exceptions;

import org.cowboycoders.ant.AntError;

/**
 * Thrown when ant chip receives a message which has "invalid parameters"
 * @author will
 *
 */
public class InvalidMessageException extends AntError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -767937266435117460L;

	public InvalidMessageException() {
		super();
	}

	public InvalidMessageException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public InvalidMessageException(String detailMessage) {
		super(detailMessage);
	}

	public InvalidMessageException(Throwable throwable) {
		super(throwable);
	}
	
	

}
