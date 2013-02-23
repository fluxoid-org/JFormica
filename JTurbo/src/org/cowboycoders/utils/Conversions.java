package org.cowboycoders.utils;

public class Conversions {
	
	private Conversions () {}
	
	// make sure we aren't truncating to int
	public static final double METRES_PER_SECOND_TO_KM_PER_HOUR = 60 * 60 / 1000.;
	
	public static final double KM_PER_HOUR_TO_METRES_PER_SECOND = (1 / METRES_PER_SECOND_TO_KM_PER_HOUR);
	
	public static void main(String [] args) {
		System.out.println(METRES_PER_SECOND_TO_KM_PER_HOUR);
		System.out.println(KM_PER_HOUR_TO_METRES_PER_SECOND);
	}

}