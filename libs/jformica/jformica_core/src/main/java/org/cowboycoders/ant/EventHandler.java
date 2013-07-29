package org.cowboycoders.ant;

import org.cowboycoders.ant.events.BroadcastListener;
import org.cowboycoders.ant.messages.responses.Response;

/**
 * Common interface to all event handlers
 * @author will
 *
 */
public interface EventHandler extends BroadcastListener<Response> {

}
