package org.cowboycoders.turbotrainers.bushido.brake;

import org.cowboycoders.turbotrainers.PowerModel;
import org.cowboycoders.turbotrainers.TurboTrainerDataListener;
import org.cowboycoders.utils.FixedPeriodUpdater;
import org.cowboycoders.utils.UpdateCallback;

/**
 * @author www.cowboycoders.org
 * 
 *         A brake resistance controller which maps a speed to a brake
 *         resistance.
 * 
 *         The mapping is implemented using a polynomial fit based on
 *         observations of the head unit behavior.
 * 
 */
public class SpeedResistanceMapper implements
		TurboTrainerDataListener {

	// Period at which the virtual speed will be updated (ms)
	private static final int POWER_MODEL_UPDATE_PERIOD_MS = 100;

	// To stop furious pedaling at the start we choose a reasonable initial
	// resistance
	private static final int INITIAL_BRAKE_RESISTANCE = 20;

	// Coefficients from polynomial fit to brake resistance -> speed
	private static final double POLY_A = 8.728;
	private static final double POLY_B = 2.92e-1;
	private static final double POLY_C = 9.6e-1;
	private static final double POLY_D = -7.7e-2;
	private static final double POLY_E = 1.172e-1;
	private static final double POLY_F = 7.203;
	private static final double POLY_G = -2.55e-2;
	private static final double POLY_H = 1.82e-3;
	private static final double POLY_I = 4.917e-4;

	// Model to estimate bike speed from power
	private PowerModel powerModel = new PowerModel();

	// Holds simulation specific data, eg. current power, rider weight etc.
	private BrakeModel bushidoDataModel;

	public SpeedResistanceMapper(BrakeModel bushidoModel) {
		this.bushidoDataModel = bushidoModel;
		bushidoDataModel.setResistance(INITIAL_BRAKE_RESISTANCE);
	}

	/**
	 * Calculates a brake resistance from the current virtual speed.
	 * 
	 * The virtual speed is calculated using a power model taking into account
	 * rider and bike weight (total weight), the current slope, etc.
	 * 
	 * The polynomial fit has been generated from data based on the relationship
	 * between the brake resistance and the virtual speed on the head unit.
	 * 
	 * @return - estimated brake resistance
	 */
	public double getBrakeResistanceFromPolynomialFit() {

		double slope = bushidoDataModel.getSlope();
		double totalWeight = bushidoDataModel.getTotalWeight();
		double virtualSpeed = bushidoDataModel.getVirtualSpeed();

		// Use a polynomial fit to data ripped from the head unit to estimate
		// brake resistance from total weight, speed and slope
		double brakeResistance = slope * POLY_A + totalWeight * POLY_B
				+ totalWeight * slope * POLY_C + virtualSpeed * POLY_D
				+ Math.pow(virtualSpeed, 2) * POLY_E + POLY_F + slope * virtualSpeed * POLY_G
				+ totalWeight * virtualSpeed * POLY_H + slope * totalWeight * virtualSpeed
				* POLY_I;

		return brakeResistance;
	}

	/**
	 * Periodically updates the virtual speed which is required for estimating
	 * the brake resistance
	 * 
	 * This is called frequently for two reasons:
	 * 
	 * 1. We want to ensure that the most recent resistance estimate is
	 *    always available to be sent to the brake. 
	 * 2. The power model requires
	 *    updating in sub-second intervals for the integration technique to work
	 *    with reasonable accuracy.
	 * 
	 */
	private UpdateCallback updateVirtualSpeed = new UpdateCallback() {
		@Override
		public void onUpdate(Object newValue) {
			
			double virtualSpeed = powerModel.updatePower((Double) newValue);
			bushidoDataModel.setVirtualSpeed(virtualSpeed);
			// Update the brake resistance from the current virtual speed
			bushidoDataModel
					.setResistance(getBrakeResistanceFromPolynomialFit());
		}
	};
	private FixedPeriodUpdater powerModelUpdater = new FixedPeriodUpdater(
			new Double(0), updateVirtualSpeed, POWER_MODEL_UPDATE_PERIOD_MS);

	@Override
	public void onPowerChange(double power) {
		
		// Update the power with which the power model is updated with
		powerModelUpdater.update(new Double(power));
		// Only starts once
		powerModelUpdater.start();

	}

	@Override
	public void onSpeedChange(double speed) {
		// Not interested
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

}
