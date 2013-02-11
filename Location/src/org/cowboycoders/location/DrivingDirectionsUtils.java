package org.cowboycoders.location;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DrivingDirectionsUtils {
	
	
	
	public static String getResponse(CharSequence charSequence) throws RemoteException {
		
		HttpURLConnection urlConnection= null;
		URL url = null;
		StringBuilder buffer = new StringBuilder();
		try
		{
		url = new URL(charSequence.toString());	
		urlConnection=(HttpURLConnection)url.openConnection();
		urlConnection.setRequestMethod("GET");
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.connect();
		InputStream dataStream = urlConnection.getInputStream();
        InputStreamReader isr = new InputStreamReader(dataStream, "UTF8");
        Reader in = new BufferedReader(isr);
        int ch;
        while ((ch = in.read()) > -1) {
            buffer.append((char)ch);
        }
        return buffer.toString();
		} catch (IOException e) {
			throw new RemoteException("Query failed ",e);
		} 
		finally{
			if (urlConnection != null) urlConnection.disconnect();
		}



	}
	
	public static class DirectionsQueryBuilder {
		
		
		private static void validateString(String name, String arg, String[] acceptedArgs) 
				throws IllegalArgumentException {
			List<String> acceptedFormats = Arrays.asList(acceptedArgs);
			if (!acceptedFormats.contains(arg)){
				StringBuilder formats = new StringBuilder();
				for (String fmt : acceptedFormats) {
					formats.append("\"" + fmt +  "\""  + ", ");
				}
				formats.setLength(formats.length() -2);
				throw new IllegalArgumentException(name + " must one of: " + formats);
			}
		}

		private String format;
		private boolean sensor = false;
		private String originString;
		private String destinationString;
		private String mode;
		private List<String> waypoints = new ArrayList<String>();
		private boolean optimiseWaypoints = false;
		private boolean avoidTolls = false;
		private boolean avoidHighways = false;
		private String units;
		private String regionBias;
		private String language;
		
		
		/**
		 * @param format either "json" or "xml"
		 */
		DirectionsQueryBuilder(String format) {
			String[] acceptedFormats = new String[]{"json", "xml"};
			validateString("format",format,acceptedFormats);
			this.format = format;
		}
		
		public String latLongToString(LatLong l) {
			StringBuilder result = new StringBuilder()
				.append(l.getLatitude())
				.append(",")
				.append(l.getLongitude());
			return result.toString();
		}
		
		public DirectionsQueryBuilder to(String destinationString){
			this.destinationString = destinationString;
			return this;
		}
		
		public DirectionsQueryBuilder from(String originString) {
			this.originString = originString;
			return this;
		}
		
		public DirectionsQueryBuilder to(LatLong latlong){
			this.destinationString = latLongToString(latlong);
			return this;
		}
		
		public DirectionsQueryBuilder from(LatLong latlong) {
			this.originString = latLongToString(latlong);;
			return this;
		}
		/**
		 * mode = "driving"
		 * @return 
		 */
		public DirectionsQueryBuilder byCar() {
			mode = "driving";
			return this;
		}
		
		/**
		 * mode = "bicycle"
		 * @return 
		 */
		public DirectionsQueryBuilder byBicycle(){
			mode = "bicycle";
			return this;
		}
		
		/**
		 * mode = "walking"
		 * @return 
		 */
		public DirectionsQueryBuilder byFoot(){
			mode = "walking";
			return this;
		}
		
		public DirectionsQueryBuilder withLocationSensor() {
			sensor = true;
			return this;
		}
		
		public DirectionsQueryBuilder avoidingTolls() {
			avoidTolls = true;
			return this;
		}
		
		public DirectionsQueryBuilder avoidingHighways() {
			avoidHighways = true;
			return this;
		}
		
		/**
		 * mode = "transit"
		 * @return 
		 */
		public DirectionsQueryBuilder byPublicTransport(){
			mode = "transit";
			return this;
		}
		
		public DirectionsQueryBuilder stoppingAt(String waypoint) {
			if(waypoints.size() > 0) {
				waypoint = "|" + waypoint;
			}
			waypoints.add(waypoint);
			return this;
		}
		
		public DirectionsQueryBuilder stoppingAt(LatLong waypoint) {
			return stoppingAt(latLongToString(waypoint));
		}
		
		public DirectionsQueryBuilder via(LatLong waypoint) {
			return via(latLongToString(waypoint));
		}
		
		public DirectionsQueryBuilder via(String waypoint) {
			return stoppingAt("via:" + waypoint);
		}
		
		/**
		 * reorders ways travelling salesman
		 * @return
		 */
		public DirectionsQueryBuilder withWaypointOptimisation() {
			optimiseWaypoints = true;
			return this;
		}
		
		/**
		 * affects text directions
		 * @return
		 */
		public DirectionsQueryBuilder usingMetricUnits() {
			units = "metric";
			return this;
		}
		
		/**
		 * affects text directions
		 * @return
		 */
		public DirectionsQueryBuilder usingImperialUnits() {
			units = "imperial";
			return this;
		}
		
		
		/**
		 * language of directions @see https://developers.google.com/maps/faq#languagesupport
		 * 
		 * @param language language code
		 * @return
		 */
		public DirectionsQueryBuilder inLanguage(String language) {
			this.language = language;
			return this;
		}
		
		/**
		 * @param regionBias ccTLD (country code top-level domain) eg. "es" for spain
		 * @return the builder
		 */
		public DirectionsQueryBuilder withRegionBias(String regionBias) {
			this.regionBias = regionBias;
			return this;
		}

		private void notNullAppend(String argName, CharSequence arg, StringBuilder query) {
			if (arg == null) return;
			query.append("&").append(argName+"=").append(arg);
		}
		
		private String concatList(List<String> list) {
			StringBuilder result = new StringBuilder();
			for (String item : list) {
				result.append(item);
			}
			if (result.length() == 0) return null;
			return result.toString();
		}
		
		private String prepareWaypoints() {
			String points = concatList(waypoints);
			String result = null;
			if (optimiseWaypoints) {
				result = "optimize:true|" + points;
			}
			if (result == null) return points;
			return result;
		}
		
		private String prepareAvoid() {
			if (avoidTolls && avoidHighways) {
				return "tolls|highways";
			} else if (avoidTolls) {
				return "tolls";
			} else if (avoidHighways) {
				return "highways";
			}
			return null;
		}
		
		

		public CharSequence build() {
			StringBuilder query = new StringBuilder("http://maps.googleapis.com/maps/api/directions/");
			query.append(format);
			if (destinationString == null || originString==null) {
				throw new IllegalArgumentException("The origin and destination are not optional");
			}
			query.append("?").append("origin=").append(originString);
			notNullAppend("waypoints",prepareWaypoints(),query);
			query.append("&").append("destination=").append(destinationString);
			query.append("&").append("sensor=").append(sensor);
			notNullAppend("mode",mode,query);
			notNullAppend("avoid",prepareAvoid(),query);
			notNullAppend("units",units,query);
			notNullAppend("region",regionBias,query);
			notNullAppend("language",language,query);
			return query;
		}
		
		
	}
	
	public static void main(String [] args) throws RemoteException {
		DirectionsQueryBuilder b = new DirectionsQueryBuilder("json").from("London").via("Manchester").stoppingAt("Chester").to("Luton").avoidingTolls().withWaypointOptimisation();
		System.out.println(b.build());
		System.out.println(getResponse(b.build()));
	}
	

}
