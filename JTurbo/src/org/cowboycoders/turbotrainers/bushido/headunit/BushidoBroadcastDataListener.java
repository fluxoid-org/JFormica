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
package org.cowboycoders.turbotrainers.bushido.headunit;

import org.cowboycoders.ant.events.BroadcastListener;
import org.cowboycoders.ant.messages.data.BroadcastDataMessage;
import org.cowboycoders.ant.utils.ArrayUtils;
import org.cowboycoders.ant.utils.ByteUtils;

import static org.cowboycoders.ant.utils.ArrayUtils.*;

public class BushidoBroadcastDataListener implements BroadcastListener<BroadcastDataMessage> {
    private Byte[] data;
    private double speed;
    private double power;
    private double cadence;
    private double distance;
    private double heartRate;
    // Packet identifiers
    private static final Byte[] PARTIAL_PACKET_PAUSED = {(byte) 0xAD , 0x01 , 0x03};
    private static final Byte[] PARTIAL_PACKET_REQUEST_DATA = {(byte) 0xAD, 0x01, 0x02};
    private static final Byte[] PARTIAL_PACKET_DATA = {(byte) 0xDD};
    private static final Byte[] PARTIAL_PACKET_SPEED_POWER_CADENCE = {(byte) 0xDD,(byte) 0x01};
    private static final Byte[] PARTIAL_PACKET_DISTANCE_HEART_RATE = {(byte) 0xDD,(byte) 0x02};
    private static Byte[] PARTIAL_PACKET_REQUEST_STATUS = {(byte) 0xAD};
    private BushidoInternalListener bushidoListener;
    
    public BushidoBroadcastDataListener(BushidoInternalListener bushidoListener) {
      this.bushidoListener = bushidoListener;
    }
    
    @Override
    public void receiveMessage(BroadcastDataMessage message) {
        data = message.getData();
        int [] unsignedData = ByteUtils.unsignedBytesToInts(data);
        if (arrayStartsWith(PARTIAL_PACKET_DATA, data)) {
            data = message.getData();
            if (arrayStartsWith(PARTIAL_PACKET_SPEED_POWER_CADENCE, data)) {
                // speed in km/h
                speed = ((unsignedData [2] << 8) + unsignedData [3]) / 10;
                power = (unsignedData [4] << 8) + unsignedData [5];
                cadence = unsignedData [6];
                bushidoListener.onSpeedChange(speed);
                bushidoListener.onPowerChange(power);
                bushidoListener.onCadenceChange(cadence);
                //System.out.println("Speed: " + speed);
                //System.out.println("Power: " + power);
                //System.out.println("Cadence: " + cadence);
            }
            if (arrayStartsWith(PARTIAL_PACKET_DISTANCE_HEART_RATE, data)) {
                // 3 byte (24 bit) shift will wrap an int
                distance = ((long)unsignedData [2] << 24) + (unsignedData [3] << 16) + (unsignedData [4] << 8) + unsignedData [5];
                heartRate = unsignedData [6];
                bushidoListener.onDistanceChange(distance);
                //System.out.println("Distance: " + distance);
                bushidoListener.onHeartRateChange(heartRate);
               // System.out.println("Heart rate: " + heartRate);
            }
        } else if (arrayStartsWith(PARTIAL_PACKET_REQUEST_STATUS, data)){
            if (arrayStartsWith(PARTIAL_PACKET_PAUSED, data)){
                //Send unpause command
                bushidoListener.onRequestPauseStatus();
            } else if (arrayStartsWith(PARTIAL_PACKET_REQUEST_DATA, data)){
                bushidoListener.onRequestData();
                //Send data
            }
        }
    }
    

}