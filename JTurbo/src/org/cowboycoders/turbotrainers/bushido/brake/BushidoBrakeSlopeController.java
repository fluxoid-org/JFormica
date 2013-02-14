package org.cowboycoders.turbotrainers.bushido.brake;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.cowboycoders.pid.GainController;
import org.cowboycoders.pid.GainParameters;
import org.cowboycoders.pid.OutputControlParameters;
import org.cowboycoders.pid.OutputController;
import org.cowboycoders.pid.PidController;
import org.cowboycoders.pid.PidParameterController;
import org.cowboycoders.pid.ProcessVariableProvider;
import org.cowboycoders.turbotrainers.PowerModel;
import org.cowboycoders.turbotrainers.PowerModelManipulator;
import org.cowboycoders.turbotrainers.TurboTrainerDataListener;
import org.cowboycoders.utils.Conversions;
import org.cowboycoders.utils.FixedPeriodUpdater;
import org.cowboycoders.utils.UpdateCallback;

public class BushidoBrakeSlopeController implements TurboTrainerDataListener {
	
	private static final int  POWER_MODEL_UPDATE_PERIOD_MS = 100; // milli-seconds
	
	private static final double PID_DERIVATIVE_GAIN = 2;
	
	private static final double PID_INTEGRAL_GAIN = 0.5;
	
	private static final double PID_PROPORTIONAL_GAIN = 0.5;
	
	private PowerModel powerModel = new PowerModel();
	
	private BushidoData bushidoDataModel;
	
	private Lock speedUpdateLock = new ReentrantLock();

	private double predictedSpeed; // metres/s
	
	private double actualSpeed; // metres/s
	
	public BushidoBrakeSlopeController(BushidoData bushidoModel) {
		this.bushidoDataModel = bushidoModel;
	}
	
	private UpdateCallback powerModelUpdateCallback  = new UpdateCallback() {

		@Override
		public void onUpdate(Object newValue) {
			double predictedSpeed = powerModel.updatePower((Double) newValue);
			//predictedSpeed = Conversions.METRES_PER_SECOND_TO_KM_PER_HOUR * predictedSpeed;
			setPredictedSpeed(predictedSpeed);
		}
		
		
	};
	
	private FixedPeriodUpdater powerModelUpdater = new FixedPeriodUpdater(new Double(0),powerModelUpdateCallback,POWER_MODEL_UPDATE_PERIOD_MS);
	
	private ProcessVariableProvider actualSpeedProvider = new ProcessVariableProvider() {

		@Override
		public double getProcessVariable() {
			return getActualSpeed();
		}
		
	};
	
	private OutputController resistanceOutputController = new OutputController() {

		@Override
		public void setOutput(double resistance) {
			bushidoDataModel.setResistance(resistance);
		}

		@Override
		public double getMaxOutput() {
			return BushidoData.RESISTANCE_MAX;
		}

		@Override
		public double getMinOutput() {
			return BushidoData.RESISTANCE_MIN;
		}


		
	};
	
	private GainController resistanceGainController = new GainController() {

		@Override
		public GainParameters getGain(OutputControlParameters parameters) {
			GainParameters defaultParameters = new GainParameters(PID_PROPORTIONAL_GAIN,PID_INTEGRAL_GAIN,PID_DERIVATIVE_GAIN);
			return defaultParameters;
		}
		
	};
	
	private PidController resistancePidController = new PidController(actualSpeedProvider, resistanceOutputController,resistanceGainController);

	@Override
	public void onSpeedChange(double speed) {
		setActualSpeed(speed* Conversions.KM_PER_HOUR_TO_METRES_PER_SECOND);
		resistancePidController.adjustSetpoint(getPredictedSpeed());
	}

	@Override
	public void onPowerChange(double power) {
		powerModelUpdater.update(new Double (power));
		// only starts once
		powerModelUpdater.start();
		
	}

	@Override
	public void onCadenceChange(double cadence) {
		// Not interested
		
	}

	@Override
	public void onDistanceChange(double distance) {
		// Not interested
		
	}

	@Override
	public void onHeartRateChange(double heartRate) {
		// Not interested
		
	}
	
	public void stop() {
		powerModelUpdater.stop();
	}
	
	protected double getPredictedSpeed() {
		// double non-atomic?
		try {
			speedUpdateLock.lock();
			return predictedSpeed;
		} finally {
			speedUpdateLock.unlock();
		}
	}
	
	
	protected  void setPredictedSpeed(double newValue) {
		// double non-atomic?
		try {
			speedUpdateLock.lock();
			BushidoBrakeSlopeController.this.predictedSpeed = newValue;
		} finally {
			speedUpdateLock.unlock();
		}
	}
	
	protected double getActualSpeed() {
		// double non-atomic?
		try {
			speedUpdateLock.lock();
			return actualSpeed;
		} finally {
			speedUpdateLock.unlock();
		}
	}
	
	
	protected  void setActualSpeed(double newValue) {
		// double non-atomic?
		try {
			speedUpdateLock.lock();
			BushidoBrakeSlopeController.this.actualSpeed = newValue;
		} finally {
			speedUpdateLock.unlock();
		}
	}
	
	public PowerModelManipulator getPowerModelManipulator() {
		return powerModel;
	}
	
	public PidParameterController getPidParameterController() {
		return resistancePidController;
	}
	

}
