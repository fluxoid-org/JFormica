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


	public Double getTotalElapsedTime() {
		return totalElapsedTime;
	}


	public void setTotalElapsedTime(Double totalElapsedTime) {
		this.totalElapsedTime = totalElapsedTime;
	}


	public Double getPreviousError() {
		return previousError;
	}


	public void setPreviousError(Double previousError) {
		this.previousError = previousError;
	}


	public Double getCurrentError() {
		return currentError;
	}


	public void setCurrentError(Double currentError) {
		this.currentError = currentError;
	}


	public Double getTimeDelta() {
		return timeDelta;
	}


	public void setTimeDelta(Double timeDelta) {
		this.timeDelta = timeDelta;
	}


	public Double getErrorIntegral() {
		return errorIntegral;
	}


	public void setErrorIntegral(Double errorIntegral) {
		this.errorIntegral = errorIntegral;
	}


	public Double getErrorDerivative() {
		return errorDerivative;
	}


	public void setErrorDerivative(Double errorDerivative) {
		this.errorDerivative = errorDerivative;
	}


	public Double getSetPoint() {
		return setPoint;
	}


	public void setSetPoint(Double setPoint) {
		this.setPoint = setPoint;
	}


	public Double getProcessVariable() {
		return processVariable;
	}


	public void setProcessVariable(Double processVariable) {
		this.processVariable = processVariable;
	}




	
}
