/**
 *     Copyright (c) 2012, Will Szumski
 *
 *     This file is part of formicidae.
 *
 *     formicidae is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     formicidae is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with formicidae.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cowboycoders.ant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.cowboycoders.ant.defines.AntDefine;
import org.cowboycoders.ant.events.BroadcastListener;
import org.cowboycoders.ant.events.BroadcastMessenger;
import org.cowboycoders.ant.events.EventMachine;
import org.cowboycoders.ant.events.MessageCondition;
import org.cowboycoders.ant.events.MessageConditionFactory;
import org.cowboycoders.ant.messages.ChannelMessage;
import org.cowboycoders.ant.messages.ChannelType;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.MessageMetaWrapper;
import org.cowboycoders.ant.messages.StandardMessage;
import org.cowboycoders.ant.messages.commands.ChannelCloseMessage;
import org.cowboycoders.ant.messages.commands.ChannelOpenMessage;
import org.cowboycoders.ant.messages.commands.ChannelRequestMessage;
import org.cowboycoders.ant.messages.commands.ChannelRequestMessage.Request;
import org.cowboycoders.ant.messages.config.ChannelAssignMessage;
import org.cowboycoders.ant.messages.config.ChannelFrequencyMessage;
import org.cowboycoders.ant.messages.config.ChannelIdMessage;
import org.cowboycoders.ant.messages.config.ChannelPeriodMessage;
import org.cowboycoders.ant.messages.config.ChannelSearchTimeoutMessage;
import org.cowboycoders.ant.messages.config.ChannelUnassignMessage;
import org.cowboycoders.ant.messages.config.ChannelAssignMessage.ExtendedAssignment;
import org.cowboycoders.ant.messages.data.BurstDataMessage;
import org.cowboycoders.ant.messages.data.BurstData;
import org.cowboycoders.ant.messages.nonstandard.CombinedBurst;
import org.cowboycoders.ant.messages.responses.ChannelResponse;
import org.cowboycoders.ant.messages.responses.ChannelStatusResponse;
import org.cowboycoders.ant.messages.responses.ChannelStatusResponse.State;
import org.cowboycoders.ant.messages.responses.ResponseCode;
import org.cowboycoders.ant.utils.BurstMessageSequenceGenerator;
import org.cowboycoders.ant.utils.ByteUtils;

public class Channel {

	public static final int SEARCH_TIMEOUT_NEVER = 255;

	public final static Logger LOGGER = Logger.getLogger(EventMachine.class
			.getName());

	private static final int RAW_CHANNEL_PERIOD_DEFAULT = 8192;

	private static final double CHANNEL_PERIOD_SCALE_FACTOR = 32768.0;

	private static final double BURST_TIMEOUT_SCALE_FACTOR = 1.5; // number of
																	// channel
																	// periods
	private static final long CONVERSION_FACTOR_SECONDS_TO_NANOSECONDS = (long) Math
			.pow(10, 9);

	private static final long BURST_TIMEOUT_NANOS_DEFAULT = rawChannelPeriodToDefaultTimeout(RAW_CHANNEL_PERIOD_DEFAULT);

	private static long rawChannelPeriodToDefaultTimeout(
			final int rawChannelPeriod) {
		return (long) (BURST_TIMEOUT_SCALE_FACTOR
				* (rawChannelPeriod / CHANNEL_PERIOD_SCALE_FACTOR) * CONVERSION_FACTOR_SECONDS_TO_NANOSECONDS);
	}

	private Node parent;

	/**
	 * @return the parent
	 */
	public Node getParent() {
		return parent;
	}

	private Long lastBurstTimeStamp;

	private long burstTimeout = BURST_TIMEOUT_NANOS_DEFAULT;

	private final BroadcastMessenger<CombinedBurst> burstMessenger = new BroadcastMessenger<CombinedBurst>();

	private CombinedBurst.Builder burstBuilder = new CombinedBurst.Builder();

	private String name = UUID.randomUUID().toString();

	private boolean free = true;

	/**
	 * The channel messaging period in seconds * 32768. Maximum messaging period
	 * is ~2 seconds.
	 * 
	 * default : {@link RAW_CHANNEL_PERIOD_DEFAULT}
	 * 
	 * Raw : not converted to seconds
	 */
	private int rawChannelPeriod = RAW_CHANNEL_PERIOD_DEFAULT;

	private Lock sendLock = new ReentrantLock();

	/**
	 * @return the sendLock
	 */
	public Lock getSendLock() {
		return sendLock;
	}

	private int number = 0;

	/**
	 * @param parent
	 *            the parent to set
	 */
	protected synchronized void setParent(Node parent) {
		this.parent = parent;
	}

	/**
	 * @return the name of channel
	 */
	public synchronized String getName() {
		return name;
	}

	/**
	 * sets the channel name
	 */
	public synchronized void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the isFree
	 */
	public synchronized boolean isFree() {
		return free;
	}

	/**
	 * @param isFree
	 *            the isFree to set
	 */
	protected synchronized void setFree(boolean free) {
		LOGGER.entering(Channel.class.getSimpleName(), "setFree");
		
		// Make sure channel is in reusable state
		if (free) {
			cleanUp();
		}
		
		// clean up complete, set free		
		this.free = free;
		LOGGER.exiting(Channel.class.getSimpleName(), "setFree");
	}
	
	/**
	 * Performs clean up operations so that the channel is ready to use again.
	 */
	private void cleanUp() {
		// assume assigned, so we don't leave in assigned state
		State state = ChannelStatusResponse.State.ASSIGNED;
		try {
			ChannelStatusResponse status = this.requestStatus();
			state = status.getState();
		} catch (ChannelError e){
			// ignore (assume assigned)
			LOGGER.warning("Error requesting channel status");
		}
		
		// if channel is open (try and close)
		if (state.equals(State.TRACKING) || state.equals(State.SEARCHING)) {
			try {
				this.close();
				
			} catch (ChannelError e){
				LOGGER.warning("Error closing channel in state: " + state);
			}
		}
		
		// update status
		try {
			ChannelStatusResponse status = this.requestStatus();
			state = status.getState();
		} catch (ChannelError e){
			// ignore (assume assigned)
			LOGGER.warning("Error requesting channel status");
		}
		
		// channel is assigned, but not open
		if (state.equals(State.ASSIGNED)) {
			try {
				this.unassign();
				
			} catch (ChannelError e){
				LOGGER.warning("Error unassigning channel in state: " + state);
			}
		}
	}

	/**
	 * @return the number
	 */
	public synchronized int getNumber() {
		return number;
	}

	private final MessageSender channelSender = new MessageSender() {

		@Override
		public List<MessageMetaWrapper<? extends StandardMessage>> send(
				StandardMessage msg) {
			MessageMetaWrapper<StandardMessage> sentMeta = Channel.this
					.send((ChannelMessage) msg);
			List<MessageMetaWrapper<? extends StandardMessage>> rtn = new ArrayList<MessageMetaWrapper<? extends StandardMessage>>(
					1);
			rtn.add(sentMeta);
			return rtn;

		}

	};

	// /**
	// * Used for queueing send events - should hold send lock
	// */
	// final ExecutorService channelExecutor =
	// Executors.newSingleThreadExecutor();
	//
	//
	//
	// /**
	// * Can be used for more advanced queued messages
	// * @return the channelExecutor
	// */
	// public ExecutorService getChannelExecutor() {
	// return channelExecutor;
	// }

	/**
	 * @param number
	 *            the number to set
	 */
	protected synchronized void setNumber(int number) {
		this.number = number;
	}

	public Channel(Node parent, int number) {
		setParent(parent);
		setNumber(number);
		this.registerRxListener(burstListener, BurstDataMessage.class);
	}

	public static class ChannelInstanceCondition implements MessageCondition {

		private int number;

		/**
		 * @return the number
		 */
		public int getNumber() {
			return number;
		}

		public ChannelInstanceCondition(int number) {
			this.number = number;
		}

		@Override
		public boolean test(StandardMessage msg) {
			if (!(msg instanceof ChannelMessage))
				return false;
			if ((msg instanceof ChannelMessage)
					&& (!(((ChannelMessage) msg).getChannelNumber() == getNumber()))) {
				return false;
			}
			return true;
		}

	}

	public static class ChannelResponseCondition implements MessageCondition {

		private int number;

		/**
		 * @return the number
		 */
		public int getNumber() {
			return number;
		}

		public ChannelResponseCondition(int number) {
			this.number = number;
		}

		@Override
		public boolean test(StandardMessage msg) {
			if (!(msg instanceof ChannelResponse))
				return false;
			if (!(msg instanceof ChannelMessage))
				return false;
			if ((msg instanceof ChannelMessage)
					&& (!(((ChannelMessage) msg).getChannelNumber() == getNumber()))) {
				return false;
			}
			return true;
		}

	}

	/**
	 * Filters burst messages if listeners exist for {@link CombinedBurst}s
	 * 
	 * See:
	 * 
	 * {@link Channel#registerBurstListener(BroadcastListener)}
	 * {@link Channel#removeBurstListener(BroadcastListener)}
	 */
	public class BurstFilterCondition implements MessageCondition {

		@Override
		public boolean test(StandardMessage msg) {
			if (burstMessenger.getListenerCount() > 0
					&& (msg instanceof BurstData))
				return false;
			return true;
		}

	}

	private final BurstFilterCondition burstFilterCondition = new BurstFilterCondition();

	/**
	 * We wrap listeners to look for message of specific class - this is a map
	 * from the original to the new one
	 */
	private Map<Object, BroadcastListener<ChannelMessage>> mAdapterListenerMap = new HashMap<Object, BroadcastListener<ChannelMessage>>();

	public synchronized <V extends ChannelMessage> void registerRxListener(
			final BroadcastListener<V> listener, final Class<V> clazz) {

		BroadcastListener<ChannelMessage> adapter = new BroadcastListener<ChannelMessage>() {

			@Override
			public void receiveMessage(ChannelMessage message) {
				// filter burst messages if we are listening for combined
				if (!burstFilterCondition.test(message)
						&& listener != burstListener)
					return;

				if (clazz.isInstance(message)
						&& message.getChannelNumber() == number) {
					listener.receiveMessage(clazz.cast(message));
				}

			}

		};

		mAdapterListenerMap.put(listener, adapter);
		parent.registerRxListener(adapter, ChannelMessage.class);

	}

	public synchronized <V extends ChannelMessage> void removeRxListener(
			final BroadcastListener<V> listener) {

		BroadcastListener<ChannelMessage> adapter = mAdapterListenerMap
				.get(listener);
		if (adapter != null) {
			parent.removeRxListener(adapter);
		} else {
			LOGGER.warning("removeRxListener: ignoring unknown listener");
		}

	}

	/**
	 * With internal locking
	 * 
	 * @param msg
	 * @return
	 */
	public MessageMetaWrapper<StandardMessage> send(ChannelMessage msg) {
		try {
			sendLock.lock();
			return sendToNode(msg);
		} finally {
			sendLock.unlock();
		}
	}

	// /**
	// * No internal locking
	// * Should hold sendLock @see getSendLock()
	// * @param msg
	// * @return
	// */
	// public MessageMetaWrapper<StandardMessage> atomicSend(ChannelMessage msg)
	// {
	// return sendToNode(msg);
	// }

	private MessageMetaWrapper<StandardMessage> sendToNode(ChannelMessage msg) {
		prepareChannelMessageForSend(msg);
		return parent.send(msg);
	}

	private ChannelMessage prepareChannelMessageForSend(ChannelMessage msg) {
		msg.setChannelNumber(number);
		return msg;
	}

	/**
	 * Sets network of channel and assigns it
	 * 
	 * @param netKeyName
	 * @param assignMessage
	 */
	public void assign(String netKeyName, ChannelAssignMessage assignMessage) {
		NetworkKey key = parent.getNetworkKey(netKeyName);
		assign(key, assignMessage);
	}

	/**
	 * Sets network of channel and assigns it
	 * 
	 * @see org.cowboycoders.ant.messages.ChannelAssignMessage
	 * @param netKeyName
	 * @param type
	 *            channel type
	 * @param extended
	 *            extended assignment parameters
	 */
	public void assign(String netKeyName, ChannelType type,
			ExtendedAssignment... extended) {
		assign(netKeyName, new ChannelAssignMessage(0, type, extended));
	}

	/**
	 * Sets channel id @see org.cowboycoders.ant.messages.ChannelIdMessage
	 * 
	 * @param deviceNumber
	 * @param deviceType
	 * @param transmissionType
	 * @param setPairingFlag
	 */
	public void setId(int deviceNumber, int deviceType, int transmissionType,
			boolean setPairingFlag) {
		ChannelIdMessage id = new ChannelIdMessage(0, deviceNumber, deviceType,
				transmissionType, setPairingFlag);
		MessageCondition condition = MessageConditionFactory
				.newResponseCondition(id.getId(),
						ResponseCode.RESPONSE_NO_ERROR);
		try {
			sendAndWaitForMessage(id, condition, 1L, TimeUnit.SECONDS, null);
		} catch (InterruptedException e) {
			handleTimeOutException(e);
		} catch (TimeoutException e) {
			handleTimeOutException(e);
		}
	}

	/**
	 * @see org.cowboycoders.ant.messages.ChannelFrequencyMessage
	 * @param channelFrequency
	 */
	public void setFrequency(int channelFrequency) {
		ChannelFrequencyMessage freq = new ChannelFrequencyMessage(0,
				channelFrequency);
		MessageCondition condition = MessageConditionFactory
				.newResponseCondition(freq.getId(),
						ResponseCode.RESPONSE_NO_ERROR);
		try {
			sendAndWaitForMessage(freq, condition, 1L, TimeUnit.SECONDS, null);
		} catch (InterruptedException e) {
			handleTimeOutException(e);
		} catch (TimeoutException e) {
			handleTimeOutException(e);
		}
	}

	/**
	 * @see org.cowboycoders.ant.messages.ChannelPeriodMessage
	 * @param period
	 */
	public void setPeriod(int period) {
		ChannelPeriodMessage periodMsg = new ChannelPeriodMessage(0, period);
		MessageCondition condition = MessageConditionFactory
				.newResponseCondition(periodMsg.getId(),
						ResponseCode.RESPONSE_NO_ERROR);
		try {
			sendAndWaitForMessage(periodMsg, condition, 1L, TimeUnit.SECONDS,
					null);
		} catch (InterruptedException e) {
			handleTimeOutException(e);
		} catch (TimeoutException e) {
			handleTimeOutException(e);
		}
		rawChannelPeriod = period;
		setBurstTimeout(rawChannelPeriodToDefaultTimeout(period));
	}

	/**
	 * @see org.cowboycoders.ant.messages.ChannelSearchTimeoutMessage 255 - no
	 *      timeout
	 * @param timeout
	 * 
	 * 
	 */
	public void setSearchTimeout(int timeout) {
		ChannelSearchTimeoutMessage msg = new ChannelSearchTimeoutMessage(0,
				timeout);
		MessageCondition condition = MessageConditionFactory
				.newResponseCondition(msg.getId(),
						ResponseCode.RESPONSE_NO_ERROR);
		try {
			sendAndWaitForMessage(msg, condition, 1L, TimeUnit.SECONDS, null);
		} catch (InterruptedException e) {
			handleTimeOutException(e);
		} catch (TimeoutException e) {
			handleTimeOutException(e);
		}
	}

	/**
	 * Not thread safe
	 * 
	 * @param msg
	 * @param condition
	 * @param timeout
	 * @param timeoutUnit
	 * @param receipt
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 */
	public StandardMessage sendAndWaitForMessage(final ChannelMessage msg,
			final MessageCondition condition, final Long timeout,
			final TimeUnit timeoutUnit, final Receipt receipt)
			throws InterruptedException, TimeoutException {

		return parent.sendAndWaitForMessage(msg, condition, timeout,
				timeoutUnit, channelSender, receipt);

	}

	// /**
	// * Queues a msg to be sent in the channelExectuor. Next message in queue
	// is only
	// * sent when the condition has been satisfied or the timeout has elapsed.
	// *
	// * Useful for DataMessages (waiting for channel event tx)
	// *
	// * @deprecated Is this useful?
	// *
	// * @param msg
	// * @param condition
	// * @param timeout
	// * @param timeoutUnit
	// */
	// @Deprecated
	// public void enqueue(final ChannelMessage msg, final MessageCondition
	// condition,
	// final Long timeout, final TimeUnit timeoutUnit ) {
	// try {
	// sendLock.lock();
	// ExecutorService channelExecutor = getChannelExecutor();
	// channelExecutor.execute(new Runnable() {
	//
	// @Override
	// public void run() {
	// try {
	// Channel.this.sendAndWaitForMessage(msg, condition, timeout, timeoutUnit,
	// timeout, null);
	// } catch (InterruptedException e) {
	// throw new ChannelError(e);
	// } catch (TimeoutException e) {
	// throw new ChannelError(e);
	// }
	//
	// }
	//
	// });
	//
	//
	// } finally {
	// sendLock.unlock();
	// }
	//
	// }

	private void handleTimeOutException(Exception e) {
		if (e instanceof InterruptedException) {
			throw new ChannelError(
					"Interuppted whilst waiting for message / reply", e);
		}
		if (e instanceof TimeoutException) {
			throw new ChannelError(
					"Timeout whilst waiting for message / reply", e);
		}

	}

	public synchronized void unassign() {
		ChannelMessage msg = new ChannelUnassignMessage(getNumber());
		MessageCondition condition = MessageConditionFactory
				.newResponseCondition(msg.getId(),
						ResponseCode.RESPONSE_NO_ERROR);
		try {
			sendAndWaitForMessage(msg, condition, 1L, TimeUnit.SECONDS, null);
		} catch (InterruptedException e) {
			handleTimeOutException(e);
		} catch (TimeoutException e) {
			handleTimeOutException(e);
		}
	}

	public synchronized void assign(NetworkKey key,
			ChannelAssignMessage assignMessage) {
		int networkNumber = 0;
		if (key != null) {
			networkNumber = key.getNumber();
		} else {
			LOGGER.warning("network key not found: default to network 0");
		}

		assignMessage.setNetworkNumber(networkNumber);

		MessageCondition condition = MessageConditionFactory
				.newResponseCondition(assignMessage.getId(),
						ResponseCode.RESPONSE_NO_ERROR);
		try {
			sendAndWaitForMessage(assignMessage, condition, 1L,
					TimeUnit.SECONDS, null);
		} catch (InterruptedException e) {
			handleTimeOutException(e);
		} catch (TimeoutException e) {
			handleTimeOutException(e);
		}

	}

	public synchronized void open() {
		ChannelMessage msg = new ChannelOpenMessage(0);
		MessageCondition condition = MessageConditionFactory
				.newResponseCondition(msg.getId(),
						ResponseCode.RESPONSE_NO_ERROR);
		try {
			sendAndWaitForMessage(msg, condition, 1L, TimeUnit.SECONDS, null);
		} catch (InterruptedException e) {
			handleTimeOutException(e);
		} catch (TimeoutException e) {
			handleTimeOutException(e);
		}
	}

	public synchronized void close() {
		ChannelMessage msg = new ChannelCloseMessage(0);
		MessageCondition noError = MessageConditionFactory
				.newResponseCondition(msg.getId(),
						ResponseCode.RESPONSE_NO_ERROR);
		MessageCondition channelClosed = MessageConditionFactory
				.newResponseCondition(MessageId.EVENT,
						ResponseCode.EVENT_CHANNEL_CLOSED);
		MessageCondition chainedCondition = MessageConditionFactory
				.newChainedCondition(noError, channelClosed);
		Receipt receipt = new Receipt();
		try {
			sendAndWaitForMessage(msg, chainedCondition, 1L, TimeUnit.SECONDS,
					receipt);
		} catch (InterruptedException e) {
			handleTimeOutException(e);
		} catch (TimeoutException e) {
			handleTimeOutException(e);
		}
	}

	/**
	 * Sends a byte array as a burst message. Will split into
	 * {@link AntDefine.ANT_STANDARD_DATA_PAYLOAD_SIZE} long chunks, so if you
	 * want complete control, make sure the total length is a multiple of this
	 * number.
	 * 
	 * @param data
	 *            array to send a burst
	 * @param timeout
	 *            timeout for complete burst
	 * @param timeoutUnit
	 *            unit for timeout
	 * @throws InterruptedException
	 *             if thread interrupted whilst waiting for completion
	 * @throws TimeoutException
	 *             if timeout expires
	 * @throws TransferException
	 *             on transfer error
	 */
	public void sendBurst(byte[] data, Long timeout, TimeUnit timeoutUnit)
			throws InterruptedException, TimeoutException, TransferException {
		final List<byte[]> list = ByteUtils.splitByteArray(data,
				AntDefine.ANT_STANDARD_DATA_PAYLOAD_SIZE);
		final BurstMessageSequenceGenerator generator = new BurstMessageSequenceGenerator();
		try {
			sendLock.lock();

			final MessageCondition completed = MessageConditionFactory
					.newResponseCondition(MessageId.EVENT,
							ResponseCode.EVENT_TRANSFER_TX_COMPLETED);
			final MessageCondition failed = MessageConditionFactory
					.newResponseCondition(MessageId.EVENT,
							ResponseCode.EVENT_TRANSFER_TX_FAILED);
			final MessageCondition inProgress = MessageConditionFactory
					.newResponseCondition(MessageId.EVENT,
							ResponseCode.TRANSFER_IN_PROGRESS);
			final MessageCondition sequenceError = MessageConditionFactory
					.newResponseCondition(MessageId.EVENT,
							ResponseCode.TRANSFER_SEQUENCE_NUMBER_ERROR);
			final MessageCondition transferInError = MessageConditionFactory
					.newResponseCondition(MessageId.EVENT,
							ResponseCode.TRANSFER_IN_ERROR);
			MessageCondition condition = new MessageCondition() {

				@Override
				public boolean test(StandardMessage msg) {
					if (completed.test(msg))
						return true;
					ChannelResponse r = null;
					if (msg instanceof ChannelResponse) {
						r = (ChannelResponse) msg;
					}
					if (failed.test(msg)) {
						throw new TransferException(r.getMessageId(),
								r.getResponseCode(), "Tx failed");
					}
					if (inProgress.test(msg)) {
						throw new TransferException(r.getMessageId(),
								r.getResponseCode(),
								"Transfer already in progress");
					}
					if (sequenceError.test(msg)) {
						throw new TransferException(r.getMessageId(),
								r.getResponseCode(),
								"Sequence Error : most likely a bug");
					}
					if (transferInError.test(msg)) {
						throw new TransferException(r.getMessageId(),
								r.getResponseCode(),
								"Transfer in Error : passed sequence checked but failed for some other reason");
					}
					return false;
				}

			};

			MessageSender massSender = new MessageSender() {

				@Override
				public ArrayList<MessageMetaWrapper<? extends StandardMessage>> send(
						StandardMessage msgIn) {
					ArrayList<MessageMetaWrapper<? extends StandardMessage>> sentMessages = new ArrayList<MessageMetaWrapper<? extends StandardMessage>>(
							list.size());

					// handle all but last
					for (int i = 0; i < list.size() - 1; i++) {
						BurstDataMessage msg = new BurstDataMessage();
						msg.setData(list.get(i));
						msg.setSequenceNumber(generator.next());
						sentMessages.add(Channel.this.send(msg));

					}

					BurstDataMessage msg = new BurstDataMessage();
					msg.setData(list.get(list.size() - 1));
					msg.setSequenceNumber(generator.finish());
					sentMessages.add(Channel.this.send(msg));

					return sentMessages;

				}

			};

			try {

				parent.sendAndWaitForMessage(null, condition, timeout,
						timeoutUnit, massSender, null);

			} catch (RuntimeException e) {
				// two levels deep
				Throwable cause = e.getCause().getCause();
				if (cause != null && cause instanceof TransferException) {
					throw (TransferException) cause;
				} else {
					throw e;
				}
			}

		} finally {
			sendLock.unlock();
		}

	}

	private final BroadcastListener<BurstDataMessage> burstListener = new BroadcastListener<BurstDataMessage>() {

		@Override
		public void receiveMessage(BurstDataMessage message) {
			CombinedBurst burst;
			long newTimeStamp = System.nanoTime();
			// if timeout
			if (lastBurstTimeStamp != null
					&& (newTimeStamp - lastBurstTimeStamp) >= burstTimeout) {
				CombinedBurst timedOutBurst = burstBuilder.timeout();
				notifyListeners(timedOutBurst);

			}
			burst = burstBuilder.addMessage(message);
			notifyListeners(burst);
			lastBurstTimeStamp = newTimeStamp;
		}

		/**
		 * Notifies listeners if new {@link CombinedBurst} is available
		 * 
		 * @param burst
		 *            may be null, in which case no action is taken.
		 */
		private void notifyListeners(CombinedBurst burst) {
			if (burst != null) {
				synchronized (Channel.this) {
					burstMessenger.sendMessage(burst);
				}
			}
		}

	};

	/**
	 * Listen for combined burst messages. Adding a burst listener will prevent
	 * any burst messages being sent to this channels RxListeners. See :
	 * {@link Channel#registerRxListener(BroadcastListener, Class)}
	 * 
	 * @param listener
	 *            new listener
	 */
	public synchronized void registerBurstListener(
			BroadcastListener<CombinedBurst> listener) {
		burstMessenger.addBroadcastListener(listener);
	}

	/**
	 * Stop listening for combined burst messages
	 * 
	 * @param listener
	 *            new listener
	 */
	public synchronized void removeBurstListener(
			BroadcastListener<CombinedBurst> listener) {
		burstMessenger.removeBroadcastListener(listener);
	}

	/**
	 * Gets current channel period (provided it was set through this class)
	 * 
	 * @return channel period in seconds
	 */
	public double getChannelPeriod() {
		return rawChannelPeriod / CHANNEL_PERIOD_SCALE_FACTOR;

	}

	/**
	 * Gets burst timeout
	 * 
	 * @return timeout in a nanoseconds
	 */
	public long getBurstTimeout() {
		return burstTimeout;
	}

	/**
	 * Sets burst timeout manually. Must be called after setting new period, as
	 * this resets to the default value;
	 * 
	 * @param burstTimeout
	 *            new timeout in nanoseconds
	 * @throws IllegalArgumentException
	 *             if timeout below zero
	 */
	public void setBurstTimeout(long burstTimeout) {
		if (burstTimeout < 0) {
			throw new IllegalArgumentException(
					"timeout must be grater than zero");
		}
		this.burstTimeout = burstTimeout;
	}
	
	
	/**
	 * Utility method to request channel status. Default timeout of 1 second.
	 * @return {@link ChannelStatusResponse) encompassing response
	 * @throws ChannelError on timeout, if interrupted whilst waiting or status not received;
	 */
	public ChannelStatusResponse requestStatus() throws ChannelError {
		
		ChannelRequestMessage msg = new  ChannelRequestMessage(Request.CHANNEL_STATUS);
		
		MessageCondition condition = MessageConditionFactory.newInstanceOfCondition(ChannelStatusResponse.class);
		
			ChannelStatusResponse response = null;
			
			try {
				response = (ChannelStatusResponse) sendAndWaitForMessage(
						msg, condition, 1L, TimeUnit.SECONDS, null);
			} catch (InterruptedException e) {
				throw new ChannelError(e);
			} catch (TimeoutException e) {
				throw new ChannelError(e);
			}
			
			return response;
			

	}
}
