/**
 *     Copyright (c) 2013, Will Szumski
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
import org.cowboycoders.ant.messages.MasterChannelType;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.MessageMetaWrapper;
import org.cowboycoders.ant.messages.StandardMessage;
import org.cowboycoders.ant.messages.commands.ChannelCloseMessage;
import org.cowboycoders.ant.messages.commands.ChannelOpenMessage;
import org.cowboycoders.ant.messages.commands.ChannelOpenRxScanModeMessage;
import org.cowboycoders.ant.messages.commands.ChannelRequestMessage;
import org.cowboycoders.ant.messages.commands.ChannelRequestMessage.Request;
import org.cowboycoders.ant.messages.config.AddChannelIdMessage;
import org.cowboycoders.ant.messages.config.ChannelAssignMessage;
import org.cowboycoders.ant.messages.config.ChannelFrequencyMessage;
import org.cowboycoders.ant.messages.config.ChannelIdMessage;
import org.cowboycoders.ant.messages.config.ChannelLowPrioritySearchTimeoutMessage;
import org.cowboycoders.ant.messages.config.ChannelPeriodMessage;
import org.cowboycoders.ant.messages.config.ChannelSearchPriorityMessage;
import org.cowboycoders.ant.messages.config.ChannelSearchTimeoutMessage;
import org.cowboycoders.ant.messages.config.ChannelTxPowerMessage;
import org.cowboycoders.ant.messages.config.ChannelUnassignMessage;
import org.cowboycoders.ant.messages.config.FrequencyAgilityMessage;
import org.cowboycoders.ant.messages.config.ProximitySearchMessage;
import org.cowboycoders.ant.messages.config.ChannelAssignMessage.ExtendedAssignment;
import org.cowboycoders.ant.messages.config.ConfigListIdMessage;
import org.cowboycoders.ant.messages.data.BurstDataMessage;
import org.cowboycoders.ant.messages.data.BurstData;
import org.cowboycoders.ant.messages.nonstandard.CombinedBurst;
import org.cowboycoders.ant.messages.nonstandard.CombinedBurst.StatusFlag;
import org.cowboycoders.ant.messages.responses.Response;
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
	
	private Network assignedNetwork;

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
	
	private MessageCondition channelFilterCondition;

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
		
		LOGGER.finer("pre close: " + state);
		
		// if channel is open (try and close)
		if (state.equals(State.TRACKING) || state.equals(State.SEARCHING)) {
			try {
				this.close();
				
			} catch (ChannelError e){
				LOGGER.warning("Error closing channel in state: " + state);
			}
		}
		
		// clear the black list
		this.blacklist(null);
		
		// update status
		try {
			ChannelStatusResponse status = this.requestStatus();
			state = status.getState();
		} catch (ChannelError e){
			// ignore (assume assigned)
			LOGGER.warning("Error requesting channel status");
		}
		
		LOGGER.finer("pre unassign: " + state);
		
		// channel is assigned, but not open
		if (state.equals(State.ASSIGNED)) {
			try {
				this.unassign();
				
			} catch (ChannelError e){
				LOGGER.warning("Error unassigning channel in state: " + state);
			}
		}
		
		// remove all channelListeners
		removeAllRxListeners();
		
		// clear name
		this.setName(UUID.randomUUID().toString());
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
		channelFilterCondition = new ChannelInstanceCondition(number);
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
			if (!(msg instanceof Response))
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
	
	private ChannelType type;

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
	
	public synchronized void removeAllRxListeners() {
		for (Object key: mAdapterListenerMap.keySet()) {
			@SuppressWarnings("unchecked")
			BroadcastListener<ChannelMessage> listener = (BroadcastListener<ChannelMessage>) key;
			
			// don't remove internal listeners !
			if (listener.equals(burstListener)) {
				continue;
			}
			removeRxListener(listener);
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
	 * @see org.cowboycoders.ant.messages.ChannelAssignMessage
	 * @param key
	 * @param type
	 *            channel type
	 * @param extended
	 *            extended assignment parameters
	 */
	public void assign(NetworkKey key, ChannelType type,
			ExtendedAssignment... extended) {
		assign(key, new ChannelAssignMessage(0, type, extended));
	}
	
	public void assign(Network network, ChannelType type,
			ExtendedAssignment... extended) {
		ChannelAssignMessage msg = new ChannelAssignMessage(network.getNumber(), type, extended);
		sendAndWaitForResponseNoError(msg);
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
	 * Sets the channelId
	 * See also {@link Channel#setId(int, int, int, boolean)}
	 * @param channelId
	 */
	public void setId(ChannelId channelId) {
		this.setId(channelId.getDeviceNumber(),
				channelId.getDeviceType(),
				channelId.getTransmissonType(),
				channelId.isPairingFlagSet());
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
				timeoutUnit, channelSender, receipt, channelFilterCondition);

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
		type = null;
		
		if (assignedNetwork != null) {
			assignedNetwork.free();
			assignedNetwork = null;
		}
	}

	public synchronized void assign(NetworkKey key,
			ChannelAssignMessage assignMessage) {
		int networkNumber = 0;
		
		// don't leak associated networks
		if(assignedNetwork != null) {
			assignedNetwork.free();
			assignedNetwork = null;
		}
		// look up network from node
		assignedNetwork = parent.getNetworkForKey(key);
		
		if (assignedNetwork != null) {
			networkNumber = assignedNetwork.getNumber();
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
		
		this.type = assignMessage.getType();

	}

	public synchronized void open() {
		ChannelMessage msg = new ChannelOpenMessage(0);
		MessageCondition condition = MessageConditionFactory
				.newResponseCondition(msg.getId(),
						ResponseCode.RESPONSE_NO_ERROR);
		try {
			sendAndWaitForMessage(msg, condition, 1L, TimeUnit.SECONDS, null);
			
			// if master channel detect wait for first transmission, else ChannelClosedExceptions
			// are thrown
			if (type != null && type instanceof MasterChannelType) {
				MessageCondition masterTransmitting = MessageConditionFactory
						.newResponseCondition(MessageId.EVENT,ResponseCode.EVENT_TX);
				parent.getEvm().waitForCondition(masterTransmitting, 5L, TimeUnit.SECONDS, null);
			}
			
			
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
					Response r = null;
					if (msg instanceof Response) {
						r = (Response) msg;
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
						timeoutUnit, massSender, null,channelFilterCondition);

			} catch (RuntimeException e) {
				Throwable cause = e.getCause();
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
			
			// sequence error might be start of new sequence so try re-adding
			if (burst != null && burst.getStatusFlags().contains(StatusFlag.ERROR_SEQUENCE_INVALID)) {
				burst = burstBuilder.addMessage(message);
				
				// case first message is also last
				if (burst != null && burst.isComplete()) {
					notifyListeners(burst);
				}
			}
			
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
	
	  /**
	   * Registers an event listener. To remove user {@link Channel#removeRxListener(BroadcastListener));
	   * @param handler event handler
	   */
	  public void registerEventHandler(ChannelEventHandler handler) {
		  this.registerRxListener(handler, Response.class);
	  }
	  
	  /**
	   * Adds an item to the exclusion/inclusion list. This is a per channel list with a maximum
	   * of four entries (0 ... 3). Must call {@link Channel#configureExclusionInclusionList(int, boolean)
	   * to activate.
	   * @param index the index to change
	   * @param id {@link ChannelId} to filter
	   */
	  public void updateExclusionInclusionList(int index, ChannelId id) {
		  	ChannelMessage msg = new AddChannelIdMessage(id.getDeviceNumber(),
		  			id.getDeviceType(),
		  			id.getTransmissonType(), 
		  			index);
			MessageCondition condition = MessageConditionFactory
					.newResponseCondition(msg.getId(),
							ResponseCode.RESPONSE_NO_ERROR);
			try {
				sendAndWaitForMessage(msg, condition, 1L,
						TimeUnit.SECONDS, null);
			} catch (InterruptedException e) {
				handleTimeOutException(e);
			} catch (TimeoutException e) {
				handleTimeOutException(e);
			}
	  }
	  
	  /**
	   * Determines how many ids in the exclusion/inclusion list
	   * and whether it is a black or white list.
	   * 
	   * Must be called before {@link Channel#open()} and only works on slaves. The
	   * behaviour is undefined if used on a master.
	   * @param listSize the number of ids you want to exclude/ include
	   * @param exclude true for blacklist, false fore white-list
	   */
	  public void configureExclusionInclusionList(int listSize, boolean exclude) {
		  	ChannelMessage msg = new ConfigListIdMessage(listSize,exclude);
			MessageCondition condition = MessageConditionFactory
					.newResponseCondition(msg.getId(),
							ResponseCode.RESPONSE_NO_ERROR);
			try {
				sendAndWaitForMessage(msg, condition, 1L,
						TimeUnit.SECONDS, null);
			} catch (InterruptedException e) {
				handleTimeOutException(e);
			} catch (TimeoutException e) {
				handleTimeOutException(e);
			}
	  }
	  
	 /**
	  * Convenience method to update and configure exclusion/inclusion list. Must be called
	  * before {@link Channel#open}
	  * @param ids to black/white list
	  * @param exclude true for blacklist, false fore white
	  */
	 private void configureExclusionInclusionList(ChannelId [] ids, boolean exclude) throws IllegalArgumentException {
		 if (ids.length > 4) {
			 throw new IllegalArgumentException("a maximum of 4 channel ids is permitted");
		 }
		 for (int i = 0 ; i < ids.length ; i++) {
			 updateExclusionInclusionList(i,ids[i]);
		 }
		 configureExclusionInclusionList(ids.length,exclude);
	 }
	 
	 /**
	  * Blacklists {@link ChannelId}s. 
	  * 
	  * You should check the ant chip has the capability to have exclusion/inclusion lists.
	  * 
	  * Must be called after assigning, but before opening channel. Can only be used on slave channels,
	  * attempting to use on a master results in undefined behaviour.
	  * 
	  * @param ids array containing ids to blacklist (max length: 4) or a zero length array to disable.
	  */
	 public void blacklist(ChannelId [] ids) {
		 if (ids == null) {
			 configureExclusionInclusionList(0, true);
			 return;
		 }
		 configureExclusionInclusionList(ids,true);
	 }
	 
	 /**
	  * Whitelist an array of ChannelIds. 
	  * 
	  * For more details see {@link Channel#blacklist(ChannelId[]))
	  * 
	  * @param ids array containing ids to blacklist (max length: 4) or a zero length array to disable.
	  */
	 public void whitelist(ChannelId [] ids) {
		 if (ids == null) {
			 configureExclusionInclusionList(0, false);
			 return;
		 }
		 configureExclusionInclusionList(ids,true);
	 }
	 
	 /**
	  * Waits for response NO_ERROR for a maximum of 1 second
	  * @param msg to send
	  * @throws ChannelError can be caused by TimeoutException, InterruptedException or thrown outright
	  * 		as a response to an error
	  */
	 public void sendAndWaitForResponseNoError(ChannelMessage msg) throws ChannelError {
			MessageCondition condition = MessageConditionFactory
					.newResponseCondition(msg.getId(),
							ResponseCode.RESPONSE_NO_ERROR);
			try {
				sendAndWaitForMessage(msg, condition, 1L,
						TimeUnit.SECONDS, null);
			} catch (InterruptedException e) {
				handleTimeOutException(e);
			} catch (TimeoutException e) {
				handleTimeOutException(e);
			}
	 }
	 
		/**
		 * Sets channel transmit power
		 * See table 9.4.3 in ANT protocol as this is chip dependent.
		 * Maximum powerLevel 4, minimum 0. Some chips only support up to level 3.
		 * @param powerLevel newPowerLevel 
		 */
		public void setTransmitPower(int powerLevel) {
			ChannelMessage msg = new ChannelTxPowerMessage(powerLevel);
			sendAndWaitForResponseNoError(msg);
		}
		
		/**
		 * Sets the proximity search threshold for this channel. 
		 * 
		 * @param threshold new threshold, must be between 0 and 10. A zero value will disable, whilst
		 * 		  larger values will find devices further away
		 */
		public void setProximitySearchThreshold(int threshold) {
			ChannelMessage msg = new ProximitySearchMessage(threshold);
			sendAndWaitForResponseNoError(msg);
		}
		
		/**
		 * Configures frequency agility for this channel. Should be used in conjunction with {@link ExtendedAssignment}.FREQUENCY_AGILITY_ENABLE
		 * at assign time. Do not use with one-way or shared channels. Support chip dependent
		 * 
		 * @param frequency1 The primary operating frequency offset in MHz from 2400MHz. Valid range: 0-124 Mhz
		 * @param frequency2 The secondary operating frequency offset in MHz from 2400MHz. Valid range: 0-124 Mhz
		 * @param frequency3 The tertiary operating frequency offset in MHz from 2400MHz. Valid range: 0-124 Mhz
		 */
		public void configureFrequencyAgility(int frequency1, int frequency2,  int frequency3) {
			ChannelMessage msg = new FrequencyAgilityMessage(frequency1,frequency2,frequency3);
			sendAndWaitForResponseNoError(msg);
		}
		
		/**
		 * Sets a Low priority search timeout
		 * Low priority search does not interrupt other channels; Support chip dependent.
		 * @param timeout timeout in seconds / 2.5. Maximum value: 255, 0 disables.
		 */
		public void setLowPrioirtySearchTimeout(int timeout) {
			ChannelMessage msg = new ChannelLowPrioritySearchTimeoutMessage(timeout);
			sendAndWaitForResponseNoError(msg);
		}
		
		/**
		 * Sets channel search priority. Higher priorities take precedence. This applies
		 * any time a when several channels go to search. Support device specific.
		 * 
		 * @param priority new priority. Default value: 0 ; Range: 0-255 
		 */
		public void setSearchPriority(int priority) {
			ChannelMessage msg = new ChannelSearchPriorityMessage(priority);
			sendAndWaitForResponseNoError(msg);
		}
		
		/**
		 * Listens to all devices matching configured {@link ChannelId} regardless
		 * of radio frequency and period. Two way communication can be achieved by
		 * sending an ExtendedDataMessage with the channel id of the channel you wish to communicate with.
		 * 
		 * All other channels must be closed to use this method. The channel should
		 * be assigned and configured as a slave. 
		 */
		public void openInRxScanMode() {
			ChannelMessage msg = new ChannelOpenRxScanModeMessage();
			sendAndWaitForResponseNoError(msg);
		}
		
}
