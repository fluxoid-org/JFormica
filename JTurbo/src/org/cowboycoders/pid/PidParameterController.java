package org.cowboycoders.pid;

public interface PidParameterController extends PidUpdateProvider {

	public abstract double getProportionalGain();

	public abstract void setProportionalGain(double proportionalGain);

	public abstract double getIntegralGain();

	public abstract void setIntegralGain(double integralGain);

	public abstract double getDerivativeGain();

	public abstract void setDerivativeGain(double derivativeGain);
	
	@Override
	public abstract void registerPidUpdateLister(PidUpdateListener listener);
	
	@Override
	public abstract void unregisterPidUpdateLister(PidUpdateListener listener);

}