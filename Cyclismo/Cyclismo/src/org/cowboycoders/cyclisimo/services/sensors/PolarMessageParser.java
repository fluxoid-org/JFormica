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
/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cowboycoders.cyclisimo.services.sensors;

import org.cowboycoders.cyclisimo.content.Sensor;

/**
 * An implementation of a Sensor MessageParser for Polar Wearlink Bluetooth HRM.
 *
 *  Polar Bluetooth Wearlink packet example;
 *   Hdr Len Chk Seq Status HeartRate RRInterval_16-bits
 *    FE  08  F7  06   F1      48          03 64
 *   where; 
 *      Hdr always = 254 (0xFE), 
 *      Chk = 255 - Len
 *      Seq range 0 to 15
 *      Status = Upper nibble may be battery voltage
 *               bit 0 is Beat Detection flag.
 *               
 *  Additional packet examples;
 *    FE 08 F7 06 F1 48 03 64           
 *    FE 0A F5 06 F1 48 03 64 03 70
 *    
 * @author John R. Gerthoffer
 */
public class PolarMessageParser implements MessageParser {

  private int lastHeartRate = 0;                   

  /**
   * Applies Polar packet validation rules to buffer.
   *   Polar packets are checked for following;
   *     offset 0 = header byte, 254 (0xFE).
   *     offset 1 = packet length byte, 8, 10, 12, 14.
   *     offset 2 = check byte, 255 - packet length.
   *     offset 3 = sequence byte, range from 0 to 15.
   *     
   * @param buffer an array of bytes to parse
   * @param i buffer offset to beginning of packet.  
   * @return whether buffer has a valid packet at offset i
   */
  private boolean packetValid (byte[] buffer, int i) {
    boolean headerValid = (buffer[i] & 0xFF) == 0xFE;
    boolean checkbyteValid = (buffer[i + 2] & 0xFF) == (0xFF - (buffer[i + 1] & 0xFF));
    boolean sequenceValid = (buffer[i + 3] & 0xFF) < 16;
    
    return headerValid && checkbyteValid && sequenceValid;
  }
  
  @Override
  public Sensor.SensorDataSet parseBuffer(byte[] buffer) {

    int heartRate = 0;
    boolean heartrateValid = false; 
    
    // Minimum length Polar packets is 8, so stop search 8 bytes before buffer ends.
    for (int i = 0; i < buffer.length - 8; i++) {
      heartrateValid = packetValid(buffer,i); 
      if (heartrateValid)  {
        heartRate = buffer[i + 5] & 0xFF;
        break; 
      }
    }

    // If our buffer is corrupted, use decaying last good value.
    if(!heartrateValid) {
      heartRate = (int) (lastHeartRate * 0.8);  
      if(heartRate < 50)
        heartRate = 0;
    }

    lastHeartRate = heartRate;                          // Remember good value for next time.

    // Heart Rate
    Sensor.SensorData.Builder b = Sensor.SensorData.newBuilder()
      .setValue(heartRate)
      .setState(Sensor.SensorState.SENDING);

    Sensor.SensorDataSet sds = Sensor.SensorDataSet.newBuilder()
      .setCreationTime(System.currentTimeMillis())
      .setHeartRate(b)
      .build();
    
    return sds;
  }

  /**
   * Applies packet validation rules to buffer
   *     
   * @param buffer an array of bytes to parse
   * @return whether buffer has a valid packet starting at index zero
   */
  @Override
  public boolean isValid(byte[] buffer) {
    return packetValid(buffer,0);
  }

  /**
   * Polar uses variable packet sizes; 8, 10, 12, 14 and rarely 16.
   * The most frequent are 8 and 10.
   * We will wait for 16 bytes.
   * This way, we are assured of getting one good one.
   * 
   * @return the size of buffer needed to parse a good packet
   */
  @Override
  public int getFrameSize() {
	return 16;									
  }

  /**
   * Searches buffer for the beginning of a valid packet.
   *     
   * @param buffer an array of bytes to parse
   * @return index to beginning of good packet, or -1 if none found.
   */
  @Override
  public int findNextAlignment(byte[] buffer) {
    // Minimum length Polar packets is 8, so stop search 8 bytes before buffer ends.
    for (int i = 0; i < buffer.length - 8; i++) {
      if (packetValid(buffer,i)) {
	    return i;
      }
    }
    return -1;
  }
}
