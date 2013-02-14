package org.cowboycoders.pid;

import java.util.HashSet;
import java.util.Set;

import org.cowboycoders.utils.TrapezoidIntegrator;

public class PidController implements PidParameterController {
	
	private double previousError = 0;
	private TrapezoidIntegrator integral = new TrapezoidIntegrator();
	private double proportionalGain = 1;
	private double integralGain = 0;
	private double derivativeGain = 0;
	private Double lastTimeStamp; //seconds
	private Long startTimeOffset;
	private ProcessVariableProvider processVariable;
	private OutputController output;
	private Set<PidUpdateListener> listeners = new HashSet<PidUpdateListener>();

	
	public PidController(ProcessVariableProvider pv, OutputController out) {
		this.processVariable = pv;
		this.output = out;
	}

	/* (non-Javadoc)
	 * @see org.cowboycoders.pid.PidParameterController#getProportionalGain()
	 */
	@Override
	public double getProportionalGain() {
		return proportionalGain;
	}

	/* (non-Javadoc)
	 * @see org.cowboycoders.pid.PidParameterController#setProportionalGain(double)
	 */
	@Override
	public void setProportionalGain(double proportionalGain) {
		this.proportionalGain = proportionalGain;
	}

	/* (non-Javadoc)
	 * @see org.cowboycoders.pid.PidParameterController#getIntegralGain()
	 */
	@Override
	public double getIntegralGain() {
		return integralGain;
	}

	/* (non-Javadoc)
	 * @see org.cowboycoders.pid.PidParameterController#setIntegralGain(double)
	 */
	@Override
	public void setIntegralGain(double integralGain) {
		this.integralGain = integralGain;
	}

	/* (non-Javadoc)
	 * @see org.cowboycoders.pid.PidParameterController#getDerivativeGain()
	 */
	@Override
	public double getDerivativeGain() {
		return derivativeGain;
	}

	/* (non-Javadoc)
	 * @see org.cowboycoders.pid.PidParameterController#setDerivativeGain(double)
	 */
	@Override
	public void setDerivativeGain(double derivativeGain) {
		this.derivativeGain = derivativeGain;
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
	
	public synchronized void adjustSetpoint(double setpoint) {
		double pv = getProcessVariableProvider().getProcessVariable();
		double error = pv - setpoint;
		if (startTimeOffset == null) {
			startTimeOffset = System.nanoTime();
		}
		double timeStamp = (System.nanoTime() - startTimeOffset) / Math.pow(10, 9);
		if (lastTimeStamp == null) {
			lastTimeStamp = timeStamp;
			setPreviousError(error);
			getErrorIntegrator().add(timeStamp,error);
			// we need a pair to compute dt
			return;
		}
		OutputController outputController = getOutputController();
		double Kp = this.getProportionalGain();
		double Ki = this.getIntegralGain();
		double Kd = this.getDerivativeGain();
		double dt = timeStamp - lastTimeStamp;
		double previousError = getPreviousError();
		double derivative = (error - previousError) / dt;
		getErrorIntegrator().add(timeStamp, error);
		double integral = getErrorIntegrator().getIntegral();
		double output = Kp*error + Ki*integral + Kd* derivative;
		setPreviousError(error);
		
		//make sure we are within bounds
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
