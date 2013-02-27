/*
 *    Copyright (c) 2013, Will Szumski
 *    Copyright (c) 2013, Doug Szumski
 *
 *    This file is part of Cyclismo.
 *
 *    Cyclismo is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Cyclismo is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Cyclismo.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cowboycoders.turbotrainers.bushido.headunit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.cowboycoders.turbotrainers.DataPacketProvider;
import org.cowboycoders.turbotrainers.bushido.BushidoUtils;
import org.cowboycoders.utils.Constants;
import org.cowboycoders.utils.LoopingListIterator;
import org.cowboycoders.utils.TrapezoidIntegrator;

/**
 * Model for current settings
 * 
 * @author will
 */
public class BushidoData {
	
	public static Logger LOGGER = Logger.getLogger(BushidoData.class.getSimpleName());
	public static byte WEIGHT_DEFAULT = 70; //kg
	public static byte WEIGHT_LOW_LIMIT = 40;

	private double speed;
	private double cadence;
	private double distance;
	private double heartRate;
	private double slope;
	private double power;
	private double weight = WEIGHT_DEFAULT;

	// in m
	TrapezoidIntegrator integralSpeed = new TrapezoidIntegrator();

	private List<DataPacketProvider> packetProviders = new ArrayList<DataPacketProvider>();
	private Iterator<DataPacketProvider> packetProvidersIterator = new LoopingListIterator<DataPacketProvider>(
			packetProviders);

	/**
	 * Provides a data packet containing current slope
	 */
	DataPacketProvider slopeProvider = new DataPacketProvider() {

		@Override
		public byte[] getDataPacket() {
			byte[] dc01Packet = BushidoUtils.getDc01Prototype();
			dc01Packet = injectSlope(dc01Packet);
			dc01Packet = injectWeight(dc01Packet);
			return dc01Packet;
		}

	};

	/**
	 * Provides a data packet containing current slope
	 */
	DataPacketProvider keepAliveProvider = new DataPacketProvider() {

		@Override
		public byte[] getDataPacket() {
			return keepAlive();
		}

	};

	{
		// TODO : SWITCH ON BUSHIDO MODE
		packetProviders.add(slopeProvider);
		packetProviders.add(keepAliveProvider);
	}

	/**
	 * @return the heartRate
	 */
	public double getHeartRate() {
		return heartRate;
	}

	/**
	 * @param heartRate
	 *            the heartRate to set
	 */
	public void setHeartRate(double heartRate) {
		this.heartRate = heartRate;
	}

	/**
	 * @return the speed
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * @return the cadence
	 */
	public double getCadence() {
		return cadence;
	}

	/**
	 * @return the power
	 */
	public double getPower() {
		return power;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
		double timeStampSeconds = System.nanoTime() / (Math.pow(10, 9));
		// this is losing precision
		// double timeStampSeconds =
		// TimeUnit.NANOSECONDS.toSeconds(System.nanoTime());
		double speedMetresPerSecond = 1000 * speed / (60 * 60);
		integralSpeed.add(timeStampSeconds, speedMetresPerSecond);
	}

	public void setPower(double power) {
		this.power = power;
	}

	public void setCadence(double cadence) {
		this.cadence = cadence;
	}

	/**
	 * true distance (travelled by wheel) as opposed to gradient compensated
	 * speed
	 * 
	 * @param distance
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}

	public void setHearRate(double heartRate) {
		this.heartRate = heartRate;
	}

	public double getSlope() {
		return slope;
	}

	public double getCompensatedDistance() {
		return integralSpeed.getIntegral();
	}

	public double getDistance() {
		return distance;
	}

	public void setSlope(double slope) {
		slope = getBoundedSlope(slope);
		this.slope = slope;
	}
	
	/**
	 * Bike + Rider 
	 * @return current weight in kilos
	 */
	public double getWeight() {
		return weight;
	}
	/**
	 * Bike + Rider weight
	 * @param weight in kilos
	 */
	public void setWeight(double weight) {
		if (weight > Constants.UNSIGNED_BYTE_MAX_VALUE) {
			weight = Constants.UNSIGNED_BYTE_MAX_VALUE;
			LOGGER.warning("Rider is too heavy, using :" + Constants.UNSIGNED_BYTE_MAX_VALUE);
		} 
		else if (weight < 0) {
			weight = WEIGHT_DEFAULT;
			LOGGER.warning("Negative rider weight, using: " + WEIGHT_DEFAULT);
		}
		else if (weight < WEIGHT_LOW_LIMIT) {
			LOGGER.warning("Rider weight low, is this correct? Weight : " + weight);
		}
		this.weight = weight;
	}

	private byte[] injectSlope(byte[] dc01Packet) {
		if (slope < 0) {
			dc01Packet[3] = (byte) 0xFF;
			dc01Packet[4] = (byte) (256 + slope * 10);
		} else {
			dc01Packet[4] = (byte) (slope * 10);
		}

		return dc01Packet;
	}
	
	private byte[] injectWeight(byte[] dc01Packet) {
		dc01Packet[5] = (byte) getWeight();
		return dc01Packet;
	}

	public void incrementSlope(double value) {
		double currentSlope = getSlope();
		setSlope(currentSlope + value);
	}

	public void incrementSlope() {
		incrementSlope(0.1);
	}

	public void decrementSlope(double value) {
		double currentSlope = getSlope();
		setSlope(currentSlope - value);
	}

	public void decrementSlope() {
		decrementSlope(0.1);
	}

	private double getBoundedSlope(double slope) {
		if (slope > 20.0) {
			return 20.0;
		}
		if (slope < -5.0) {
			return -5.0;
		}
		return slope;
	}

	private byte[] keepAlive() {
		byte[] dc02Packet = BushidoUtils.getDc02Prototype();
		return dc02Packet;
	}

	/**
	 * Send data packets in order specified by packetProvders
	 * 
	 * @return
	 */
	public byte[] getDataPacket() {
		return packetProvidersIterator.next().getDataPacket();
	}

}