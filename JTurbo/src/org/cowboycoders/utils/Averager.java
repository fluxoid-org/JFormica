package org.cowboycoders.utils;

public interface Averager {

	/**
	 * Add a value to the averager
	 * @param y the value to add
	 */
	public abstract void add(double y);

	/**
	 * @return the last submitted value, null if no values submitted
	 */
	public abstract Double getLastValue();

	public abstract double getAverage();
	
	public int getNumberOfSamples();

}