package org.cowboycoders.ant.messages.responses.exceptions;

import org.cowboycoders.ant.AntError;

/**
 * Thrown when an invalid network number is provided.
 * valid network numbers are between 0 and MAX_NET-1.
 * @author will
 *
 */
public class InvalidNetworkException extends AntError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1491376172972269636L;

	public InvalidNetworkException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InvalidNetworkException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

	public InvalidNetworkException(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	public InvalidNetworkException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}
	
	

}
