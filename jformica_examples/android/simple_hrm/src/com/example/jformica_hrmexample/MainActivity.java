package com.example.jformica_hrmexample;

import android.os.Bundle;

import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.cowboycoders.ant.AntError;
import org.cowboycoders.ant.Channel;
import org.cowboycoders.ant.ChannelError;
import org.cowboycoders.ant.NetworkKey;
import org.cowboycoders.ant.Node;
import org.cowboycoders.ant.events.BroadcastListener;
import org.cowboycoders.ant.interfaces.AndroidAntTransceiver;
import org.cowboycoders.ant.interfaces.AntCommunicationException;
import org.cowboycoders.ant.interfaces.AntRadioServiceNotInstalledException;
import org.cowboycoders.ant.interfaces.AntStatus;
import org.cowboycoders.ant.interfaces.AntStatusUpdate;
import org.cowboycoders.ant.interfaces.DisableReason;
import org.cowboycoders.ant.interfaces.ServiceAlreadyClaimedException;
import org.cowboycoders.ant.messages.SlaveChannelType;
import org.cowboycoders.ant.messages.data.BroadcastDataMessage;

import com.example.jformica_hrmexample.R;

public class MainActivity extends Activity {
  public static String TAG = "Formica Hrm Ex";
  private static final NetworkKey ANT_SPORT = new NetworkKey (0xB9,0xA5,0x21,0xFB,0xBD,0x72,0xC3,0x45);
  private Channel channel;
  private Node node;
  private Handler handler = new Handler();
  private Lock mAntLock = new ReentrantLock();
  private Lock mUpdateLock = new ReentrantLock();
  private Lock mStateLock = new ReentrantLock();
  private State mState = State.DISCONNECTED;
  private boolean updateHrm = false;
  private ExecutorService executor = Executors.newSingleThreadExecutor();
  private String connectString;
  private String disconnectString;
  private Button connectButton;

  enum State {
    CONNECTED, DISCONNECTED,
  }

  private BroadcastListener<AntStatusUpdate> listener = new BroadcastListener<AntStatusUpdate>() {

    @Override
    public void receiveMessage(AntStatusUpdate status) {
      if (status.status == AntStatus.DISABLED) {
        final DisableReason r = (DisableReason) status.optionalArg;
        Log.w(TAG, "Disabled with reason: " + r.toString());
        handler.post(new Runnable() {

          @Override
          public void run() {
            if (r == DisableReason.POWER_OFF) {
              showToast("Ant chip powered off", Toast.LENGTH_LONG);
              disconnect();
            }
            else if (r == DisableReason.INTERFACE_CLAIMED) {
              showToast("Ant chip claimed by someone else", Toast.LENGTH_LONG);
              disconnect();
            }

            try {
              mStateLock.lock();
              mState = State.DISCONNECTED;
            } finally {
              mStateLock.unlock();
            }
          }

        });
      }

    }

  };

  /** Called when the user clicks the Send button */
  public void onConnect(View view) {
    Thread t;
    if ((connectButton).getText().equals(connectString)) {
      t = new Thread() {

        @Override
        public void run() {
          try {
            Log.e(TAG, "connect");
            mAntLock.lock();
            mStateLock.lock();
            
            if (mState == MainActivity.State.CONNECTED) {
              return;
            }

            // assume disconnected until we complete
            MainActivity.this.mState = MainActivity.State.DISCONNECTED;

            AndroidAntTransceiver antchip = new AndroidAntTransceiver(
                MainActivity.this.getApplicationContext());

            MainActivity.this.node = new Node(antchip);

            NetworkKey key = new NetworkKey(0xB9, 0xA5, 0x21, 0xFB, 0xBD, 0x72,
                0xC3, 0x45);

            node.registerStatusListener(MainActivity.this.listener);

            try {

              node.start();
            } catch (ServiceAlreadyClaimedException e) {
              antchip.requestForceClaimInterface(TAG);
              // After force claim you have to press connect again
              // as I don't think there is a way is way to receive
              // user response
              throw e;
            }

            node.reset();

            MainActivity.this.channel = node.getFreeChannel();

            channel.setName("C:HRM");

            SlaveChannelType channelType = new SlaveChannelType();
            channel.assign(ANT_SPORT, channelType);

            channel.registerRxListener(new Listener(),
                BroadcastDataMessage.class);

            channel.setId(0, 120, 0, false);

            channel.setFrequency(57);

            channel.setPeriod(8070);

            channel.setSearchTimeout(255);

            channel.open();

            handler.post(new Runnable() {

              @Override
              public void run() {
                connectButton.setText(disconnectString);
              }

            });

            try {
              mUpdateLock.lock();
              updateHrm = true;
            } finally {
              mUpdateLock.unlock();
            }

            MainActivity.this.mState = MainActivity.State.CONNECTED;

          } catch (final AntRadioServiceNotInstalledException e) {
            handler.post(new Runnable() {
              @Override
              public void run() {
                exceptionToToast(e);
                try {
                  mStateLock.lock();
                  mState = MainActivity.State.DISCONNECTED;
                } finally {
                  mStateLock.unlock();
                }
                disconnect();
              }

            });

          } catch (final AntCommunicationException e) {
            handler.post(new Runnable() {

              @Override
              public void run() {
                exceptionToToast(e);
                try {
                  mStateLock.lock();
                  mState = MainActivity.State.DISCONNECTED;
                } finally {
                  mStateLock.unlock();
                }
                disconnect();
              }

            });

          } catch (final ChannelError e) {
            handler.post(new Runnable() {

              @Override
              public void run() {
                exceptionToToast(e);
                try {
                  mStateLock.lock();
                  mState = MainActivity.State.DISCONNECTED;
                } finally {
                  mStateLock.unlock();
                }
                disconnect();
              }

            });

          }catch (final AntError e) {
            handler.post(new Runnable() {

              @Override
              public void run() {
                exceptionToToast(e);
                try {
                  mStateLock.lock();
                  mState = MainActivity.State.DISCONNECTED;
                } finally {
                  mStateLock.unlock();
                }
                disconnect();
              }

            });

          } finally {
            mAntLock.unlock();
            mStateLock.unlock();
          }
        }

      };

    } else {
      t = new Thread() {
        @Override
        public void run() {
          try {
            mAntLock.lock();
            mStateLock.lock();
            
            if (mState == MainActivity.State.DISCONNECTED) {
              return;
            }
            Log.e(TAG, "disconnect");

            if (channel == null) { return; }

            try {
              channel.close();
            } catch (ChannelError e) {
              // ant probbaly not connected
              Log.e(TAG, e.toString());
            } catch (final AntCommunicationException e) {
              // must likely to occur if usb stick unplugged /
              // a force claim has occurred behind our backs
              handler.post(new Runnable() {

                @Override
                public void run() {
                  exceptionToToast(e);
                }

              });
            }

            node.freeChannel(channel);

            try {

              node.stop();

            } catch (AntError e) {
              // if already stopped throws an exception
              Log.e(TAG, e.toString());
            }
            
            node = null;
            channel = null;

            Runnable r = new Runnable() {

              @Override
              public void run() {
                MainActivity.this.disconnect();
              }

            };

            handler.post(r);
            

          } finally {
            mState = MainActivity.State.DISCONNECTED;
            mAntLock.unlock();
            mStateLock.unlock();
          }

        }
      };

    }


        executor.execute(t);


  }

  private void disconnect() {
    TextView hrmView = (TextView) findViewById(R.id.hrmView);
    try {
      mUpdateLock.lock();
      updateHrm = false;
      hrmView.setText(R.string.no_hrm);
    } finally {
      mUpdateLock.unlock();
    }
    
    try{
      mAntLock.lock();
      if (node != null) {
        node.stop();
      }
      
    }finally {
      mAntLock.unlock();
    }

    connectButton.setText(connectString);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    connectString = getString(R.string.connect);
    disconnectString = getString(R.string.disconnect);
    connectButton = (Button) findViewById(R.id.connectButton);

  }

  class Listener implements BroadcastListener<BroadcastDataMessage> {

    @Override
    public void receiveMessage(final BroadcastDataMessage message) {
      handler.post(new Runnable() {
        @Override
        public void run() {
          try {
            mUpdateLock.lock();
            if (updateHrm) {
              TextView hrmView = (TextView) findViewById(R.id.hrmView);
              hrmView.setText(message.getData()[7].toString());
            }
          } finally {
            mUpdateLock.unlock();
          }

        }
      });

    }

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.activity_main, menu);
    return true;
  }

  /*
   * (non-Javadoc)
   * @see android.app.Activity#onDestroy()
   */
  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (node != null) {
      try {
        mAntLock.unlock();
        node.stop();
      } catch (AntError e) {
        Log.e(TAG, e.toString());
      } finally {
        mAntLock.unlock();
      }
    }

  }

  private void exceptionToToast(Exception e) {
    StringWriter writer = new StringWriter();
    writer.append(("Caught Exception: "));
    writer.append(e.toString());
    writer.append("\n\n");
    e.printStackTrace(new PrintWriter(writer));
    Log.e(TAG,writer.getBuffer().toString());
    showToast(writer.getBuffer(), Toast.LENGTH_LONG);
  }

  private void showToast(CharSequence text, Integer duration) {
    Context context = getApplicationContext();
    duration = (duration == null) ? Toast.LENGTH_SHORT : duration;
    Toast toast = Toast.makeText(context, text, duration);
    toast.show();
  }

  /*
   * (non-Javadoc)
   * @see android.app.Activity#onPause()
   */
  @Override
  protected void onPause() {
    super.onPause();
    Log.i(TAG, " onPause");
  }

  /*
   * (non-Javadoc)
   * @see android.app.Activity#onResume()
   */
  @Override
  protected void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
    Log.i(TAG, " onResume");
  }

  /*
   * (non-Javadoc)
   * @see android.app.Activity#onStop()
   */
  @Override
  protected void onStop() {
    // TODO Auto-generated method stub
    super.onStop();
    Log.i(TAG, " onStop");
  }

}
