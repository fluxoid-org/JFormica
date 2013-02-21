package org.cowboycoders.pid;

import java.util.HashSet;
import java.util.Set;

import org.cowboycoders.utils.TrapezoidIntegrator;

public class PidController implements PidParameterController {
	
	private double previousError = 0;
	private TrapezoidIntegrator integral = new TrapezoidIntegrator();
	
	// last values
	private double proportionalGain = 1;
	private double integralGain = 0;
	private double derivativeGain = 0;
	
	private Double lastTimeStamp; //seconds
	private Long startTimeOffset;
	private double elapsedTime;
	private ProcessVariableProvider processVariable;
	private OutputController output;
	private Set<PidUpdateListener> listeners = new HashSet<PidUpdateListener>();
	private GainController gainController;
	
	
	public PidController(ProcessVariableProvider pv, OutputController out, GainController gc) {
		this.processVariable = pv;
		this.output = out;
		this.gainController = gc;
	}

	/* (non-Javadoc)
	 * @see org.cowboycoders.pid.PidParameterController#getProportionalGain()
	 */
	@Override
	public double getProportionalGain() {
		return proportionalGain;
	}

	/* (non-Javadoc)
	 * @see org.cowboycoders.pid.PidParameterController#getIntegralGain()
	 */
	@Override
	public double getIntegralGain() {
		return integralGain;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.pid.PidParameterController#getDerivativeGain()
	 */
	@Override
	public double getDerivativeGain() {
		return derivativeGain;
	}


	protected double getPreviousError() {
		return previousError;
	}

	protected void setPreviousError(double previousError) {
		this.previousError = previousError;
	}

	protected TrapezoidIntegrator getErrorIntegrator() {
		return integral;
	}

	protected void setErrorIntegrator(TrapezoidIntegrator integral) {
		this.integral = integral;
	}

	protected ProcessVariableProvider getProcessVariableProvider() {
		return processVariable;
	}

	protected void setProcessVariableProvider(ProcessVariableProvider processVariable) {
		this.processVariable = processVariable;
	}

	protected OutputController getOutputController() {
		return output;
	}

	protected void setOutputController(OutputController output) {
		this.output = output;
	}

	protected double getLastTimeStamp() {
		return lastTimeStamp;
	}

	protected void setLastTimeStamp(double lastTimeStamp) {
		this.lastTimeStamp = lastTimeStamp;
	}
	
	protected double getElapsedTime() {
		return elapsedTime;
	}

	protected void setElapsedTime(double elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	
	protected synchronized GainController getGainController() {
		return gainController;
	}
	
	@Override
	public synchronized boolean setGainController(GainController gainController) {
		this.gainController = gainController;
		return true;
	}
	

	protected void setProportionalGain(double proportionalGain) {
		this.proportionalGain = proportionalGain;
	}

	protected void setIntegralGain(double integralGain) {
		this.integralGain = integralGain;
	}

	protected void setDerivativeGain(double derivativeGain) {
		this.derivativeGain = derivativeGain;
	}

	/**
	 * Calculate the control signal using a positional PID controller
	 * 
	 * @param setpoint - target output, in this case power in Watts.
	 */
	public synchronized void adjustSetpoint(double setpoint) {
		
		// Get the current power value (process variable)
		double pv = getProcessVariableProvider().getProcessVariable();
		
		// Calculate the difference between the current power value and
		// the target power value (set point). This is the error term.
		double error = setpoint - pv;
	
		// Construct a timer (seconds)
		if (startTimeOffset == null) {
			startTimeOffset = System.nanoTime();
		}
		double timeStamp = (System.nanoTime() - startTimeOffset) / Math.pow(10, 9);
		
		// Handle the initial case when no previous error exists
		if (lastTimeStamp == null) {
			lastTimeStamp = timeStamp;
			setPreviousError(error);
			getErrorIntegrator().add(timeStamp,error);
			return;
		}
		
		OutputController outputController = getOutputController();
		GainController gainController = getGainController();
		
		// Calculate the time elapsed since the last update
		double dt = timeStamp - lastTimeStamp;
		
		// Calculate the derivative term from the previous error using 
		// the finite difference method
		double previousError = getPreviousError();
		double derivative = (error - previousError) / dt;
		
		// Calculate the integral term
		getErrorIntegrator().add(timeStamp, error);
		double integral = getErrorIntegrator().getIntegral();
		
		// Step the timer forward
		double elapsedTime = getElapsedTime() + dt;
		setElapsedTime(elapsedTime);
		
		OutputControlParameters outputParameters = new OutputControlParameters(
				elapsedTime, previousError, error, dt, integral, derivative, setpoint, pv
				);
		
		GainParameters gain = gainController.getGain(outputParameters);
		
		// Get the PID coefficients
		double Kp = gain.getProportionalGain();
		double Ki = gain.getIntegralGain();
		double Kd = gain.getDerivativeGain();
		double output = Kp*error + Ki*integral + Kd* derivative;
		
		// Update a reference to the previous values
		setProportionalGain(Kp);
		setDerivativeGain(Kd);
		setIntegralGain(Ki);
		
		setPreviousError(error);
		
		// Make sure that the control signal is within set bounds
		if (output > outputController.getMaxOutput()) output = outputController.getMaxOutput();
		else if (output < outputController.getMinOutput()) output = outputController.getMinOutput();
		
		synchronized(listeners) {
			for (PidUpdateListener listener: listeners) {
				listener.onPidUpdate(setpoint, pv, output, error);
			}
		}
		
		outputController.setOutput(output);
	}
	
	/**
	 * {@link PidController#adjustSetpoint(double)} is supposed to polled regularly
	 * Should you wish to take a break, use this method before restarting.
	 */
	public synchronized void reset() {
		startTimeOffset = null;
		lastTimeStamp = null;
		elapsedTime = 0;
		setErrorIntegrator(new TrapezoidIntegrator());
		setPreviousError(0);
	}

	/* (non-Javadoc)
	 * @see org.cowboycoders.pid.PidParameterController#registerPidUpdateLister(org.cowboycoders.pid.PidUpdateListener)
	 */
	@Override
	public void registerPidUpdateLister(PidUpdateListener listener) {
		synchronized(listeners) {
			listeners.add(listener);
		}
	}

	/* (non-Javadoc)
	 * @see org.cowboycoders.pid.PidParameterController#unregisterPidUpdateLister(org.cowboycoders.pid.PidUpdateListener)
	 */
	@Override
	public void unregisterPidUpdateLister(PidUpdateListener listener) {
		synchronized(listeners) {
			listeners.remove(listener);
		}
	}
	

}
