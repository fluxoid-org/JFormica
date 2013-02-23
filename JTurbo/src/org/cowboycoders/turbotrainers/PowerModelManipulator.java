package org.cowboycoders.turbotrainers;

public interface PowerModelManipulator {

	/**
	 * Should we roll backwards down hills or "put our feet down"
	 */
	public abstract boolean isNegativeVelocityAllowed();

	/**
	 * Should we roll backwards down hills or "put our feet down"
	 */
	public abstract void setNegativeVelocityAllowed(
			boolean negativeVelocityAllowed);

	public abstract double getWindSpeed();

	public abstract void setWindSpeed(double windSpeed);

	public abstract double getCurrentBearing();

	public abstract void setCurrentBearing(double currentBearing);

	public abstract double getWindDirectionDegrees();

	public abstract void setWindDirectionDegrees(double windDirectionDegrees);

	public abstract double getAirDensity();

	public abstract void setAirDensity(double airDensity);

	public abstract double getIncrementalDragAreaSpokes();

	public abstract void setIncrementalDragAreaSpokes(
			double incrementalDragAreaSpokes);

	public abstract double getDragArea();

	public abstract void setDragArea(double dragArea);

	public abstract double getGradientAsPercentage();

	public abstract void setGradientAsPercentage(double gradientAsPercentage);

	public abstract double getCoefficentRollingResistance();

	public abstract void setCoefficentRollingResistance(
			double coefficentRollingResistance);

	public abstract double getTotalMass();

	public abstract void setTotalMass(double totalMass);

	public abstract double getKineticEnergy();

	public abstract double getMomentOfInertiaWheels();

	public abstract void setMomentOfInertiaWheels(double momentOfInertiaWheels);

	public abstract double getOutsideRadiusTire();

	public abstract void setOutsideRadiusTire(double outsideRadiusTire);

	public abstract double getVelocity();

	public abstract double getAirVelocity();

	public abstract double getYaw();

	void setVelocity(double velocity);

}