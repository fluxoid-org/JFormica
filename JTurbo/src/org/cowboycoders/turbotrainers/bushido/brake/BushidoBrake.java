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
package org.cowboycoders.turbotrainers.bushido.brake;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.cowboycoders.ant.Channel;
import org.cowboycoders.ant.NetworkKey;
import org.cowboycoders.ant.Node;
import org.cowboycoders.ant.events.BroadcastListener;
import org.cowboycoders.ant.events.MessageCondition;
import org.cowboycoders.ant.messages.ChannelMessage;
import org.cowboycoders.ant.messages.SlaveChannelType;
import org.cowboycoders.ant.messages.StandardMessage;
import org.cowboycoders.ant.messages.data.BroadcastDataMessage;
import org.cowboycoders.ant.utils.AntUtils;
import org.cowboycoders.ant.utils.ChannelMessageSender;
import org.cowboycoders.ant.utils.EnqueuedMessageSender;
import org.cowboycoders.pid.PidParameterController;
import org.cowboycoders.turbotrainers.AntTurboTrainer;
import org.cowboycoders.turbotrainers.Parameters.CommonParametersInterface;
import org.cowboycoders.turbotrainers.Mode;
import org.cowboycoders.turbotrainers.PowerModelManipulator;
import org.cowboycoders.turbotrainers.TooFewAntChannelsAvailableException;
import org.cowboycoders.turbotrainers.TurboTrainerDataListener;
import org.cowboycoders.utils.IterationOperator;
import org.cowboycoders.utils.IterationUtils;

public class BushidoBrake extends AntTurboTrainer {
	
	  public static final Mode [] SUPPORTED_MODES = new Mode [] {
		  	Mode.TARGET_SLOPE
		  };
	  
	  {	
		  //switch on controller type (different controllers support diff modes)
		  setSupportedModes(SUPPORTED_MODES);
	  }

	public final static Logger LOGGER = Logger
			.getLogger(BushidoBrake.class.getName());
	
	public static final byte[] PACKET_REQUEST_VERSION = AntUtils.padToDataLength(new int [] {(byte) 0xAc, 0x02});

	private ArrayList<BroadcastListener<? extends ChannelMessage>> listeners = new ArrayList<BroadcastListener<? extends ChannelMessage>>();

	private Node node;
	private NetworkKey key;
	private Channel channel;
	private EnqueuedMessageSender channelMessageSender;
	private BrakeModel model;

	private Lock requestDataLock = new ReentrantLock();
	private boolean requestDataInProgess = false;
	private AbstractController resistanceController; 

	private Runnable requestDataCallback = new Runnable() {

		@Override
		public void run() {
			try {
				requestDataLock.lock();
				requestDataInProgess = false;
			} finally {
				requestDataLock.unlock();
			}

		}

	};

	public class BushidoUpdatesListener implements BushidoBrakeInternalListener {

		private BrakeModel model;

		/**
		 * Only route responses through this member
		 */
		private ChannelMessageSender channelSender;

		public BushidoUpdatesListener(BrakeModel model,
				ChannelMessageSender channelSender) {
			this.model = model;
			this.channelSender = channelSender;
		}

		@Override
		public void onRequestData(Byte[] data) {

			// We don't want thread's queueing up waiting to be serviced.
			// Subject to a race but we will respond to next request.
			try {
				requestDataLock.lock();
				if (requestDataInProgess)
					return;
				requestDataInProgess = true;
			} finally {
				requestDataLock.unlock();
			}

			byte[] bytes = null;
			synchronized (model) {
				bytes = model.getDataPacket();
			}

			channelSender.sendMessage(AntUtils.buildBroadcastMessage(bytes),
					requestDataCallback);
		}

		@Override
		public void onSpeedChange(final double speed) {
			synchronized (model) {
				model.setActualSpeed(speed);
			}
			synchronized (dataChangeListeners) {
				IterationUtils.operateOnAll(dataChangeListeners,
						new IterationOperator<TurboTrainerDataListener>() {
							@Override
							public void performOperation(
									TurboTrainerDataListener dcl) {
								dcl.onSpeedChange(speed);
							}

						});
			}

			// We are integrating for distance. As onDistanceChange() doesn't
			// receive values directly
			// manually update with new value obtained through integration
			synchronized (model) {
				this.onDistanceChange(model.getActualDistance(true));
			}

		}

		@Override
		public void onPowerChange(final double power) {
			synchronized (model) {
				model.setPower(power);
			}
			synchronized (dataChangeListeners) {
				IterationUtils.operateOnAll(dataChangeListeners,
						new IterationOperator<TurboTrainerDataListener>() {
							@Override
							public void performOperation(
									TurboTrainerDataListener dcl) {
								dcl.onPowerChange(power);
							}

						});
			}

		}

		@Override
		public void onCadenceChange(final double cadence) {
			synchronized (model) {
				model.setCadence(cadence);
			}
			synchronized (dataChangeListeners) {
				IterationUtils.operateOnAll(dataChangeListeners,
						new IterationOperator<TurboTrainerDataListener>() {
							@Override
							public void performOperation(
									TurboTrainerDataListener dcl) {
								dcl.onCadenceChange(cadence);
							}

						});
			}
		}

		@Override
		public void onDistanceChange(final double distance) {

			synchronized (dataChangeListeners) {
				IterationUtils.operateOnAll(dataChangeListeners,
						new IterationOperator<TurboTrainerDataListener>() {
							@Override
							public void performOperation(
									TurboTrainerDataListener dcl) {
								synchronized (model) {
									dcl.onDistanceChange(distance);
								}
							}

						});
			}
		}

		@Override
		public void onChangeCounter(int counter) {
			synchronized (model) {
				model.setCounter(counter);
			}
		}

		@Override
		public void onChangeLeftPower(double power) {
			synchronized (model) {
				model.setPowerLeft(power);
			}

		}

		@Override
		public void onChangeRightPower(double power) {
			synchronized (model) {
				model.setPowerRight(power);
			}
		}

		@Override
		public void onReceiveSoftwareVersion(String version) {
			System.out.println("bushido soft version:" + version);

		}

		@Override
		public void onChangeBrakeTemperature(double temp) {
			synchronized (model) {
				model.setBrakeTemperature(temp);
			}

		}

		@Override
		public void onChangeBalance(int balance) {
			synchronized (model) {
				model.setPowerBalance(balance);
			}

		}

		@Override
		public void onHeartRateChange(double heartRate) {
			// not recieved

		}

	};



	public BushidoBrake(Node node, AbstractController controller) {
		super(node);
		this.resistanceController = controller;
		this.node = node;
		this.key = new NetworkKey(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00);
		key.setName("N:PUBLIC");
	}

	/**
	 * Call after start.
	 * 
	 * @param listener
	 * @param clazz
	 */
	private <V extends ChannelMessage> void registerChannelRxListener(
			BroadcastListener<V> listener, Class<V> clazz) {
		synchronized (listeners) {
			listeners.add(listener);
			channel.registerRxListener(listener, clazz);
		}
	}

	private <V extends ChannelMessage> void unregisterRxListener(
			BroadcastListener<? extends ChannelMessage> listener) {
		channel.removeRxListener(listener);
	}

	public void start() throws InterruptedException, TimeoutException {
		startConnection();
		// resetOdometer();
		// startCycling();
	}

	public void startConnection() throws InterruptedException, TimeoutException {
		node.start();
		node.setNetworkKey(0, key);
		channel = node.getFreeChannel();

		if (channel == null) {
			throw new TooFewAntChannelsAvailableException();
		}
		channel.setName("C:BUSHIDO_BRAKE");
		SlaveChannelType channelType = new SlaveChannelType();
		channel.assign("N:PUBLIC", channelType);

		channel.setId(0, 0x51, 0, false);

		channel.setFrequency(60);

		channel.setPeriod(4096);

		channel.setSearchTimeout(Channel.SEARCH_TIMEOUT_NEVER);

		channel.open();

		channelMessageSender = new EnqueuedMessageSender(channel);

		// initConnection();

		// startCycling();

		if (getCurrentMode() == Mode.TARGET_SLOPE) {
			this.model = new TargetSlopeModel();
		}
		
		
		BushidoUpdatesListener updatesListener = new BushidoUpdatesListener(
				model, this.getMessageSender());
		BushidoBrakeBroadcastDataListener dataListener = new BushidoBrakeBroadcastDataListener(
				updatesListener);
		this.registerChannelRxListener(dataListener, BroadcastDataMessage.class);
		
		resistanceController.start(model);
		
		this.registerDataListener(resistanceController);
	}

	public String requestVersion() {
		try {
			getMessageSender().pause(true);
			BroadcastDataMessage msg = new BroadcastDataMessage();
			msg.setData(BushidoBrake.PACKET_REQUEST_VERSION);
			channel.sendAndWaitForMessage(msg, AntUtils.CONDITION_CHANNEL_TX, 10L,
					TimeUnit.SECONDS, null);

		} catch (InterruptedException e) {
			LOGGER.warning("interrupted waiting for version");

		} catch (TimeoutException e) {
			LOGGER.warning("timeout waiting for version");
		} finally {
			getMessageSender().pause(false);
		}

		return null;
	}

	// public void startCycling() throws InterruptedException, TimeoutException
	// {
	// // not needed
	// }
	//
	// private void initConnection() throws InterruptedException,
	// TimeoutException {
	// // not needed
	//
	// }
	//
	// private void disconnect() throws InterruptedException, TimeoutException{
	// // not needed
	// }

	private StandardMessage sendAndRetry(final ChannelMessage msg,
			final MessageCondition condition, final int maxRetries,
			final long timeoutPerRetry, final TimeUnit timeoutUnit)
			throws InterruptedException, TimeoutException {
		return AntUtils.sendAndRetry(channel, msg, condition, maxRetries,
				timeoutPerRetry, timeoutUnit);
	}

	public EnqueuedMessageSender getMessageSender() {
		return channelMessageSender;
	}

	public void stop() throws InterruptedException, TimeoutException {

		// make sure no-one sends and unpause which will cancel our stop request
		synchronized (listeners) {
			for (BroadcastListener<? extends ChannelMessage> listener : listeners) {
				unregisterRxListener(listener);
			}
		}
		
		this.unregisterDataListener(resistanceController);
		resistanceController.stop();
		
		// disconnect();
		channel.close();
		channel.unassign();
		node.freeChannel(channel);
		// let external controiller stop node
		// node.stop();
	}

	@Override
	public boolean supportsSpeed() {
		return true;
	}

	@Override
	public boolean supportsPower() {
		return true;
	}

	@Override
	public boolean supportsCadence() {
		return true;
	}

	@Override
	public boolean supportsHeartRate() {
		return false;
	}
	

	@Override
	public void setParameters(CommonParametersInterface parameters)
			throws IllegalArgumentException {
		synchronized (model) {
			model.setParameters(parameters);
		}
		
	}

	@Override
	public double getTarget() {
		synchronized (model) {
			return model.getTarget();
		}
	}

}
