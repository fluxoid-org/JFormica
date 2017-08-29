/**
 *     Copyright (c) 2013, Will Szumski
 *
 *     This file is part of formicidae.
 *
 *     formicidae is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     formicidae is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with formicidae.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cowboycoders.ant.messages.responses;

import org.cowboycoders.ant.messages.responses.exceptions.*;


/**
 * Maps response codes to exceptions or null if response does not indicate an error
 * condition
 * @author will
 *
 */
public class ResponseExceptionFactory {

	private ResponseExceptionFactory() {

	}

	private static ResponseExceptionFactory factory = new ResponseExceptionFactory();

	public static ResponseExceptionFactory getFactory() {
		return factory;
	}

	/**
	 * Maps response codes to exceptions or null if response does not indicate an error
	 * condition
	 * @param response code to lookup
	 * @return exception associated with response code or null
	 */
	public RuntimeException map(ResponseCode response) {
		switch (response) {
		case CHANNEL_IN_WRONG_STATE:
			return new ChannelStateException();
		case CHANNEL_NOT_OPENED:
			return new ChannelClosedException();
		case CHANNEL_ID_NOT_SET:
			return new ChannelIdNotSetException();
		case CLOSE_ALL_CHANNELS:
			return new ChannelsStillOpenException();
		case MESSAGE_SIZE_EXCEEDS_LIMIT:
			return new MessageLengthException();
		case INVALID_MESSAGE:
			return new InvalidMessageException();
		case INVALID_NETWORK_NUMBER:
			return new InvalidNetworkException();
		case INVALID_LIST_ID:
			return new InvalidListIdException();
		case INVALID_SCAN_TX_CHANNEL:
			return new TransmissionInScanModeException();
		case INVALID_PARAMETER_PROVIDED:
			return new InvalidParameterException();
		default:
				return null;
		}
	}

	/**
	 * Throws the appropriate exception on error response
	 * @param response TODO : document this
	 * @throws RuntimeException TODO : document this
	 */
	public void throwOnError(ResponseCode response) throws RuntimeException {
		RuntimeException e = map(response);
		if (e != null) {
			throw e;
		}
	}

	/**
	 * Extracts response code from {@link Response}. See:
	 * {@link ResponseExceptionFactory#throwOnError(ResponseCode)}
	 * @param response TODO : document this
	 * @throws RuntimeException TODO : document this
	 */
	public void throwOnError(Response response) throws RuntimeException {
		throwOnError(response.getResponseCode());
	}

}
