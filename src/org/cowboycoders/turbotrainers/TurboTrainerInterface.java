package org.cowboycoders.turbotrainers;

import java.util.concurrent.TimeoutException;

public interface TurboTrainerInterface {

  public abstract void registerDataListener(TurboTrainerDataListener listener);

  public abstract void unregisterDataListener(TurboTrainerDataListener listener);

  public abstract boolean supportsSpeed();

  public abstract boolean supportsPower();

  public abstract boolean supportsCadence();

  public abstract boolean supportsHeartRate();

  public abstract void setSlope(double gradient);
  
  public abstract double getSlope();

  public abstract void start() throws TurboCommunicationException, InterruptedException, TimeoutException;

  public abstract void stop() throws InterruptedException, TimeoutException;
  
  //void registerExceptionHandler(ExceptionHandler handler);
  
  //void unregisterExceptionHandler(ExceptionHandler handler);

}