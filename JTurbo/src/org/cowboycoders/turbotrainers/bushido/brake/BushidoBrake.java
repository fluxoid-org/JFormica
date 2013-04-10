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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
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
import org.cowboycoders.turbotrainers.bushido.brake.BushidoBrake.VersionRequestCallback;
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

	private VersionRequestCallback versionRequestCallback;

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
								// allow hooks in controller to determine which speed we send
								double speedToSend = resistanceController.onSpeedChange(speed);
								dcl.onSpeedChange(speedToSend);
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
								double powerToSend = resistanceController.onPowerChange(power);
								dcl.onPowerChange(powerToSend);
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
								double cadenceToSend = resistanceController.onCadenceChange(cadence);
								dcl.onCadenceChange(cadenceToSend);
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
									double distanceToSend = resistanceController.onDistanceChange(distance);
									dcl.onDistanceChange(distanceToSend);
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
			LOGGER.info("received bushido soft version:" + version);

			synchronized(BushidoBrake.this) {
				if (versionRequestCallback != null) {
					versionRequestCallback.onVersionReceived(version);
					// make available for garbage collection
					versionRequestCallback = null; 
				}
			}

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

	}
	
	public static interface VersionRequestCallback {
		public void onVersionReceived(String versionString);
	}

	public void requestVersion(VersionRequestCallback versionRequestCallback) {
		if (versionRequestCallback == null) {
			throw new IllegalArgumentException("callback must be non-null");
		}
		
		synchronized(this) {
			this.versionRequestCallback = versionRequestCallback;
		}
		
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
		
		/**
		 * Called when below speed
		 * @param speed
		 */
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

	/**
	 * Compound class that controls CalibrationState transitions, as well as the "actions" that can be performed
	 * on that state.
	 * @author will
	 *
	 */
	private static abstract class CalibrationStateAction {
		
		private final CalibrationState state;
		private final boolean allowNullTransition;
		
		public CalibrationStateAction(CalibrationState state, boolean allowNullTransition) {
			this.state = state;
			this.allowNullTransition = allowNullTransition;
		}
		
		public CalibrationStateAction(CalibrationState state) {
			this(state, false);
		}
		
		public final CalibrationState getMappedState() {
			return state;
		}
		
		public final boolean shouldAllowNullTransition() {
			return allowNullTransition;
		}
		
		public final boolean performTransition(CalibrationState newState) throws IllegalStateException {
			if (newState == state) return false;
			if (!shouldAllowNullTransition() && newState == null) throw new IllegalStateException("Null state disallowed");
			String newStateString = newState == null ? "null" : newState.toString();
			String stateString = state == null ? "null" : state.toString();
			LOGGER.info("state transition: " + stateString + " -> " + newStateString);
			return onPerformTransition(newState);
		}
		
		/**
		 * Should perform any necessary operations to make the transition.
		 * @param newState target state
		 * @return false if no transition made, but was expect state. True, otherwise.
		 * @throws IllegalStateException thrown if unexpected state is encountered.
		 */
		public abstract boolean onPerformTransition (
				org.cowboycoders.turbotrainers.bushido.brake.CalibrationState newState) throws IllegalStateException;
		
		/**
		 * State dependent behaviour. Called once when transition to this state is first made.
		 * @param data
		 */
		public abstract void onStateSelected(Byte[] data);
	}
	

	private class CalibrationController {
		
		private static final int RADIO_QUIET_TIMEOUT_MILLI_SECONDS = 1000;
		private boolean started = false;
		private long timeout;
		private Long startTimeStamp;
		private Timer timer;
		private CallibrationCallback callback;
		private CalibrationStateMachine stateMachine;

		private TimerTask timeOutMonitor;

		private TimerTask stopDetector;
		
		private Timer radioSilenceTimer;
		
		private abstract class AntHandler {
			
			//submit through single thread so we don't multiple threads trying to send at same time
			private Executor singleThreadPool = Executors.newSingleThreadExecutor();

			public abstract void doSend() throws InterruptedException,
					TimeoutException;

			public void send() {
				try {
					doSend();
				} catch (InterruptedException e) {
					LOGGER.warning("interrupted during calibration");
					callback.onFailure(new CalibrationException(e));
					stop();
					return;
				} catch (TimeoutException e) {
					LOGGER.warning("timeout waiting for calibration to finish");
					callback.onFailure(new CalibrationException(e));
					stop();
					return;
				}
			}
			
			public void sendOnDifferentThread() {
				
				singleThreadPool.execute(new Runnable() {
					public void run() {
						send();
					}
					
				});

			}
		}
		
		private class CalibrationStateMachine {
			
			private final Map<CalibrationState, CalibrationStateAction> wrappedStates =
					new HashMap<CalibrationState, CalibrationStateAction>();
			private CalibrationState currentState = null;
			
			{	
				addState(null, new CalibrationStateAction(null) {

					@Override
					public boolean onPerformTransition(
							org.cowboycoders.turbotrainers.bushido.brake.CalibrationState newState)
							throws IllegalStateException {
						
						// if not in calibration we need to request to switch to calibration mode 
						if (newState == CalibrationState.NON_CALIBRATION_MODE) {
							
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

							}.sendOnDifferentThread();
							
							return false;
	
						}
						
						
						else if (newState != CalibrationState.CALIBRATION_MODE) {
							throw new IllegalStateException();
						}
						return true;
					}

					@Override
					public void onStateSelected(Byte[] data) {
						// no processing needed
						
					}
					
				});
				
				
				addState(CalibrationState.CALIBRATION_MODE, new CalibrationStateAction(CalibrationState.CALIBRATION_MODE) {

					@Override
					public boolean onPerformTransition(
							org.cowboycoders.turbotrainers.bushido.brake.CalibrationState newState)
							throws IllegalStateException {
						if (newState != CalibrationState.CALIBRATION_REQUESTED) throw new IllegalStateException();
						return true;
					}

					@Override
					public void onStateSelected(Byte[] data) {
						LOGGER.info(CalibrationState.CALIBRATION_MODE.toString());	
					}
				});
				
				
				addState(CalibrationState.CALIBRATION_REQUESTED, new CalibrationStateAction(CalibrationState.CALIBRATION_REQUESTED) {

					@Override
					public boolean onPerformTransition(
							org.cowboycoders.turbotrainers.bushido.brake.CalibrationState newState)
							throws IllegalStateException {
						if (newState != CalibrationState.UP_TO_SPEED) throw new IllegalStateException();
						return true;
					}

					@Override
					public void onStateSelected(Byte[] data) {
						LOGGER.info(CalibrationState.CALIBRATION_REQUESTED.toString());
						BushidoBrake.this.registerDataListener(speedListener);
					}
					
				});
				
				
				addState(CalibrationState.UP_TO_SPEED, new CalibrationStateAction(CalibrationState.UP_TO_SPEED) {

					@Override
					public boolean onPerformTransition(
							org.cowboycoders.turbotrainers.bushido.brake.CalibrationState newState)
							throws IllegalStateException {
						if (newState != CalibrationState.NO_ERROR) throw new IllegalStateException();
						return true;
					}

					@Override
					public void onStateSelected(Byte[] data) {
						LOGGER.info(CalibrationState.UP_TO_SPEED.toString());
					}
					
				});
				
				
				addState(CalibrationState.NO_ERROR, new CalibrationStateAction(CalibrationState.NO_ERROR) {

					@Override
					public boolean onPerformTransition(
							org.cowboycoders.turbotrainers.bushido.brake.CalibrationState newState)
							throws IllegalStateException {
						if (newState != CalibrationState.STOPPED) throw new IllegalStateException();
						return true;
					}

					@Override
					public void onStateSelected(Byte[] data) {
						LOGGER.info(CalibrationState.NO_ERROR.toString());
						synchronized (replyListener) {
							if (radioSilenceTimer == null) {
								newRadioSilenceTimer();
							}
						}
						
					}
					
				});
				
				addState(CalibrationState.STOPPED, new CalibrationStateAction(CalibrationState.STOPPED) {

					@Override
					public boolean onPerformTransition(
							org.cowboycoders.turbotrainers.bushido.brake.CalibrationState newState)
							throws IllegalStateException {
						if (newState != CalibrationState.NON_CALIBRATION_MODE) throw new IllegalStateException();
						return true;
					}

					@Override
					public void onStateSelected(Byte[] data) {
						LOGGER.info(CalibrationState.STOPPED.toString());
						callback.onRequestResumePedalling();
					}
					
				});
				
				addState(CalibrationState.NON_CALIBRATION_MODE, new CalibrationStateAction(CalibrationState.NON_CALIBRATION_MODE) {

					@Override
					public boolean onPerformTransition(
							org.cowboycoders.turbotrainers.bushido.brake.CalibrationState newState)
							throws IllegalStateException {
						if (newState != CalibrationState.CALIBRATION_VALUE_READY) throw new IllegalStateException();
						return true;
					}

					@Override
					public void onStateSelected(Byte[] data) {
						
						LOGGER.info(CalibrationState.NON_CALIBRATION_MODE.toString());
						//new StatusPoller().start();
						
						final BroadcastDataMessage msg = new BroadcastDataMessage();
						msg.setData(BushidoBrake.PACKET_POST_RESTART_CALIBRATION_RERSUME);

						new AntHandler() {

							@Override
							public void doSend() throws InterruptedException,
									TimeoutException {
								
								int RETRIES = 5;
								long duration = CalibrationController.this.nanosRemaining(TimeUnit.SECONDS.toNanos(10L));
								duration = (long)((double) duration / (double)RETRIES);
								
								//set a min timeout
								if (duration < TimeUnit.SECONDS.toNanos(1L)) {
									RETRIES = 1;
									duration = CalibrationController.this.nanosRemaining(TimeUnit.SECONDS.toNanos(2L));
								}
								
								sendAndRetry(msg,
										AntUtils.CONDITION_CHANNEL_TX,RETRIES,duration,
										TimeUnit.NANOSECONDS);

							}

						}.sendOnDifferentThread();
					}
					
				});
				
				
				addState(CalibrationState.CALIBRATION_VALUE_READY, new CalibrationStateAction(CalibrationState.CALIBRATION_VALUE_READY) {

					@Override
					public boolean onPerformTransition(
							org.cowboycoders.turbotrainers.bushido.brake.CalibrationState newState)
							throws IllegalStateException {
						if (newState != CalibrationState.CALIBRATED) throw new IllegalStateException();
						return true;
					}

					@Override
					public void onStateSelected(Byte[] data) {
						LOGGER.info(CalibrationState.CALIBRATION_VALUE_READY.toString());
						LOGGER.info("Requesting calibration value");

						final BroadcastDataMessage msg = new BroadcastDataMessage();
						msg.setData(BushidoBrake.PACKET_REQUEST_CALIBRATION_VALUE);

						new AntHandler() {

							@Override
							public void doSend() throws InterruptedException,
									TimeoutException {
								
								long duration = CalibrationController.this.nanosRemaining(TimeUnit.SECONDS.toNanos(2L));
								
								channel.sendAndWaitForMessage(msg,
										AntUtils.CONDITION_CHANNEL_TX, duration,
										TimeUnit.NANOSECONDS, null);

							}

						}.sendOnDifferentThread();

					}
					
				});
				
				addState(CalibrationState.CALIBRATED, new CalibrationStateAction(CalibrationState.CALIBRATED) {

					@Override
					public boolean onPerformTransition(
							org.cowboycoders.turbotrainers.bushido.brake.CalibrationState newState)
							throws IllegalStateException {
						// final state : no transitions should occur
						throw new IllegalStateException();
					}

					@Override
					public void onStateSelected(Byte[] data) {
						LOGGER.info(CalibrationState.CALIBRATED.toString());
						BigInteger val = new BigInteger(new byte[] {0x00,data[4],data[5]});
						double calibrationValue = val.doubleValue() / 10.0;
						callback.onSuccess(calibrationValue);
						stop();

					}
					
				});
				
			}
			
			private void addState(CalibrationState state, CalibrationStateAction wrappedState )throws IllegalArgumentException {
				if (wrappedState.getMappedState() != state) throw new IllegalArgumentException("wrapped state must match state it is wrapping");
				wrappedStates.put(state, wrappedState);
				
			}
			
			private final void setTargetState(CalibrationState newState, Byte[] data) {
				CalibrationStateAction wrappedState = wrappedStates.get(currentState);
				if (wrappedState == null) {
					throw new IllegalStateException("State must be mapped to a wrapped value. State: " + currentState);
				}
				if (wrappedState.performTransition(newState)) {
					currentState = newState;
					wrappedState = wrappedStates.get(newState);
					wrappedState.onStateSelected(data);
				}
			}
			
			public synchronized final void setTargetState(CalibrationState newState) {
				setTargetState(newState, null);
			}
			
			public synchronized final void setTargetState(Byte [] data) {
				CalibrationState newState = null; 

				if (ArrayUtils.arrayStartsWith(
						PARTIAL_PACKET_CALIBRATION_STATUS, data)) {
					if (data[1] == 0x06 && data[3] == 0x02) {
						newState = CalibrationState.CALIBRATION_MODE;																			
					} else if (data[1] == 0x06 && data[3] == 0x05) {
						newState = CalibrationState.UP_TO_SPEED;
					} else if (data[1] == 0x06) {
						newState = CalibrationState.CALIBRATION_REQUESTED; 
					} else if (data[1] == 0x03 && data[3] == 0x0c) {
						newState = CalibrationState.NO_ERROR;
					} else if (data[1] == 0x03 && data[3] == 0x42) {
						newState = CalibrationState.CALIBRATED;
					} else if (data[1] == 0x03 && data[3] == 0x4d) { // 22:03:00:4d:00:00:00:00
						newState = CalibrationState.CALIBRATION_VALUE_READY;
					} else if (data[1] == 0x03) { // 22:03:00:00:00:00:00:00
						newState = CalibrationState.NON_CALIBRATION_MODE;
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
//					String stateAsString = newState == null ? "null" : newState
//							.toString();
//					LOGGER.info("current state: " + stateAsString);
					
					setTargetState(newState, data);
				}
				
				
				
			}
			
			
			public synchronized void reset() {
				currentState = null;
			}
		}
		
		/**
		 * Handles exceptions for state changes
		 * @author will
		 *
		 */
		private abstract class StateSetter {
			
			protected abstract void doStateChange() throws IllegalStateException;
			
			public final void changeState() {
				try {
					doStateChange();
				} catch (IllegalStateException e) {
					// something went wrong which led to an unexpected state change. Assume calibration irreversably failed
					// and restart with remaining time
					LOGGER.info("Illegal state transition: restarting calibration");
					long timeLeft = CalibrationController.this.nanosRemaining(null);
					CallibrationCallback oldCallBack = callback;
					stop();
					start(oldCallBack,timeLeft);
				}
			}
		}




		private void newRadioSilenceTimer() {
			radioSilenceTimer = new Timer();
			stopDetector = new TimerTask() {

				@Override
				public void run() {
					// state : stopped
					new StateSetter() {

						@Override
						protected void doStateChange()
								throws IllegalStateException {
							stateMachine.setTargetState(CalibrationState.STOPPED);
						}
						
					}.changeState();

				}

			};
			timer.schedule(stopDetector, RADIO_QUIET_TIMEOUT_MILLI_SECONDS);
		}
		


		private BroadcastListener<BroadcastDataMessage> replyListener = new BroadcastListener<BroadcastDataMessage>() {

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
				
				final Byte[] data = message.getData();
				
				new StateSetter() {

					@Override
					protected void doStateChange()
							throws IllegalStateException {
						stateMachine.setTargetState(data);
					}
					
				}.changeState();


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
			stateMachine = new CalibrationStateMachine();
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

			channel.registerRxListener(replyListener,
					BroadcastDataMessage.class);


			callback.onRequestStartPedalling();
		}

		public synchronized void stop() {
			if (!started)
				return;
			timer.cancel();
			started = false;
			getMessageSender().pause(false);

			startTimeStamp = null;
			timer = null;
			channel.removeRxListener(replyListener);
			BushidoBrake.this.unregisterDataListener(speedListener);
			stateMachine.reset();
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
		
		// cause related threads to be shutdown
		calibrationController.stop();

		// make sure no-one sends and unpause which will cancel our stop request
		synchronized (listeners) {
			for (BroadcastListener<? extends ChannelMessage> listener : listeners) {
				unregisterRxListener(listener);
			}
		}

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
