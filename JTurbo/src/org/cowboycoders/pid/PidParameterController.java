package org.cowboycoders.pid;

public interface PidParameterController extends PidUpdateProvider {
	
	/**
	 * gets last value
	 * @return
	 */
	public abstract double getProportionalGain();
	
	/**
	 * gets last value
	 * @return
	 */
	public abstract double getIntegralGain();
	
	/**
	 * gets last value
	 * @return
	 */
	public abstract double getDerivativeGain();
	
	
	/**
	 * Swaps out the current gain controller
	 * @param gainController the new controller
	 * @return true, if swap successful, otherwise false.
	 */
	public boolean setGainController(GainController gainController);

	
	@Override
	public abstract void registerPidUpdateLister(PidUpdateListener listener);
	
	@Override
	public abstract void unregisterPidUpdateLister(PidUpdateListener listener);
	
	

}