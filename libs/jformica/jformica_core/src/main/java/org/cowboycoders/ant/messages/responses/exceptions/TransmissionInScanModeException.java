package org.cowboycoders.ant.messages.responses.exceptions;

import org.cowboycoders.ant.AntError;

/**
 * Thrown when attempting to transmit on ANT channel 0 in scan mode.
 * @author will
 *
 */
public class TransmissionInScanModeException extends AntError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -767937266435117460L;

	public TransmissionInScanModeException() {
		super();
	}

	public TransmissionInScanModeException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public TransmissionInScanModeException(String detailMessage) {
		super(detailMessage);
	}

	public TransmissionInScanModeException(Throwable throwable) {
		super(throwable);
	}
	
	

}
