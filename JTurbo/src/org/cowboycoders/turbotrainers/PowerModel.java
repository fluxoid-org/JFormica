package org.cowboycoders.turbotrainers;

import org.cowboycoders.utils.TrapezoidIntegrator;

public class PowerModel implements PowerModelManipulator {
	
	public static final double GRAVITATIONAL_ACCELERATION = 9.81;
	double windSpeed = 0;//2.94;
	double currentBearing = 340;
	double windDirectionDegrees = 160; // direction : 180 is south
	double airDensity = 1.2234; // kg·m−3
	double incrementalDragAreaSpokes = 0.0044;
	double dragArea = 0.255 ;//57; //Coefficient of drag x frontal area
	double gradientAsPercentage = 0.00;// 0.3;
	double coefficentRollingResistance = 0.0032;
	double totalMass = 70.0;
	//double kineticEnergy = 0.;
	double momentOfInertiaWheels = 0.14; //kg m^2 for bike
	double outsideRadiusTire = 0.311;
	
	private boolean negativeVelocityAllowed = false;
	
	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#isNegativeVelocityAllowed()
	 */
	@Override
	public boolean isNegativeVelocityAllowed() {
		return negativeVelocityAllowed;
	}


	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#setNegativeVelocityAllowed(boolean)
	 */
	@Override
	public void setNegativeVelocityAllowed(boolean negativeVelocityAllowed) {
		this.negativeVelocityAllowed = negativeVelocityAllowed;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#getWindSpeed()
	 */
	@Override
	public double getWindSpeed() {
		return windSpeed;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#setWindSpeed(double)
	 */
	@Override
	public void setWindSpeed(double windSpeed) {
		this.windSpeed = windSpeed;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#getCurrentBearing()
	 */
	@Override
	public double getCurrentBearing() {
		return currentBearing % 360;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#setCurrentBearing(double)
	 */
	@Override
	public void setCurrentBearing(double currentBearing) {
		this.currentBearing = currentBearing;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#getWindDirectionDegrees()
	 */
	@Override
	public double getWindDirectionDegrees() {
		return windDirectionDegrees % 360;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#setWindDirectionDegrees(double)
	 */
	@Override
	public void setWindDirectionDegrees(double windDirectionDegrees) {
		this.windDirectionDegrees = windDirectionDegrees;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#getAirDensity()
	 */
	@Override
	public double getAirDensity() {
		return airDensity;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#setAirDensity(double)
	 */
	@Override
	public void setAirDensity(double airDensity) {
		this.airDensity = airDensity;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#getIncrementalDragAreaSpokes()
	 */
	@Override
	public double getIncrementalDragAreaSpokes() {
		return incrementalDragAreaSpokes;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#setIncrementalDragAreaSpokes(double)
	 */
	@Override
	public void setIncrementalDragAreaSpokes(double incrementalDragAreaSpokes) {
		this.incrementalDragAreaSpokes = incrementalDragAreaSpokes;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#getDragArea()
	 */
	@Override
	public double getDragArea() {
		return dragArea;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#setDragArea(double)
	 */
	@Override
	public void setDragArea(double dragArea) {
		this.dragArea = dragArea;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#getGradientAsPercentage()
	 */
	@Override
	public double getGradientAsPercentage() {
		return gradientAsPercentage;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#setGradientAsPercentage(double)
	 */
	@Override
	public void setGradientAsPercentage(double gradientAsPercentage) {
		this.gradientAsPercentage = gradientAsPercentage;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#getCoefficentRollingResistance()
	 */
	@Override
	public double getCoefficentRollingResistance() {
		return coefficentRollingResistance;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#setCoefficentRollingResistance(double)
	 */
	@Override
	public void setCoefficentRollingResistance(double coefficentRollingResistance) {
		this.coefficentRollingResistance = coefficentRollingResistance;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#getTotalMass()
	 */
	@Override
	public double getTotalMass() {
		return totalMass;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#setTotalMass(double)
	 */
	@Override
	public void setTotalMass(double totalMass) {
		this.totalMass = totalMass;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#getKineticEnergy()
	 */
	@Override
	public double getKineticEnergy() {
		synchronized (this){
			return kineticEnergy.getIntegral();
		}
	}
	
	private void setKineticEnergy(double newEnergy) {
		synchronized (this){
			kineticEnergy.setIntegral(newEnergy);
		}
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#getMomentOfInertiaWheels()
	 */
	@Override
	public double getMomentOfInertiaWheels() {
		return momentOfInertiaWheels;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#setMomentOfInertiaWheels(double)
	 */
	@Override
	public void setMomentOfInertiaWheels(double momentOfInertiaWheels) {
		this.momentOfInertiaWheels = momentOfInertiaWheels;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#getOutsideRadiusTire()
	 */
	@Override
	public double getOutsideRadiusTire() {
		return outsideRadiusTire;
	}



	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#setOutsideRadiusTire(double)
	 */
	@Override
	public void setOutsideRadiusTire(double outsideRadiusTire) {
		this.outsideRadiusTire = outsideRadiusTire;
	}



	public double getTangentialWindVelocity() {
		double windDirectionRadians = Math.toRadians(getWindDirectionDegrees());
		double bearingRadians = Math.toRadians(this.getCurrentBearing());
		return Math.cos(bearingRadians - windDirectionRadians) * getWindSpeed();
	}
	
	public double getNormalWindVelocity() {
		double windDirectionRadians = Math.toRadians(getWindDirectionDegrees());
		double bearingRadians = Math.toRadians(this.getCurrentBearing());
		return Math.sin(bearingRadians - windDirectionRadians) * getWindSpeed();
	}
	
	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#getVelocity()
	 */
	@Override
	public double getVelocity() {
		double energy = getKineticEnergy();
		double I = getMomentOfInertiaWheels();
		double r = this.getOutsideRadiusTire();
		boolean negative = energy < 0 ? true : false;
		energy = Math.abs(energy);
		double velocity = Math.sqrt(2 * energy / (getTotalMass() + I/Math.pow(r,2)));
		if (negative) return -velocity;
		return velocity;
		//return 8.36;
	}
	
	/**
	 * Force power model to a new velocity
	 * @param velocity the new velocity
	 */
	@Override
	public synchronized void setVelocity(final double velocity) {
		double I = getMomentOfInertiaWheels();
		double r = this.getOutsideRadiusTire();
		// energy required for given speed
		double energy =  0.5 * Math.pow(velocity,2) * (getTotalMass() + I/Math.pow(r,2));
		// negative energy for negative velocities
		if (velocity < 0) {
			energy = -energy;
		}
		setKineticEnergy(energy);
	}
	
	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#getAirVelocity()
	 */
	@Override
	public double getAirVelocity() {
		return getTangentialWindVelocity() + getVelocity();
	}
	
	/* (non-Javadoc)
	 * @see org.cowboycoders.turbotrainers.PowerModelManipulator#getYaw()
	 */
	@Override
	public double getYaw() {
		double radians = 0.;
		double airVelocity_ = getAirVelocity();
		if (airVelocity_ == 0) {
			radians = Math.PI / 2;
		} else {
			radians = Math.atan(getNormalWindVelocity()/getAirVelocity());
		}
		return radians;
	}
	
	public double getPowerLostToAerodynamicDrag() {
		double Vg = Math.abs(getVelocity());
		double Va = getAirVelocity();
		double p = getAirDensity();
		double CdA = getDragArea();
		double Fw = this.getIncrementalDragAreaSpokes();
		return 0.5 * Math.pow(Va, 2) * Vg * p * (CdA + Fw);
	}
	
	/**
	 * Gradient transformed to angle
	 * @return
	 */
	public double getRoadAngle() {
		return Math.atan(this.getGradientAsPercentage() /100);
	}
	
	public double getPowerLostToRollingResistance() {
		double Vg = Math.abs(getVelocity());
		double roadAngle = getRoadAngle();
		double Crr = getCoefficentRollingResistance();
		double mass = getTotalMass();
		double g = GRAVITATIONAL_ACCELERATION;
		return Vg * Math.cos(roadAngle) * Crr * mass * g;
				
	}
	
	public double getPowerLostToWheelBearings() {
		double Vg = Math.abs(getVelocity());
		return Vg * ( 91 +8.7 * Vg) * Math.pow(10, -3);			
	}
	
	public double getPowerToIncreasePotentialEnergy() {
		double mass = getTotalMass();
		double Vg = getVelocity();
		double g = GRAVITATIONAL_ACCELERATION;
		double roadAngle = getRoadAngle();
		return Vg * mass * g * Math.sin(roadAngle);		
	}
	
	private TrapezoidIntegrator kineticEnergy = new TrapezoidIntegrator();
//	private boolean running = false;
//	
//	private synchronized void start() {
//		if (running) return;
//		updatePower(0);
//		running = true;
//	}
	
	public double updatePower(double power) {
		double kineticPower;
//		if (power < 0 && getVelocity() < 0.00001 && !isNegativeVelocityAllowed() ) {
//			kineticPower = 
//					0;
//		}
		if (getVelocity() >=0 ) {
			kineticPower = 
					power - 
					getPowerLostToAerodynamicDrag() -
					getPowerLostToRollingResistance() - 
					getPowerToIncreasePotentialEnergy() -
					getPowerLostToWheelBearings();
		} else { 
			kineticPower = 
					power + 
					getPowerLostToAerodynamicDrag() +
					getPowerLostToRollingResistance() + 
					getPowerToIncreasePotentialEnergy() +
					getPowerLostToWheelBearings();
		}
		double timestamp = getTimestamp();
		synchronized(this) {
			kineticEnergy.add(timestamp, kineticPower);
		}
		
		double velocity = getVelocity();
		if (!isNegativeVelocityAllowed() && velocity < 0) {
			velocity = 0.;
			reset();
		}
		return velocity;
	}
	
	public void reset() {
		synchronized(this) {
			kineticEnergy =  new TrapezoidIntegrator();
		}
	}


protected double getTimestamp() {
	return System.nanoTime() / Math.pow(10, 9);
}
	
	public static void main(String [] args) throws InterruptedException {
		PowerModel pm = new PowerModel() {
			double timestamp = System.nanoTime() / Math.pow(10, 9);
			protected double getTimestamp() {
				return timestamp += 1;
			}
		};
		pm.setVelocity(20.0);
		double ACCURACY = 0.00000000001;
		assert pm.getVelocity() < 20 + ACCURACY && pm.getVelocity() > 20 - ACCURACY : "setVelocity is bust";
		System.out.println("initial velocity = " + pm.getVelocity());
		pm.setNegativeVelocityAllowed(false);
		for (int i = 0 ; i< 1000 ; i++) {
			System.out.println(pm.updatePower(400));
		}
		for (int i = 0 ; i< 10000 ; i++) {
			System.out.println(pm.updatePower(0));
		}

	}
	
	
}
