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
package org.cowboycoders.turbotrainers;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.cowboycoders.ant.Channel;
import org.cowboycoders.ant.Node;
import org.cowboycoders.ant.events.MessageCondition;
import org.cowboycoders.ant.events.MessageConditionFactory;
import org.cowboycoders.ant.messages.ChannelMessage;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.MessageMetaWrapper;
import org.cowboycoders.ant.messages.StandardMessage;
import org.cowboycoders.ant.messages.responses.ResponseCode;
import org.cowboycoders.turbotrainers.bushido.headunit.BushidoHeadunit;

public abstract class AntTurboTrainer implements TurboTrainerInterface {

	public final static Logger LOGGER = Logger.getLogger(AntTurboTrainer.class
			.getName());

	/**
	 * Weak set
	 */
	protected Set<TurboTrainerDataListener> dataChangeListeners = Collections
			.newSetFromMap(new WeakHashMap<TurboTrainerDataListener, Boolean>());

	private Node node;

	public AntTurboTrainer(Node node) {
		this.node = node;
	}

	/**
	 * @return the node
	 */
	public Node getNode() {
		return node;
	}

	public abstract void start() throws TooFewAntChannelsAvailableException,
			TurboCommunicationException, InterruptedException, TimeoutException;

	protected Set<TurboTrainerDataListener> getDataChangeListeners() {
		return dataChangeListeners;
	}

	// dangerous at moment as we are using dataChangeListeners directly
//	protected void setDataChangeListeners(
//			Set<TurboTrainerDataListener> dataChangeListeners) {
//		this.dataChangeListeners = dataChangeListeners;
//	}

	@Override
	public void unregisterDataListener(TurboTrainerDataListener listener) {
		synchronized (dataChangeListeners) {
			dataChangeListeners.remove(listener);
		}
	}

	/**
	 * Stored in weak set, so keep a reference : no anonymous classes
	 */
	public void registerDataListener(TurboTrainerDataListener listener) {
		synchronized (dataChangeListeners) {
			dataChangeListeners.add(listener);
		}
	}

}
