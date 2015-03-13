/**
 *     Copyright (c) 2013, Will Szumski
 *
 *     This file is part of formicidae.
 *
 *     formicidae is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     formicidae is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with formicidae.  If not, see <http://www.gnu.org/licenses/>.
 */
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