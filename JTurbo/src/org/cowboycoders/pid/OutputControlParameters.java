package org.cowboycoders.pid;

public class OutputControlParameters {
	private Double totalElapsedTime;
	private Double previousError;
	private Double currentError;
	private Double timeDelta;
	private Double errorIntegral;
	private Double errorDerivative;
	private Double setPoint;
	private Double processVariable;
	
	
	public OutputControlParameters(Double totalElapsedTime,
			Double previousError, Double currentError, Double timeDelta,
			Double errorIntegral, Double errorDerivative, Double setPoint,
			Double processVariable) {
		super();
		this.totalElapsedTime = totalElapsedTime;
		this.previousError = previousError;
		this.currentError = currentError;
		this.timeDelta = timeDelta;
		this.errorIntegral = errorIntegral;
		this.errorDerivative = errorDerivative;
		this.setPoint = setPoint;
		this.processVariable = processVariable;
	}


	protected Double getTotalElapsedTime() {
		return totalElapsedTime;
	}


	protected Double getPreviousError() {
		return previousError;
	}


	protected Double getCurrentError() {
		return currentError;
	}


	protected Double getTimeDelta() {
		return timeDelta;
	}


	protected Double getErrorIntegral() {
		return errorIntegral;
	}


	protected Double getErrorDerivative() {
		return errorDerivative;
	}


	protected Double getSetPoint() {
		return setPoint;
	}


	protected Double getProcessVariable() {
		return processVariable;
	}

	
}
