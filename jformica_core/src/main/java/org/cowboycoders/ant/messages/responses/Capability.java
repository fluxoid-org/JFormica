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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ant node capabilities
 * @author will
 *
 */

public enum Capability {
  

  
  //////////////////////////////////////////////
  // Standard capabilities defines
  //////////////////////////////////////////////
  NO_RX_CHANNELS                (0x01,CapabilityCategory.STANDARD),
  NO_TX_CHANNELS                (0x02,CapabilityCategory.STANDARD),
  NO_RX_MESSAGES                (0x04,CapabilityCategory.STANDARD),
  NO_TX_MESSAGES                (0x08,CapabilityCategory.STANDARD),
  NO_ACKD_MESSAGES              (0x10,CapabilityCategory.STANDARD),
  NO_BURST_TRANSFER             (0x20,CapabilityCategory.STANDARD),

  //////////////////////////////////////////////
  // Advanced capabilities defines
  //////////////////////////////////////////////
  /** 
   * Support for this functionality has been dropped 
   * @deprecated */
  OVERUN_UNDERRUN               (0x01,CapabilityCategory.ADVANCED),
  NETWORK_ENABLED               (0x02,CapabilityCategory.ADVANCED),
  /** This Version of the AP1 does not support transmit and only had a limited release */
  AP1_VERSION_2                 (0x04,CapabilityCategory.ADVANCED),
  SERIAL_NUMBER_ENABLED         (0x08,CapabilityCategory.ADVANCED),
  PER_CHANNEL_TX_POWER_ENABLED  (0x10,CapabilityCategory.ADVANCED),
  LOW_PRIORITY_SEARCH_ENABLED   (0x20,CapabilityCategory.ADVANCED),
  SCRIPT_ENABLED                (0x40,CapabilityCategory.ADVANCED),
  SEARCH_LIST_ENABLED           (0x80,CapabilityCategory.ADVANCED),

  //////////////////////////////////////////////
  // Advanced capabilities 2 defines
  //////////////////////////////////////////////
  LED_ENABLED                   (0x01,CapabilityCategory.ADVANCED2),
  EXT_MESSAGE_ENABLED           (0x02,CapabilityCategory.ADVANCED2),
  SCAN_MODE_ENABLED             (0x04,CapabilityCategory.ADVANCED2),
  RESERVED                      (0x08,CapabilityCategory.ADVANCED2),
  PROX_SEARCH_ENABLED           (0x10,CapabilityCategory.ADVANCED2),
  EXT_ASSIGN_ENABLED            (0x20,CapabilityCategory.ADVANCED2),
  FREE_1                        (0x40,CapabilityCategory.ADVANCED2),
  FIT1_ENABLED                  (0x80,CapabilityCategory.ADVANCED2),

  //////////////////////////////////////////////
  // Advanced capabilities 3 defines
  //////////////////////////////////////////////
  SENSRCORE_ENABLED             (0x01,CapabilityCategory.ADVANCED3),
  RESERVED_1                    (0x02,CapabilityCategory.ADVANCED3),
  RESERVED_2                    (0x04,CapabilityCategory.ADVANCED3),
  RESERVED_3                    (0x08,CapabilityCategory.ADVANCED3),
  
  ;
  
  /**
   * code as defined in ant spec
   */
  private byte code;
  
  /**
   * elements category
   */
  private CapabilityCategory category;
  

  /**
   * maps code to {@code} Capability
   */
  private static Map<Byte,Capability> codeMap 
    = new HashMap<Byte,Capability>();
  
  private static Map<CapabilityCategory, List<Capability>> categoryMap
    = new HashMap<CapabilityCategory, List<Capability>>();
  
  static {
    for (CapabilityCategory c : CapabilityCategory.values()) {
      categoryMap.put(c, new ArrayList<Capability>());
    }
  }
  
  static {
    for( Capability c : Capability.values() ) {
      codeMap.put(c.getCode(), c);
      categoryMap.get(c.getCategory()).add(c);
    }
  }
  
  
  Capability(int code, CapabilityCategory category) {
    setCode((byte) code);
    setCategory(category);
  }
  
  /**
   * 
   * @param code {@code int} representation
   * @return Capability, code maps to
   */
  public static Capability lookUp(int code) {
    return codeMap.get((byte) code);
  }
  
  /**
   * @return the code
   */
  public byte getCode() {
    return code;
  }


  /**
   * @param code the code to set
   */
  private void setCode(byte code) {
    this.code = code;
  }
  
  /**
   * @return the category
   */
  public CapabilityCategory getCategory() {
    return category;
  }

  /**
   * @param category the category to set
   */
  private void setCategory(CapabilityCategory category) {
    this.category = category;
  }
  
  public static List<Capability> getCapabilitiesInCategory(CapabilityCategory category) {
    List<Capability> rtn = new ArrayList<Capability>();
    List<Capability> local = categoryMap.get(category);
    for (int i = 0 ; i< local.size() ; i++) {
      rtn.add(local.get(i));
    }
    
    return rtn;
  }
  
  

}
