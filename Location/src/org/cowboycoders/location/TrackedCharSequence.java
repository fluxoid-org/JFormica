package org.cowboycoders.location;

import java.util.Iterator;

public class TrackedCharSequence implements Iterable<Character>, Iterator<Character> {
	
	private CharSequence chars;
	private int currentIndex = 0;
	
	
	public TrackedCharSequence(CharSequence sequence) {
		this.chars = sequence;
	}

	public CharSequence getInput() {
		return chars;
	}
	
	public CharSequence getRemainingChars() {
		return chars.subSequence(currentIndex, chars.length());
	}
	

	@Override
	public Iterator<Character> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		if (currentIndex < ( chars.length())) {
			return true;
		}
		return false;
	}

	@Override
	public Character next() {
		return chars.charAt(currentIndex++);
	}

	@Override
	public void remove() {
		// not supported
	}

}
