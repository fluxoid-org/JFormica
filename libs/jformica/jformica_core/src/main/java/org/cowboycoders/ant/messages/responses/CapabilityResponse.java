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
package org.cowboycoders.ant.messages.responses;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.cowboycoders.ant.messages.Constants.DataElement;
import org.cowboycoders.ant.messages.MessageException;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.StandardMessage;

/**
 * Devices capabilities
 * @author will
 *
 */
public class CapabilityResponse extends StandardMessage {
  
  /**
   * class logger
   */
  public final static Logger LOGGER = Logger.getLogger(CapabilityResponse.class .getName()); 
  
  /**
   * The additional elements we are adding to channelmessage
   */
  private static DataElement [] additionalElements = 
      new DataElement [] {
    DataElement.MAX_CHANNELS,
    DataElement.MAX_NETWORKS,
    DataElement.STANDARD_CAPABILITIES,
    DataElement.ADVANCED_CAPABILITIES,
    DataElement.ADVANCED_CAPABILITIES,
    DataElement.ADVANCED_CAPABILITIES,
  };
  
  public CapabilityResponse() {
    super(MessageId.CAPABILITIES, additionalElements);
    setAllElementsMustBePresent(true);
  }

  @Override
  public void validate() throws MessageException {
    // no additional validation needded
  }
  
  /**
   * 
   * @param category to check capabilities
   * @return a list of capabilities from that category
   */
  public List<Capability> getCapabilitiesList(CapabilityCategory category) {
    List<Capability> rtn = new ArrayList<Capability>();
    byte capabilityByte = 0;
    switch(category) {
      case STANDARD:
        capabilityByte = getDataElement(DataElement.STANDARD_CAPABILITIES).byteValue();
        break;
      case ADVANCED:
        capabilityByte = getDataElement(DataElement.ADVANCED_CAPABILITIES).byteValue();
        break;
      case ADVANCED2:
        capabilityByte = getDataElement(DataElement.ADVANCED_CAPABILITIES,1).byteValue();
        break;        
      case ADVANCED3:
        capabilityByte = getDataElement(DataElement.ADVANCED_CAPABILITIES,2).byteValue();
        break;       
    }
    
    LOGGER.finer("capabilityByte :" + String.format("%x", capabilityByte));
    
    for (Capability c : Capability.getCapabilitiesInCategory(category)) {
      byte mask = c.getCode();
      LOGGER.finer("mask :" + String.format("%x", mask));
      if ((capabilityByte & mask) != 0) {
        rtn.add(c);
      }
    }
    return rtn;
  }
  
  /**
   * Checks whether device has a specific capability
   * @param capability to check
   * @return true if device has this capability, false otherwise
   */
  public boolean hasCapability(Capability capability) {
    List<Capability> capabilities = getCapabilitiesList(CapabilityCategory.STANDARD);
    capabilities.addAll(getCapabilitiesList(CapabilityCategory.ADVANCED));
    capabilities.addAll(getCapabilitiesList(CapabilityCategory.ADVANCED2));
    capabilities.addAll(getCapabilitiesList(CapabilityCategory.ADVANCED3));
    if (capabilities.contains(capability)) return true;
    return false;
  }
  
  /**
   * @return max number of channels this ant device supports
   */
  public int getMaxChannels() {
    return getDataElement(DataElement.MAX_CHANNELS);
  }
  
  /**
   * @return max number of networks this ant device supports
   */
  public int getMaxNetworks() {
    return getDataElement(DataElement.MAX_NETWORKS);
  }
  
  
 

}
