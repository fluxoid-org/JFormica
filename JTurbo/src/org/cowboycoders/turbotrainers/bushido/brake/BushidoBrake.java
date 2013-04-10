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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
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
import org.cowboycoders.ant.temp.BushidoBrakeModel;
import org.cowboycoders.ant.utils.AntUtils;
import org.cowboycoders.ant.utils.ArrayUtils;
import org.cowboycoders.ant.utils.BigIntUtils;
import org.cowboycoders.ant.utils.ChannelMessageSender;
import org.cowboycoders.ant.utils.EnqueuedMessageSender;
import org.cowboycoders.pid.PidParameterController;
import org.cowboycoders.turbotrainers.AntTurboTrainer;
import org.cowboycoders.turbotrainers.Parameters.CommonParametersInterface;
import org.cowboycoders.turbotrainers.Mode;
import org.cowboycoders.turbotrainers.PowerModelManipulator;
import org.cowboycoders.turbotrainers.TooFewAntChannelsAvailableException;
import org.cowboycoders.turbotrainers.TurboTrainerDataListener;
import org.cowboycoders.turbotrainers.bushido.brake.BushidoBrake.CallibrationCallback;
import org.cowboycoders.utils.IterationOperator;
import org.cowboycoders.utils.IterationUtils;

public class BushidoBrake extends AntTurboTrainer {

	public static final Mode[] SUPPORTED_MODES = new Mode[] { Mode.TARGET_SLOPE };

	{
		// switch on controller type (different controllers support diff modes)
		setSupportedModes(SUPPORTED_MODES);
	}

	public final static Logger LOGGER = Logger.getLogger(BushidoBrake.class
			.getName());

	public static final byte[] PACKET_REQUEST_VERSION = AntUtils
			.padToDataLength(new int[] { (byte) 0xAc, 0x02 });
	
	public static final byte[] PACKET_START_CALIBRATION = AntUtils
			.padToDataLength(new int[] { (byte) 0x23, 0x63 });
//	public static final byte[] PACKET_REQUEST_CALIBRATION_STATUS = AntUtils
//			.padToDataLength(new int[] { (byte) 0x23, 0x58 });
	public static final byte[] PACKET_REQUEST_CALIBRATION_VALUE = AntUtils
			.padToDataLength(new int[] { 0x23, 0x4d });
	public static final byte[] PACKET_POST_RESTART_CALIBRATION_RERSUME = 
			AntUtils.padToDataLength(new int[]{0x23,0x58});

	private static final Byte[] PARTIAL_PACKET_CALIBRATION_STATUS = new Byte[] { 0x22 };

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
			channel.sendAndWaitForMessage(msg, AntUtils.CONDITION_CHANNEL_TX,
					10L, TimeUnit.SECONDS, null);

		} catch (InterruptedException e) {
			LOGGER.warning("interrupted waiting for version");

		} catch (TimeoutException e) {
			LOGGER.warning("timeout waiting for version");
		} finally {
			getMessageSender().pause(false);
		}

		return null;
	}

	/**
	 * Callback to facilitate UI hooks
	 * 
	 * @author will
	 * 
	 */
	public static interface CallibrationCallback {
		/**
		 * Called when user is required to speed up to 40 km/h
		 */
		void onRequestStartPedalling();

		/**
		 * Called once user has reached 25 mph (40 kmh). Must instruct user to
		 * stop pedalling until requested.
		 */
		void onReachedCalibrationSpeed();

		/**
		 * Called after onReachedCalibrationSpeed() and once user has slowed
		 * down to zero kmh. User must start pedalling to receive new
		 * calibration value
		 */
		void onRequestResumePedalling();

		/**
		 * Only called on success
		 * 
		 * @param calibrationValue
		 *            new calibration value
		 */
		void onSuccess(double calibrationValue);

		/**
		 * Only called if calibration fails
		 * 
		 * @param exception
		 *            reason for failure, may be caused by:
		 *            InterruptedException, TimeoutException
		 */
		void onFailure(CalibrationException exception);

		void onBelowSpeedReminder(double speed);

	}

	/**
	 * Thrown on calibration failure
	 * 
	 * @author will
	 * 
	 */
	public static class CalibrationException extends Exception {

		private static final long serialVersionUID = -5196590652828662691L;

		private CalibrationException() {
			super();
		}

		private CalibrationException(String message, Throwable cause) {
			super(message, cause);
		}

		private CalibrationException(String message) {
			super(message);
		}

		private CalibrationException(Throwable cause) {
			super(cause);
		}

	}

//	private class StatusPoller extends Thread {
//		public void run() {
//			BroadcastDataMessage msg = new BroadcastDataMessage();
//			msg.setData(BushidoBrake.PACKET_REQUEST_CALIBRATION_STATUS);
//			// while (true) {
//			try {
//				doSend(msg);
//			} catch (InterruptedException e) {
//				LOGGER.info("interrupted polling for calibration status");
//				// break;
//			}
//			if (Thread.interrupted()) {
//			}
//			// break;
//			// }
//		}
//
//		protected void doSend(BroadcastDataMessage msg)
//				throws InterruptedException {
//			try {
//				channel.sendAndWaitForMessage(msg,
//						AntUtils.CONDITION_CHANNEL_TX, 2L, TimeUnit.SECONDS,
//						null);
//			} catch (TimeoutException e) {
//				LOGGER.warning("timeout waiting for version");
//			}
//		}
//	}
	

	/**
	 * Compound class that controls CalibrationState transitions, as well as the "actions" that can be performed
	 * on that state.
	 * @author will
	 *
	 */
	private static abstract class CalibrationStateAction {
		
		private final BushidoBrakeModel.CalibrationState state;
		private final boolean allowNullTransition;
		
		public CalibrationStateAction(BushidoBrakeModel.CalibrationState state, boolean allowNullTransition) {
			this.state = state;
			this.allowNullTransition = allowNullTransition;
		}
		
		public CalibrationStateAction(BushidoBrakeModel.CalibrationState state) {
			this(state, false);
		}
		
		public final BushidoBrakeModel.CalibrationState getMappedState() {
			return state;
		}
		
		public final boolean shouldAllowNullTransition() {
			return allowNullTransition;
		}
		
		public final boolean performTransition(BushidoBrakeModel.CalibrationState newState) throws IllegalStateException {
			if (newState == state) return false;
			if (!shouldAllowNullTransition() && newState == null) throw new IllegalStateException("Null state disallowed");
			return onPerformTransition(newState);
		}
		
		/**
		 * Should perform any necessary operations to make the transition.
		 * @param newState target state
		 * @return false if no transition made, but was expect state. True, otherwise.
		 * @throws IllegalStateException thrown if unexpected state is encountered.
		 */
		public abstract boolean onPerformTransition (
				org.cowboycoders.ant.temp.BushidoBrakeModel.CalibrationState newState) throws IllegalStateException;
		
		/**
		 * State dependent behaviour. Called once when transition to this state is first made.
		 * @param data
		 */
		public abstract void processPacket(Byte[] data);
	}
	

	private class CalibrationController {
		
		
		private class CalibrationStateMachine {
			
			private final Map<BushidoBrakeModel.CalibrationState, CalibrationStateAction> wrappedStates =
					new HashMap<BushidoBrakeModel.CalibrationState, CalibrationStateAction>();
			private BushidoBrakeModel.CalibrationState currentState = null;
			
			{
				addState(BushidoBrakeModel.CalibrationState.BELOW_SPEED, new CalibrationStateAction(BushidoBrakeModel.CalibrationState.BELOW_SPEED) {

					@Override
					public boolean onPerformTransition(
							org.cowboycoders.ant.temp.BushidoBrakeModel.CalibrationState newState)
							throws IllegalStateException {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public void processPacket(Byte[] data) {
						// TODO Auto-generated method stub
						
					}
					
				});
			}
			
			private void addState(BushidoBrakeModel.CalibrationState state, CalibrationStateAction wrappedState )throws IllegalArgumentException {
				if (wrappedState.getMappedState() != state) throw new IllegalArgumentException("wrapped state must match state it is wrapping");
				wrappedStates.put(state, wrappedState);
				
			}
			
			public synchronized final void processPacket(Byte [] data) {
				BushidoBrakeModel.CalibrationState newState = null; 
				//FIXME : add if
				CalibrationStateAction wrappedState = wrappedStates.get(currentState);
				if (wrappedState == null) {
					throw new IllegalStateException("State must be mapped to a wrapped value. State: " + currentState);
				}
				if (wrappedState.performTransition(newState)) {
					currentState = newState;
					wrappedState = wrappedStates.get(newState);
					wrappedState.processPacket(data);
				}
				
			}
			
			
			public synchronized void reset() {
				currentState = null;
			}
		}
		

		private abstract class AntHandler {

			public abstract void doSend() throws InterruptedException,
					TimeoutException;

			public void send() {
				try {
					doSend();
				} catch (InterruptedException e) {
					LOGGER.warning("interrupted waiting for calibration start");
					callback.onFailure(new CalibrationException(e));
					stop();
					return;
				} catch (TimeoutException e) {
					LOGGER.warning("timeout waiting for calibration start");
					callback.onFailure(new CalibrationException(e));
					stop();
					return;
				}
			}
			
			public void sendOnDifferentThread() {
				new Thread() {
					public void run() {
						send();
					}
				}.start();
			}
		}

//		private StatusPoller statusPoller;
		private boolean started = false;
		private long timeout;
		private Long startTimeStamp;
		private Timer timer;
		private CallibrationCallback callback;

		private TimerTask timeOutMonitor;

		private TimerTask stopDetector;

		private BroadcastListener<BroadcastDataMessage> replyListener = new BroadcastListener<BroadcastDataMessage>() {

			private Timer radioSilenceTimer;

			private void newRadioSilenceTimer() {
				radioSilenceTimer = new Timer();
				stopDetector = new TimerTask() {

					@Override
					public void run() {
						callback.onRequestResumePedalling();
					}

				};
				timer.schedule(stopDetector, 1000);
			}

			@Override
			public void receiveMessage(BroadcastDataMessage message) {
				// if radio not quiet start a new timer (we assume quiet when timer successfully elapses)
				synchronized (this) {
					if (radioSilenceTimer != null) {
						stopDetector.cancel();
						radioSilenceTimer.cancel();
						newRadioSilenceTimer();
					}
				}
				Byte[] data = message.getData();
				if (ArrayUtils.arrayStartsWith(
						PARTIAL_PACKET_CALIBRATION_STATUS, data)) {
					BushidoBrakeModel.CalibrationState state = null;
					double calibrationValue = 0;
					if (data[1] == 0x06 && data[3] == 0x02) {
						state = BushidoBrakeModel.CalibrationState.BELOW_SPEED; // non-calibration
																				// mode
					} else if (data[1] == 0x06 && data[3] == 0x05) {
						state = BushidoBrakeModel.CalibrationState.UP_TO_SPEED; // seems
																				// to
																				// only
																				// get
																				// sent
																				// when
																				// slowing
																				// down
																				// (does
																				// it
																				// mean
																				// we
																				// got
																				// up
																				// to
																				// speed?!)
						// callback.onReachedCalibrationSpeed();
					} else if (data[1] == 0x06) {
						state = BushidoBrakeModel.CalibrationState.CALIBRATION_REQUESTED; // calibration
																							// mode
						LOGGER.info("calibration request confirmed");
						BushidoBrake.this.registerDataListener(speedListener);
					} else if (data[1] == 0x03 && data[3] == 0x0c) {
						state = BushidoBrakeModel.CalibrationState.NO_ERROR;
						// we recieve this multiple times, make sure we only
						// start one timer
						synchronized (this) {
							if (radioSilenceTimer == null) {
								newRadioSilenceTimer();
							}
						}
					} else if (data[1] == 0x03 && data[3] == 0x42) {
						state = BushidoBrakeModel.CalibrationState.CALIBRATED;
						BigInteger val = new BigInteger(new byte[] {0x00,data[4],data[5]});
						calibrationValue = val.doubleValue() / 10.0;
						callback.onSuccess(calibrationValue);
						stop();

					} else if (data[1] == 0x03 && data[3] == 0x4d) { // 22:03:00:4d:00:00:00:00
						
						LOGGER.info("Requesting calibration value");

						final BroadcastDataMessage msg = new BroadcastDataMessage();
						msg.setData(BushidoBrake.PACKET_REQUEST_CALIBRATION_VALUE);

						new AntHandler() {

							@Override
							public void doSend() throws InterruptedException,
									TimeoutException {
								channel.sendAndWaitForMessage(msg,
										AntUtils.CONDITION_CHANNEL_TX, 10L,
										TimeUnit.SECONDS, null);

							}

						}.sendOnDifferentThread();
						
						//callback.onFailure(new CalibrationException(
						//		"Error calibrating"));
						//stop();
					} else if (data[1] == 0x03) { // 22:03:00:00:00:00:00:00
						LOGGER.info("calibration: post restart");
						//new StatusPoller().start();
						
						final BroadcastDataMessage msg = new BroadcastDataMessage();
						msg.setData(BushidoBrake.PACKET_POST_RESTART_CALIBRATION_RERSUME);

						new AntHandler() {

							@Override
							public void doSend() throws InterruptedException,
									TimeoutException {
								
								sendAndRetry(msg,
										AntUtils.CONDITION_CHANNEL_TX,5,2L,
										TimeUnit.SECONDS);

							}

						}.sendOnDifferentThread();
						
						
						
					}
					else {
						StringBuilder packet = new StringBuilder();
						Formatter formatter = new Formatter(packet);
						for (byte b : data) {
							formatter.format("%02x:", b);
						}
						LOGGER.warning("unknown status packet: "
								+ packet.toString());
					}
					String stateAsString = state == null ? "null" : state
							.toString();
					LOGGER.info("current state: " + stateAsString);
				}

			}

		};

		TurboTrainerDataListener speedListener = new TurboTrainerDataListener() {

			@Override
			public void onSpeedChange(double speed) {
				if (speed >= 40) {
					callback.onReachedCalibrationSpeed();
				} else {
					callback.onBelowSpeedReminder(speed);
				}

			}

			@Override
			public void onPowerChange(double power) {
				// Not interested

			}

			@Override
			public void onCadenceChange(double cadence) {
				// Not interested

			}

			@Override
			public void onDistanceChange(double distance) {
				// Not interested

			}

			@Override
			public void onHeartRateChange(double heartRate) {
				// Not interested

			}

		};

		public CalibrationController() {
		}

		public long nanosRemaining(Long defaultIfLess) {
			long timeLeft;
			timeLeft = timeout - (System.nanoTime() - startTimeStamp);
			if (defaultIfLess != null && timeLeft > defaultIfLess)
				return defaultIfLess;
			return timeLeft;
		}

		public synchronized void start(CallibrationCallback callback,
				long timeout) {
			if (started)
				throw new IllegalStateException(
						"calibration already in progress");
			if (callback == null)
				throw new IllegalArgumentException("callback must be non null");
			this.timeout = timeout;
			this.callback = callback;

			started = true;
			getMessageSender().pause(true);

			timer = new Timer();
			timeOutMonitor = new TimerTask() {

				@Override
				public void run() {
					stop();
				}

			};
			timer.schedule(timeOutMonitor,
					TimeUnit.NANOSECONDS.toMillis(timeout));
			startTimeStamp = System.nanoTime();

			final BroadcastDataMessage msg = new BroadcastDataMessage();
			msg.setData(BushidoBrake.PACKET_START_CALIBRATION);
			final long duration = nanosRemaining(TimeUnit.SECONDS.toNanos(2));

			new AntHandler() {

				@Override
				public void doSend() throws InterruptedException,
						TimeoutException {
					channel.sendAndWaitForMessage(msg,
							AntUtils.CONDITION_CHANNEL_TX, duration,
							TimeUnit.NANOSECONDS, null);

				}

			}.send();

			channel.registerRxListener(replyListener,
					BroadcastDataMessage.class);

//			statusPoller = new StatusPoller();
//			statusPoller.start();

			callback.onRequestStartPedalling();
		}

		public synchronized void stop() {
			if (!started)
				return;
			timer.cancel();
			started = false;
			getMessageSender().pause(false);
//			if (statusPoller != null) {
//				statusPoller.interrupt();
//				statusPoller = null;
//			}
			startTimeStamp = null;
			timer = null;
			channel.removeRxListener(replyListener);
			BushidoBrake.this.unregisterDataListener(speedListener);
		}

	}

	private CalibrationController calibrationController = new CalibrationController();

/**
	 * Asynchronously calibrates the brake.
	 * @param callback various calibration hooks {@link BushidoBrake.CalibrationCallback)
	 * @param timeout timeout for calibration to complete (in seconds)
	 * @return new calibration value (as would be displayed on headunit)
	 */
	public double calibrate(CallibrationCallback callback, long timeout) {
		double calibrationValue = 0;
		calibrationController
				.start(callback, TimeUnit.SECONDS.toNanos(timeout));
		return calibrationValue;
	}

	private boolean checkWithinTimeLimit(long start, long timeout,
			TimeUnit units) {
		if (System.nanoTime() - start < units.toNanos(timeout))
			return true;
		return false;
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
