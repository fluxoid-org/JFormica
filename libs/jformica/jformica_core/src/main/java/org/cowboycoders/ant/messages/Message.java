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

import org.cowboycoders.ant.defines.AntMesg;

/**
 * Encapsulation of an ANT message. Contains common functionality
 * for standard and extended messages.
 * 
 * @author will
 *
 */
public class Message implements Messageable {
  
  /** Holds the ant message type **/
  private MessageId id;
  
  /**
   * The variable component of an Ant message with a given message ID. 
   * Excludes: the sync byte, length, message ID and checksum
   */
  private ArrayList<Byte> payload;
  
  public Message() {
    this(MessageId.INVALID,null);
  }
   
  private Message(MessageId id, ArrayList<Byte> payload){
    if (id == null) {
      id = MessageId.INVALID;
    }
    if ( payload  == null ) {
      payload = new ArrayList<Byte>();
    }
    this.payload = payload;
    this.id = id;
    
  }
  
  /**
   * Used to create a message with an empty payload.
   * Useful when constructing messages by adding data to
   * the payload in stages
   * @param id the message id of the ant message
   */
  protected Message(MessageId id) {
    this(id,null);
  }
  
  /*
  public Message(Byte id, ArrayList<Byte> payload){
    this(MessageId.lookUp(id),payload);
  }
  
  public Message(Message message) {
    this(message.getId(),message.getPayload());
  }
  */
  
  /**
   * Gets a copy of the current {@code Message.payload}
   * @return {@code payload} as {@code Arraylist<Byte>}
   */
  @SuppressWarnings("unchecked")
  protected ArrayList<Byte> getPayload() {
    return (ArrayList<Byte>)payload.clone();
  }

  /**
   * Sets the {@code Message.payload}
   * @param payload replaces the current <code>payload</code> with
   *            a copy of the <code>ArrayList<code> passed in
   */
  @SuppressWarnings("unchecked")
  protected void setPayload(ArrayList<Byte> payload) {
    this.payload = (ArrayList<Byte>)payload.clone();
  }

  /* (non-Javadoc)
   * @see org.cowboycoders.ant.messages.MessageInterface#getId()
   */
  @Override
  public MessageId getId() {
    return id;
  }

  /**
   * Sets the message id of the ANT message that this class encapsulates
   * @param id {@code MessageType} to set
   */
  protected void setId(MessageId id){
    this.id = id;
  }
  
  /**
   * Clears all data from message and sets the
   * id to {@code MessageId.Invalid}
   */
  public void reset() {
    setPayload(new ArrayList<Byte>());
    setId(MessageId.INVALID);
  }
  
  /* (non-Javadoc)
   * @see org.cowboycoders.ant.messages.MessageInterface#getPayloadSize()
   */
  @Override
  public byte getPayloadSize() {
    return (byte) payload.size();
  }
  
  /* (non-Javadoc)
   * @see org.cowboycoders.ant.messages.MessageInterface#encode()
   */
  @Override
  public byte [] encode() {
    List<Byte >payload = getPayloadToSend();
    return getArrayFromPayload(payload);
  }

/* (non-Javadoc)
 * @see org.cowboycoders.ant.messages.MessageInterface#getPayloadToSend()
 */
@Override
public List<Byte> getPayloadToSend() {
    return getPayload();
  }

/* (non-Javadoc)
 * @see org.cowboycoders.ant.messages.MessageInterface#getStandardPayload()
 */
@Override
public ArrayList<Byte> getStandardPayload() {
  return getPayload();
  
}

/* (non-Javadoc)
 * @see org.cowboycoders.ant.messages.MessageInterface#setStandardPayload(java.util.ArrayList)
 */
@Override
public void setStandardPayload(ArrayList<Byte> payload) throws ValidationException {
  setPayload(payload);
  
}

/**
 * @{inheritDoc}
 */
  protected void decode(byte[] buffer, boolean noChecks) throws MessageException {
    
    if ( !noChecks && buffer.length <= AntMesg.MESG_DATA_OFFSET ) {
      throw new MessageException("too few bytes to process (message incomplete)");
    }
    
    id = MessageId.lookUp(buffer[AntMesg.MESG_ID_OFFSET]);
    byte[] payload = Arrays.copyOfRange(buffer, AntMesg.MESG_DATA_OFFSET, buffer.length);
    
    ArrayList<Byte> payLoadList = new ArrayList<Byte>();
    for (Byte b : payload) {
      payLoadList.add(b);
    }
    
    setPayload(payLoadList);
  }
  
/**
 * Populates the {@code Message} with data. Should only
 * perform validation if {@code noChecks} is false
 * 
 * @param buffer the ant message as an array of raw bytes
 * @param nochecks skips the sanity check
 * @throws MessageException on decoding error
 */
    @Override
    public
    final void decode(byte[] buffer) throws MessageException {
      decode(buffer,false);      
    }
  
    /**
     * Returns a copy of the {@code Message}. This should be overridden
     * in any child classes. 
     * @return a clone 
     * @throws CloneNotSupportException if an error occured whilst cloning
     */
  @Override
  public Message clone() {
    Message msg = new Message();
    try {
      msg.decode(this.toArray(),true);
    } catch (MessageException e) {
        // toArray / decode is by design reversible
        throw new RuntimeException("Should never reach here");
    }
    return msg;   
  }
  
  
  /**
   * Helper for encode / toArray
   * @param payload to convert to array
   * @return payload as array
   */
  private byte [] getArrayFromPayload(List<Byte> payload) {
    byte payloadSize = (byte) payload.size();
    byte [] rtn = new byte[payloadSize + AntMesg.MESG_HEADER_SIZE];
    
    rtn[AntMesg.MESG_SIZE_OFFSET] = payloadSize;
    rtn[AntMesg.MESG_ID_OFFSET] = id.getMessageID();
    for (byte i = AntMesg.MESG_DATA_OFFSET ; i < rtn.length ; i++) {
      rtn[i] = payload.get(i - AntMesg.MESG_DATA_OFFSET);
    }
    
    return rtn;
  }
  
  /**
   * {@inheritDoc}
   */
  public byte [] toArray() {
    List<Byte >payload = getPayload();
    return getArrayFromPayload(payload);
  }
  
  

}

