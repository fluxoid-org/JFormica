package org.cowboycoders.ant.events;

import org.junit.Test;

/**
 * Created by fluxoid on 8/12/17.
 */

public class BroadcastMessengerTest {


  @Test(timeout=500)
  public void removeSelfShouldNotDeadlock() {
    final BroadcastMessenger<Object> bus = new BroadcastMessenger<>();
    bus.addBroadcastListener(new BroadcastListener<Object>() {

      @Override
      public void receiveMessage(Object message) {
        bus.removeBroadcastListener(this);
      }
    });

    bus.sendMessage(new Object());
  }


}
