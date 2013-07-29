package org.cowboycoders.ant.messages.responses.exceptions;

import org.cowboycoders.ant.AntError;

/**
 * Thrown when ant chip receives a message which longer than expected
 * @author will
 *
 */
public class MessageLengthException extends AntError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -767937266435117460L;

	public MessageLengthException() {
		super();
	}

	public MessageLengthException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public MessageLengthException(String detailMessage) {
		super(detailMessage);
	}

	public MessageLengthException(Throwable throwable) {
		super(throwable);
	}
	
	

}
