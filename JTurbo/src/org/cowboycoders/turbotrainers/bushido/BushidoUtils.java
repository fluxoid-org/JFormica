/*
*    Copyright (c) 2013, Will Szumski
*    Copyright (c) 2013, Doug Szumski
*
*    This file is part of Cyclismo.
*
*    Cyclismo is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Cyclismo is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Cyclismo.  If not, see <http://www.gnu.org/licenses/>.
*/
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
