package org.cowboycoders.location;

public class LatLong {
	
	private double latitude;
	
	private double longitude;
	
	public LatLong(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public static LatLong fromMicro(int lat, int lng) {
		double latitude = (lat / Math.pow(10, 6));
		double longitude = (lng / Math.pow(10, 6));
		return new LatLong(latitude,longitude);
	}
	
	public int toMicro(double number) {
		return (int) Math.round(number * Math.pow(10, 6));
	}
	
	
	

}
