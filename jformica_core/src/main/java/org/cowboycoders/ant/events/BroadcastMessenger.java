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
/**
 *
 */
package org.cowboycoders.ant.events;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Stores ant messages with thread safe access
 *
 * @author will
 *
 */
public class BroadcastMessenger<V> implements MessageDispatcher<V> {


	/**
	 * Contains all classes listening for new messages
	 */
	Set<BroadcastListener<V>> listeners = new HashSet<BroadcastListener<V>>();

	/**
	 * Used to lock {@code listeners}
	 */
	ReentrantReadWriteLock listenerLock = new ReentrantReadWriteLock();

	/**
	 * Adds a listener
	 *
	 * @param listener TODO: document this
	 */
	@Override
	public void addBroadcastListener(BroadcastListener<V> listener) {
		try {
			listenerLock.writeLock().lock();
			listeners.add(listener);
		} finally {
			listenerLock.writeLock().unlock();
		}
	}

	/**
	 * @param listener to be removed
	 */
	@Override
	public void removeBroadcastListener(BroadcastListener<V> listener) {
    try {
			listenerLock.writeLock().lock();
			listeners.remove(listener);
		} finally {
			listenerLock.writeLock().unlock();
		}
	}

	/**
	 * Returns current number of listeners
	 * @return number of listeners
	 */
	@Override
	public int getListenerCount() {
		try {
			listenerLock.readLock().lock();
			return listeners.size();
		} finally {
			listenerLock.readLock().unlock();
		}
	}

	/**
	 * sends all listeners the message
	 *
	 * @param message TODO: document this
	 */
	@Override
	public void sendMessage(final V message) {
		try {
      // write lock, so BroadcastListener can remove itself in receiveMessage()
      // body. This guarantees it will not receive any more messages.
			listenerLock.writeLock().lock();
      // clone, so don't modify underlying collection during iteration
      HashSet<BroadcastListener<V>> clone = new HashSet<>(listeners);
			for (final BroadcastListener<V> listener : clone) {
				listener.receiveMessage(message);
			}
		} finally {
			listenerLock.writeLock().unlock();
		}

	}

}
