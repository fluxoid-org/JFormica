package org.cowboycoders.ant.utils;

import java.util.AbstractQueue;
import java.util.LinkedList;

public abstract class AbstractFixedSizeQueue<V> extends AbstractQueue<V> implements FixedSizeQueue<V> {

	protected LinkedList<V> queue;

	protected int maxSize;
	
	protected LinkedList<V> getQueue() {
		return queue;
	}

	protected int getMaxSize() {
		return maxSize;
	}

	public AbstractFixedSizeQueue(int maxSize) {
		this.queue = new LinkedList<V>();
		this.maxSize = maxSize;
	}

	@Override
	public boolean offer(V e) {
		displace(e);
		return true;
	}

	@Override
	public V displace(V e) {
		V rtn = null;
		if (queue.size() >= maxSize) {
			rtn = queue.poll();
		}
		queue.add(e);
		return rtn;
	}

	@Override
	public int size() {
		return queue.size();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [queue=" + queue + "]";
	}

}