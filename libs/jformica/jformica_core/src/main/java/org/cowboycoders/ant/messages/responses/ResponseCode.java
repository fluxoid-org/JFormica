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

import java.util.HashMap;

/**
 * Warning: must compare getCodes() as there are duplicates
 * and only one ResponseCode is returned from map
 * @author will
 *
 */
public enum ResponseCode {
  
   RESPONSE_NO_ERROR                          (0x00),
   NO_EVENT                                   (0x00),
   // Combined
   RESPONSE_NO_ERROR_OR_NO_EVENT              (0x00),

   EVENT_RX_SEARCH_TIMEOUT                    (0x01),
   EVENT_RX_FAIL                              (0x02),
   EVENT_TX                                   (0x03),
   EVENT_TRANSFER_RX_FAILED                   (0x04),
   EVENT_TRANSFER_TX_COMPLETED                (0x05),
   EVENT_TRANSFER_TX_FAILED                   (0x06),
   EVENT_CHANNEL_CLOSED                       (0x07),
   EVENT_RX_FAIL_GO_TO_SEARCH                 (0x08),
   EVENT_CHANNEL_COLLISION                    (0x09),
  /** a pending transmit transfer has begun */
   EVENT_TRANSFER_TX_START                    (0x0A),

   EVENT_CHANNEL_ACTIVE                       (0x0F),
  
  /** only enabled in FIT1 */
   EVENT_TRANSFER_NEXT_DATA_BLOCK			  (0x11),
   EVENT_TRANSFER_TX_NEXT_MESSAGE             (0x11),

  /** returned on attempt to perform an action from the wrong channel state */
   CHANNEL_IN_WRONG_STATE                     (0x15),
  /** returned on attempt to communicate on a channel that is not open */
   CHANNEL_NOT_OPENED                         (0x16),
  /** returned on attempt to open a channel without setting the channel ID */
   CHANNEL_ID_NOT_SET                         (0x18),
  /** returned when attempting to start scanning mode, when channels are still open */
   CLOSE_ALL_CHANNELS                         (0x19),

  /** returned on attempt to communicate on a channel with a TX transfer in progress */
   TRANSFER_IN_PROGRESS                       (0x1F),
  /** returned when sequence number is out of order on a Burst transfer */
   TRANSFER_SEQUENCE_NUMBER_ERROR             (0x20),
   TRANSFER_IN_ERROR                          (0x21),
   TRANSFER_BUSY                              (0x22),

  /** returned if there is a framing error on an incomming message */
   INVALID_MESSAGE_CRC                        (0x26),
  /** returned if a data message is provided that is too large */
   MESSAGE_SIZE_EXCEEDS_LIMIT                 (0x27),
  /** returned when the message has an invalid parameter */
   INVALID_MESSAGE                            (0x28),
  /** returned when an invalid network number is provided */
   INVALID_NETWORK_NUMBER                     (0x29),
  /** returned when the provided list ID or size exceeds the limit */
   INVALID_LIST_ID                            (0x30),
  /** returned when attempting to transmit on channel 0 when in scan mode */
   INVALID_SCAN_TX_CHANNEL                    (0x31),
  
  /** returned when an invalid parameter is specified in a configuration message */
   INVALID_PARAMETER_PROVIDED                 (0x33),

   EVENT_SERIAL_QUE_OVERFLOW                  (0x34),
  /** ANT event que has overflowed and lost 1 or more events */
   EVENT_QUE_OVERFLOW                         (0x35),

  /** debug event for XOSC16M on LE1 */
   EVENT_CLK_ERROR                            (0x36),

  /** error writing to script, memory is full */
   SCRIPT_FULL_ERROR                          (0x40),
  /** error writing to script, bytes not written correctly */
   SCRIPT_WRITE_ERROR                         (0x41),
  /** error accessing script page */
   SCRIPT_INVALID_PAGE_ERROR                  (0x42),
  /** the scripts are locked and can't be dumped */
   SCRIPT_LOCKED_ERROR                        (0x43),

  /** returned to the Command_SerialMessageProcess function, so no reply message is generated */
   NO_RESPONSE_MESSAGE                        (0x50),
  /** default return to any mesg when the module determines that the mfg procedure has not been fully completed */
   RETURN_TO_MFG                              (0x51),

  /** Fit1 only event added for timeout of the pairing state after the Fit module becomes active */
   FIT_ACTIVE_SEARCH_TIMEOUT                  (0x60),
  /** Fit1 only */
   FIT_WATCH_PAIR                             (0x61),
  /** Fit1 only */
   FIT_WATCH_UNPAIR                           (0x62),

   USB_STRING_WRITE_FAIL                      (0x70),

  /** Internal only events below this point */
   INTERNAL_ONLY_EVENTS                       (0x80),
  /** INTERNAL: Event for a receive message */
   EVENT_RX                                   (0x80),
  /** INTERNAL: EVENT for a new active channel */
   EVENT_NEW_CHANNEL                          (0x81),
  /** INTERNAL: Event to allow an upper stack events to pass through lower stacks */
   EVENT_PASS_THRU                            (0x82),

  /** INTERNAL: Event to replace any event we do not wish to go out, will also zero the size of the Tx message */
   EVENT_BLOCKED                              (0xFF), 
   
   ;
   
   private static HashMap<Byte, ResponseCode> idTypeMap = 
       new HashMap<Byte, ResponseCode>();
   
   private byte code;
   
   /**
   * @return the code
   */
  public byte getCode() {
    return code;
  }
  
  static {
    for( ResponseCode c : ResponseCode.values() ) {
      idTypeMap.put(c.getCode(), c);
    }
  }

  /**
   * @param code the code to set
   */
  private void setCode(byte code) {
    this.code = code;
  }
  /**
   * All codes are truncated to a byte
   * @param code in ant spec
   */
  ResponseCode(int code) {
     setCode((byte)code);
   }
  
  /**
   * Returns the response type for a given response ID
   * 
   * Warning: this isn't guaranteed to return a given
   * response code, as there are duplicates. 
   * 
   * Call getCode() on the returned object and compare to
   * that given from the ResponseType you were expecting
   * 
   * @param responseCode byte value of the response ID
   * @return the type mapped to {@code ResponseCode} or null
   *            if mapping doesn't exist   
   */
  public static ResponseCode lookUp(byte responseCode) {
    return idTypeMap.get(responseCode);  
  }
  
  

}
