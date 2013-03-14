package org.cowboycoders.cyclisimo.util;

public enum Units {
  METRIC,
  IMPERIAL;
  
  private static double KG_TO_POUNDS = 2.20462;
  
  public double convertWeight(Units from, double weight) {
    if (from == this) return weight;
    if (this == IMPERIAL) return KG_TO_POUNDS * weight;
    else return (1. / KG_TO_POUNDS) * weight;
  }
  
}