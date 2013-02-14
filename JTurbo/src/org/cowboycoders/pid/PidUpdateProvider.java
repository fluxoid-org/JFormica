package org.cowboycoders.pid;

public interface PidUpdateProvider {
	void registerPidUpdateLister(PidUpdateListener listener);
	void unregisterPidUpdateLister(PidUpdateListener listener);
}
