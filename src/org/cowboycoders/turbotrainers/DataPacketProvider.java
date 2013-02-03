package org.cowboycoders.turbotrainers;

public interface DataPacketProvider {
  
  /**
   * Supplies a data place in Broadcast packet
   * @return the data as an 8 byte array
   */
  byte [] getDataPacket();
  
}
