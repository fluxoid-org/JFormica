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

import org.cowboycoders.turbotrainers.DataPacketProvider;
import org.cowboycoders.turbotrainers.Parameters.CommonParametersInterface;
import org.cowboycoders.turbotrainers.TurboBaseModel;
import org.cowboycoders.turbotrainers.bushido.BushidoUtils;
import org.cowboycoders.utils.LoopingListIterator;

/**
 * Model for current settings
 * 
 * @author will
 */
public abstract class AbstractBushidoModel extends TurboBaseModel {
	
	private List<DataPacketProvider> packetProviders = new ArrayList<DataPacketProvider>();
	private Iterator<DataPacketProvider> packetProvidersIterator = new LoopingListIterator<DataPacketProvider>(
			packetProviders);


	/**
	 * Provides a data packet containing current slope
	 */
	protected final DataPacketProvider keepAliveProvider = new DataPacketProvider() {

		@Override
		public byte[] getDataPacket() {
			return keepAlive();
		}

	};

	protected void addPacketProvider(DataPacketProvider provider) {
		packetProviders.add(provider);
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