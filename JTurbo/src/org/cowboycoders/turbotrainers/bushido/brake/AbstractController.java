package org.cowboycoders.turbotrainers.bushido.brake;

import org.cowboycoders.turbotrainers.TurboTrainerDataListener;

public abstract class AbstractController implements TurboTrainerDataListener {

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
	 * Hook to control speed passed to listeners. Default implementation passes speed back unchanged.
	 * 
	 * If overriding, do not rely on
	 * {@link TurboTrainerDataListener#onSpeedChange} being called before this as ordering of calls can not be guaranteed.
	 * 
	 * @param speed the actual speed of the wheel. 
	 * @return speed you want listeners to observe (e.g virtual speed)
	 */
	
	public double onNotifyNewSpeed(double speed) {
		return speed;
	}
	
	

}
