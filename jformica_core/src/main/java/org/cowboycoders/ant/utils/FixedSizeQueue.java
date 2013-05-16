package org.cowboycoders.ant.utils;

import java.util.LinkedList;
import java.util.Queue;

public interface FixedSizeQueue<V> extends Queue<V>  {

	/**
	 * Displaces and returns HEAD of queue if full, otherwise just adds
	 * element.
	 * @param element to add
	 * @return the displaced element or null if nothing was displaced
	 */
	public abstract V displace(V e);
	
	
}