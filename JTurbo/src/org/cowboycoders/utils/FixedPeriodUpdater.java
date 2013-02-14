package org.cowboycoders.utils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;


public class FixedPeriodUpdater {
	
	public final static Logger LOGGER = Logger.getLogger(FixedPeriodUpdater.class .getName());
	
	private Lock updateLock = new ReentrantLock();
	
	private Object latestValue;

	private UpdateCallback updateCallback;

	private boolean running = false;
	
	private Timer timer;

	private long period;
	
	protected Object getLatestValue() {
		return latestValue;
	}

	protected void setLatestValue(Object lastestValue) {
		this.latestValue = lastestValue;
	}
	
	/**
	 * Period in milliseconds
	 * @param callback
	 * @param period
	 */
	public FixedPeriodUpdater(Object initialValue, UpdateCallback callback, long period) {
		if (callback == null) {
			throw new NullPointerException("callback cannot be null");
		}
		this.updateCallback = callback;
		this.period = period;
		setLatestValue(initialValue);
	}

	private class UpdaterTimerTask extends TimerTask {
		
		@Override
		public void run() {
			try {
				updateLock.lock();
				Object newValue = getLatestValue();
				FixedPeriodUpdater.this.updateCallback.onUpdate(newValue);
			} finally {
				updateLock.unlock();
			}
			
		}
		
	}
	
	public void update(Object value) {
		try {
			updateLock.lock();
			setLatestValue(value);
		} finally {
			updateLock.unlock();
		}
	}
	
	public synchronized void start() {
		if (running) return;
		running  = true;
		timer = new Timer();
		timer.schedule(new UpdaterTimerTask(), 0, period);
	}
	
	public synchronized void stop() {
		if (!running) return;
		running  = false;
		timer.cancel();
	}
	
	public static void main(String [] args) throws InterruptedException {
		UpdateCallback callback = new UpdateCallback() {

			@Override
			public void onUpdate(Object newValue) {
				System.out.println(newValue);
				
			}
			
		};
		FixedPeriodUpdater up = new FixedPeriodUpdater(10,callback, 100);
		up.start();
		Thread.sleep(1000);
		up.update(19);
		Thread.sleep(200);
		up.update(33);
		Thread.sleep(200);
		up.update(27);
		Thread.sleep(200);
		up.stop();
		
	}
	
	
}
