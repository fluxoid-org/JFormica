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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.cowboycoders.ant.utils.BigIntUtils;
import org.cowboycoders.turbotrainers.DataPacketProvider;
import org.cowboycoders.turbotrainers.bushido.BushidoUtils;
import org.cowboycoders.utils.LoopingListIterator;
import org.cowboycoders.utils.TrapezoidIntegrator;

/**
 * Model for current settings
 * 
 * @author will
 */
public class BushidoData {
	
  private static final byte RESISTANCE_PACKET_IDENTIFER = 0x01;
  private static final int RESISTANCE_PACKET_IDENTIFER_INDEX = 0x00;
  
  // limits upto and including
  private static final int RESISTANCE_LOW_LIMIT = -414;
  private static final int RESISTANCE_HIGH_LIMIT = 3327;
  public static final double BALANCE_MAX = 100;
  public static final double BALANCE_MIN = 0;
  public static final double RESISTANCE_MAX = 100;
  public static final double RESISTANCE_MIN = 0;
  
  double speed;
  double cadence;
  double resistance;
  double power;
  double powerBalance = 50;
  double brakeTemperature;
  long counter = 0;
  private double powerLeft; // currently unsure of exactly this is
  private double powerRight;
  
  public double getPowerLeft() {
	return powerLeft;
}


public void setPowerLeft(double powerLeft) {
	this.powerLeft = powerLeft;
}


public double getPowerRight() {
	return powerRight;
}


public void setPowerRight(double powerRight) {
	this.powerRight = powerRight;
}


/**
   * Unknown counter value
   * @return
   */
  public long getCounter() {
	return counter;
}


  /**
   * Unknown counter value
   * @return
   */
public void setCounter(long counter) {
	this.counter = counter;
}


/**
   * 
   * @return speed in km/h
   */
  public double getSpeed() {
	return speed;
}


  /**
   * 
   * @param speed km/h
   */
public void setSpeed(double speed) {
	this.speed = speed;
    double timeStampSeconds = System.nanoTime() / (Math.pow(10, 9));
    double speedMetresPerSecond = 1000 * speed / (60 * 60);
    synchronized(speedIntegrator) {
    	speedIntegrator.add(timeStampSeconds, speedMetresPerSecond);
    }
}

/**
 * Distance in m
 * @return
 */
public double getDistance() {
    synchronized(speedIntegrator) {
    	return speedIntegrator.getIntegral();
    }
}

/**
 * 
 * @return cadence as rpm
 */
public double getCadence() {
	return cadence;
}


/**
 * 
 * @param cadence as rpm
 */
public void setCadence(double cadence) {
	this.cadence = cadence;
}


/**
 * @return brake resistance as percent
 */
public double getResistance() {
	return resistance;
}


/**
 * Sets brake resistance in percent
 * @param resistance
 */
public void setResistance(double resistance) {
	if (resistance > RESISTANCE_MAX) resistance = RESISTANCE_MAX;
	if (resistance < RESISTANCE_MIN) resistance = RESISTANCE_MIN;
	this.resistance = resistance;
}


/**
 * 
 * @return power in watts
 */
public double getPower() {
	return power;
}

/**
 * 
 * @param power in watts
 */
public void setPower(double power) {
	this.power = power;
}


/**
 * power balance (between 0-100) : 50 balanced
 * @return
 */
public double getPowerBalance() {
	return powerBalance;
}

/**
 * power balance (between 0-100) : 50 balanced
 * @param powerBalance
 */
public void setPowerBalance(double powerBalance) {
	if (powerBalance > BALANCE_MAX) powerBalance = BALANCE_MAX;
	if (powerBalance < BALANCE_MIN) powerBalance = BALANCE_MIN;
	this.powerBalance = powerBalance;
}


/**
 * Brake temp in degrees
 * @return temp
 */
public double getBrakeTemperature() {
	return brakeTemperature;
}


/**
 * Sets Brake temp in degrees
 * @param new temp
 */
public void setBrakeTemperature(double brakeTemperature) {
	this.brakeTemperature = brakeTemperature;
}


// in m
  TrapezoidIntegrator speedIntegrator = new TrapezoidIntegrator();
 
  
  private List<DataPacketProvider> packetProviders = new ArrayList<DataPacketProvider>();
  private Iterator<DataPacketProvider> packetProvidersIterator = 
      new LoopingListIterator<DataPacketProvider>(packetProviders);
  
  
  /**
   * Provides a data packet containing current resistance
   */
  DataPacketProvider resistanceProvider = new DataPacketProvider() {

    @Override
    public byte[] getDataPacket() {
      byte [] resistancePacket = new byte[8];
      resistancePacket[RESISTANCE_PACKET_IDENTIFER_INDEX] = RESISTANCE_PACKET_IDENTIFER; // this is the packet Identifier
      resistancePacket = injectResistance(resistancePacket);
      return resistancePacket;
    }
    
  };
  

  
  {
   packetProviders.add(resistanceProvider);
  }
  

  /**
   * Send data packets in order specified by packetProvders
   * @return
   */
  public byte[] getDataPacket() {
    return packetProvidersIterator.next().getDataPacket();
  }


protected byte[] injectResistance(byte[] resistancePacket) {
	double percentage = getResistance();
	long resistanceBrakeValue = (int)(((percentage /100) * (RESISTANCE_HIGH_LIMIT - RESISTANCE_LOW_LIMIT)) + RESISTANCE_LOW_LIMIT);
	BigInteger bigResistance = BigIntUtils.convertLong(resistanceBrakeValue);
	byte [] bytes = BigIntUtils.clipToByteArray(bigResistance, 2);
	resistancePacket[1] = bytes[0];
	resistancePacket[2] = bytes[1];
	return resistancePacket;
}

public static void print(byte [] packet) {
	for (byte b : packet) {
		System.out.printf("%02x:", b);
	}
	System.out.println();
}

public static void main(String[] args) {
	BigInteger bi = new BigInteger(new byte[] {(byte) 254, 98});
	System.out.println("resistance low: "+ bi);
	bi = new BigInteger(new byte[] {(byte)0x0c, (byte) 0xff});
	System.out.println("resistance high: "+ bi);
	System.out.println();
	BushidoData bd = new BushidoData();
	bd.setResistance(0);
	print(bd.getDataPacket());
	bd.setResistance(100);
	print(bd.getDataPacket());
}

}