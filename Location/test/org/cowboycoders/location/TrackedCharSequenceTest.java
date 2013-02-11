package org.cowboycoders.location;

import static org.junit.Assert.*;

import org.junit.Test;

public class TrackedCharSequenceTest {

	@Test
	public void test() {
		// test iteration no exceptions
		TrackedCharSequence t = new TrackedCharSequence("Hello");
		assertEquals(5,t.getRemainingChars().length());
		for (char c : t) {
			System.out.println(c);
		}
		assertEquals(0,t.getRemainingChars().length());
		
	}

}
