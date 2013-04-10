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
import org.cowboycoders.utils.SlopeTimeAverager;
import org.cowboycoders.utils.UpdateCallback;

public class PidBrakeController extends AbstractController {
	
	private static final int  POWER_MODEL_UPDATE_PERIOD_MS = 100; // milli-seconds
	
	private static final double PID_DERIVATIVE_GAIN = 2;
	
	private static final double PID_INTEGRAL_GAIN = 0.5;
	
	private static final double PID_PROPORTIONAL_GAIN = 0.5;
	
	private static final int STARTING_RESISTANCE = 0; // %
	
	// if acceleration is +/- this value we assume we have reach a steady state. 
	// This is when we try and sync actual wheel speed to prdicted wheel speed
	private static final Double ACTUAL_SPEED_STEADY_STATE_THRESHOLD = 0.28; // m/s^2
	
	private static final int MIN_SLOPE_SAMPLES = 5;

	// Don't try and simulate speeds below this due to inaccuracies in readings at low speed
	// Rolling resistance on turbo becomes excessively large such that lowest resistance setting is unrealistically hard
	private static final double LOW_SPEED_LIMIT = 1.8; // m/s ~ 4 mph
	
	private PowerModel powerModel = new PowerModel();
	
	private Lock speedUpdateLock = new ReentrantLock();

	private double predictedSpeed = -1; // metres/s
	
	private double actualSpeed = -1; // metres/s
	
	private SlopeTimeAverager predictedSpeedSlopeAverager = new SlopeTimeAverager();
	
	private SlopeTimeAverager actualSpeedSlopeAverager = new SlopeTimeAverager();
	
//	public PidBrakeController(BrakeModel bushidoModel) {
//		this.bushidoDataModel = bushidoModel;
//		bushidoDataModel.setResistance(getEstimatedResistance());
//		actualSpeedSlopeAverager.setThreshold(ACTUAL_SPEED_STEADY_STATE_THRESHOLD, -ACTUAL_SPEED_STEADY_STATE_THRESHOLD);
//	}
	
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
			double actualSpeed = getActualSpeed();
			return actualSpeed;
		}
		
	};
	
	private OutputController resistanceOutputController = new OutputController() {

		@Override
		public void setOutput(double resistance) {
			BrakeModel bushidoDataModel = getDataModel();
			// enforce estimated resistance for first update
			if (needsSync) {
				bushidoDataModel.setResistance(getEstimatedResistance());
				needsSync = false;
				return;
			}
			bushidoDataModel.setResistance(resistance + getEstimatedResistance());
		}
		
		/**
		 * Centred on getEstimatedResistance()
		 */
		@Override
		public double getMaxOutput() {
			return BrakeModel.RESISTANCE_MAX - getEstimatedResistance();
		}
		
		/**
		 * Centred on getEstimatedResistance()
		 */
		@Override
		public double getMinOutput() {
			return BrakeModel.RESISTANCE_MIN - getEstimatedResistance();
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

	private boolean needsSync = true;

	@Override
	public double onSpeedChange(double speed) {
		BrakeModel bushidoDataModel = getDataModel();
		setActualSpeed(speed* Conversions.KM_PER_HOUR_TO_METRES_PER_SECOND);
		double predictedSpeed = getPredictedSpeed();
		double actualSpeed = getActualSpeed();

		// test for stationary -> non-stationary transition
		// speed is on average increasing and is below our threshold
		if (predictedSpeed <= LOW_SPEED_LIMIT 
				&& predictedSpeedSlopeAverager.getAverage() > 0 ) {
			// only sync if actualSpeed is higher : actualSpeed will be lower than predicted if slowing down
			// as real speed drops faster than model predicts
			//if (actualSpeed > predictedSpeed) {
				// resync
				needsSync = true;
				resistancePidController.reset();
				// use high resistance to force sync at lower speed
				bushidoDataModel.setResistance(getEstimatedResistance());
			//}
			
		}
		// don't mess with brake resistance until reached steady state
		if (!needsSync || actualSpeedSlopeAverager.getNumberOfSamples() >= MIN_SLOPE_SAMPLES && actualSpeedSlopeAverager.isWithinThreshold()) {
			if (needsSync) {
				powerModel.setVelocity(actualSpeed);
				setPredictedSpeed(actualSpeed);
			}
			resistancePidController.adjustSetpoint(getPredictedSpeed());
		}
		
		return speed;
		//actualSpeedUpdated = true;
	}
	
	/**
	 * Estimated resistance for given gradient
	 * @return
	 */
	protected double getEstimatedResistance() {
		return STARTING_RESISTANCE + powerModel.getGradientAsPercentage();
	}

	@Override
	public double onPowerChange(double power) {
		
		powerModelUpdater.update(new Double (power));
		// only starts once
		powerModelUpdater.start();
		
		return power;
		
	}


	
	public void stop() {
		powerModelUpdater.stop();
	}
	
	protected double getPredictedSpeed() {
		// double non-atomic?
		try {
			speedUpdateLock.lock();
//			if (predictedSpeed <= LOW_SPEED_LIMIT) {
//				return LOW_SPEED_LIMIT;
//			}
			return predictedSpeed;
		} finally {
			speedUpdateLock.unlock();
		}
	}
	
	
	protected  void setPredictedSpeed(double newValue) {
		// double non-atomic?
		try {
			speedUpdateLock.lock();
			PidBrakeController.this.predictedSpeed = newValue;
			// gradient averager
			predictedSpeedSlopeAverager.add(predictedSpeed);
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
			PidBrakeController.this.actualSpeed = newValue;
			actualSpeedSlopeAverager.add(actualSpeed);
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
	
	@Override
	public void onStart() {
		BrakeModel bushidoDataModel = getDataModel();
		bushidoDataModel.setResistance(getEstimatedResistance());
		actualSpeedSlopeAverager.setThreshold(ACTUAL_SPEED_STEADY_STATE_THRESHOLD, -ACTUAL_SPEED_STEADY_STATE_THRESHOLD);
	}
	

	

}
