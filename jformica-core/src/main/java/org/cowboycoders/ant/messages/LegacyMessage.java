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
package org.cowboycoders.ant.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cowboycoders.ant.ChannelId;
import org.cowboycoders.ant.defines.AntMesg;
import org.cowboycoders.ant.messages.Constants.DataElement;
import org.cowboycoders.ant.utils.ByteUtils;
import org.cowboycoders.ant.utils.IntUtils;
import org.cowboycoders.ant.utils.ValidationUtils;

/**
 * 
 * Represents an extended (legacy) ant message
 * 
 * @author will
 * 
 */
public class LegacyMessage extends Message implements
		ExtendedInformationQueryable, DeviceInfoQueryable, DeviceInfoSettable {

	private static final byte DATA_OFFSET = 5;
	private static final byte EXTENDED_OFFSET = 1;
	private static final byte PAYLOAD_LENGTH = 13;
	private static final DataElement[] extendedElements = {
			DataElement.DEVICE_NUMBER, DataElement.DEVICE_TYPE,
			DataElement.TRANSMISSION_TYPE };

	/*
	 * public LegacyMessage(Byte id, ArrayList<Byte> payload) { super(id,
	 * payload); }
	 * 
	 * public LegacyMessage(MessageId id, ArrayList<Byte> payload) { super(id,
	 * payload); }
	 */

	public LegacyMessage() {
		super();
		addExtendedElementsToPayload();
	}

	private void addExtendedElementsToPayload() {
		ArrayList<Byte> payload = getPayload();

		// reserve space for channel id
		if (payload.size() == 0) {
			payload.add(Byte.valueOf((byte) 0));
			setPayload(payload);
		}
		// don't have to worry about if elements are
		// there or not as this is only called by constructor
		// and reset
		for (DataElement element : extendedElements) {
			payload = getPayload();
			for (int i = 0; i < element.getLength(); i++) {
				payload.add(Byte.valueOf((byte) 0));
			}
			setPayload(payload);
			try {
				setDataElement(element, 0);
			} catch (ValidationException e) {
				throw new FatalMessageException(
						"Error initialising extended data", e);
			}
		}
	}

	/*
	 * public LegacyMessage(Message message) { super(message); }
	 */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<Byte> getStandardPayload() {
		ArrayList<Byte> extendedPayload = super.getStandardPayload();
		if (extendedPayload.size() == 0) {
			return extendedPayload;
		}

		ArrayList<Byte> standardPayload = new ArrayList<Byte>();
		standardPayload.add(extendedPayload.get(0));
		if (extendedPayload.size() <= DATA_OFFSET) {
			return extendedPayload;
		}
		List<Byte> subList = extendedPayload.subList(DATA_OFFSET,
				extendedPayload.size());
		subList = subList.subList(0, AntMesg.MESG_DATA_SIZE - 1);
		standardPayload.addAll(subList);
		return standardPayload;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws ValidationException
	 *             if payload malformed
	 */
	public void setStandardPayload(ArrayList<Byte> payload)
			throws ValidationException {
		if (payload.size() == 0) {
			setPayload(new ArrayList<Byte>());
			return;
		}

		try {
			ArrayList<Byte> extendedPayload = getPayload();
			if (extendedPayload.size() == 0) {
				extendedPayload.add(payload.get(0));
			} else {
				extendedPayload.set(0, payload.get(0));
			}

			if (payload.size() == 1) {
				setPayload(extendedPayload);
				return;
			}

			for (int i = 1; i < payload.size() + DATA_OFFSET
					&& i - DATA_OFFSET < AntMesg.MESG_DATA_SIZE - 1; i++) {
				if (extendedPayload.size() == i) {
					extendedPayload.add((byte) 0);
				}
				if (i >= DATA_OFFSET) {
					Byte value = payload.get(i - DATA_OFFSET);
					extendedPayload.set(i, value);
				}
			}
			setPayload(extendedPayload);
		} catch (IndexOutOfBoundsException e) {
			throw new ValidationException("Payload malformed", e);
		}
	}

	/**
	 * verifies message is in extended format
	 * 
	 * @throws MessageException
	 *             if not in expected format
	 */
	private static void checkExtendedFormat(Message message)
			throws MessageException {
		ArrayList<Byte> payload = message.getPayload();
		int payloadLength = payload.size();
		int expectedLength = PAYLOAD_LENGTH;
		MessageId id = message.getId();

		if (id != MessageId.EXT_ACKNOWLEDGED_DATA
				&& id != MessageId.EXT_BROADCAST_DATA
				&& id != MessageId.EXT_BURST_DATA) {
			throw new MessageException("Message id not a known extended value");
		}

		if (payloadLength != expectedLength) {
			throw new MessageException("Malformed extended packet "
					+ "(number of bytes does not match that expected)");
		}

	}

	@Override
	public void decode(byte[] buffer, boolean noChecks) throws MessageException {
		super.decode(buffer, noChecks);
		if (!noChecks) {
			checkExtendedFormat(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LegacyMessage clone() {
		LegacyMessage msg = new LegacyMessage();
		try {
			msg.decode(this.toArray(), true);
		} catch (MessageException e) {
			// encode / decode is by design reversible
			throw new RuntimeException("Should never reach here");
		}
		return msg;
	}

	@Override
	public Integer getExtendedData(DataElement element) {
		Integer rtn = null;
		ArrayList<Byte> payload = getPayload();
		// DataElements [] extendedElements = {DataElements.DEVICE_NUMBER,
		// DataElements.DEVICE_TYPE, DataElements.TRANSMISSION_TYPE};

		int index = EXTENDED_OFFSET;
		for (DataElement e : extendedElements) {
			if (e == element) {
				rtn = ByteUtils.lsbMerge(payload.subList(index,
						index += e.getLength()));
				break;
			}
			index += e.getLength();
		}

		return rtn;
	}

	/**
	 * Calculates max settable value for a given {@code DataElements}
	 * 
	 * @param element
	 *            to check
	 * @return the maximum permitted value
	 */
	private int calculateMaxValue(DataElement element) {
		return (int) Math.pow(2, (element.getLength() * 8)) - 1;
	}

	/**
	 * validates a value which has been requested to be set is within sensible
	 * limits
	 * 
	 * @param element
	 *            the {@code DataElements} member to check
	 * @param value
	 *            the value to be checked
	 * @throws ValidationException
	 *             if value is not within sensible limits
	 */
	private void validateExtendedData(DataElement element, Integer value)
			throws ValidationException {
		int maxValue = calculateMaxValue(element);
		if (value > maxValue || value < 0) {
			throw new ValidationException("Invalid " + element
					+ ": Must be between 0 and " + maxValue);
		}
	}

	/**
	 * inserts extended element bytes into the payload
	 * 
	 * @param element
	 *            corresponding {@code DataElements}
	 * @param bytesToInsert
	 *            a {@code List} of Bytes to insert
	 * @return true on success, else false
	 */
	private boolean insertExtendedBytes(DataElement element,
			List<Byte> bytesToInsert) {
		assert bytesToInsert.size() == element.getLength() : "Number of bytes to insert doesn't match expected element length";

		boolean completed = false;
		ArrayList<Byte> payload = getPayload();

		int index = EXTENDED_OFFSET;
		for (DataElement e : extendedElements) {
			if (e == element) {
				for (byte b : bytesToInsert) {
					payload.set(index, b);
					index++;
				}
				completed = true;
				break;
			}
			index += e.getLength();
		}

		setPayload(payload);

		return completed;
	}

	/**
	 * Helper method to set extended data info for a given {@code DataElements}
	 * member
	 * 
	 * @param element
	 *            the corresponding {@code DataElements} to insert data for
	 * @param value
	 *            the value that is being inserted
	 * @throws ValidationException
	 *             if requested value is out of expected range
	 */
	private void setDataElement(DataElement element, int value)
			throws ValidationException {

		if (!Arrays.asList(extendedElements).contains(element)) {
			throw new FatalMessageException(
					"Arg, element, not in expected list");
		}

		boolean completed = false;
		validateExtendedData(element, value);
		List<Byte> insertionBytes = ByteUtils.lsbSplit(value,
				element.getLength());

		completed = insertExtendedBytes(element, insertionBytes);

		if (!completed) {
			throw new FatalMessageException("Byte insertion failed");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cowboycoders.ant.messages.DeviceInfoSettable#setDeviceNumber(int)
	 */
	@Override
	public void setDeviceNumber(int deviceId) throws ValidationException {
		DataElement element = DataElement.DEVICE_NUMBER;
		setDataElement(element, deviceId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cowboycoders.ant.messages.DeviceInfoSettable#setDeviceType(int)
	 */
	@Override
	public void setDeviceType(int deviceType) throws ValidationException {
		ValidationUtils.maxMinValidator(0, ChannelId.MAX_DEVICE_TYPE,
				deviceType, MessageExceptionFactory
						.createMaxMinExceptionProducable("deviceType"));
		Integer wholeElement = getExtendedData(DataElement.DEVICE_TYPE);
		if (wholeElement == null) {
			wholeElement = 0;
		}
		wholeElement = IntUtils.setMaskedBits(wholeElement,
				ChannelId.DEVICE_TYPE_MASK, deviceType);
		setDataElement(DataElement.DEVICE_TYPE, wholeElement);
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	public void setTransmissionType(int transmissionType)
			throws ValidationException {
		DataElement element = DataElement.TRANSMISSION_TYPE;
		setDataElement(element, transmissionType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cowboycoders.ant.messages.DeviceInfoQueryable#getDeviceNumber()
	 */
	@Override
	public Integer getDeviceNumber() {
		return getExtendedData(DataElement.DEVICE_NUMBER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cowboycoders.ant.messages.DeviceInfoQueryable#getDeviceType()
	 */
	@Override
	public Byte getDeviceType() {
		Integer rtn = getExtendedData(DataElement.DEVICE_TYPE);
		if (rtn == null) {
			return null;
		}
		rtn = rtn & ChannelId.DEVICE_TYPE_MASK;
		return rtn.byteValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cowboycoders.ant.messages.DeviceInfoQueryable#getTransmissionType()
	 */
	@Override
	public Byte getTransmissionType() {
		return getExtendedData(DataElement.TRANSMISSION_TYPE).byteValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cowboycoders.ant.messages.Message#reset()
	 */
	@Override
	public void reset() {
		super.reset();
		addExtendedElementsToPayload();
	}

	@Override
	public void setChannelId(ChannelId id) {
		setDeviceNumber(id.getDeviceNumber());
		setDeviceType(id.getDeviceType());
		setTransmissionType(id.getTransmissonType());
		setPairingFlag(id.isPairingFlagSet());
	}

	@Override
	public ChannelId getChannelId() {
		ChannelId id = ChannelId.Builder.newInstance()
				.setDeviceNumber(getDeviceNumber())
				.setDeviceType(getDeviceType())
				.setTransmissonType(getTransmissionType())
				.setPairingFlag(isPairingFlagSet()).build();
		return id;
	}

	@Override
	public void setPairingFlag(boolean pair) {
		int value = pair ? 1 : 0;
		Integer wholeElement = getExtendedData(DataElement.DEVICE_TYPE);
		   if (wholeElement == null) {
			   wholeElement = 0;
		   }
		value = IntUtils.setMaskedBits(wholeElement, ChannelId.PAIRING_FLAG_MASK, value);
		setDataElement(DataElement.DEVICE_TYPE, value); 
	}

	@Override
	public Boolean isPairingFlagSet() {
		 Integer unmasked = getExtendedData(DataElement.DEVICE_TYPE);
		 if (unmasked == null) {
			 return null;
		 }
		 return (unmasked & ChannelId.PAIRING_FLAG_MASK) > 0 ? true : false ;
	}

}
