/**
 * inspired from http://soulsolutions.com.au/Articles/Encodingforperformance/tabid/96/Default.aspx
 */

package org.cowboycoders.location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.javatuples.Pair;

public class PolyLineUtils {
	
	public static Logger logger = Logger.getLogger(PolyLineUtils.class.toString());
	
	/**
	 * Utility class
	 */
	private PolyLineUtils() {
		
	}
	
	public static int CHUNK_SIZE = 5;
	public static int ASCII_OFFSET = 63;
	
	/**
	 * @param polyLine
	 * @return
	 */
	public static List<LatLong> decode(CharSequence polyLine) throws DecodeException {
		try{
			List<LatLong> latLongs = new ArrayList<LatLong>();
			TrackedCharSequence polyLineChars = new TrackedCharSequence(polyLine);
			double latitude = 0;
			double longitude = 0;
			while (polyLineChars.hasNext()) {
				//getValue iterates through the TrackedCharSequence
				latitude += getValue(polyLineChars);
				longitude += getValue(polyLineChars);
				latLongs.add(new LatLong(latitude,longitude));
			}
			
			return latLongs;
		} catch (RuntimeException e) {
			throw new DecodeException(e);
		}
	}
	
	/**
	 * Encodes  list of latlongs into a polyline
	 * @param latLongs
	 * @return
	 */
	public static CharSequence encode(List<LatLong> latLongs) {
		StringBuilder polyLine = new StringBuilder();
		int lastLat = 0;
		int lastLong = 0;
		for (LatLong l : latLongs) {
			
			int currentLat = (int) Math.round(l.getLatitude() * Math.pow(10, 5));
			int currentLong = (int) Math.round(l.getLongitude() * Math.pow(10, 5));
			
			polyLine.append(encodeNumber(currentLat - lastLat));
			polyLine.append(encodeNumber(currentLong - lastLong));
			
			lastLat = currentLat;
			lastLong = currentLong;
		}
		return polyLine;
	}
	
	
	public static CharSequence encodeNumber(int number) {
		
		int shifted = number << 1;
		
		if (number < 0) {
			shifted = ~shifted;
		}
		
	   StringBuilder encoded = new StringBuilder();
	    while (shifted >= 0x20)
	    {
	        //while another chunk follows
	        encoded.append((char)((0x20 | (shifted & 0x1f)) + ASCII_OFFSET)); 
	        //OR value with 0x20, convert to decimal and add 63
	        shifted >>= CHUNK_SIZE; //shift to next chunk
	    }
	    encoded.append((char)(shifted + ASCII_OFFSET));
	    return encoded;
	}

	/**
	 * Escape for string literal
	 */
	public static CharSequence escapePolyLine(CharSequence c) {
		String str = new StringBuilder(c).toString();
		return str.replace("/","//");
	}
	
	/**
	 * unescape string literals
	 */
	public static CharSequence unEscapePolyLine(CharSequence c) {
		String str = new StringBuilder(c).toString();
		return str.replace("//","/");
	}
	
	/**
	 * Returns the next value, iterating through the TrackedCharSequence until the required
	 * number of chars have been processed
	 * 
	 * @return the next lat/long value
	 */
	public static double getValue(TrackedCharSequence polyLineChars) {
		long result = 0;
		int shift = 0;
		int currentByte = 0;
		
		if (!polyLineChars.hasNext()) {
			return 0.;
		}
		
		do {
			currentByte = (int) polyLineChars.next() - ASCII_OFFSET ;
			//ignore extra chunk bit (use 5 bits from chunk)
			logger.finer("currentbyte: " + (currentByte & 0x1f));
			result |= (currentByte & 0x1f) << shift;
			shift += CHUNK_SIZE;
		} while (currentByte >= 0x20); //6th bit set when an extra chunk is needed 
		
		// the zero that was introduced from the initial left shift will have been inverted to one if original was negative
		result = ((result & 1) > 0) ? ~(result >> 1) : (result >> 1);
		
		return (double)result / Math.pow(10, 5);
		
	}
	
	

	
	
	
	

}
