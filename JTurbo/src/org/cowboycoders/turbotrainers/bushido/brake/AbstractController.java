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

	public void final start(BrakeModel bushidoModel) {
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
	
	

}
