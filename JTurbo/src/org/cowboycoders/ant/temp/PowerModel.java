package org.cowboycoders.ant.temp;

import org.cowboycoders.utils.Conversions;
import org.cowboycoders.utils.TrapezoidIntegrator;

public class PowerModel {
	
	public static final double GRAVITATIONAL_ACCELERATION = 9.81;
	double windSpeed = 0;//2.94;
	double currentBearing = 340;
	double windDirectionDegrees = 160; // direction : 180 is south
	double airDensity = 1.2234; // kg·m−3
	double incrementalDragAreaSpokes = 0.0044;
	double dragArea = 0.255 ;//57; //Coefficient of drag x frontal area
	double gradientAsPercentage = 00.0;// 0.3;
	double coefficentRollingResistance = 0.0032;
	double totalMass = 60.0;
	//double kineticEnergy = 0.;
	double momentOfInertiaWheels = 0.14; //kg m^2 for bike
	double outsideRadiusTire = 0.311;
	
	private boolean negativeVelocityAllowed = false;
	
	/**
	 * Should we roll backwards down hills or "put our feet down"
	 */
	public boolean isNegativeVelocityAllowed() {
		return negativeVelocityAllowed;
	}


	/**
	 * Should we roll backwards down hills or "put our feet down"
	 */
	public void setNegativeVelocityAllowed(boolean negativeVelocityAllowed) {
		this.negativeVelocityAllowed = negativeVelocityAllowed;
	}



	protected double getWindSpeed() {
		return windSpeed;
	}



	protected void setWindSpeed(double windSpeed) {
		this.windSpeed = windSpeed;
	}



	protected double getCurrentBearing() {
		return currentBearing % 360;
	}



	protected void setCurrentBearing(double currentBearing) {
		this.currentBearing = currentBearing;
	}



	protected double getWindDirectionDegrees() {
		return windDirectionDegrees % 360;
	}



	protected void setWindDirectionDegrees(double windDirectionDegrees) {
		this.windDirectionDegrees = windDirectionDegrees;
	}



	protected double getAirDensity() {
		return airDensity;
	}



	protected void setAirDensity(double airDensity) {
		this.airDensity = airDensity;
	}



	protected double getIncrementalDragAreaSpokes() {
		return incrementalDragAreaSpokes;
	}



	protected void setIncrementalDragAreaSpokes(double incrementalDragAreaSpokes) {
		this.incrementalDragAreaSpokes = incrementalDragAreaSpokes;
	}



	protected double getDragArea() {
		return dragArea;
	}



	protected void setDragArea(double dragArea) {
		this.dragArea = dragArea;
	}



	protected double getGradientAsPercentage() {
		return gradientAsPercentage;
	}



	protected void setGradientAsPercentage(double gradientAsPercentage) {
		this.gradientAsPercentage = gradientAsPercentage;
	}



	protected double getCoefficentRollingResistance() {
		return coefficentRollingResistance;
	}



	protected void setCoefficentRollingResistance(double coefficentRollingResistance) {
		this.coefficentRollingResistance = coefficentRollingResistance;
	}



	protected double getTotalMass() {
		return totalMass;
	}



	protected void setTotalMass(double totalMass) {
		this.totalMass = totalMass;
	}



	protected double getKineticEnergy() {
		synchronized (this){
			return kineticEnergy.getIntegral();
		}
	}



	protected double getMomentOfInertiaWheels() {
		return momentOfInertiaWheels;
	}



	protected void setMomentOfInertiaWheels(double momentOfInertiaWheels) {
		this.momentOfInertiaWheels = momentOfInertiaWheels;
	}



	protected double getOutsideRadiusTire() {
		return outsideRadiusTire;
	}



	protected void setOutsideRadiusTire(double outsideRadiusTire) {
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
	
	public double getAirVelocity() {
		return getTangentialWindVelocity() + getVelocity();
	}
	
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
		SimpleSpeedLogger logger = new SimpleSpeedLogger();
		logger.newLog();
		PowerModel pm = new PowerModel() {
			double timestamp = System.nanoTime() / Math.pow(10, 9);
			protected double getTimestamp() {
				return timestamp += 0.5;
			}
		};
		pm.setNegativeVelocityAllowed(false);
		for (int i = 0 ; i< 65 ; i++) {
			logger.onSpeedUpdate(pm.updatePower(400) * Conversions.METRES_PER_SECOND_TO_KM_PER_HOUR);
		}
		for (int i = 0 ; i< 200 ; i++) {
			logger.onSpeedUpdate(pm.updatePower(0)* Conversions.METRES_PER_SECOND_TO_KM_PER_HOUR);
		}

	}
	
	
}
