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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cowboycoders.ant.ChannelId;
import org.cowboycoders.ant.defines.AntMesg;
import org.cowboycoders.ant.messages.Constants.DataElement;
import org.cowboycoders.ant.utils.ByteUtils;
import org.cowboycoders.ant.utils.IntUtils;
import org.cowboycoders.ant.utils.ValidationUtils;

/**
 * 
 * Represents an extended ant message
 * 
 * @author will
 * 
 */
public class ExtendedMessage extends Message implements
		ExtendedInformationQueryable, DeviceInfoQueryable, DeviceInfoSettable, RssiInfoQueryable,
		TimestampInfoQueryable {

	public static final byte EXTENDED_FLAG_OFFSET = 9;
	public static final byte MIN_LENGTH = 10;

	public enum ExtendedFlag {
		DISABLE((byte) 0x00), 
		ENABLE_RX_TIMESTAMP(
				(byte) 0x20,
				DataElement.RX_TIMESTAMP), ENABLE_RSSI_OUTPUT((byte) 0x40,
				DataElement.RSSI_MEASUREMENT_TYPE,
				DataElement.RSSI_THRESHOLD_CONFIG, DataElement.RSSI_VALUE), 
		ENABLE_CHANNEL_ID(
				(byte) 0x80, 
				DataElement.DEVICE_NUMBER,
				DataElement.DEVICE_TYPE,
				DataElement.TRANSMISSION_TYPE);

		private byte mask;
		private DataElement[] elements;
		private byte length = 0;

		/**
		 * Creates an extended message flag with corresponding mask
		 * 
		 * @param mask
		 *            the mask
		 */
		ExtendedFlag(byte mask, DataElement... elements) {
			this.setMask(mask);
			if (elements != null) {
				this.elements = new DataElement[elements.length];
			}
			addElements(elements);
		}

		private void addElements(DataElement[] elements) {
			for (int i = 0; i < elements.length; i++) {
				setLength(getLength() + elements[i].getLength());
				this.elements[i] = elements[i];
			}
		}

		/**
		 * Sets the associated mask for the extended message flag
		 * 
		 * @param mask
		 *            the mask
		 */
		private void setMask(byte flag) {
			this.mask = flag;
		}

		/**
		 * Gets the associated mask for the extended message flag
		 * 
		 * @param mask
		 *            the mask
		 */
		public byte getMask() {
			return mask;
		}

		/**
		 * gets the total length (number of bytes) of extra payload expected
		 * with this extended flag
		 * 
		 * @return
		 */
		public byte getLength() {
			return length;
		}

		private void setLength(int length) {
			this.length = (byte) length;
		}

		/**
		 * Returns an array of elements associated with this flag
		 * 
		 * @return
		 */
		public DataElement[] getElements() {
			return elements;
		}

	}

	/*
	 * public ExtendedMessage(Byte id, ArrayList<Byte> payload) { super(id,
	 * payload); }
	 * 
	 * public ExtendedMessage(MessageId id, ArrayList<Byte> payload) { super(id,
	 * payload); }
	 */

	public ExtendedMessage() {
		super();
	}

	/**
	 * Converts a generic message holding a payload in extended format to an
	 * extended message
	 * 
	 * @param message
	 *            must have a payload in extended format
	 * @throws MessageException
	 *             if {@code message} not in extended format
	 */
	/**
	 * private ExtendedMessage(Message message) throws MessageException {
	 * super(message); checkExtendedFormat(message.getPayload()); }
	 */

	/**
	 * verifies message is in extended format
	 * 
	 * @throws MessageException
	 *             if not in expected format
	 */
	private static void checkExtendedFormat(ArrayList<Byte> payload)
			throws MessageException {
		byte extendedFlag;
		int payloadLength = payload.size();

		try {
			extendedFlag = payload.get(EXTENDED_FLAG_OFFSET);
		} catch (IndexOutOfBoundsException e) {
			throw new MessageException(
					"Payload not long enough to be an extended message", e);
		}

		byte expectedLength = MIN_LENGTH;
		for (ExtendedFlag flag : ExtendedFlag.values()) {
			if ((extendedFlag & flag.getMask()) != 0) {
				expectedLength += flag.getLength();
			}
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
			checkExtendedFormat(getPayload());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Byte> getPayloadToSend() {
		// don't strip off extended data
		List<Byte> standardDataPacket = super.getStandardPayload();
		return standardDataPacket;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws ValidationException
	 *             if payload malformed
	 */
	public void setStandardPayload(ArrayList<Byte> payload)
			throws ValidationException {
		try {
			ArrayList<Byte> extendedPayload = getPayload();
			for (int i = 0; i < AntMesg.MESG_DATA_SIZE; i++) {
				if (extendedPayload.size() <= i) {
					extendedPayload.add(Byte.valueOf((byte) 0));
				}
				extendedPayload.set(i, payload.get(i));
			}
			setPayload(extendedPayload);
		} catch (IndexOutOfBoundsException e) {
			throw new ValidationException("Malformed payload", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<Byte> getStandardPayload() {
		ArrayList<Byte> extendedPayload = super.getStandardPayload();
		ArrayList<Byte> rtn = new ArrayList<Byte>();
		rtn.addAll(extendedPayload.subList(0, AntMesg.MESG_DATA_SIZE));
		return rtn;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ExtendedMessage clone() {
		ExtendedMessage msg = new ExtendedMessage();
		try {
			msg.decode(this.toArray(), true);
		} catch (MessageException e) {
			// toArray / decode is by design reversible
			throw new RuntimeException("Should never reach here");
		}
		return msg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cowboycoders.ant.messages.Extendedable#getExtendedData(org.cowboycoders
	 * .ant.messages.Constants.DataElements)
	 */
	@Override
	public Integer getExtendedData(DataElement element) {

		ExtendedFlag flag = ExtendedFlag.DISABLE;
		List<DataElement> extendedElements = new ArrayList<DataElement>();
		ArrayList<Byte> payload = getPayload();
		if (payload.size() <= EXTENDED_FLAG_OFFSET) {
			return null;
		}
		byte flagValue = payload.get(EXTENDED_FLAG_OFFSET);
		Integer rtn = null;

		for (ExtendedFlag i : ExtendedFlag.values()) {
			List<DataElement> elements = Arrays.asList(i.getElements());
			if ((i.getMask() & flagValue) != 0) {
				extendedElements.addAll(elements);
			}
			if (elements.contains(element)) {
				flag = i;
				break;
			}
		}
		if (flag == ExtendedFlag.DISABLE) {
			return null;
		}

		int index = EXTENDED_FLAG_OFFSET + 1;
		for (DataElement e : extendedElements) {
			if (e == element) {
				rtn = ByteUtils.lsbMerge(payload.subList(index,
						index += e.getLength()));
			}
			index += e.getLength();
		}

		return rtn;
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
		Integer rtn = getExtendedData(DataElement.TRANSMISSION_TYPE);
		if (rtn == null) {
			return null;
		}
		return rtn.byteValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cowboycoders.ant.messages.TimestampInfoQueryable#getRxTimeStamp()
	 */
	@Override
	public Integer getRxTimeStamp() {
		return getExtendedData(DataElement.RX_TIMESTAMP);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cowboycoders.ant.messages.RssiInfoQueryable#getRssiMeasurementType()
	 */
	@Override
	public Byte getRssiMeasurementType() {
		Integer rtn = getExtendedData(DataElement.RSSI_MEASUREMENT_TYPE);
		if (rtn == null) {
			return null;
		}
		return rtn.byteValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cowboycoders.ant.messages.RssiInfoQueryable#getRssiThresholdConfig()
	 */
	@Override
	public Byte getRssiThresholdConfig() {
		Integer rtn = getExtendedData(DataElement.RSSI_THRESHOLD_CONFIG);
		if (rtn == null) {
			return null;
		}
		return rtn.byteValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cowboycoders.ant.messages.RssiInfoQueryable#getRssiValue()
	 */
	@Override
	public Byte getRssiValue() {
		Integer rtn = getExtendedData(DataElement.RSSI_VALUE);
		if (rtn == null) {
			return null;
		}
		return rtn.byteValue();
	}
	
	/**
	 * Sets channel id contained in extended bytes
	 * @param id the id to set. A null value will remove the extended bytes and update flag.
	 */
	public void setChannelId(ChannelId id) {
		// this will clear the info
		if(id == null) {
			setDataElement(DataElement.DEVICE_NUMBER, null);
			return;
		}
		setDeviceNumber(id.getDeviceNumber());
		setDeviceType(id.getDeviceType());
		setTransmissionType(id.getTransmissonType());
		setPairingFlag(id.isPairingFlagSet());
	}
	
	/**
	 * Sets a DataElement contained in extended data bytes
	 * @param element the element to set
	 * @param value the new value, or null to clear
	 */
	public void setDataElement(DataElement element, Integer value) {
		ArrayList<Byte> payload = getPayload();
		Map<DataElement,Integer> oldValues = new HashMap<DataElement,Integer>();
		
		final int oldSize = payload.size();
		
		//System.out.println("oldSize: " + oldSize);

		// add an extended flag if it doesn't exist
		for (int i = 0; i <= EXTENDED_FLAG_OFFSET - oldSize; i++) {
			payload.add(Byte.valueOf((byte) 0));
		}
		
		//System.out.println("newSize: " + payload.size());

		byte flagValue = payload.get(EXTENDED_FLAG_OFFSET);
		ExtendedFlag flag = getFlagFromDataElement(element);
		
		// backup old values
		for (ExtendedFlag f : ExtendedFlag.values()) {
			// a null value signifies we want to clear so don't backup
			if (f.equals(flag) && value == null) continue;
			
			List<DataElement> elements = Arrays.asList(f.getElements());
			if ((f.getMask() & flagValue) != 0) {
				for (DataElement e : elements) {
					oldValues.put(e, getExtendedData(e));
				}
			}
		}
		
		flagValue = doSetElement(element, value, payload, flagValue);
		
		
		for (DataElement e : oldValues.keySet()) {
			// don't restore what we just replaced
			if (e.equals(element)) continue;
			flagValue = doSetElement(e, oldValues.get(e), payload,flagValue);
		}
		
		payload.set(EXTENDED_FLAG_OFFSET, flagValue);
		
		setPayload(payload);
		

	}

	private byte doSetElement(DataElement element, Integer value,
			ArrayList<Byte> payload,
			byte flagValue) {
		
		List<DataElement> extendedElements = new ArrayList<DataElement>();
		ExtendedFlag flag = getFlagFromDataElement(element);
		
		// toggle element mask
		// case channelId set
		if ((flag.getMask() & flagValue) != 0) {
			if (value == null) {
				// clear masked value
				flagValue ^= flag.getMask();
			}
		} else { // case channelId not set
			if (value != null) {
				// set masked value
				flagValue |= flag.getMask();
			}
		}
		
		// get expected list of data elements after flag toggle 
		for (ExtendedFlag f : ExtendedFlag.values()) {
			List<DataElement> elements = Arrays.asList(f.getElements());
			if ((f.getMask() & flagValue) != 0) {
				
				extendedElements.addAll(elements);
			}
		}
		
		// correct payload length for flag toggle
		final int expectedExtendedSize = payload.size() -1 - EXTENDED_FLAG_OFFSET;
		int actualExtendedSize = 0;
		for (DataElement e : extendedElements) {
			actualExtendedSize += e.getLength();
		}
		
		//System.out.println("expected: " +expectedExtendedSize);
		//System.out.println("actual: " +actualExtendedSize);
		
		int delta = actualExtendedSize - expectedExtendedSize;
		
		//System.out.println("delta: " + delta);

		for (int i = 0; i < Math.abs(delta); i++) {
			if (delta < 0) {
				int lastIndex = payload.size() - 1;
				payload.remove(lastIndex);
			} else {
				payload.add((byte)0);
			}
			
		}
		
		// after resize clear extended bytes bytes
		if (delta != 0) {
			for (int i =  EXTENDED_FLAG_OFFSET + 1 ; i < payload.size() ; i++) {
				payload.set(i,(byte) 0);
			}
		}
		
		// insert the bytes
		int index = EXTENDED_FLAG_OFFSET + 1;
		for (DataElement e : extendedElements) {
			if (e == element) {
				List<Byte> bytesToInsert = ByteUtils.lsbSplit(value, e.getLength());
				for (int c = 0; c < element.getLength() ; c++) {
					payload.set(index + c, bytesToInsert.get(c));
				}
			}
			index += e.getLength();
		}
		
		return flagValue;
	}

	private ExtendedFlag getFlagFromDataElement(DataElement element) {
		ExtendedFlag flag = null;
		
		// discover flag associated with element
		for (ExtendedFlag f : ExtendedFlag.values()) {
			List<DataElement> elements = Arrays.asList(f.getElements());
				if (elements.contains(element)) {
					flag = f;
					break;
				}
		}
		return flag;
	}

	public void setTransmissionType(int transmissionType) {
		   ValidationUtils.maxMinValidator(0, ChannelId.MAX_TRANSMISSION_TYPE, transmissionType, 
			       MessageExceptionFactory.createMaxMinExceptionProducable("transmissionType")
			       );
		   setDataElement(DataElement.TRANSMISSION_TYPE, transmissionType);
		
	}

	public void setDeviceType(int deviceType) {
		   ValidationUtils.maxMinValidator(0, ChannelId.MAX_DEVICE_TYPE, deviceType, 
			       MessageExceptionFactory.createMaxMinExceptionProducable("deviceType")
			       );
		   Integer wholeElement = getExtendedData(DataElement.DEVICE_TYPE);
		   if (wholeElement == null) {
			   wholeElement = 0;
		   }
		   wholeElement = IntUtils.setMaskedBits(wholeElement, ChannelId.DEVICE_TYPE_MASK, deviceType);
		   setDataElement(DataElement.DEVICE_TYPE, wholeElement); 
	}

	public void setDeviceNumber(int deviceId) {
		   ValidationUtils.maxMinValidator(0, ChannelId.MAX_DEVICE_NUMBER, deviceId, 
			       MessageExceptionFactory.createMaxMinExceptionProducable("deviceNumber")
			       );
		   setDataElement(DataElement.DEVICE_NUMBER, deviceId); 
	}

	@Override
	public ChannelId getChannelId() {
	  	ChannelId id = ChannelId.Builder.newInstance()
	      		.setDeviceNumber(getDeviceNumber())
	      		.setDeviceType(getDeviceType())
	      		.setTransmissonType(getTransmissionType())
	      		.setPairingFlag(isPairingFlagSet())
	      		.build();
	  	return id;
	}

	@Override
	public Boolean isPairingFlagSet() {
		 Integer unmasked = getExtendedData(DataElement.DEVICE_TYPE);
		 if (unmasked == null) {
			 return null;
		 }
		 return (unmasked & ChannelId.PAIRING_FLAG_MASK) > 0 ? true : false ;
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



}
