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
package org.cowboycoders.turbotrainers.bushido.brake;

import java.math.BigInteger;

import org.cowboycoders.ant.events.BroadcastListener;
import org.cowboycoders.ant.messages.data.BroadcastDataMessage;
import org.cowboycoders.ant.utils.ByteUtils;

import static org.cowboycoders.ant.utils.ArrayUtils.*;

public class BushidoBrakeBroadcastDataListener implements BroadcastListener<BroadcastDataMessage> {
	
    private Byte[] data;
    
    private double speed;
    private double cadence;
    private double power;
    private double powerBalance = 50;
    private double brakeTemperature;
    private long counter = 0;
    private double powerLeft; // currently unsure of exactly this is
    private double powerRight;
    
    // Packet identifiers
    private static final Byte[] PARTIAL_PACKET_VERSION_NUMBER = {(byte) 0xAD , 0x02 };
    private static final Byte[] PARTIAL_PACKET_POWER = {(byte) 0x01};
    private static final Byte[] PARTIAL_PACKET_SPEED_CADENCE_BALANCE = {(byte) 0x02};
    private static final Byte[] PARTIAL_PACKET_COUNTER = {(byte) 0x08};
    private static final Byte[] PARTIAL_PACKET_TEMPERATURE_STATUS = {(byte) 0x10};
    private static Byte[] PARTIAL_PACKET_REQUEST_DATA = {(byte) 0xAD, 0x01};
    private BushidoBrakeInternalListener bushidoListener;
    
    public BushidoBrakeBroadcastDataListener(BushidoBrakeInternalListener bushidoListener) {
      this.bushidoListener = bushidoListener;
    }
    
    @Override
    public void receiveMessage(BroadcastDataMessage message) {
        data = message.getData();
        int [] unsignedData = ByteUtils.unsignedBytesToInts(data);
        if (arrayStartsWith(PARTIAL_PACKET_REQUEST_DATA, data)) {
        	bushidoListener.onRequestData(data);
        }
        else if (arrayStartsWith(PARTIAL_PACKET_POWER, data)) {
        	BigInteger leftPower = new BigInteger(new byte [] {data[1],data[2]});
        	BigInteger power = new BigInteger(new byte [] {data[3],data[4]});
        	BigInteger rightPower = new BigInteger(new byte [] {data[5],data[6]});
        	bushidoListener.onChangeLeftPower(leftPower.doubleValue());
        	bushidoListener.onPowerChange(power.doubleValue());
        	bushidoListener.onChangeRightPower(rightPower.doubleValue());
        }
        else if (arrayStartsWith(PARTIAL_PACKET_SPEED_CADENCE_BALANCE,data)) {
        	// speed * 10
        	BigInteger speed = new BigInteger(new byte[] {data[1],data[2]});
        	int cadence = unsignedData[3];
        	int balance = unsignedData[4];
        	bushidoListener.onSpeedChange(speed.doubleValue() / 10);
        	bushidoListener.onCadenceChange(cadence);
        	bushidoListener.onChangeBalance(balance);
        }
        else if (arrayStartsWith(PARTIAL_PACKET_COUNTER,data)) {
        	// i have assumed byte one is part of this data
        	BigInteger counter = new BigInteger(new byte[] {data[1],data[2],data[3],data[4]});
        	bushidoListener.onChangeCounter((int) counter.longValue());
        }
        else if (arrayStartsWith(PARTIAL_PACKET_TEMPERATURE_STATUS,data)) {
        	int temp = unsignedData[4];
        	int status = unsignedData[3];
        	bushidoListener.onChangeBrakeTemperature(temp);
        } 
        else if (arrayStartsWith(PARTIAL_PACKET_VERSION_NUMBER,data)) {
        	int major = unsignedData[2];
        	int minor = unsignedData[3];
        	BigInteger build = new BigInteger(new byte[] {data[4],data[5]});
        	StringBuilder versionString = new StringBuilder();
        	versionString.append(major + ".");
        	versionString.append(minor + ".");
        	versionString.append(build);
        	bushidoListener.onReceiveSoftwareVersion(versionString.toString());
        }
        
    }
    

}