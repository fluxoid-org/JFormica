package org.cowboycoders.turbotrainers;

public class Parameters {
	
	private Parameters() {};
	
	public static interface CommonParametersInterface {
		
		/**
		 * rider weight + bike weight
		 * @return Total weight in kg
		 */
		public abstract double getTotalWeight();
		
		/**
		 * @return rider weight in kg
		 */
		public abstract double getRiderWeight();
		
		/**
		 * @return bike weight in kg
		 */
		public abstract double getBikeWeight();

		/**
		 * @return wind speed in m/
		 */
		public abstract double getWindSpeed();
		
		/**
		 * @return current bearing in degrees
		 */
		public abstract double getCurrentBearing();
		
		/**
		 * Wind direction: reported by the direction from which it originates
		 * @return wind direction
		 */
		public abstract double getWindDirectionDegrees();
		
		/**
		 * Air density in kg·m−3
		 * @return air density
		 */
		public abstract double getAirDensity();
		
		/**
		 * @return incremental drag area of spokes
		 */
		public abstract double getIncrementalDragAreaSpokes();
		
		/**
		 * Coefficient of drag (Cd) x frontal area (A) units: m^2
		 * @return drag area
		 */
		public abstract double getDragArea();
		
		/**
		 * @return coefficient of rolling resistance
		 */
		public abstract double getCoefficentRollingResistance();
		
		/**
		 * moment of inertia in kg m^2
		 * @return moment of inertia
		 */
		public abstract double getMomentOfInertiaWheels();
		
		/**
		 * outside tire radius in m
		 * @return soutside tire radius
		 */
		public abstract double getOutsideRadiusTire();

	}
	
	protected static class Common implements CommonParametersInterface {
		
		private double riderWeight;
		private double bikeWeight;
		
		private double windSpeed; 
		private double currentBearing; 
		private double windDirectionDegrees; 
		private double airDensity; 
		private double incrementalDragAreaSpokes; 
		private double dragArea;  
		private double coefficentRollingResistance; 
		private double momentOfInertiaWheels; 
		private double outsideRadiusTire;
		
		private Common(double riderWeight, double bikeWeight,
				double windSpeed, double currentBearing,
				double windDirectionDegrees, double airDensity,
				double incrementalDragAreaSpokes, double dragArea,
				double coefficentRollingResistance, double momentOfInertiaWheels,
				double outsideRadiusTire) {
			super();
			this.riderWeight = riderWeight;
			this.bikeWeight = bikeWeight;
			this.windSpeed = windSpeed;
			this.currentBearing = currentBearing;
			this.windDirectionDegrees = windDirectionDegrees;
			this.airDensity = airDensity;
			this.incrementalDragAreaSpokes = incrementalDragAreaSpokes;
			this.dragArea = dragArea;
			this.coefficentRollingResistance = coefficentRollingResistance;
			this.momentOfInertiaWheels = momentOfInertiaWheels;
			this.outsideRadiusTire = outsideRadiusTire;
		}
		
		/* (non-Javadoc)
		 * @see org.cowboycoders.turbotrainers.CommonInterface#getTotalWeight()
		 */
		@Override
		public double getTotalWeight() {
			return riderWeight + bikeWeight;
		}
		
		/* (non-Javadoc)
		 * @see org.cowboycoders.turbotrainers.CommonInterface#getRiderWeight()
		 */
		@Override
		public double getRiderWeight() {
			return riderWeight;
		}

		/* (non-Javadoc)
		 * @see org.cowboycoders.turbotrainers.CommonInterface#getBikeWeight()
		 */
		@Override
		public double getBikeWeight() {
			return bikeWeight;
		}

		/* (non-Javadoc)
		 * @see org.cowboycoders.turbotrainers.CommonInterface#getWindSpeed()
		 */
		@Override
		public double getWindSpeed() {
			return windSpeed;
		}

		/* (non-Javadoc)
		 * @see org.cowboycoders.turbotrainers.CommonInterface#getCurrentBearing()
		 */
		@Override
		public double getCurrentBearing() {
			return currentBearing;
		}

		/* (non-Javadoc)
		 * @see org.cowboycoders.turbotrainers.CommonInterface#getWindDirectionDegrees()
		 */
		@Override
		public double getWindDirectionDegrees() {
			return windDirectionDegrees;
		}

		/* (non-Javadoc)
		 * @see org.cowboycoders.turbotrainers.CommonInterface#getAirDensity()
		 */
		@Override
		public double getAirDensity() {
			return airDensity;
		}

		/* (non-Javadoc)
		 * @see org.cowboycoders.turbotrainers.CommonInterface#getIncrementalDragAreaSpokes()
		 */
		@Override
		public double getIncrementalDragAreaSpokes() {
			return incrementalDragAreaSpokes;
		}

		/* (non-Javadoc)
		 * @see org.cowboycoders.turbotrainers.CommonInterface#getDragArea()
		 */
		@Override
		public double getDragArea() {
			return dragArea;
		}


		/* (non-Javadoc)
		 * @see org.cowboycoders.turbotrainers.CommonInterface#getCoefficentRollingResistance()
		 */
		@Override
		public double getCoefficentRollingResistance() {
			return coefficentRollingResistance;
		}

		/* (non-Javadoc)
		 * @see org.cowboycoders.turbotrainers.CommonInterface#getMomentOfInertiaWheels()
		 */
		@Override
		public double getMomentOfInertiaWheels() {
			return momentOfInertiaWheels;
		}

		/* (non-Javadoc)
		 * @see org.cowboycoders.turbotrainers.CommonInterface#getOutsideRadiusTire()
		 */
		@Override
		public double getOutsideRadiusTire() {
			return outsideRadiusTire;
		}
		
	}
	
	protected static class CommonDelegate  implements CommonParametersInterface {
		
		private Common common;

		CommonDelegate(Common common) {
			this.common = common;
		}

		@Override
		public double getTotalWeight() {
			return common.getTotalWeight();
		}

		@Override
		public double getRiderWeight() {
			return common.getRiderWeight();
		}

		@Override
		public double getBikeWeight() {
			return common.getBikeWeight();
		}

		@Override
		public double getWindSpeed() {
			return common.getWindSpeed();
		}

		@Override
		public double getCurrentBearing() {
			return common.getCurrentBearing();
		}

		@Override
		public double getWindDirectionDegrees() {
			return common.getWindDirectionDegrees();
		}

		@Override
		public double getAirDensity() {
			return common.getAirDensity();
		}

		@Override
		public double getIncrementalDragAreaSpokes() {
			return common.getIncrementalDragAreaSpokes();
		}

		@Override
		public double getDragArea() {
			return common.getDragArea();
		}

		@Override
		public double getCoefficentRollingResistance() {
			return common.getCoefficentRollingResistance();
		}

		@Override
		public double getMomentOfInertiaWheels() {
			return common.getMomentOfInertiaWheels();
		}

		@Override
		public double getOutsideRadiusTire() {
			return common.getOutsideRadiusTire();
		}
		
	}
	
	/*
	 * The following classes allow type checking against current mode. To prevent bugs relating
	 * to someone not setting correct value for current mode
	 */
	
	
	public static class TargetSlope extends CommonDelegate {
		
		private TargetSlope(Common common, double slope) {
			super(common);
			this.slope = slope;
		}
		
		private double slope;
		

		public double getSlope() {
			return slope;
		}
			
	}
	
	public static class TargetPower extends CommonDelegate {
		
		private TargetPower(Common common, double power) {
			super(common);
			this.power = power;
		}
		
		private double power;
		

		public double getPower() {
			return power;
		}
		
		
		
	}
	
	/**
	 * Builder turbo trainer parameter objects
	 * @author will
	 *
	 */
	public static class Builder {
		
		private double windSpeed = 0;//2.94;
		private double currentBearing = 340;
		private double windDirectionDegrees = 160; // direction : 180 is south
		private double airDensity = 1.2234; // kg·m−3
		private double incrementalDragAreaSpokes = 0.0044;
		private double dragArea = 0.255 ;//57; //Coefficient of drag x frontal area
		private double coefficentRollingResistance = 0.0032;
		private double momentOfInertiaWheels = 0.14; //kg m^2 for bike
		private double outsideRadiusTire = 0.311;

		private final double bikeWeight; //kg

		private final double riderWeight; //kg

		
		/**
		 * Builder with constant weight
		 * @param weight in kg
		 */
		public Builder(double riderWeight, double bikeWeight) {
			this.riderWeight = riderWeight;
			this.bikeWeight = bikeWeight;
		}
		
		
		public Builder setWindSpeed(double windSpeed) {
			this.windSpeed = windSpeed;
			return this;
		}


		public Builder setCurrentBearing(double currentBearing) {
			this.currentBearing = currentBearing;
			return this;
		}


		public Builder setWindDirectionDegrees(double windDirectionDegrees) {
			this.windDirectionDegrees = windDirectionDegrees;
			return this;
		}


		public Builder setAirDensity(double airDensity) {
			this.airDensity = airDensity;
			return this;
		}


		public Builder setIncrementalDragAreaSpokes(double incrementalDragAreaSpokes) {
			this.incrementalDragAreaSpokes = incrementalDragAreaSpokes;
			return this;
		}


		public Builder setDragArea(double dragArea) {
			this.dragArea = dragArea;
			return this;
		}


		public Builder setCoefficentRollingResistance(double coefficentRollingResistance) {
			this.coefficentRollingResistance = coefficentRollingResistance;
			return this;
		}


		public Builder setMomentOfInertiaWheels(double momentOfInertiaWheels) {
			this.momentOfInertiaWheels = momentOfInertiaWheels;
			return this;
		}


		public Builder setOutsideRadiusTire(double outsideRadiusTire) {
			this.outsideRadiusTire = outsideRadiusTire;
			return this;
		}


		private Common build() {
			Common parameters = new Common (riderWeight,  bikeWeight,
					 windSpeed,  currentBearing,
					 windDirectionDegrees,  airDensity,
					 incrementalDragAreaSpokes,  dragArea,
					 coefficentRollingResistance,  momentOfInertiaWheels,
					 outsideRadiusTire);
			return parameters;
		}
		
		
		/*
		 * build...() deliberately make the user provide new value each time.
		 * The alternative is to extend and use covariant returns but then have 
		 * to main more classes
		 */
		
		/**
		 * @param power new target
		 * @return parameters
		 */
		public TargetPower buildTargetPower(double power) {
			return new TargetPower(build(),power);
		}
		
		public TargetSlope buildTargetSlope(double slope) {
			return new TargetSlope(build(),slope);
		}
		
		
	}
	
	public static void main(String [] args) {
		Builder builder = new Builder(70,7);
		System.out.println(builder.setAirDensity(1).setDragArea(2).buildTargetPower(100).getAirDensity());
	}
	

}
