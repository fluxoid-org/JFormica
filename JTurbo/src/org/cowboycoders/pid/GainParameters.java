package org.cowboycoders.pid;


public class GainParameters {
	
	private final double proportionalGain;
	private final double integralGain; 
	private final double derivativeGain;
	
	public double getProportionalGain() {
		return proportionalGain;
	}
	public double getIntegralGain() {
		return integralGain;
	}
	public double getDerivativeGain() {
		return derivativeGain;
	}
	public GainParameters(double proportionalGain, double integralGain,
			double derivativeGain) {
		super();
		this.proportionalGain = proportionalGain;
		this.integralGain = integralGain;
		this.derivativeGain = derivativeGain;
	}
	
	

}
