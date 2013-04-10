package org.cowboycoders.turbotrainers.bushido.brake;

import org.cowboycoders.turbotrainers.PowerModel;
import org.cowboycoders.utils.Conversions;
import org.cowboycoders.utils.FixedPeriodUpdater;
import org.cowboycoders.utils.SimpleCsvLogger;
import org.cowboycoders.utils.UpdateCallback;

/**
 * @author www.cowboycoders.org
 *
 *         A brake resistance controller which maps a speed, brake resistance
 *         and power.
 *
 *         The mapping is implemented using a surface fit to calibration data
 *         generated from measuring power as a function of actual wheel speed
 *         and brake resistance.
 *
 */
public class SpeedResistancePowerMapper extends AbstractController {

	private SimpleCsvLogger logger;

	private static final String ABSOLUTE_RESISTANCE_HEADING = "Absolute Resistance";
	private static final String VIRTUAL_SPEED_HEADING = "Virtual Speed";
	private static final String ACTUAL_SPEED_HEADING = "Actual Speed";

	// Period at which the virtual speed will be updated (ms)
	private static final int POWER_MODEL_UPDATE_PERIOD_MS = 100;

	// Initial brake resistance
	private static final int INITIAL_BRAKE_RESISTANCE = 0;

	// 16 coefficients from polynomial surface fit mapping speed and power to
	// brake resistance
	private static final double[] SURF_COEFFS = {-1.26245135e+02,
			-1.75248240e-01, 1.16057178e-03, -1.05642895e-06, 1.89452066e+01,
			2.53875165e-02, -7.82709342e-05, 7.02887221e-08, -4.44753771e-01,
			-7.38460610e-04, 2.72192310e-06, -1.43856918e-09, 4.67359862e-03,
			6.13101536e-06, -2.62701814e-08, 2.21264118e-11};

	// Model to estimate speed from power
	private final PowerModel powerModel = new PowerModel();

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
	public final double getBrakeResistanceFromSurfaceFit() {

		final BrakeModel bushidoDataModel = getDataModel();

		// We have a virtual speed calculated from the actual power being
		// produced and we
		// want to set the brake resistance so that it is appropriate for these
		// two quantities.

		final double actualPower = bushidoDataModel.getPower();
		final double virtualSpeed = bushidoDataModel.getVirtualSpeed();

		// Order of the polynomial fit
		final int order = (int) Math.sqrt(SURF_COEFFS.length) - 1;

		// Calculate the brake resistance from virtual speed and actual power
		// using the
		// surface fit
		double brakeResistance = 0.0;
		int k = 0;
		for (int i = 0; i < order + 1; ++i) {
			for (int j = 0; j < order + 1; ++j) {
				brakeResistance += SURF_COEFFS[k++] * Math.pow(virtualSpeed, i)
						* Math.pow(actualPower, j);
			}
		}

		return brakeResistance;
	}

	/**
	 * Periodically updates the virtual speed which is required for estimating
	 * the brake resistance
	 * 
	 * This is called frequently for two reasons:
	 * 
	 * 1. We want to ensure that the most recent resistance estimate is always
	 * available to be sent to the brake. 2. The power model requires updating
	 * in sub-second intervals for the integration technique to work with
	 * reasonable accuracy.
	 * 
	 */
	private final UpdateCallback updateVirtualSpeed = new UpdateCallback() {
		@Override
		public void onUpdate(final Object newValue) {
			final BrakeModel bushidoDataModel = getDataModel();
			final double virtualSpeed = powerModel.updatePower((Double) newValue)
					* Conversions.METRES_PER_SECOND_TO_KM_PER_HOUR;
			bushidoDataModel.setVirtualSpeed(virtualSpeed);
			// Update the brake resistance from the current virtual speed
			bushidoDataModel
					.setAbsoluteResistance((int) getBrakeResistanceFromSurfaceFit());
			synchronized (this) {
				if (logger != null) {
					logger.update(VIRTUAL_SPEED_HEADING, virtualSpeed);
					logger.update(ABSOLUTE_RESISTANCE_HEADING,
							bushidoDataModel.getAbsoluteResistance());
				}
			}
		}
	};

	private final FixedPeriodUpdater powerModelUpdater = new FixedPeriodUpdater(
			new Double(0), updateVirtualSpeed, POWER_MODEL_UPDATE_PERIOD_MS);

	@Override
	public final double onPowerChange(final double power) {

		// Update the power with which the power model is updated with
		powerModelUpdater.update(new Double(power));
		// Only starts once
		powerModelUpdater.start();

		return power;

	}

	@Override
	public final double onSpeedChange(final double speed) {
		synchronized (this) {
			if (logger != null) {
				logger.update(ACTUAL_SPEED_HEADING, speed);
			}
		}
		return getDataModel().getVirtualSpeed();
	}

	@Override
	public final void onStart() {
		getDataModel().setResistance(INITIAL_BRAKE_RESISTANCE);
	}

	public void stop() {
		powerModelUpdater.stop();
	}

	public synchronized void enableLogging(final String dir, final String filename) {
		this.logger = new SimpleCsvLogger(dir, filename, ACTUAL_SPEED_HEADING,
				VIRTUAL_SPEED_HEADING, ABSOLUTE_RESISTANCE_HEADING);
		this.logger.addTime(true);
		this.logger.append(true);
	}

}
