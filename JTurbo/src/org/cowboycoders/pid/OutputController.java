package org.cowboycoders.pid;

public interface OutputController {
	
	/**
	 * The new output bounded by {@link OutputController#getMaxOutput()},
	 * {@link OutputController#getMinOutput()},
	 * @param value
	 */
	public void setOutput(double value);
	
	/**
	 * The maximum the output should be
	 * @return the max
	 */
	public double getMaxOutput();
	
	/**
	 * The minimum the output should be
	 * @return the min
	 */
	public double getMinOutput();
	
	
}
