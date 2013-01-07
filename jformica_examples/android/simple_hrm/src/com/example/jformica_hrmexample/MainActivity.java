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
import org.cowboycoders.ant.interfaces.ServiceAlreadyClaimedException;
import org.cowboycoders.ant.messages.SlaveChannelType;
import org.cowboycoders.ant.messages.data.BroadcastDataMessage;

import com.example.jformica_hrmexample.R;

enum State {
  CONNECTED, DISCONNECTED,
}

public class MainActivity extends Activity {
  public static String TAG = "Formica Hrm Ex";

  private Channel channel;
  private Node node;
  private Handler handler = new Handler();
  private Lock mAntLock = new ReentrantLock();
  private Lock mUpdateLock = new ReentrantLock();
  private Lock mStateLock = new ReentrantLock();
  private State mState = State.DISCONNECTED;
  private boolean updateHrm = false;
  private ExecutorService executor = Executors.newSingleThreadExecutor();

  /** Called when the user clicks the Send button */
  public void onConnect(View view) {
    final Button button = (Button) view;
    final String connectString = getString(R.string.connect);
    final String disconnectString = getString(R.string.disconnect);
    Thread t;
    State newState = null;
    if ((button).getText().equals(connectString)) {
      newState = State.CONNECTED;
      t = new Thread() {

        @Override
        public void run() {
          try {
            mAntLock.lock();
            AndroidAntTransceiver antchip = new AndroidAntTransceiver(
                MainActivity.this.getApplicationContext());

            MainActivity.this.node = new Node(antchip);

            NetworkKey key = new NetworkKey(0xB9, 0xA5, 0x21, 0xFB, 0xBD, 0x72,
                0xC3, 0x45);
            key.setName("N:ANT+");

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

            node.setNetworkKey(0, key);

            MainActivity.this.channel = node.getFreeChannel();

            channel.setName("C:HRM");

            SlaveChannelType channelType = new SlaveChannelType();
            channel.assign("N:ANT+", channelType);

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
                Log.e(TAG, "connect");
                button.setText(disconnectString);
              }

            });

            try {
              mUpdateLock.lock();
              updateHrm = true;
            } finally {
              mUpdateLock.unlock();
            }

          } catch (final AntRadioServiceNotInstalledException e) {
            handler.post(new Runnable() {
              @Override
              public void run() {
                exceptionToToast(e);
              }

            });

          } catch (final AntCommunicationException e) {
            handler.post(new Runnable() {

              @Override
              public void run() {
                exceptionToToast(e);
              }

            });

          } finally {
            mAntLock.unlock();
          }
        }

      };

    } else {
      newState = State.DISCONNECTED;
      t = new Thread() {
        @Override
        public void run() {
          try {
            mAntLock.lock();

            if (channel == null) { return; }

            try {
              channel.close();
            } catch (ChannelError e) {
              // ant probbaly not connected
              Log.e(TAG, e.toString());
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
                TextView hrmView = (TextView) findViewById(R.id.hrmView);
                try {
                  mUpdateLock.lock();
                  updateHrm = false;
                  hrmView.setText(R.string.no_hrm);
                } finally {
                  mUpdateLock.unlock();
                }

                button.setText(connectString);
                Log.e(TAG, "disconnect");

              }

            };

            handler.post(r);

          } finally {
            mAntLock.unlock();
          }

        }
      };

    }

    try {
      mStateLock.lock();
      // Ensure we don't enqueue two transitions to same state.
      // Would occur if someone were to hammer to connect/disconnect button
      // before the text changed
      if (newState != mState) {
        executor.execute(t);
        mState = newState;
      }
    } finally {
      mStateLock.unlock();
    }

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
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
        node.stop();
      } catch (AntError e) {
        Log.e(TAG, e.toString());
      }
    }

  }

  private void exceptionToToast(Exception e) {
    StringWriter writer = new StringWriter();
    writer.append(("Caught Exception: "));
    writer.append(e.toString());
    writer.append("\n\n");
    e.printStackTrace(new PrintWriter(writer));
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
