package org.cowboycoders.location;

public class LatLongAlt extends LatLong {
  
  private double altitude;

  /**
   * @return the altitude
   */
  public double getAltitude() {
    return altitude;
  }

  public LatLongAlt(double latitude, double longitude, double altitude) {
    super(latitude, longitude);
    this.altitude = altitude;
    
  }

}
