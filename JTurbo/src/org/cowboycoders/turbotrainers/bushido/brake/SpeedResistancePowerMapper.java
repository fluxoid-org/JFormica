package org.cowboycoders.turbotrainers.bushido.brake;

import java.io.File;

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


	// Period at which the virtual speed will be updated (ms)
	private static final int POWER_MODEL_UPDATE_PERIOD_MS = 100;

	// Brake resistance upon power up of the brake
	private static final int INITIAL_BRAKE_RESISTANCE = 100;
	// Below this limit power-speed characteristics 
	// don' change appreciably for the brake
	private static final int MINIMUM_BRAKE_RESISTANCE = 100;
	// It becomes very hard to pedal around ~700 
	// and the tyre starts slipping even with high tension in the brake. 
	private static final int MAXIMUM_BRAKE_RESISTANCE = 1000;
	// Minimum simulation speed. Used in conjunction with minimum simulation power.
	// Below this pedaling speed we drop to minimum resistance
	// Currently the surface fit doesn't map well below about this value for lowe powers and 
	// the brake also switches off at some speed > 0
	private static final int MINIMUM_SIMULATION_SPEED = 10;
	// The surface fit works at higher power for slow speeds. This is 
	private static final int MINIMUM_SIMULATION_SPEED_OVERRIDE_POWER = 250;
	
	// 16 coefficients from polynomial surface fit mapping speed and power to
	// brake resistance
	private static final double[] SURF_COEFFS = { 
		   7.62670239e+02,   8.36403498e+00,  -1.93426172e-02,   1.19616898e-05,
		  -1.26187201e+02,  -3.08275665e-02,   5.64269493e-04,  -4.42943697e-07,
		   3.74310573e+00,  -5.51901879e-03,  -2.52126663e-06,   4.64788059e-09,
		  -4.24931219e-02,   1.11216056e-04,  -8.58263602e-08,   1.58166813e-11
		  };

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
		
		// If the speed is too low, just send back the minimum resistance. 
		// Only do this if the power is also low
		if ((virtualSpeed < MINIMUM_SIMULATION_SPEED) && (actualPower < MINIMUM_SIMULATION_SPEED_OVERRIDE_POWER)) {
			return MINIMUM_BRAKE_RESISTANCE;
		}

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
			logToCsv(VIRTUAL_SPEED_HEADING, virtualSpeed);
			logToCsv(ABSOLUTE_RESISTANCE_HEADING,
					bushidoDataModel.getAbsoluteResistance());
				
			
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
		
		logToCsv(ACTUAL_SPEED_HEADING, speed);

		return getDataModel().getVirtualSpeed();
	}

	@Override
	public final void onStart() {
		getDataModel().setResistanceBounds(MINIMUM_BRAKE_RESISTANCE, MAXIMUM_BRAKE_RESISTANCE);
		getDataModel().setResistance(INITIAL_BRAKE_RESISTANCE);
	}
	
	@Override
	public final void onStop() {
		powerModelUpdater.stop();
	}

	@Override
	protected SimpleCsvLogger getCsvLogger(File file) {
		SimpleCsvLogger logger = new SimpleCsvLogger(file, ACTUAL_SPEED_HEADING,
				VIRTUAL_SPEED_HEADING, ABSOLUTE_RESISTANCE_HEADING);
		logger.addTime(true);
		logger.append(true);
		return logger;
	}



}
