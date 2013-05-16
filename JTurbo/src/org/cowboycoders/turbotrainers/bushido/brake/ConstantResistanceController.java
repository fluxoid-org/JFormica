package org.cowboycoders.turbotrainers.bushido.brake;

import java.io.File;

import org.cowboycoders.utils.SimpleCsvLogger;

public class ConstantResistanceController extends AbstractController {

	private SimpleCsvLogger logger;
	private int resistance = 0;


	@Override
	public double onSpeedChange(double speed) {
		getDataModel().setAbsoluteResistance(resistance);
		logToCsv(ABSOLUTE_RESISTANCE_HEADING, getDataModel().getAbsoluteResistance());
		logToCsv(ACTUAL_SPEED_HEADING, speed);
		return speed;
	}

	@Override
	public double onPowerChange(double power) {
		synchronized (this) {
			if (logger != null) {
				logger.update(POWER_HEADING, power);
			}
		}
		return power;
	}
	
	
	public void setAbsoluteResistance(int newVal) {
		this.resistance = newVal;
	}
	
	@Override
	public void onStart() {
		getDataModel().setAbsoluteResistance(resistance);
	}

	@Override
	protected SimpleCsvLogger getCsvLogger(File file) {
		SimpleCsvLogger logger = new SimpleCsvLogger(file,ACTUAL_SPEED_HEADING,POWER_HEADING,ABSOLUTE_RESISTANCE_HEADING);
		logger.addTime(false);
		logger.append(true);
		return logger;
	}

	@Override
	public void onStop() {
		// do nothing
	}

}
