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
import org.cowboycoders.turbotrainers.DataPacketProvider;
import org.cowboycoders.turbotrainers.Parameters;
import org.cowboycoders.turbotrainers.Parameters.CommonParametersInterface;
import org.cowboycoders.turbotrainers.bushido.BushidoUtils;


/**
 * Provides data packets for setting the slope and weight on the Bushido Head unit
 * 
 * Slope and weight are sent to the Bushido head unit via the PC interface in the
 * same data packet. This data packet starts with DC01.
 * 
 * @author will
 */
public class BushidoTargetSlopeModel extends AbstractBushidoModel {
	
	//The slope is set in the following bytes of the DC01 packet:
	private static final int SLOPE_BYTE_1 = 3;
	private static final int SLOPE_BYTE_2 = 4;
	//The slope is sent in the format percentage * 10
	private static final int SLOPE_MULTIPLIER = 10;
	// The slope is bounded within this range:
	private static final double SLOPE_MAX = 20.0;
	private static final double SLOPE_MIN = -5.0;
	//The weight is set in this byte of the DC01 packet
	private static final int WEIGHT_BYTE = 5;
	
	/**
	 * Provides a data packet into which the current slope and weight are injected
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
	
	private double slope;
	
	// Packets are to be sent in this sequence
	{	
		addPacketProvider(slopeProvider);
		addPacketProvider(keepAliveProvider);
	}

	// Set slope bits in the packet
	private byte[] injectSlope(byte[] dc01Packet) {
		double slope = getSlope();
		if (slope < 0) {
			dc01Packet[SLOPE_BYTE_1] = (byte) 0xFF;
			dc01Packet[SLOPE_BYTE_2] = (byte) (256 + slope * SLOPE_MULTIPLIER);
		} else {
			dc01Packet[SLOPE_BYTE_2] = (byte) (slope * SLOPE_MULTIPLIER);
		}

		return dc01Packet;
	}
	
	// Sets weight bits in a packet
	private byte[] injectWeight(byte[] dc01Packet) {
		dc01Packet[WEIGHT_BYTE] = (byte) getTotalWeight();
		return dc01Packet;
	}
	

	@Override
	public void setParameters(CommonParametersInterface parameters)
			throws IllegalArgumentException {
		Parameters.TargetSlope castParameters = null;
		try {
			castParameters = (Parameters.TargetSlope) parameters;
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("Expecting target slope",e);
		}
		setTotalWeight(castParameters.getTotalWeight());
		setSlope(castParameters.getSlope());
		
	}
	
	public double getTarget() {
		return getSlope();
	}

	public double getSlope() {
		return slope;
	}

	public void setSlope(double slope) {
		slope = getBoundedSlope(slope);
		this.slope = slope;
	}

	private double getBoundedSlope(double slope) {
		if (slope > SLOPE_MAX) {
			return SLOPE_MAX;
		}
		if (slope < -SLOPE_MIN) {
			return -SLOPE_MIN;
		}
		return slope;
	}

}
