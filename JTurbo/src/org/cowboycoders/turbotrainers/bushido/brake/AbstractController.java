package org.cowboycoders.turbotrainers.bushido.brake;

import java.io.File;

import org.cowboycoders.turbotrainers.TurboTrainerDataHooks;
import org.cowboycoders.utils.SimpleCsvLogger;

public abstract class AbstractController implements TurboTrainerDataHooks {
	
	private SimpleCsvLogger logger;
	
	// some standard logging headings for convenience
	protected static final String ABSOLUTE_RESISTANCE_HEADING = "Absolute Resistance";
	protected static final String VIRTUAL_SPEED_HEADING = "Virtual Speed";
	protected static final String ACTUAL_SPEED_HEADING = "Actual Speed";
	protected static final String POWER_HEADING = "Power";
	protected static final String CADENCE_HEADING = "Cadence";
	
	protected synchronized void setCsvLogger(SimpleCsvLogger logger) {
		this.logger = logger;
	}
	/**
	 * Must override {@link AbstractController#getCsvLogger(File)} or manually set with
	 * {@link AbstractController#setCsvLogger(SimpleCsvLogger)}
	 * 
	 * @param heading in csv file
	 * @param value value associated with heading
	 */
	protected synchronized void logToCsv(String heading, Object value) {
		if (logger != null) {
			logger.update(heading, value);
		}
	}

	private BrakeModel bushidoDataModel;
	private boolean started = false;

	protected BrakeModel getDataModel() {
		return this.bushidoDataModel;
	}

	private void setDataModel(BrakeModel bushidoModel) {
		this.bushidoDataModel = bushidoModel;
	}

	public boolean isStarted() {
		return started;
	}

	public synchronized final void start(BrakeModel bushidoModel) {
		if (started) return;
		started  = true;
		setDataModel(bushidoModel);
		onStart();
	}
	
	public synchronized void stop() {
		if (!started) return;
		started = false;
		onStop();
	}
	
	/**
	 * Guaranteed to only be called if not already started
	 */
	public abstract void onStart();
	

	/**
	 * Guaranteed to only be called if not already stopped
	 */
	public abstract void onStop();
	
	/**
	 * Default implementation passes value back unchanged, see: {@link TurboTrainerDataHooks#onSpeedChange(double)}
	 */
	@Override
	public double onSpeedChange(double speed) {
		return speed;
	}
	
	/**
	 * Default implementation passes value back unchanged, see: {@link TurboTrainerDataHooks#onPowerChange(double)}
	 */
	@Override
	public double onPowerChange(double power) {
		return power;
	}
	
	/**
	 * Default implementation passes value back unchanged, see: {@link TurboTrainerDataHooks#onCadenceChange(double)}
	 */
	@Override
	public double onCadenceChange(double cadence) {
		return cadence;
	}
	
	/**
	 * Default implementation passes value back unchanged, see: {@link TurboTrainerDataHooks#onDistanceChange(double)}
	 */
	@Override
	public double onDistanceChange(double distance) {
		return distance;
	}
	
	/**
	 * Default implementation requests csv logger
	 * @param file
	 */
	public void enableLogging(File file) {
		setCsvLogger(getCsvLogger(file));
	}
	
	/**
	 * Must return non-null value if you want to use {@link AbstractController#logToCsv(String, Object))
	 * @param file to log csv data to
	 * @return new {@link SimpleCsvLogger}
	 */
	protected SimpleCsvLogger getCsvLogger(File file) {
		return null;
	}
	

}
