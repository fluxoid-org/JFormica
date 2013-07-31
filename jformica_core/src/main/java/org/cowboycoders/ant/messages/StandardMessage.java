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
/**
 * 
 */
package org.cowboycoders.ant.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cowboycoders.ant.messages.Constants.DataElement;
import org.cowboycoders.ant.utils.BitUtils;
import org.cowboycoders.ant.utils.DataElementUtils;
import org.cowboycoders.ant.utils.IntUtils;
import org.cowboycoders.ant.utils.ValidationUtils;

/**
 * Standard Decorator with no added functionality
 * 
 * @author will
 *
 */
public abstract class StandardMessage
    implements MessageDecorator, Messageable {
    
  /**
   * The message being decorated
   */
  private Message message;
  
  /**
   * original mesasgeId - used to check this doesn't change during a decode
   */
  private final MessageId id;
  
  
  /**
   * If true, will validate that the message length is at least as long as
   * {@code messageElements#size()} 
   */
  private boolean allElementsMustBePresent = false;
  
  /**
   * holds the total length (in bytes) of all elements in {@code messageElements} 
   */
  private int totalElementLength = 0;
  
  /**
   *  Stores the message elements in the order they appear in the message
   */
  private DataElement [] messageElements;
  
  /**
   * @return the messageElements
   */
  private DataElement[] getMessageElements() {
    return messageElements;
  }

  /**
   * @param messageElements the messageElements to set
   */
  private void setMessageElements(DataElement[] messageElements) {
    this.messageElements = messageElements;
  }

  protected StandardMessage(MessageId id, 
      DataElement[] messageElements) {
    this(null, id, messageElements);
  }
  
  /**
   * Decorates a copy of the message 
   * @param message the <code>Message</code> to decorate
   * @throws FatalMessageException if there is an error building message
   */
  protected StandardMessage(Message message, MessageId id, 
      ArrayList<DataElement> messageElements) {
    this(message,id,messageElements.toArray(new DataElement[0]));
  }
  
  /**
   * Decorates a copy of the message 
   * @param message the <code>Message</code> to decorate
   * @throws FatalMessageException if there is an error building message
   */
  protected StandardMessage(Message message, MessageId id, 
      DataElement [] messageElements) {
    
    this.id = id;
    
    ArrayList<Byte> payload = new ArrayList<Byte>();

    if (message == null) {
      message = new Message();
    }
    
    if (messageElements == null) {
      throw new FatalMessageException("StandardMessage: messageElements cannot be null");
    }
    
    this.message = message.clone();
    this.message.reset();
    this.message.setId(id);
    this.messageElements = messageElements;
    
    for (DataElement element : messageElements) {
      int elementLength = element.getLength();
      totalElementLength += elementLength;
      for (int i= 0 ; i < elementLength ; i++) {
        payload.add((byte) 0);
      }
    }
    
    try {
      this.message.setStandardPayload(payload);
    } catch (ValidationException e) {
      throw new FatalMessageException("Error setting payload", e);
    }
    
  }
  
  /**
   * {@inheritDoc} 
   */
  @Override
  public final MessageId getId() {
    return message.getId();
  }
  
  /**
   * {@inheritDoc} 
   */
  protected
  final void setId(MessageId id) {
    message.setId(id);
  }
  
  /**
   * {@inheritDoc} 
   */
  @Override
  public final byte getPayloadSize() {
    return message.getPayloadSize();
  }
  
  /**
   * {@inheritDoc} 
   */
  @Override
  public final byte[] encode() {
    return message.encode();
  }
  
  /**
   * {@inheritDoc} 
   */
  @Override
  public final List<Byte> getPayloadToSend() {
    return message.getPayloadToSend();
  }
  
  /**
   * {@inheritDoc} 
   */
  
  protected
  final void decode(byte[] buffer, boolean noChecks) throws MessageException {
    message.decode(buffer, noChecks);
    if (!noChecks) {
      if (message.getId() != this.id) {
        throw new MessageException("Mesage Id does not match that expected for" +
        		" " + this.getClass());
      }
      if (this.allElementsMustBePresent && getStandardPayload().size() < totalElementLength) {
        throw new MessageException("Insuffucient data for" +
            " " + this.getClass());
      }
      validate();
    }
  }
  
  /**
   * {@inheritDoc} 
   */
  @Override
  public
  final void decode(byte[] buffer) throws MessageException {
    decode(buffer,false);
  }
  
  /**
   * This method should validate the payload,
   * so that calls to the getters will not
   * throw exceptions. Throws a {@code MessagException} if invalid.
   * 
   * Checking the messageId is equal to be that passed into
   * the constructor is already taken care of.
   * 
   * setAllElementsMustBePresent() can be used to add additional
   * message length validation. If set, the message length must
   * be equal to the length of the messageElements passed into 
   * constructor.
   * 
   * Called after decode.
   */
  public abstract void validate() throws MessageException;

  /**
   * {@inheritDoc} 
   */
  @Override
  public final Message getBackendMessage() {
    Message message = this.message;
    return message;
  }

  @Override
  public final ArrayList<Byte> getStandardPayload() {
    return message.getStandardPayload();
  }

  @Override
  public final void setStandardPayload(ArrayList<Byte> payload)
      throws ValidationException {
    message.setStandardPayload(payload);
    
  }
  
  /**
   * Sets the value of a DataElement in a given payload
   * @param element to set
   * @param value to set
   * @param skip the number of identical elements to skip before reaching desired element
   * @return true on success, else false
   */
  protected boolean setDataElement(DataElement element, Integer value, int skip) {
    ArrayList<Byte> payload = getStandardPayload();
    boolean completed = false;
    completed = DataElementUtils.setDataElement(payload,messageElements,element,value,0,skip);
    if (completed) {
      try {
        setStandardPayload(payload);
      } catch (ValidationException e) {
        throw new FatalMessageException("Cannot set payload");
      }
      
    }
    return completed;
  }
  
  protected boolean setDataElement(DataElement element, Integer value) {
    return setDataElement(element,value,0);
  }
  
  /**
   * Gets the value of a DataElement in a given payload
   * @param element to get data for
   * @param skip the number of identical elements to skip before returning
   * @return the data associated with the element
   */
  protected Integer getDataElement(DataElement element,int skip) {
    Integer rtn = null;
    ArrayList<Byte> payload = getStandardPayload();
    int offset = 0; 
    rtn = DataElementUtils.getDataElement(payload, messageElements, element, offset,skip);
    return rtn;
  }
  
  /**
   * See {@code getDataElement(element,skip)}
   */
  protected Integer getDataElement(DataElement element) {
    return getDataElement(element,0);
  }
  
  /**
   * Appends  A DataElement to messageElements, the field that represents
   * the current message structure
   * @param element the element to append
   */
  protected void addOptionalDataElement(DataElement element) {
    messageElements = getMessageElements();
    DataElement[] newElements = Arrays.copyOf(messageElements, 
        messageElements.length + 1);
    newElements[messageElements.length] = element;
    setMessageElements(newElements);
    ArrayList<Byte> payload = getStandardPayload();
    for (int i= 0 ; i < element.getLength() ; i++) {
      payload.add((byte) 0);
    }
    try {
      this.message.setStandardPayload(payload);
    } catch (ValidationException e) {
      throw new FatalMessageException("Error setting payload", e);
    }
  }
  
  /**
   * Sets individual bits of given {@code DataElement} specified
   * using a mask and value. The value's left most bit is aligned
   * with the left most bit of the mask. Values must be positive,
   * with a value no greater that the maximum number that can be
   * represented by within the masked bits.
   * 
   * The mask's bits must be contiguous, or the behaviour
   * is undefined.
   * 
   * @param element the whole element to apply the mask to
   * @param value the value to set in the mask bits
   * @param mask only bits marked in mask are changed
   */
  protected void setPartialDataElement(DataElement element, int value, int mask) {
    int wholeElement = getDataElement(element);
    try {
    	wholeElement = IntUtils.setMaskedBits(wholeElement,mask ,value);
    } catch (IllegalArgumentException e) {
    	throw new FatalMessageException(e);
    }
    setDataElement(element, wholeElement);
  }

  
  /**
   * Convenience method to perform a max-min validation before setting
   * @param element
   * @param value
   * @param skip the number of identical elements to skip
   * @throws ValidationException
   */
  protected void setAndValidateDataElement(DataElement element, int value, int skip) 
      throws ValidationException {
    Integer maxValue = element.getMaxValue();
    Integer minValue = element.getMinValue();
    if (maxValue == null) {
      maxValue = Integer.MAX_VALUE;
    }
    if (minValue == null) {
      minValue = 0;
    }
    ValidationUtils.maxMinValidator(minValue, maxValue, value, 
        MessageExceptionFactory.createMaxMinExceptionProducable(element.toString())
        );
    setDataElement(element,value,skip);
  }
  
  /**
   * Convenience method to perform a max-min validation before setting
   * @param element
   * @param value
   * @throws ValidationException
   */
  protected void setAndValidateDataElement(DataElement element, int value) throws ValidationException {
    setAndValidateDataElement(element,value,0);
  }
  
  /**
   * {@inheritDoc}
   */
  public byte [] toArray() {
    return message.toArray();
  }
  
  /**
   * @param flag true to set, false to clear
   */
  protected void setAllElementsMustBePresent(boolean flag) {
    this.allElementsMustBePresent = flag;
  }

  


  
}
