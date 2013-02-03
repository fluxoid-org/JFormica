package org.cowboycoders.turbotrainers.bushido.headunit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.cowboycoders.turbotrainers.DataPacketProvider;
import org.cowboycoders.turbotrainers.bushido.BushidoUtils;
import org.cowboycoders.utils.LoopingListIterator;
import org.cowboycoders.utils.TrapezoidIntegral;

/**
 * Model for current settings
 * 
 * @author will
 */
public class BushidoData {

  double speed;
  double cadence;
  double distance;
  double heartRate;
  double slope;
  double power;
  
  // in m
  TrapezoidIntegral integralSpeed = new TrapezoidIntegral();
 
  
  private List<DataPacketProvider> packetProviders = new ArrayList<DataPacketProvider>();
  private Iterator<DataPacketProvider> packetProvidersIterator = 
      new LoopingListIterator<DataPacketProvider>(packetProviders);
  
  
  /**
   * Provides a data packet containing current slope
   */
  DataPacketProvider slopeProvider = new DataPacketProvider() {

    @Override
    public byte[] getDataPacket() {
      byte [] dc01Packet = BushidoUtils.getDc01Prototype();
      dc01Packet = injectSlope(dc01Packet);
      return dc01Packet;
    }
    
  };
  
  /**
   * Provides a data packet containing current slope
   */
  DataPacketProvider keepAliveProvider = new DataPacketProvider() {

    @Override
    public byte[] getDataPacket() {
      return keepAlive();
    }
    
  };
  

  
  {
    //TODO : SWITCH ON BUSHIDO MODE
   packetProviders.add(slopeProvider);
   packetProviders.add(keepAliveProvider);
  }
  
  /**
   * @return the heartRate
   */
  public double getHeartRate() {
    return heartRate;
  }

  /**
   * @param heartRate the heartRate to set
   */
  public void setHeartRate(double heartRate) {
    this.heartRate = heartRate;
  }

  /**
   * @return the speed
   */
  public double getSpeed() {
    return speed;
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

  public void setSpeed(double speed) {
    this.speed = speed;
    double timeStampSeconds = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime());
    double speedMetresPerSecond = 1000 * speed / (60 * 60);
    integralSpeed.add(timeStampSeconds, speedMetresPerSecond);
  }

  public void setPower(double power) {
    this.power = power;
  }

  public void setCadence(double cadence) {
    this.cadence = cadence;
  }
  
  /**
   * true distance (travelled by wheel) as opposed to gradient compensated speed
   * @param distance
   */
  public void setDistance(double distance) {
    this.distance = distance;
  }

  public void setHearRate(double heartRate) {
    this.heartRate = heartRate;
  }

  public double getSlope() {
    return slope;
  }
  
  public double getCompensatedDistance() {
    return integralSpeed.getIntegral();
  }

  public double getDistance() {
    return distance;
  }

  public void setSlope(double slope) {
    slope = getBoundedSlope(slope);
    this.slope = slope;
  }

  private byte[] injectSlope(byte[] dc01Packet) {
    if (slope < 0) {
      dc01Packet[3] = (byte) 0xFF;
      dc01Packet[4] = (byte) (256 + slope * 10);
    } else {
      dc01Packet[4] = (byte) (slope * 10);
    }

    return dc01Packet;
  }

  public void incrementSlope(double value) {
    double currentSlope = getSlope();
    setSlope(currentSlope + value);
  }
  
  public void incrementSlope() {
    incrementSlope(0.1);
  }

  public void decrementSlope(double value) {
    double currentSlope = getSlope();
    setSlope(currentSlope - value);
  }
  
  public void decrementSlope() {
    decrementSlope(0.1);
  }
  

  private double getBoundedSlope(double slope) {
    if (slope > 20.0) { return 20.0; }
    if (slope < -5.0) { return -5.0; }
    return slope;
  }

  private byte[] keepAlive() {
    byte[] dc02Packet = BushidoUtils.getDc02Prototype();
    return dc02Packet;
  }
  
  /**
   * Send data packets in order specified by packetProvders
   * @return
   */
  public byte[] getDataPacket() {
    return packetProvidersIterator.next().getDataPacket();
  }

}