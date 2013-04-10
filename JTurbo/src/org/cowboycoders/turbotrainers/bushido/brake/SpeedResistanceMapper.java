package org.cowboycoders.turbotrainers.bushido.brake;

import org.cowboycoders.turbotrainers.PowerModel;
import org.cowboycoders.turbotrainers.TurboTrainerDataListener;
import org.cowboycoders.utils.Conversions;
import org.cowboycoders.utils.FixedPeriodUpdater;
import org.cowboycoders.utils.SimpleCsvLogger;
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
public class SpeedResistanceMapper extends AbstractController {

	private static final String ABSOLUTE_RESISTANCE_HEADING = "Absolute Resistance";

	private static final String VIRTUAL_SPEED_HEADING = "Virtual Speed";

	private static final String ACTUAL_SPEED_HEADING = "Actual Speed";

	// Period at which the virtual speed will be updated (ms)
	private static final int POWER_MODEL_UPDATE_PERIOD_MS = 100;
	
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
	private final PowerModel powerModel = new PowerModel();
	
	private SimpleCsvLogger logger;

	

//	public SpeedResistanceMapper(BrakeModel bushidoModel) {
//		this.bushidoDataModel = bushidoModel;
//		bushidoDataModel.setResistance(INITIAL_BRAKE_RESISTANCE);
//	}

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
		final BrakeModel bushidoDataModel = getDataModel();
		final double slope = bushidoDataModel.getSlope();
		final double totalWeight = bushidoDataModel.getTotalWeight();
		final double actualSpeed = bushidoDataModel.getActualSpeed();

		// Use a polynomial fit to data ripped from the head unit to estimate
		// brake resistance from total weight, speed and slope
		final double brakeResistance = slope * POLY_A + totalWeight * POLY_B
				+ totalWeight * slope * POLY_C + actualSpeed * POLY_D
				+ Math.pow(actualSpeed, 2) * POLY_E + POLY_F + slope * actualSpeed * POLY_G
				+ totalWeight * actualSpeed * POLY_H + slope * totalWeight * actualSpeed
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
	private final UpdateCallback updateVirtualSpeed = new UpdateCallback() {
		@Override
		public void onUpdate(final Object newValue) {
			final BrakeModel bushidoDataModel = getDataModel();
			final double virtualSpeed = powerModel.updatePower((Double) newValue) * Conversions.METRES_PER_SECOND_TO_KM_PER_HOUR;
			bushidoDataModel.setVirtualSpeed(virtualSpeed);
			// Update the brake resistance from the current virtual speed
			bushidoDataModel
					.setAbsoluteResistance((int)getBrakeResistanceFromPolynomialFit());
			synchronized (this) {
				if (logger != null) {
					logger.update(VIRTUAL_SPEED_HEADING, virtualSpeed);
					logger.update(ABSOLUTE_RESISTANCE_HEADING, bushidoDataModel.getAbsoluteResistance());
				}
			}
		}
	};
	private final FixedPeriodUpdater powerModelUpdater = new FixedPeriodUpdater(
			new Double(0), updateVirtualSpeed, POWER_MODEL_UPDATE_PERIOD_MS);





	@Override
	public double onPowerChange(final double power) {
		
		// Update the power with which the power model is updated with
		powerModelUpdater.update(new Double(power));
		// Only starts once
		powerModelUpdater.start();
		
		return power;
	}

	@Override
	public double onSpeedChange(final double speed) {
		
		synchronized (this) {
			if (logger != null) {
				logger.update(ACTUAL_SPEED_HEADING, speed);
			}
		}
		
		final BrakeModel bushidoDataModel = getDataModel();
		// return virtual speed instead!
		return bushidoDataModel.getVirtualSpeed();
	}


	
	@Override
	public void onStart() {
		getDataModel().setResistance(INITIAL_BRAKE_RESISTANCE);
	}

	public void stop() {
		powerModelUpdater.stop();
	}
	
	public synchronized void enableLogging(final String dir, final String filename) {
		this.logger = new SimpleCsvLogger(dir,filename,ACTUAL_SPEED_HEADING,VIRTUAL_SPEED_HEADING,ABSOLUTE_RESISTANCE_HEADING);
		this.logger.addTime(true);
		this.logger.append(true);
	}
	


}
