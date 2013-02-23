package org.cowboycoders.utils;

public class SlopeTimeAverager implements Averager {
	
	public static int NUMBER_OF_VALUES_TO_AVERAGE = 10;
	Long timeOffset;
	Double lastTimeStamp;
	double lastValue;
	Double thresholdMax;
	Double thresholdMin;
	
	private RunningAverager runningAverager = new RunningAverager(NUMBER_OF_VALUES_TO_AVERAGE);
	

	@Override
	public void add(double y) {
		if(timeOffset == null) {
			timeOffset = System.nanoTime();
		}
		
		double timestamp = getTimeStamp();
		
		// handle initial case where no previous timestamp exists
		if (lastTimeStamp == null) {
			lastTimeStamp = timestamp;
			lastValue = y;
			return;
		}
		
		double rise = y - lastValue;
		double run = timestamp - lastTimeStamp;
		
		runningAverager.add(rise/run);
		
	}
	
	/**
	 * Timestamp in seconds
	 * @return timestamp
	 */
	protected double getTimeStamp() {
		double timestamp = System.nanoTime() / Math.pow(10,9); //seconds
		return timestamp;
	}

	@Override
	public Double getLastValue()  {
		return runningAverager.getLastValue();
	}

	@Override
	public double getAverage() {
		return runningAverager.getAverage();
	}
	
	public void setThreshold(Double max, Double min) {
		thresholdMax = max;
		thresholdMin = min;
	}
	
	protected boolean isBelowMaxThreshold() {
		if (thresholdMax == null) return true;
		if (getAverage() <= thresholdMax) return true;
		return false;
	}
	
	protected boolean isAboveMinThreshold() {
		if (thresholdMin == null) return true;
		if (getAverage() >= thresholdMin) return true;
		return false;
	}
	
	public boolean isWithinThreshold() {
		return (isBelowMaxThreshold() && isAboveMinThreshold());
	}
	
	//TODO: auto reset if not updated within certain time frame
	// setAutoReset(Double timeInSeconds) - null disable
	// or sucession of same value? 
	
	public static void main(String [] args) {
		SlopeTimeAverager sa = new SlopeTimeAverager() {
			double timestamp = 0;
			protected double getTimeStamp() {
				timestamp += 1.; //seconds
				return timestamp;
			}
		};
		
		sa.add(0);
		sa.add(1);
		sa.add(2);
		sa.add(3);
		sa.add(10);
		sa.add(5);
		
		System.out.println(sa.getAverage());
	}

	@Override
	public int getNumberOfSamples() {
		return runningAverager.getNumberOfSamples();
	}
}
