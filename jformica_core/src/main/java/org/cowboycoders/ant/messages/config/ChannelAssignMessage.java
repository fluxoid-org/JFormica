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
package org.cowboycoders.ant.messages.config;

import java.util.HashSet;
import java.util.Set;

import org.cowboycoders.ant.messages.ChannelMessage;
import org.cowboycoders.ant.messages.ChannelType;
import org.cowboycoders.ant.messages.ValidationException;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.Constants.DataElement;

/**
 * Channel assignment message
 * @author will
 *
 */
public class ChannelAssignMessage extends ChannelMessage {
  
  /**
   * Possible extended assignment options
   * @author will
   *
   */
  public enum ExtendedAssignment {
    BACKGROUND_SCANNING_ENABLE      (0x01),
    FREQUENCY_AGILITY_ENABLE        (0x04),
    ;
    
    /**
     * value in the ant spec
     */
    public int code;
    
    ExtendedAssignment (int code) {
      this.code =  code;
    }
  }
  
  /**
   * The additional elements we are adding to channelmessage
   */
  private static DataElement [] additionalElements = 
      new DataElement [] {
    DataElement.CHANNEL_TYPE,
    DataElement.NETWORK_NUMBER,
    //DataElements.EXTENDED_ASSIGNMENT,
  };

  private ChannelType type;
  

/**
   * Assignment message - multiple extended assignment options
   * @param networkNo network we are assigning
   * @param type type of channel required
   * @param extended extended assignment options
   * @throws ValidationException on error constructing this message
   */
  public ChannelAssignMessage(Integer networkNo, ChannelType type,
      Set<ExtendedAssignment> extended) {
    super(MessageId.ASSIGN_CHANNEL, 0,additionalElements);
    this.type = type;
    setChannelType(type);
    setExtendedAssignment(extended);
    setNetworkNumber(networkNo);
    
  }
  
  
  public ChannelAssignMessage(Integer networkNo, ChannelType type) {
    this(networkNo,type, generateExtendedSet(new ExtendedAssignment[0]));
  }
  
  public ChannelAssignMessage(ChannelType type) {
    this(0,type, generateExtendedSet(new ExtendedAssignment[0]));
  }
  
  public ChannelAssignMessage(Integer networkNo, ChannelType type,
      ExtendedAssignment ... extended) {
    this(networkNo,type, generateExtendedSet(extended));
  }
  
  private static Set<ExtendedAssignment> generateExtendedSet(
      ExtendedAssignment [] extended) {
    Set<ExtendedAssignment> ea = new HashSet<ExtendedAssignment>();
    if (extended != null) {
      for (ExtendedAssignment e : extended) {
        ea.add(e);
      }
    }
    return ea;
  }

  public void setNetworkNumber(int network) {
    setDataElement(DataElement.NETWORK_NUMBER,network);
  }

  private void setExtendedAssignment(Set<ExtendedAssignment> extended) {
    if (extended == null || extended.size() == 0 ) {
      return;
    }
    // this is optional so, we didn't add it on the constructor
    addOptionalDataElement(DataElement.EXTENDED_ASSIGNMENT);
    int code = 0;
    for (ExtendedAssignment ea : extended) {
      code  |= ea.code;
    }
    setDataElement(DataElement.EXTENDED_ASSIGNMENT, code);
    
  }

  private void setChannelType(ChannelType type) {
    setDataElement(DataElement.CHANNEL_TYPE,type.getChannelTypeCode());
  }
  
  public ChannelType getType() {
	return type;
}
  

  
}
