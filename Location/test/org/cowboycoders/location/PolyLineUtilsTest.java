package org.cowboycoders.location;

import static org.junit.Assert.*;
import static org.cowboycoders.location.PolyLineUtils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import org.junit.BeforeClass;
import org.junit.Test;

public class PolyLineUtilsTest {
	
	@BeforeClass
	public static void beforeClass() {
		logger.setLevel(Level.FINEST);
		logger.addHandler(new ConsoleHandler());
		try {
			logger.addHandler(new FileHandler("log.txt"));
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static List<LatLong> test(CharSequence c) throws DecodeException {
		List<LatLong> ls = decode(c);
		System.out.println("number of lat longs: " + ls.size());
		for (int i = 0 ; i< ls.size() ; i++) {
			LatLong l = ls.get(i);
			System.out.println(l.getLatitude() + " " + l.getLongitude());
			System.out.println();
		}
		return ls;
	}
	
	private static CharSequence testEncode(double lat, double lng) {
		List<LatLong> latLongs = new ArrayList<LatLong>();
		LatLong l = new LatLong(lat ,lng);
		latLongs.add(l);
		System.out.println(encode(latLongs));
		return encode(latLongs);
	}

	@Test
	public void testEnc() throws DecodeException {
		assertEquals(testEncode(38.5 ,	-120.2).toString(),"_p~iF~ps|U");
	}
	
	@Test
	public void testDec() throws DecodeException {
		assertEquals(0.00008,decode("////").get(0).getLatitude(),0.0000001);
		testEncode(0.00008,0.00008);
	}

}
