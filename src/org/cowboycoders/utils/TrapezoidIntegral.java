package org.cowboycoders.utils;

public class TrapezoidIntegral {
  
  Double lastY;
  Double lastX;
  
  double integral = 0;
  
  public double add(double x, double y) {
    if (lastY == null) {
      lastY = y;
      lastX = x;
      return 0;
    }
    integral += 0.5 * (x - lastX) * (y + lastY);
    lastY = y;
    lastX = x;
    return integral;
  }
  

  /**
   * @return the integral
   */
  public double getIntegral() {
    return integral;
  }



  /**
   * @param args
   */
  public static void main(String[] args) {
    TrapezoidIntegral I = new TrapezoidIntegral();
    for (int i = 0 ; i <= 100 ; i++) {
      I.add(i, i);
    }
    System.out.println(I.getIntegral());

  }

}
