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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Stores ant messages with thread safe access
 * 
 * @author will
 * 
 */
public class BroadcastMessenger<V> {

	private static final ExecutorService SHARED_SINGLE_THREAD_EXECUTOR = Executors
			.newSingleThreadExecutor(new ThreadFactory() {
				@Override
				public Thread newThread(Runnable runnable) {
					Thread thread = Executors.defaultThreadFactory().newThread(
							runnable);
					thread.setName("BroadcastMessengerThread");
					thread.setDaemon(true);
					return thread;
				}
			});

	/**
	 * Used to concurrently notify listeners
	 */
	ExecutorService dispatchPool;

	/**
	 * Contains all classes listening for new messages
	 */
	Set<BroadcastListener<V>> listeners = new HashSet<BroadcastListener<V>>();

	/**
	 * Used to lock {@code listeners}
	 */
	ReentrantReadWriteLock listenerLock = new ReentrantReadWriteLock();

	/**
	 * Backed by an unbounded {@see java.util.concurrent.ThreadPoolExecutor}
	 */
	public BroadcastMessenger() {
		dispatchPool = SHARED_SINGLE_THREAD_EXECUTOR;
	}

	/**
	 * Use a custom {@code java.util.concurrent.ThreadPoolExecutor}
	 * 
	 * {@see java.util.concurrent.ThreadPoolExecutor} for explantion of
	 * parameters.
	 * 
	 * @param coreSize
	 * @param maxSize
	 * @param timeout
	 * @param timeoutUnit
	 * @param backingQueue
	 */
	public BroadcastMessenger(int coreSize, int maxSize, int timeout,
			TimeUnit timeoutUnit, BlockingQueue<Runnable> backingQueue) {
		dispatchPool = new ThreadPoolExecutor(coreSize, maxSize, timeout,
				timeoutUnit, backingQueue);
	}

	/**
	 * Adds a listener
	 * 
	 * @param listener
	 */
	public void addBroadcastListener(BroadcastListener<V> listener) {
		try {
			listenerLock.writeLock().lock();
			listeners.add(listener);
		} finally {
			listenerLock.writeLock().unlock();
		}
	}

	/**
	 * removes a listener
	 * 
	 * @param listener
	 */
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
	 * @param message
	 */
	public void sendMessage(final V message) {
		try {
			listenerLock.readLock().lock();
			for (final BroadcastListener<V> listener : listeners) {
				;
				dispatchPool.execute(new Runnable() {
					@Override
					public void run() {
						listener.receiveMessage(message);
					}
				});
			}
		} finally {
			listenerLock.readLock().unlock();
		}

	}

}