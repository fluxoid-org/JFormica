package org.cowboycoders.ant.events;

import static org.junit.Assert.*;

import org.junit.Test;

public class BroadcastMesengerTest {

  @Test
  public void test() {
    BroadcastMessenger<Integer> messenger = new BroadcastMessenger<Integer>();
    for(int i=0; i < 10 ; i ++) {
      final int j = i;
      System.out.println(j);
      messenger.addBroadcastListener( new BroadcastListener<Integer>() {
        
        
        @Override
        public void receiveMessage(Integer message) {
          System.out.println(j + " Thread: " + Thread.currentThread().getId());
        }
        
      });
      
      
    }
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    messenger.sendMessage(5);
    messenger.sendMessage(5);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
