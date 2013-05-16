/**
 *     Copyright (c) 2012, Will Szumski
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

import org.cowboycoders.ant.defines.AntMesg;
import org.cowboycoders.ant.messages.Constants.DataElements;
import org.cowboycoders.ant.utils.ByteUtils;

/**
 * 
 * Represents an extended ant message
 * @author will
 *
 */
public class ExtendedMessage extends Message 
  implements ExtendedInformationQueryable, DeviceInfoQueryable, RssiInfoQueryable, TimestampInfoQueryable {
  
  public static final byte EXTENDED_FLAG_OFFSET = 9;
  public static final byte MIN_LENGTH = 10;
  
  public enum ExtendedFlag {
    DISABLE ((byte)0x00),
    ENABLE_RX_TIMESTAMP ((byte)0x20 , DataElements.RX_TIMESTAMP ),
    ENABLE_RSSI_OUTPUT  ((byte)0x40 , DataElements.RSSI_MEASUREMENT_TYPE, 
        DataElements.RSSI_THRESHOLD_CONFIG, DataElements.RSSI_VALUE ),
    ENABLE_CHANNEL_ID   ((byte)0x80 , DataElements.DEVICE_TYPE, DataElements.DEVICE_NUMBER,
        DataElements.TRANSMISSION_TYPE);
    
    private byte mask;
    private DataElements [] elements;
    private byte length = 0;
    
    /**
     * Creates an extended message flag with corresponding mask
     * @param mask the mask
     */
    ExtendedFlag(byte mask, DataElements ... elements) {
      this.setMask(mask);
      if (elements != null) {
        this.elements = new DataElements[elements.length];
      }
      addElements(elements);
    }
    
    private void addElements(DataElements[] elements) {
      for (int i =0 ; i< elements.length; i++) {
        setLength(getLength() + elements[i].getLength());
        this.elements[i] = elements[i];
      }
    }

    /**
     * Sets the associated mask for the extended message flag
     * @param mask the mask
     */
    private void setMask(byte flag) {
      this.mask = flag;
    }
    
    /**
     * Gets the associated mask for the extended message flag
     * @param mask the mask
     */
    public byte getMask() {
      return mask;
    }
    
    /**
     * gets the total length (number of bytes) of extra payload
     * expected with this extended flag 
     * @return
     */
    public byte getLength() {
      return length;
    }
    
    private void setLength(int length) {
      this.length = (byte)length;
    }
    
    /**
     * Returns an array of elements associated with this flag
     * @return
     */
    public DataElements[] getElements() {
      return elements;
    }
    
    
  }
  
  /*
  public ExtendedMessage(Byte id, ArrayList<Byte> payload) {
    super(id, payload);
  }

  public ExtendedMessage(MessageId id, ArrayList<Byte> payload) {
    super(id, payload);
  }
  */
  
  public ExtendedMessage() {
    super();
  }
  
  /**
   * Converts a generic message holding a payload in extended format
   * to an extended message
   * @param message must have a payload in extended format
   * @throws MessageException if {@code message} not in extended format
   */
/**
  private ExtendedMessage(Message message) throws MessageException {
    super(message);
    checkExtendedFormat(message.getPayload());
  }
  */
  
  /**
   * verifies message is in extended format
   * @throws MessageException if not in expected format
   */
  private static void checkExtendedFormat(ArrayList<Byte> payload) 
      throws MessageException {
    byte extendedFlag;
    int payloadLength = payload.size();
    
    try {
      extendedFlag = payload.get(EXTENDED_FLAG_OFFSET);
    } catch (IndexOutOfBoundsException e) {
      throw new MessageException("Payload not long enough to be an extended message",e);
    }
    
    byte expectedLength = MIN_LENGTH;
    for ( ExtendedFlag flag : ExtendedFlag.values() ) {
      if ((extendedFlag & flag.getMask()) != 0) {
        expectedLength += flag.getLength();
      }
    }
    
    if (payloadLength != expectedLength) {
      throw new MessageException("Malformed extended packet " +
      		"(number of bytes does not match that expected)");
    }
    
  }
  
  
  @Override
  public void decode(byte[] buffer, boolean noChecks) throws MessageException {
    super.decode(buffer,noChecks);
    if (!noChecks) {
      checkExtendedFormat(getPayload());
    }
  }

  /**
   * {@inheritDoc} 
   */
  @Override
  public List<Byte> getPayloadToSend() {
    // strip off extended data
    List<Byte> standardDataPacket = getStandardPayload();
    return standardDataPacket;
  }
  
  /**
   * {@inheritDoc} 
   * @throws ValidationException if payload malformed
   */
  public void setStandardPayload(ArrayList<Byte> payload) throws ValidationException {
    try {
      ArrayList<Byte> extendedPayload = getPayload();
      for (int i = 0 ; i < AntMesg.MESG_DATA_SIZE ; i++) {
        if(extendedPayload.size() <= i) {
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
      msg.decode(this.toArray(),true);
    } catch (MessageException e) {
        // toArray / decode is by design reversible
        throw new RuntimeException("Should never reach here");
    }
    return msg;   
  }

  /* (non-Javadoc)
   * @see org.cowboycoders.ant.messages.Extendedable#getExtendedData(org.cowboycoders.ant.messages.Constants.DataElements)
   */
  @Override
  public Integer getExtendedData(DataElements element){
    
    ExtendedFlag flag = ExtendedFlag.DISABLE;
    List<DataElements> extendedElements = new ArrayList<DataElements>();
    ArrayList<Byte> payload = getPayload();
    if (payload.size() <= EXTENDED_FLAG_OFFSET) {
      return null;
    }
    byte flagValue = payload.get(EXTENDED_FLAG_OFFSET);
    Integer rtn = null;
    
    for (ExtendedFlag i : ExtendedFlag.values()) {
      List<DataElements> elements = Arrays.asList(i.getElements());
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
    for (DataElements e : extendedElements) {
      if (e == element) {
        rtn = ByteUtils.lsbMerge(payload.subList(index, index += e.getLength()));
      }
      index += e.getLength();
    }
    
    return rtn;
  }
  
  
  /* (non-Javadoc)
   * @see org.cowboycoders.ant.messages.DeviceInfoQueryable#getDeviceNumber()
   */
  @Override
  public Integer getDeviceNumber() {
    return getExtendedData(DataElements.DEVICE_NUMBER);
  }
  
  /* (non-Javadoc)
   * @see org.cowboycoders.ant.messages.DeviceInfoQueryable#getDeviceType()
   */
  @Override
  public Byte getDeviceType() {
    Integer rtn = getExtendedData(DataElements.DEVICE_TYPE);
    if (rtn == null) {
      return null;
    }
    return rtn.byteValue(); 
  }
  
  /* (non-Javadoc)
   * @see org.cowboycoders.ant.messages.DeviceInfoQueryable#getTransmissionType()
   */
  @Override
  public Byte getTransmissionType() {
    Integer rtn = getExtendedData(DataElements.TRANSMISSION_TYPE);
    if (rtn == null) {
      return null;
    }
    return rtn.byteValue(); 
  }
  
  /* (non-Javadoc)
   * @see org.cowboycoders.ant.messages.TimestampInfoQueryable#getRxTimeStamp()
   */
  @Override
  public Integer getRxTimeStamp() {
    return getExtendedData(DataElements.RX_TIMESTAMP);
  }
  
  /* (non-Javadoc)
   * @see org.cowboycoders.ant.messages.RssiInfoQueryable#getRssiMeasurementType()
   */
  @Override
  public Byte getRssiMeasurementType() {
    Integer rtn = getExtendedData(DataElements.RSSI_MEASUREMENT_TYPE);
    if (rtn == null) {
      return null;
    }
    return rtn.byteValue(); 
  }
  
  /* (non-Javadoc)
   * @see org.cowboycoders.ant.messages.RssiInfoQueryable#getRssiThresholdConfig()
   */
  @Override
  public Byte getRssiThresholdConfig() {
    Integer rtn = getExtendedData(DataElements.RSSI_THRESHOLD_CONFIG);
    if (rtn == null) {
      return null;
    }
    return rtn.byteValue(); 
  }
  
  /* (non-Javadoc)
   * @see org.cowboycoders.ant.messages.RssiInfoQueryable#getRssiValue()
   */
  @Override
  public Byte getRssiValue() {
    Integer rtn = getExtendedData(DataElements.RSSI_VALUE);
    if (rtn == null) {
      return null;
    }
    return rtn.byteValue(); 
  }
 
  
}
