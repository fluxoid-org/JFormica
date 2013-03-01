package org.cowboycoders.turbotrainers;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public abstract class GenericTurboTrainer implements TurboTrainerInterface {
	
	
	private Mode [] supportedModes;
	private Mode currentMode;
	
	protected void setSupportedModes(Mode ... modes) {
		if (modes == null) {
			supportedModes = new Mode[0];
		} else {
			supportedModes = modes;
		}
		Arrays.sort(supportedModes);
	}
	
	protected GenericTurboTrainer() {
		setSupportedModes(new Mode[0]);
	}
	
	@Override
	public Mode[] modesSupported() {
		return supportedModes;
	}

	@Override
	public void setMode(Mode mode) throws IllegalArgumentException {
		if (Arrays.binarySearch(supportedModes, mode) < 0) {
			throw new IllegalArgumentException();
		}
		currentMode = mode;
	}
	
	public Mode getCurrentMode() {
		return this.currentMode;
	}
	
	
	/**
	 * Weak set
	 */
	protected Set<TurboTrainerDataListener> dataChangeListeners = Collections
				.newSetFromMap(new WeakHashMap<TurboTrainerDataListener, Boolean>());

	protected Set<TurboTrainerDataListener> getDataChangeListeners() {
		return dataChangeListeners;
	}

	@Override
	public void unregisterDataListener(TurboTrainerDataListener listener) {
		synchronized (dataChangeListeners) {
			dataChangeListeners.remove(listener);
		}
	}

	/**
	 * Stored in weak set, so keep a reference : no anonymous classes
	 */
	public void registerDataListener(TurboTrainerDataListener listener) {
		synchronized (dataChangeListeners) {
			dataChangeListeners.add(listener);
		}
	}
	

}
