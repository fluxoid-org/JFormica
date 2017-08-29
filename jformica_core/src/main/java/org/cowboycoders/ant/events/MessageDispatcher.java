package org.cowboycoders.ant.events;

/**
 * Created by fluxoid on 05/01/17.
 */
public interface MessageDispatcher<V> {

	void addBroadcastListener(BroadcastListener<V> listener);

	void removeBroadcastListener(BroadcastListener<V> listener);

	int getListenerCount();

	void sendMessage(V message);
}
