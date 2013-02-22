package org.cowboycoders.utils;

import org.cowboycoders.ant.utils.FixedSizeFifo;

public class Averager {
	
	private FixedSizeFifo<Double> values;
	private double average = 0;
	private int samplesToAverage;
	private Double lastValue;
	
	public Averager(int samplesToAverage) {
		this.samplesToAverage = samplesToAverage;
		values = new FixedSizeFifo<Double>(samplesToAverage);
	}
	
	/**
	 * Add a value to the averager
	 * @param y the value to add
	 * @return averageSlope
	 */
	public double add(double y) {
		lastValue = y;
		int oldSize = values.size();
		
		Double displaced = values.displace(y);
		
		// handle case where Fifo is not full
		if (oldSize < samplesToAverage) {
			int size = values.size();
			average = (size -1.) / size * average + 1./size * y;
			return average;
		}
		
		if (displaced != null) {
			average -= (displaced / values.size());
		}
			average += (y / values.size());
			
		return average;
	}
	
	/**
	 * @return the last submitted value
	 * @throws NullPointerException if a value has not been submitted
	 */
	public double getLastValue() throws NullPointerException {
		// null pointer exception if not updated
		return lastValue;
	}
	
	public double getAverage() {
		return average;
	}
	
	public double getAverageSlow() {
		double total = 0;
		for (double value : values) {
			total += value;
		}
		return total / values.size();
	}
	
	public void addSlow(double y) {
		values.displace(y);
	}
	
	public static void main(String [] args) {
		long first = 0;
		long second = 0;
		int big = 99999;
		long start = System.nanoTime();
		Averager sa = new Averager(big);
		for (int i = 0 ; i < big ; i ++) {
			sa.addSlow(i);
			System.out.println(sa.getAverage());
		}
		System.out.println(sa.getAverageSlow());
		first = System.nanoTime() - start;
		System.out.println();
		
		start = System.nanoTime();
		sa = new Averager(big);
		for (int i = 0 ; i < big ; i ++) {
			System.out.println(sa.add(i));
		}
		System.out.println(sa.getAverage());
		second = System.nanoTime() - start;
		System.out.println();
		
		System.out.println(first);
		System.out.println(second);
	}

}
