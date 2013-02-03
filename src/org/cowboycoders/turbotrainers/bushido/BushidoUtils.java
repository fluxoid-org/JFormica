package org.cowboycoders.turbotrainers.bushido;

public class BushidoUtils {
  private BushidoUtils() {
    
  }
  
  // GENERATE FREQUENTLY MODIFED PACKETS

  public static byte[] getDc01Prototype() {
    //DC01 Packet prototype
    byte[] dc01Packet = {(byte)0xdc, 0x01, 0x00, 0x00, 0x00, 0x4d, 0x00, 0x00};
    return dc01Packet;
  }
  
  public static byte [] getDc02Prototype() {
    byte[] dc02Packet ={ (byte) 0xdc, 0x02, 0x00, (byte) 0x99, 0x00, 0x00,
      0x00, 0x00 };
    return dc02Packet;
  }

}
