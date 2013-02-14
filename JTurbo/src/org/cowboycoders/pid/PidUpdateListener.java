package org.cowboycoders.pid;

public interface PidUpdateListener {
	
	void onPidUpdate(double setpoint, double processValue, double output, double error);

}
