package org.cowboycoders.utils;

import org.cowboycoders.ant.utils.FixedSizeFifo;

public class RunningAverager implements Averager {
	
	private FixedSizeFifo<Double> values;
	private Double lastValue;
	
	public RunningAverager(int samplesToAverage) {
		values = new FixedSizeFifo<Double>(samplesToAverage);
	}
	
	/* (non-Javadoc)
	 * @see org.cowboycoders.utils.Averager#add(double)
	 */
	@Override
	public void add(double y) {
		lastValue = y;
		values.offer(y);
	}
	
	/* (non-Javadoc)
	 * @see org.cowboycoders.utils.Averager#getLastValue()
	 */
	@Override
	public Double getLastValue() throws NullPointerException {
		return lastValue;
	}
	
	/* (non-Javadoc)
	 * @see org.cowboycoders.utils.Averager#getAverage()
	 */
	@Override
	public double getAverage() {
		double total = 0;
		for (double value : values) {
			total += value;
		}
		return total / values.size();
	}
	

	public static void main(String [] args) {
		RunningAverager a = new RunningAverager(10);
		a.add(4);
		a.add(6);
		a.add(4);
		a.add(6);
		a.add(4);
		a.add(6);
		a.add(4);
		a.add(6);
		System.out.println(a.getAverage());
	}

	@Override
	public int getNumberOfSamples() {
		return values.size();
	}

}
