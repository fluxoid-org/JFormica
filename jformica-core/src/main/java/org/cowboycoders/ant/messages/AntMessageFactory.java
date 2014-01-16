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


import org.cowboycoders.ant.defines.AntMesg;
import org.cowboycoders.ant.messages.data.AcknowledgedDataMessage;
import org.cowboycoders.ant.messages.data.BroadcastDataMessage;
import org.cowboycoders.ant.messages.data.BurstDataMessage;
import org.cowboycoders.ant.messages.data.ExtendedAcknowledgedDataMessage;
import org.cowboycoders.ant.messages.data.ExtendedBroadcastDataMessage;
import org.cowboycoders.ant.messages.data.ExtendedBurstDataMessage;
import org.cowboycoders.ant.messages.data.LegacyExtendedAcknowledgedDataMessage;
import org.cowboycoders.ant.messages.data.LegacyExtendedAcknowledgedDataMessage;
import org.cowboycoders.ant.messages.data.LegacyExtendedBurstDataMessage;
import org.cowboycoders.ant.messages.notifications.SerialErrorMessage;
import org.cowboycoders.ant.messages.notifications.StartupMessage;
import org.cowboycoders.ant.messages.responses.CapabilityResponse;
import org.cowboycoders.ant.messages.responses.ChannelIdResponse;
import org.cowboycoders.ant.messages.responses.Response;
import org.cowboycoders.ant.messages.responses.ChannelStatusResponse;
import org.cowboycoders.ant.messages.responses.SerialNumberResponse;
import org.cowboycoders.ant.messages.responses.VersionResponse;

/**
 * Returns the correct message for a give data packet
 * @author will
 *
 */
public class AntMessageFactory {
  
  private static final int STANDARD_PACKET_SIZE = 11;
  
  /**
   * Finds the correct message handler
   * @param data raw data from ant api
   * @return the appropriate handler
   * @throws MessageException on decoding error 
   */
  public static StandardMessage createMessage(byte[] data) throws MessageException {
    
    
    MessageId id = MessageId.lookUp(data[AntMesg.MESG_ID_OFFSET]);
    StandardMessage msg = null;
    
    switch(id) {
      
      case BROADCAST_DATA: 
        if (data.length > STANDARD_PACKET_SIZE) {
          msg = new ExtendedBroadcastDataMessage();
          break;
        } 
        msg = new BroadcastDataMessage();
        break;
        
      case ACKNOWLEDGED_DATA:
        if (data.length > STANDARD_PACKET_SIZE) {
          msg = new ExtendedAcknowledgedDataMessage();
          break;
        } 
        msg = new AcknowledgedDataMessage();
        break;
        
      case BURST_DATA:
        if (data.length > STANDARD_PACKET_SIZE) {
          msg = new ExtendedBurstDataMessage();
          break;
        } 
        msg = new BurstDataMessage();
        break;
        
      case EXT_ACKNOWLEDGED_DATA:
        msg = new LegacyExtendedAcknowledgedDataMessage();
        break;
        
      case EXT_BURST_DATA:
        msg = new LegacyExtendedBurstDataMessage();
        break;
        
      case EXT_BROADCAST_DATA:
        msg = new LegacyExtendedAcknowledgedDataMessage();
        break;
        
      case SERIAL_ERROR:
        msg = new SerialErrorMessage();
        break;
        
      case STARTUP:
        msg = new StartupMessage();
        break;
        
      case CAPABILITIES:
        msg = new CapabilityResponse();
        break;
        
      case CHANNEL:
        msg = new ChannelIdResponse();
        break;
        
      case RESPONSE_EVENT:
        msg = new Response();
        break;
        
      case CHANNEL_STATUS:
        msg = new ChannelStatusResponse();
        break;
        
      case GET_SERIAL_NUM:
        msg = new SerialNumberResponse();
        break;
      
      case VERSION:
        msg = new VersionResponse();
        break;
	default:
		break;
      
    }
    
    if (msg == null) {
      return null;
    }
    
     msg.decode(data);

    
    
    
    return msg;
    
  }
  
  
  
  private AntMessageFactory() {
    
  }

}
