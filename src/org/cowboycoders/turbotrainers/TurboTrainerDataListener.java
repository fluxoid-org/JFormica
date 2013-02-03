package org.cowboycoders.turbotrainers;

public interface TurboTrainerDataListener {

  public abstract void onSpeedChange(double speed);

  public abstract void onPowerChange(double power);

  public abstract void onCadenceChange(double cadence);

  public abstract void onDistanceChange(double distance);

  public abstract void onHeartRateChange(double heartRate);

}