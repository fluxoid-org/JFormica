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
package org.cowboycoders.cyclisimo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import org.cowboycoders.cyclisimo.content.Bike;
import org.cowboycoders.cyclisimo.fragments.CourseSetupFragment;
import org.cowboycoders.cyclisimo.fragments.CourseSetupFragment.CourseSetupObserver;
import org.cowboycoders.cyclisimo.services.ITrackRecordingService;
import org.cowboycoders.cyclisimo.services.TrackRecordingServiceConnection;
import org.cowboycoders.cyclisimo.turbo.TurboService;
import org.cowboycoders.cyclisimo.util.IntentUtils;


public class CourseSetupActivity extends Activity {
  
  public CourseSetupActivity() {
    super();
  }
  
  private static final String TAG = "CourseSetupActivty";
  private CourseSetupObserver courseSetupObserver;
  
  /**
   * long reference assignment non atomic
   * @return the trackId
   */
  private synchronized Long getTrackId() {
    return trackId;
  }

  /**
   * long reference assignment non atomic
   * @param trackId the trackId to set
   */
  private synchronized void setTrackId(Long trackId) {
    Log.d(TAG, "setTrackID"  + trackId);
    this.trackId = trackId;
  }

  /**
   * @return the modeString
   */
  private String getModeString() {
    return modeString;
  }

  /**
   * @param modeString the modeString to set
   */
  private  void setModeString(String modeString) {
    this.modeString = modeString;
  }

  protected Long trackId;
  protected String modeString;
  private Button goButton;
  private boolean mIsBound;
  
  private ServiceConnection mConnection = new ServiceConnection() {

    public void onServiceConnected(ComponentName className, IBinder binder) {
      TurboService s = ((TurboService.TurboBinder) binder).getService();
      s.start(getTrackId(),CourseSetupActivity.this);
      Toast.makeText(CourseSetupActivity.this, "Connected to turbo service",
          Toast.LENGTH_SHORT).show();
      // no longer needed
      doUnbindService();
      startRecording();
    }

    public void onServiceDisconnected(ComponentName className) {
      Toast.makeText(CourseSetupActivity.this, "Disconnected from turbo service",
          Toast.LENGTH_SHORT).show();
    }
  };

  
  void doBindService() {
    bindService(new Intent(this, TurboService.class), mConnection,
        Context.BIND_AUTO_CREATE);
    mIsBound = true;
  }
  
  void doUnbindService() {
    if (mIsBound) {
        // Detach our existing connection.
        unbindService(mConnection);
        mIsBound = false;
    }
}
  
  private void startServiceInBackround() {
    Intent intent = new Intent(this, TurboService.class);
    this.startService(intent);
  }
  
  
  private TrackRecordingServiceConnection trackRecordingServiceConnection;
  
  private Runnable bindChangedCallback = new Runnable() {

    @Override
    public void run() {
      
      boolean success = true;
      
      if (!startNewRecording) {
        return;
      }

      ITrackRecordingService service = trackRecordingServiceConnection.getServiceIfBound();
      if (service == null) {
        Log.d(TAG, "service not available to start a new recording");
        return;
      }
      try {
        long id = service.startNewTrack();
        service.pauseCurrentTrack();
        startNewRecording = false;
        Intent intent = IntentUtils.newIntent(CourseSetupActivity.this, TrackDetailActivity.class)
            .putExtra(TrackDetailActivity.EXTRA_TRACK_ID, id)
            .putExtra(TrackDetailActivity.EXTRA_USE_COURSE_PROVIDER, false)
            .putExtra(TrackDetailActivity.EXTRA_COURSE_TRACK_ID, trackId);
        startActivity(intent);
        Toast.makeText(
            CourseSetupActivity.this, R.string.track_list_record_success, Toast.LENGTH_SHORT).show();
      } catch (Exception e) {
        Toast.makeText(CourseSetupActivity.this, R.string.track_list_record_error, Toast.LENGTH_LONG)
            .show();
        Log.e(TAG, "Unable to start a new recording.", e);
        success = false;
      } 
      
      CourseSetupActivity.this.finish(success);
      
    }
    
    
    
  };
  private boolean startNewRecording = false;
  private Bike bike;

  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setVolumeControlStream(TextToSpeech.Engine.DEFAULT_STREAM);
      setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
      this.setContentView(R.layout.course_select);
      
      trackRecordingServiceConnection = new TrackRecordingServiceConnection(
          this, bindChangedCallback);
      
      
      this.goButton = (Button) this.findViewById(R.id.course_select_go);
      
      Button cancelButton = (Button) this.findViewById(R.id.course_select_cancel);
      
      cancelButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          CourseSetupActivity.this.finish(false);
        }
        
      });
      
      goButton.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View v) {
          startServiceInBackround();
          doBindService();
        }
        
      });
      
      
      this.courseSetupObserver = new CourseSetupFragment.CourseSetupObserver() {
         
        @Override
        public void onTrackIdUpdate(Long trackIdIn) {
          setTrackId(trackIdIn);
          validate();
        }
        
        @Override
        public void onCourseModeUpdate(String modeStringIn) {
          setModeString(modeStringIn);
          validate();
          
        }

        @Override
        public void onBikeUpdate(Bike bikeIn) {
          String bikeDesc = bikeIn == null ? "null" : bikeIn.getName();
          Log.d(TAG,"new bike: " + bikeDesc);
          setBike(bikeIn);
          validate();
        }
      };
      
       getFragmentManager().beginTransaction().replace(R.id.course_select_preferences,
       new CourseSetupFragment(courseSetupObserver)).commit();
  }
  
  
  
  private synchronized void setBike(Bike bike) {
    this.bike = bike;
 
  }
  
  private synchronized Bike getBike() {
    return bike;
  }

  /* (non-Javadoc)
   * @see android.app.Activity#onStart()
   */
  @Override
  protected void onStart() {
    super.onStart();
    
        
  }

  protected void finish(boolean trackStarted) {
    Intent resultData = new Intent();
    if (trackStarted) {
      setResult(Activity.RESULT_OK, resultData);
    } else {
      setResult(Activity.RESULT_CANCELED, resultData);
    }
    finish();
    
  }
  
  


  /**
   * this disables/enables the go button
   */
  private void validate() {
    String mode = getModeString();
    
    boolean valid = true;
    
    // must have selected a bike
    if (getBike() == null) {
      updateUi(false);
      return;
    }
    
    if (mode.equals(getString(R.string.settings_courses_mode_simulation_value))) {
   
      if (!validateSimulationMode()) {
        valid = false;
      }
    }
    
    updateUi(valid);
    
  }
  
  @Override
  public void finish() {
    
    super.finish();
  }
  
  /**
   * Starts a new recording.
   */
  private void startRecording() {
    startNewRecording = true;
    trackRecordingServiceConnection.startAndBind();

    /*
     * If the binding has happened, then invoke the callback to start a new
     * recording. If the binding hasn't happened, then invoking the callback
     * will have no effect. But when the binding occurs, the callback will get
     * invoked.
     */
    bindChangedCallback.run();
  }
  
  @Override
  protected void onStop() {
    super.onStop();
    trackRecordingServiceConnection.unbind();
  }

  private void updateUi(final boolean valid) {
    Log.d(TAG,"updating ui: " + valid);
    this.runOnUiThread(new Runnable() {

      @Override
      public void run() {
        goButton.setEnabled(valid);
      }
      
    });

  }

  private boolean validateSimulationMode() {
    Long localTrackId = getTrackId();
    
    if(localTrackId == null) {
      return false;
    }
    else if (localTrackId.equals(-1L)) {
      return false;
    }
    return true;
  }

  
  
 
}    