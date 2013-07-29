package org.cowboycoders.ant.messages.responses.exceptions;

import org.cowboycoders.ant.AntError;

/**
 * Thrown when invalid configuration commands are requested
 * @author will
 *
 */
public class InvalidParameterException extends AntError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -767937266435117460L;

	public InvalidParameterException() {
		super();
	}

	public InvalidParameterException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public InvalidParameterException(String detailMessage) {
		super(detailMessage);
	}

	public InvalidParameterException(Throwable throwable) {
		super(throwable);
	}
	
	

}
