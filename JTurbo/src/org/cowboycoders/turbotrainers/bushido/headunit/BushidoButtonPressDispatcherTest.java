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
package org.cowboycoders.turbotrainers.bushido.headunit;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BushidoButtonPressDispatcherTest {
  
  static int numberReceived = 0;
  
  @Before
  public void resetRecieved() {
    numberReceived = 0;
  }
  
  final ButtonPressDispatcher bpd = new ButtonPressDispatcher(new BushidoInternalListener() {

    @Override
    public void onRequestPauseStatus() {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void onButtonPressFinished(BushidoButtonPressDescriptor descriptor) {
      System.out.println(descriptor.getButton());
      System.out.println(descriptor.getDuration());
      System.out.println();
      numberReceived++;
      
    }

    @Override
    public void onRequestData() {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void onRequestKeepAlive() {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void onSpeedChange(double speed) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void onPowerChange(double power) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void onCadenceChange(double cadence) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void onDistanceChange(double distance) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void onHeartRateChange(double heartRate) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void onButtonPressActive(BushidoButtonPressDescriptor descriptor) {
      // TODO Auto-generated method stub
      
    }
    
    
  });

  //@Test
  public void test_trigger_within_window() throws InterruptedException {

    
    Thread t = new Thread() {

      public void run() {
        for (int i = 0 ; i < 8 ; i ++) {
          //if (i % 2 == 0) {
          if (false){
            System.out.println("long");
            bpd.submitButtonPress(BushidoButtonPressDescriptor.fromByte((byte)0xc3));
          //}
          //else if (i % 3 == 0) {
           // bpd.submitButtonPress(BushidoButtonPressDescriptor.fromByte((byte)0x83));
          } else {
            System.out.println("short");
            bpd.submitButtonPress(BushidoButtonPressDescriptor.fromByte((byte)0x03));
          }
          try {
            Thread.sleep(250);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        
      }
      
    };
    
    t.start();
    t.join();
    
    Thread.sleep(280);
    
    // should trigger 1
    assertEquals(1,numberReceived);
    
  }
  
  //@Test
  public void test_trigger_upper() throws InterruptedException {

    
    Thread t = new Thread() {

      public void run() {
        for (int i = 0 ; i < 8 ; i ++) {
          //if (i % 2 == 0) {
          if (false){
            System.out.println("long");
            bpd.submitButtonPress(BushidoButtonPressDescriptor.fromByte((byte)0xc3));
          //}
          //else if (i % 3 == 0) {
           // bpd.submitButtonPress(BushidoButtonPressDescriptor.fromByte((byte)0x83));
          } else {
            System.out.println("short");
            bpd.submitButtonPress(BushidoButtonPressDescriptor.fromByte((byte)0x03));
          }
          try {
            Thread.sleep(280);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        
      }
      
    };
    
    t.start();
    t.join();
    
    Thread.sleep(280);
    
    // should trigger 8
    assertEquals(8,numberReceived);
    
  }
  
  //@Test
  public void test_trigger_lower() throws InterruptedException {

    
    Thread t = new Thread() {

      public void run() {
        for (int i = 0 ; i < 8 ; i ++) {
          //if (i % 2 == 0) {
          if (false){
            System.out.println("long");
            bpd.submitButtonPress(BushidoButtonPressDescriptor.fromByte((byte)0xc3));
          //}
          //else if (i % 3 == 0) {
           // bpd.submitButtonPress(BushidoButtonPressDescriptor.fromByte((byte)0x83));
          } else {
            System.out.println("short");
            bpd.submitButtonPress(BushidoButtonPressDescriptor.fromByte((byte)0x03));
          }
          try {
            Thread.sleep(10);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        
      }
      
    };
    
    t.start();
    t.join();
    
    Thread.sleep(280);
    
    // should trigger 8
    assertEquals(8,numberReceived);
    
  }
  
  @Test
  public void test_trigger_sequence() throws InterruptedException {

    System.out.println("Start test_trigger_sequence\n");
    Thread t = new Thread() {

      public void run() {
        for (int i = 0 ; i < 8 ; i ++) {
          //if (i % 2 == 0) {
          if (i % 3 == 0 && i != 0){
            System.out.println("long");
            bpd.submitButtonPress(BushidoButtonPressDescriptor.fromByte((byte)0xc3));
          }
          else if (i % 2 == 0 && i != 0) {
            System.out.println("medium");
            bpd.submitButtonPress(BushidoButtonPressDescriptor.fromByte((byte)0x83));
          } else {
            System.out.println("short");
            bpd.submitButtonPress(BushidoButtonPressDescriptor.fromByte((byte)0x03));
          }
          try {
            Thread.sleep(10);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        
      }
      
    };
    
    t.start();
    t.join();
    
    Thread.sleep(280);
    
    
  }
  

}
