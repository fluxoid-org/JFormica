/*
*    Copyright (c) 2013, Will Szumski
*    Copyright (c) 2013, Doug Szumski
*
*    This file is part of Cyclismo.
*
*    Cyclismo is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Cyclismo is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Cyclismo.  If not, see <http://www.gnu.org/licenses/>.
*/
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
