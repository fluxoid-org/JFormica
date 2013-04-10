package org.cowboycoders.turbotrainers.bushido.brake;

import org.cowboycoders.turbotrainers.TurboTrainerDataHooks;

public abstract class AbstractController implements TurboTrainerDataHooks {

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

	public final void start(BrakeModel bushidoModel) {
		if (started) return;
		started  = true;
		setDataModel(bushidoModel);
		onStart();
	}
	
	public void stop() {
		if (!started) return;
		started = false;
	}
	
	public abstract void onStart();
	
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
	
	
	
	

}
