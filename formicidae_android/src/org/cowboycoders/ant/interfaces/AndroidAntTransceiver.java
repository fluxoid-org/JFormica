/*
 * Copyright 2010 Dynastream Innovations Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.cowboycoders.ant.interfaces;

import com.dsi.ant.AntInterface;
import com.dsi.ant.AntInterfaceIntent;
import com.dsi.ant.AntMesg;
import com.dsi.ant.exception.AntInterfaceException;
import com.dsi.ant.exception.AntServiceNotConnectedException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.cowboycoders.ant.events.BroadcastListener;
import org.cowboycoders.ant.events.BroadcastMessenger;
import org.cowboycoders.ant.messages.AntMessageFactory;
import org.cowboycoders.ant.messages.MessageException;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.StandardMessage;
import org.cowboycoders.ant.messages.responses.VersionResponse;



/**
 * Receives ant messages and dispatches that to a {@code BroadcastMessage}}
 * @author will
 *
 */
public class AndroidAntTransceiver extends AbstractAntTransceiver implements AntChipInterface {
  
  private static final String TAG = "AndroidAntTransceiver";
  
  /**
   * Context which controls lifetime
   */
  private Context mContext ;
  
  /**
   * Did we claim the interface? - use {@code lock} 
   */
  private boolean mClaimedAntInterface;
  
  /**
   * Filter to for status related intents
   */
  private IntentFilter statusIntentFilter;

  /**
   * Android ant interface
   */
  private AntInterface mAntReceiver;
  
  /**
   * true if we have connected to {@code AntInterface} - use {@code lock}
   */
  private boolean mServiceConnected = false;
  
  /**
   * class lock
   */
  final Lock lock = new ReentrantLock();
  
  
  /**
   * Notified when connection to {@code AntInterface} is established
   */
  private final Condition notDisconnected  = lock.newCondition();
  
  /**
   * notified when interface claimed
   */
  private final Condition notClaimed  = lock.newCondition();
  
  /**
   * notified when interface enabled
   */
  private final Condition mInterfaceEnabledCondition  = lock.newCondition();
  
  /**
   * Set to true when enabled
   */
  private boolean mInterfaceEnabled = false;
  
  /**
   * notified on start up reset
   */
  private final Condition mAntInitialResetCondition = lock.newCondition();
  
  /**
   * Has ant had initial reset
   */
  private boolean mAntInitialReset = false;
  
  /**
   * Track number of intital resets
   */
  private int mInitialResetCount = 0;
  
  /**
   * wait for this number of resets before returning in start()
   */
  private int mInitialResetMax = 1;
  
  /**
   * notified when message received
   */
  private final Condition mReceivedMessageCondition  = lock.newCondition();
  
  /**
   * flag for spurious wakes
   */
  private boolean mReceivedMessage  = false;
  
  /** String used to represent ant in the radios list. */
  private static final String RADIO_ANT = "ant";
  
  private boolean mRunning = false;
  
  private Condition mRunningChanged = lock.newCondition();
  
  private boolean mWaitingForVersion = true;
  
  private Condition mVersionMessageReceived = lock.newCondition();
  
  
  /**
   * class status messenger
   */
  private BroadcastMessenger<AntStatusUpdate> mStatusMessenger;
  
  
  
  private class StatusListener implements BroadcastListener<AntStatusUpdate> {

    @Override
    public void receiveMessage(AntStatusUpdate message) {
      Log.d(TAG, " in StatusListener");
      try { 
        lock.lock();
        if (message.status == AntStatus.ENABLED) {
          setRunning(true);
        }
        //if (message.status == AntStatus.DISABLED) {
        //  mRunning = false;
        //}
      } finally {
        lock.unlock();
      }
      
    }
    
  }
  //private boolean mResetRequested = true;
  
  public AndroidAntTransceiver(Context context){
    this(context,null,null);
  }
  
  public AndroidAntTransceiver(Context context, BroadcastMessenger<byte[]> rxMessenger, 
      BroadcastMessenger<AntStatusUpdate> statusMessenger) {
    this.registerStatusMessenger(statusMessenger);
    this.registerRxMesenger(rxMessenger);
    this.mContext = context;
    
    mStatusMessenger = new BroadcastMessenger<AntStatusUpdate>();
    
    mStatusMessenger.addBroadcastListener(new StatusListener());
    
    this.registerStatusMessenger(mStatusMessenger);
    
    mClaimedAntInterface = false;
    
    // ANT intent broadcasts.
    statusIntentFilter = new IntentFilter();
    statusIntentFilter.addAction(AntInterfaceIntent.ANT_ENABLED_ACTION);
    statusIntentFilter.addAction(AntInterfaceIntent.ANT_ENABLING_ACTION);
    statusIntentFilter.addAction(AntInterfaceIntent.ANT_DISABLED_ACTION);
    statusIntentFilter.addAction(AntInterfaceIntent.ANT_DISABLING_ACTION);
    statusIntentFilter.addAction(AntInterfaceIntent.ANT_RESET_ACTION);
    statusIntentFilter.addAction(AntInterfaceIntent.ANT_INTERFACE_CLAIMED_ACTION);
    statusIntentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
    mAntReceiver = new AntInterface();
    
  }

  /**
   * @throws AntRadioServiceNotInstalledException if ant radio service not installed
   * @throws AntCommunicationException if there is aproblem communicating with the ant chip
   */
  @Override
  public boolean start() throws AntRadioServiceNotInstalledException, AntCommunicationException {
    
    try  {
      lock.lock();
      if (mRunning) {
        // already started - just ignore
        Log.d(TAG,"Already started - ignoring request to start again");
        return true;
      }
      
      return startHelper();
      
    } finally {
      lock.unlock();
    }
    
  }
  
  private boolean startHelper() throws AntRadioServiceNotInstalledException, AntCommunicationException {
    boolean initialised = false;
    
    if(AntInterface.hasAntSupport(mContext))
    {
      
      
        mContext.registerReceiver(mAntStatusReceiver, statusIntentFilter);
        
        if(!mAntReceiver.initService(mContext, mAntServiceListener))
        {
            // Need the ANT Radio Service installed.
            Log.e(TAG, "AntChannelManager Constructor: No ANT Service.");
            throw new AntRadioServiceNotInstalledException();
        }
        else
        {   

              try {
                
                // make sure mServiceConnected = true
                lock.lock();
                waitForServiceConnected();
                
                if(mServiceConnected)
                {
                    //try
                    //{
                        //mClaimedAntInterface = mAntReceiver.hasClaimedInterface();
                   
                        waitForClaimedInterface();
                        
                        if(mClaimedAntInterface)
                        {   
                            receiveAntRxMessages(true);
                          Log.d(TAG, "AndroidAntTransceiver Constructor: connected.");
                          
                          
                          try {
                            if(!mAntReceiver.isEnabled()) {
                              waitForAntEnabled();
                              waitForInititalReset();
                              waitForRunningUpdate();
                              waitForVersionResponse();
                              waitForRadioQuiet();
                              }
                          } catch (AntInterfaceException e) {
                            throw new AntCommunicationException("unable to verify enabled state");
                          }
                          
                          initialised = true;
                          
                        }
                 
                        
                        
                   // }
                    //catch (AntInterfaceException e)
                    //{   
                    //  Log.d(TAG, "AndroidAntTransceiver Constructor: Antinerface error");
                    //}
                    
                    
                }
              } catch (InterruptedException e) {
                Log.e(TAG, "unable to connect to ant service");
                throw new AntCommunicationException("Unable to connect to ant service",e);
                //return false;
            } catch (TimeoutException e) {
                throw new AntCommunicationException("Timeout waiting for initial" +
                                                    "configuration to complete" ,e);
              } finally  {
                lock.unlock();
              }
            //initialised = true;
        }
    }
    
    Log.d(TAG, "AndroidAntTransceiver Constructor: returning " + initialised);
    return initialised;
  }

  
  
  /**
   * Should hold lock
   * @throws InterruptedException 
   */
  private void waitForServiceConnected() throws TimeoutException, InterruptedException {
    final long oldTimestamp = (long) (System.nanoTime() / Math.pow(10,6));
    while(mServiceConnected == false) {
      final int timeout = 1000;
      notDisconnected.await(timeout, TimeUnit.MILLISECONDS);
      if((System.nanoTime() / Math.pow(10,6)) - oldTimestamp >  timeout) {
        throw new TimeoutException("Timeout waiting for connection");
      }
    }
  }
  
  /**
   * Should hold lock
   * @throws TimeoutException 
   * 
   */
  private void waitForAntEnabled() throws InterruptedException, TimeoutException {
    final long oldTimestamp = (long) (System.nanoTime() / Math.pow(10,6));
    while(mInterfaceEnabled == false) {
      final int timeout = 1000;
      mInterfaceEnabledCondition.await(timeout, TimeUnit.MILLISECONDS);
      if((System.nanoTime() / Math.pow(10,6)) - oldTimestamp >  timeout) {
        throw new TimeoutException("Timeout waiting for ant to be enabled");
      }
    }
  }

  /**
   * Should hold lock
   * @throws InterruptedException
   * @throws TimeoutException 
   */
  private void waitForClaimedInterface() throws InterruptedException, TimeoutException {
      final long oldTimestamp = (long) (System.nanoTime() / Math.pow(10,6));
      while(mClaimedAntInterface == false) {
        final int timeout = 1000;
        notClaimed.await(timeout, TimeUnit.MILLISECONDS);
        if((System.nanoTime() / Math.pow(10,6)) - oldTimestamp >  timeout) {
          throw new TimeoutException("Timeout waiting for interface to be claimed");
        }
      }
  }
  
  /**
   * Should hold lock
   * @throws InterruptedException
   */
  private void waitForInititalReset() throws InterruptedException, TimeoutException {
      final long oldTimestamp = (long) (System.nanoTime() / Math.pow(10,6));
      while(mAntInitialReset == false) {
        final int timeout = 1000;
        mAntInitialResetCondition.await(timeout, TimeUnit.MILLISECONDS);
        if((System.nanoTime() / Math.pow(10,6)) - oldTimestamp >  timeout) {
          throw new TimeoutException("Timeout waiting for inital resets");
        }
      }
  }
  
  /**
   * Should hold lock
   * @throws InterruptedException
   * @throws TimeoutException 
   */
  private void waitForRunningUpdate() throws InterruptedException, TimeoutException {
      final long oldTimestamp = (long) (System.nanoTime() / Math.pow(10,6));
      while(mRunning== false) {
        final int timeout = 1000;
        mRunningChanged.await(timeout, TimeUnit.MILLISECONDS);
        if((System.nanoTime() / Math.pow(10,6)) - oldTimestamp >  timeout) {
          throw new TimeoutException("Timeout waiting for running to be enabled");
        }
      }
  }
  
  /**
   * Should hold lock
   * @throws InterruptedException
   * @throws TimeoutException 
   */
  private void waitForVersionResponse() throws InterruptedException, TimeoutException {
      final long oldTimestamp = (long) (System.nanoTime() / Math.pow(10,6));
      while(this.mWaitingForVersion) {
        final int timeout = 1000;
        this.mVersionMessageReceived.await(timeout, TimeUnit.MILLISECONDS);
        if((System.nanoTime() / Math.pow(10,6)) - oldTimestamp >  timeout) {
          throw new TimeoutException("Timeout waiting for running to be enabled");
        }
      }
  }
  
  private void waitForRadioQuiet() throws InterruptedException {
    try {
       while(true){
         long oldTimestamp = (long) (System.nanoTime() / Math.pow(10,6));
          while(!this.mReceivedMessage) {
            final int timeout = 5;
            this.mReceivedMessageCondition.await(timeout, TimeUnit.MILLISECONDS);
            if((System.nanoTime() / Math.pow(10,6)) - oldTimestamp >  timeout) {
              throw new TimeoutException("Timeout waiting for running to be enabled");
            }
          }
          this.mReceivedMessage = false;
       }
    } catch (TimeoutException e) {
      Log.d(TAG, "Radio gone quiet");
    }
}

  @Override
  public void stop() {
    this.selfShutDown();
  }

  @Override
  public void send(byte[] message) throws AntCommunicationException  {
    try {
      lock.lock();
      Log.d(TAG,"Sending a packet");
      mAntReceiver.ANTTxMessage(message);
    } catch (AntInterfaceException e) {
      throw new AntCommunicationException(e);
    } finally {
      lock.unlock();
    }
    
    Log.d(TAG,"Sent a packet");
    
  }
  

  /** Receives all of the ANT status intents. */
  private final BroadcastReceiver mAntStatusReceiver = new BroadcastReceiver() 
  {      

    public void onReceive(Context context, Intent intent) 
     {
        String ANTAction = intent.getAction();

        Log.d(TAG, "enter onReceive: " + ANTAction);
        if (ANTAction.equals(AntInterfaceIntent.ANT_ENABLING_ACTION))
        {
            Log.i(TAG, "onReceive: ANT ENABLING");
            broadcastStatus(AntStatus.ENABLING);
            //mEnabling = true;
            ////mDisabling = false;
            //mAntStateText = mContext.getString(R.string.Text_Enabling);
            //if(mCallbackSink != null)
              //  mCallbackSink.notifyAntStateChanged();
        }
        else if (ANTAction.equals(AntInterfaceIntent.ANT_ENABLED_ACTION)) 
        {
           Log.i(TAG, "onReceive: ANT ENABLED");
           
           try {
             lock.lock();
             mInterfaceEnabled = true;
             mInterfaceEnabledCondition.signalAll();
             
           } finally {
             lock.unlock();
           }
           
           broadcastStatus(AntStatus.ENABLED);
           
           //mEnabling = false;
           //mDisabling = false;
           //if(mCallbackSink != null)
             //  mCallbackSink.notifyAntStateChanged();
        }
        else if (ANTAction.equals(AntInterfaceIntent.ANT_DISABLING_ACTION))
        {
            Log.i(TAG, "onReceive: ANT DISABLING");
            //mEnabling = false;
            //mDisabling = true;
            //mAntStateText = mContext.getString(R.string.Text_Disabling);
            //if(mCallbackSink != null)
              //  mCallbackSink.notifyAntStateChanged();
            broadcastStatus(AntStatus.DISABLING);
        }
        else if (ANTAction.equals(AntInterfaceIntent.ANT_DISABLED_ACTION)) 
        {
           Log.i(TAG, "onReceive: ANT DISABLED");
           
           try {
             lock.lock();
             mInterfaceEnabled = false;
             
           } finally {
             lock.unlock();
           }
           
           broadcastStatus(AntStatus.DISABLED,DisableReason.OTHER_SHUTDOWN);
           //mHrmState = ChannelStates.CLOSED;
           //mSdmState = ChannelStates.CLOSED;
           //mWeightState = ChannelStates.CLOSED;
           //mAntStateText = mContext.getString(R.string.Text_Disabled);
           
           //mEnabling = false;
           //mDisabling = false;
           
           //if(mCallbackSink != null)
           //{
              // mCallbackSink.notifyChannelStateChanged(WEIGHT_CHANNEL);
             //  mCallbackSink.notifyChannelStateChanged(SDM_CHANNEL);
               //mCallbackSink.notifyChannelStateChanged(HRM_CHANNEL);
               //mCallbackSink.notifyAntStateChanged();
          // }
           //Log.i(TAG, "Stopping service.");
           //mContext.stopService(new Intent(mContext, ANTPlusService.class));
        }
        else if (ANTAction.equals(AntInterfaceIntent.ANT_RESET_ACTION))
        {
           Log.d(TAG, "onReceive: ANT RESET");
           
           //Log.i(TAG, "Stopping service.");
           //mContext.stopService(new Intent(mContext, ANTPlusService.class));
           try {
             lock.lock();
             if(mAntInitialReset == false)
             {
               mInitialResetCount++;
               if (mInitialResetCount >= mInitialResetMax) {
                 mAntInitialReset = true;
                 mAntInitialResetCondition.signalAll();
               }
               /*
                //Someone else triggered an ANT reset
                Log.d(TAG, "onReceive: ANT RESET: Resetting state");
                
                if(mHrmState != ChannelStates.CLOSED)
                {
                   mHrmState = ChannelStates.CLOSED;
                   if(mCallbackSink != null)
                       mCallbackSink.notifyChannelStateChanged(HRM_CHANNEL);
                }
                
                if(mSdmState != ChannelStates.CLOSED)
                {
                   mSdmState = ChannelStates.CLOSED;
                   if(mCallbackSink != null)
                       mCallbackSink.notifyChannelStateChanged(SDM_CHANNEL);
                }
                
                if(mWeightState != ChannelStates.CLOSED)
                {
                   mWeightState = ChannelStates.CLOSED;
                   if(mCallbackSink != null)
                       mCallbackSink.notifyChannelStateChanged(WEIGHT_CHANNEL);
                }
                */
             }
             else
             {   
               broadcastStatus(AntStatus.RESET);
                 //mResetRequested = false;
                 //receiveAntRxMessages(true);
                //mAntResetSent = false;
                //Reconfigure event buffering
                //setAntConfiguration();
                //Check if opening a channel was deferred, if so open it now.
                //if(mDeferredHrmStart)
                //{
                 //   openChannel(HRM_CHANNEL, false);
                //    mDeferredHrmStart = false;
                //}
               // if(mDeferredSdmStart)
               // {
                //    openChannel(SDM_CHANNEL, false);
                 //   mDeferredSdmStart = false;
               // }
                //if(mDeferredWeightStart)
               // {
                    //openChannel(WEIGHT_CHANNEL, false);
                    //mDeferredWeightStart = false;
                //}
             }
             
             
           } finally {
             lock.unlock();
           }
           
        }
        else if (ANTAction.equals(AntInterfaceIntent.ANT_INTERFACE_CLAIMED_ACTION)) 
        {
           Log.i(TAG, "onReceive: ANT INTERFACE CLAIMED");
           
           boolean wasClaimed;
           
           // Could also read ANT_INTERFACE_CLAIMED_PID from intent and see if it matches the current process PID.
           try
           {   lock.lock();
               wasClaimed = mClaimedAntInterface;
               mClaimedAntInterface = mAntReceiver.hasClaimedInterface();

               if(mClaimedAntInterface)
               {
                   Log.i(TAG, "onReceive: ANT Interface claimed");
                   
                   notClaimed.signalAll();

                   receiveAntRxMessages(true);
               }
               else
               {
                   // Another application claimed the ANT Interface...
                   if(wasClaimed)
                   {
                       // ...and we had control before that.  
                       Log.i(TAG, "onReceive: ANT Interface released");
                       
                       Log.i(TAG, "Stopping service.");
                       
                       receiveAntRxMessages(false);
                       
                       shutDown();
                       broadcastStatus(AntStatus.DISABLED,DisableReason.INTERFACE_CLAIMED);
                       //mContext.stopService(new Intent(mContext, ANTPlusService.class));

                      // receiveAntRxMessages(false);
                       
                       //mAntStateText = mContext.getString(R.string.Text_ANT_In_Use);
                       //if(mCallbackSink != null)
                          // mCallbackSink.notifyAntStateChanged();
                   }
               }
           }
           catch(AntInterfaceException e)
           {
               throw new AntCommunicationException(e);
           } finally {
             lock.unlock();
           }
        }
        else if (ANTAction.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED))
        {
            Log.i(TAG, "onReceive: AIR_PLANE_MODE_CHANGED");
            if(isAirPlaneModeOn())
            {
                try {
                  lock.lock();
                  mInterfaceEnabled = false;
                  
                } finally {
                  lock.unlock();
                }
                
                shutDown();
                broadcastStatus(AntStatus.DISABLED,DisableReason.AIRPLANE_MODE_ENABLED);
                
                //Log.i(TAG, "Stopping service.");
               // mContext.stopService(new Intent(mContext, ANTPlusService.class));
                
                //if(mCallbackSink != null)
                //{
                //    mCallbackSink.notifyChannelStateChanged(WEIGHT_CHANNEL);
                //    mCallbackSink.notifyChannelStateChanged(SDM_CHANNEL);
                //    mCallbackSink.notifyChannelStateChanged(HRM_CHANNEL);
                //    mCallbackSink.notifyAntStateChanged();
                //}
            //}
            //else
            //{
            //    if(mCallbackSink != null)
            //        mCallbackSink.notifyAntStateChanged();
            }
        }
        //if(mCallbackSink != null)
          //  mCallbackSink.notifyAntStateChanged();
     }
    
  };
  
  /**
   * Class for receiving notifications about ANT service state.
   */
  private AntInterface.ServiceListener mAntServiceListener = new AntInterface.ServiceListener()
  {
      public void onServiceConnected()
      {
          Log.d(TAG, "mAntServiceListener onServiceConnected()");
          
          try
          {
              lock.lock();
              mServiceConnected = true;
              notDisconnected.signalAll();
              
              mClaimedAntInterface = mAntReceiver.hasClaimedInterface();

              if (mClaimedAntInterface)
              {
                Log.d(TAG, "service connected - already claimed");
                  // mAntMessageReceiver should be registered any time we have
                  // control of the ANT Interface
                  receiveAntRxMessages(true);
                  
              } else
              {
                  // Need to claim the ANT Interface if it is available, now
                  // the service is connected
                  mClaimedAntInterface = mAntReceiver.claimInterface();
                  Log.d(TAG, "service connected - claimed interface");
              }
          } catch (AntInterfaceException e)
          {
            throw new AntCommunicationException(e);
          } finally {
            lock.unlock();
          }

          Log.d(TAG, "mAntServiceListener Displaying icons only if radio enabled");
          //if(mCallbackSink != null)
             // mCallbackSink.notifyAntStateChanged();
      }

      public void onServiceDisconnected()
      {
          Log.d(TAG, "mAntServiceListener onServiceDisconnected()");

          //mServiceConnected = false;
          
          try { 
            lock.lock();
            
            mServiceConnected = false;
            
            if (mClaimedAntInterface)
            {
                receiveAntRxMessages(false);
            }
            
            
          } finally {
            lock.unlock();
          }



          //if(mCallbackSink != null)
            //  mCallbackSink.notifyAntStateChanged();
      }
  };
  
  public boolean isAirPlaneModeOn() {
    if(!Settings.System.getString(mContext.getContentResolver(),
        Settings.System.AIRPLANE_MODE_RADIOS).contains(RADIO_ANT))
    return false;
    if(Settings.System.getInt(mContext.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 0)
        return false;
    

        Field field;
        try {
          field = Settings.System.class.getField("AIRPLANE_MODE_TOGGLEABLE_RADIOS");
          if(Settings.System.getString(mContext.getContentResolver(),
              (String) field.get(null)).contains(RADIO_ANT))
            return false;
          else
            return true;
        } catch (NoSuchFieldException e) {
            Log.w(TAG,"NoSuchFieldException: assuming airplane mode enabled");
            return true;
        } catch (IllegalAccessException e) {
          Log.w(TAG,"IllegalAccessException: assuming airplane mode enabled");
            return true;
        }


  }
  
  /**
   * Enable/disable receiving ANT Rx messages.
   *
   * @param register If want to register to receive the ANT Rx Messages
   */
  private void receiveAntRxMessages(boolean register)
  {
      if(register)
      {
          Log.i(TAG, "receiveAntRxMessages: START");
          mContext.registerReceiver(mAntMessageReceiver, new IntentFilter(AntInterfaceIntent.ANT_RX_MESSAGE_ACTION));
      }
      else
      {
          try
          {
              mContext.unregisterReceiver(mAntMessageReceiver);
          }
          catch(IllegalArgumentException e)
          {
              // Receiver wasn't registered, ignore as that's what we wanted anyway
          }

          Log.i(TAG, "receiveAntRxMessages: STOP");
      }
  }  
  
  
  
  private final BroadcastReceiver mAntMessageReceiver = new BroadcastReceiver() 
  {      
     public void onReceive(Context context, Intent intent) 
     {
        String ANTAction = intent.getAction();

        Log.d(TAG, "enter onReceive: " + ANTAction);
        if (ANTAction.equals(AntInterfaceIntent.ANT_RX_MESSAGE_ACTION)) 
        {
           Log.d(TAG, "onReceive: ANT RX MESSAGE");

           byte[] ANTRxMessage = intent.getByteArrayExtra(AntInterfaceIntent.ANT_MESSAGE);
           
           responseEventHandler(ANTRxMessage);
           
        }
     }
  };
     
     /**
      * Handles response and channel event messages
      * @param ANTRxMessage
      */
     private void responseEventHandler(byte[] ANTRxMessage)
     {
   
       try {
        lock.lock();
        
        this.mReceivedMessage = true;
        this.mReceivedMessageCondition.signalAll();
        
        if(mWaitingForVersion) {
          StandardMessage msg = null;
          try {
           msg = AntMessageFactory.createMessage(ANTRxMessage);
         } catch (MessageException e) {
           Log.w(TAG,"Error converting raw data to type StandardMessage");
         }
          if(msg != null && msg instanceof VersionResponse) {
            mVersionMessageReceived.signalAll();
            mWaitingForVersion = false;
          } 
        }
        
        broadcastRxMessage(ANTRxMessage);
        
       } finally {
         lock.unlock();
       }
     }
     
 private void shutDown() {
   
   try
   {
       mContext.unregisterReceiver(mAntStatusReceiver);
   }
   catch(IllegalArgumentException e)
   {
       // Receiver wasn't registered, ignore as that's what we wanted anyway
   }
   
   receiveAntRxMessages(false);
   

       try
       {   
           lock.lock();
           if(mServiceConnected)
           {
             if(mClaimedAntInterface)
             {
                 Log.d(TAG, "AntChannelManager.shutDown: Releasing interface");

                 mAntReceiver.releaseInterface();
             }

             mAntReceiver.stopRequestForceClaimInterface();
             
         }
           
           setRunning(false);
           mAntReceiver.releaseService();
           
           while(mAntReceiver.isServiceConnected()) {
             Thread.sleep(100);
           }
           
           
       }
       catch(AntServiceNotConnectedException e)
       {
           // Ignore as we are disconnecting the service/closing the app anyway
       }
       catch(AntInterfaceException e)
       {
          Log.w(TAG, "Exception in AntChannelManager.shutDown", e);
       } catch (InterruptedException e) {
         throw new AntCommunicationException("Interuppted waiting for radio to go quiet ",e);
      }
       finally {
         lock.unlock();
       }
       
   }
 
 private void setRunning(boolean enabled) {
   mRunning = enabled;
   mRunningChanged.signalAll();
   if (!enabled) {

     mInterfaceEnabled = false;
     

     mAntInitialReset = false;
     

     mInitialResetCount = 0;
     

     mInitialResetMax = 1;
     

     
     mReceivedMessage  = false;
     

     
     mRunning = false;
     

     
     mWaitingForVersion = true;
     
     //mStatusMessenger = new BroadcastMessenger<AntStatusUpdate>();
     
     //mStatusMessenger.addBroadcastListener(new StatusListener());
     
     //this.registerStatusMessenger(mStatusMessenger);
     
     mClaimedAntInterface = false;
     
     //mAntReceiver = new AntInterface();
     
     
   }
 }
  
  /**
   * Unregisters all our receivers in preparation for application shutdown
   */
  private void selfShutDown()
  {   
      try {
        lock.lock();
        
        broadcastStatus(AntStatus.DISABLED,DisableReason.SELF_SHUTDOWN);
        shutDown();
        
      } finally {
        lock.unlock();
      }
      
      
  }

  
}
