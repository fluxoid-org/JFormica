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
 * Model for current settings
 * 
 * @author will
 */
public class BushidoTargetSlopeModel extends AbstractBushidoModel {
	
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
	private double slope;


	{	
		addPacketProvider(slopeProvider);
		addPacketProvider(keepAliveProvider);
	}


	private byte[] injectSlope(byte[] dc01Packet) {
		double slope = getSlope();
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
	

	@Override
	public void setParameters(CommonParametersInterface parameters)
			throws IllegalArgumentException {
		Parameters.TargetSlope castParameters = null;
		try {
			castParameters = (Parameters.TargetSlope) parameters;
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("Expecting target slope",e);
		}
		setWeight(castParameters.getTotalWeight());
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
		if (slope > 20.0) {
			return 20.0;
		}
		if (slope < -5.0) {
			return -5.0;
		}
		return slope;
	}





}