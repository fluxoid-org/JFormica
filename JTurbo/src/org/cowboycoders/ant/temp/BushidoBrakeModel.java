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
package org.cowboycoders.ant.temp;
import java.math.BigInteger;
import org.cowboycoders.ant.utils.BigIntUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.cowboycoders.turbotrainers.DataPacketProvider;
import org.cowboycoders.turbotrainers.bushido.BushidoUtils;
import org.cowboycoders.turbotrainers.bushido.brake.CalibrationState;
import org.cowboycoders.utils.LoopingListIterator;
import org.cowboycoders.utils.TrapezoidIntegrator;

/**
 * Model for current settings
 * 
 * @author will
 */
public class BushidoBrakeModel {
  
  boolean calibrationStateSent = true; 
  
  private double wheelSpeed;
  private double cadence;
  private double power;
  private byte balance;
  private int counter = 0;
  private double rightPower;
  private double leftPower;
  
  CalibrationState calibrationState = CalibrationState.CALIBRATED;
  /**
   * @return the calibrationState
   */
  public CalibrationState getCalibrationState() {
    return calibrationState;
  }

  /**
   * @param calibrationState the calibrationState to set
   */
  public synchronized void setCalibrationState(CalibrationState calibrationState) {
    while (calibrationStateSent == false) {
      try {
        this.wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    calibrationStateSent = false;
    this.calibrationState = calibrationState;
  }



  private double calibrationValue = 20.0;
 
  
  /**
   * @return the calibrationValue
   */
  public double getCalibrationValue() {
    return calibrationValue;
  }

  /**
   * @param calibrationValue the calibrationValue to set
   */
  public void setCalibrationValue(double calibrationValue) {
    this.calibrationValue = calibrationValue;
  }



  private static int MAX_COUNTER_VALUE = 16777215; // 3 bytes
  
 
  /**
   * @return the balance
   */
  public byte getBalance() {
    return balance;
  }

  /**
   * @param balance the balance to set
   */
  public void setBalance(byte balance) {
    this.balance = balance;
  }

  /**
   * @param wheelSpeed the wheelSpeed to set
   */
  public void setWheelSpeed(double wheelSpeed) {
    this.wheelSpeed = wheelSpeed;
  }

  /**
   * @param cadence the cadence to set
   */
  public void setCadence(double cadence) {
    this.cadence = cadence;
  }

  /**
   * @param power the power to set
   */
  public void setPower(double power) {
    this.power = power;
  }
  

  /**
   * @return the speed
   */
  public double getWheelSpeed() {
    return wheelSpeed;
  }

  /**
   * @return the cadence
   */
  public double getCadence() {
    return cadence;
  }

  /**
   * @return the power
   */
  public double getPower() {
    return power;
  }


  private List<DataPacketProvider> packetProviders = new ArrayList<DataPacketProvider>();
  private Iterator<DataPacketProvider> packetProvidersIterator = 
      new LoopingListIterator<DataPacketProvider>(packetProviders);
  
  
  /**
   * Provides a data packet containing software version
   */
  DataPacketProvider softwareVersionProvider = new DataPacketProvider() {

    @Override
    public byte[] getDataPacket() {
      byte [] softwareVersionPacket = new byte[8];
      softwareVersionPacket[0] = (byte) 0xad;
      softwareVersionPacket[1] = 0x02;
      
      // 3 byte software version id : displayed as if byte was converted to a string
      softwareVersionPacket[3] = 0x01;
      softwareVersionPacket[4] = 0x02;
      softwareVersionPacket[5] = 0x03;
      
      return softwareVersionPacket;
    }
    
  };
  
  
  /**
   * Unknown function : possibly additional version packet
   */
  DataPacketProvider ad01Provider = new DataPacketProvider() {

    @Override
    public byte[] getDataPacket() {
      byte [] ad01 = new byte[8];
      ad01[0] = (byte) 0xad;
      ad01[1] = 0x01;
      ad01[3] = 0x0a;
      ad01[6] = 0x06;
      ad01[7] = (byte) 0x9c;
      return ad01;
    }
    
  };
  
  DataPacketProvider powerProvider = new DataPacketProvider() {

    @Override
    public byte[] getDataPacket() {
      byte [] powerPacket = new byte[8];
      powerPacket[0] = (byte) 0x01;  
      insertPowerBytes(powerPacket);
      return powerPacket;
    }

    
  };
  
  /**
   * Another unknown
   */
  DataPacketProvider zeroFourProivder = new DataPacketProvider() {

    @Override
    public byte[] getDataPacket() {
      byte [] zeroFour = new byte[8];
      zeroFour[0] = (byte) 0x04;
      return zeroFour;
    }

    
  };
  
  
  DataPacketProvider speedCadenceBalanceProivder = new DataPacketProvider() {

    @Override
    public byte[] getDataPacket() {
      byte [] zeroTwo = new byte[8];
      zeroTwo[0] = (byte) 0x02;
      insertSpeedBytes(zeroTwo);
      insertCadenceBytes(zeroTwo);
      insertBalanceBytes(zeroTwo);
      return zeroTwo;
    }
    
  };
  
  DataPacketProvider counterProvider = new DataPacketProvider() {

    @Override
    public byte[] getDataPacket() {
      byte [] counterPacket = new byte[8];
      counterPacket[0] = (byte) 0x08;
      insertCounterBytes(counterPacket);
      return counterPacket;
    }
    
  };
  
  
  DataPacketProvider monitoringProvider = new DataPacketProvider() {

    @Override
    public byte[] getDataPacket() {
      byte [] packet = new byte[8];
      packet[0] = (byte) 0x10;
      return packet;
    }
    
  };
  
  DataPacketProvider calibrationProvider = new DataPacketProvider() {

    @Override
    public byte[] getDataPacket() {
      byte [] packet = new byte[8];
      packet[0] = (byte) 0x22;
      switch (calibrationState) {
        case CALIBRATION_REQUESTED:
          packet[1] = 0x06;
          break;
        case CALIBRATION_MODE:
          packet[1] = 0x06;
          packet[3] = 0x02;
          break;
        case UP_TO_SPEED:
          packet[1] = 0x06;
          packet[3] = 0x05;
          break;
        case NO_ERROR:
          packet[1] = 0x03;
          packet[3] = 0x0c;
          break;
        case CALIBRATED:
          packet[1] = 0x03;
          packet[3] = 0x42;
          BigInteger bigCalibration = BigIntUtils.convertUnsignedInt((int)(calibrationValue * 10));
          byte [] bytes = BigIntUtils.clipToByteArray(bigCalibration, 2);
          packet[4] = bytes [0];
          packet[5] = bytes [1];
          break;
        case CALIBRATION_VALUE_READY:
          packet[1] = 0x23;
          packet[3] = 0x4d;
          break;
        
      }
      
      synchronized(BushidoBrakeModel.this){
        calibrationStateSent = true;
        BushidoBrakeModel.this.notifyAll();
      }
      
      return packet;
    }
    
  };


  private void insertPowerBytes(byte[] powerPacket) {
    BigInteger bigPower = BigIntUtils.convertUnsignedInt((int)leftPower);;
    byte [] powerBytes = BigIntUtils.clipToByteArray(bigPower, 2);
    
    // left power
    powerPacket[1] = powerBytes[0];
    powerPacket[2] = powerBytes[1];
    
    bigPower = BigIntUtils.convertUnsignedInt((int)power);
    powerBytes = BigIntUtils.clipToByteArray(bigPower, 2);
    
    // average power
    powerPacket[3] = powerBytes[0];
    powerPacket[4] = powerBytes[1];
    
    bigPower = BigIntUtils.convertUnsignedInt((int)rightPower);
    powerBytes = BigIntUtils.clipToByteArray(bigPower, 2);
    
    // right power
    powerPacket[5] = powerBytes[0];
    powerPacket[6] = powerBytes[1];
    
  }
  
  
  protected void insertCounterBytes(byte[] packet) {
    BigInteger bigCounter = BigIntUtils.convertUnsignedInt((int)counter);;
    byte [] counterBytes = BigIntUtils.clipToByteArray(bigCounter, 3);
    
    packet[2] = counterBytes[0];
    packet[3] = counterBytes[1];
    packet[4] = counterBytes[2];
    
    counter++;
    
    if(counter > MAX_COUNTER_VALUE) {
      counter = 0;
    }
    
  }

  protected void insertBalanceBytes(byte[] zeroTwo) {
    zeroTwo[4] = (byte) balance;
    
  }

  protected void insertCadenceBytes(byte[] zeroTwo) {
    zeroTwo[3] = (byte) cadence;
    
  }

  private void insertSpeedBytes(byte[] zeroTwo) {
    BigInteger bigSpeed = BigIntUtils.convertUnsignedInt((int)wheelSpeed * 10);;
    byte [] speedBytes = BigIntUtils.clipToByteArray(bigSpeed, 2);
    zeroTwo[1] = speedBytes[0];
    zeroTwo[2] = speedBytes[1];
  }

  
  {
    //TODO : SWITCH ON BUSHIDO MODE
   packetProviders.add(this.ad01Provider);
   packetProviders.add(this.powerProvider);
   packetProviders.add(this.speedCadenceBalanceProivder);
   packetProviders.add(this.zeroFourProivder);
   packetProviders.add(this.counterProvider);
   packetProviders.add(this.monitoringProvider);
   packetProviders.add(this.calibrationProvider);
  }
  

  
  /**
   * Send data packets in order specified by packetProvders
   * @return
   */
  public byte[] getDataPacket() {
    return packetProvidersIterator.next().getDataPacket();
  }
  
  
  
  public double getRightPower() {
	return rightPower;
}

public void setRightPower(double rightPower) {
	this.rightPower = rightPower;
}

public double getLeftPower() {
	return leftPower;
}

public void setLeftPower(double leftPower) {
	this.leftPower = leftPower;
}

public static void main(String [] args) {
    BushidoBrakeModel model = new BushidoBrakeModel();
    model.setPower(1000);
    model.setWheelSpeed(14);
    model.setCalibrationState(CalibrationState.CALIBRATED);
    //
    for (byte b : model.powerProvider.getDataPacket()) {
      System.out.printf("%02x ", b);
    }
    System.out.println();
    
    
    BigInteger bigPower = BigIntUtils.convertInt((int)-432);;
    byte [] powerBytes = BigIntUtils.clipToByteArray(bigPower, 2);
    
    for (byte b : powerBytes) {
      System.out.printf("%2x ", b);
    }
    System.out.println();
    
    for (int i = 0 ; i <7 ; i++) {
      for (byte b : model.getDataPacket()) {
        System.out.printf("%2x ", b);
      }
      System.out.println();
      
    }
    
    model.setCalibrationState(CalibrationState.CALIBRATION_MODE);
    
    
    
  }

}