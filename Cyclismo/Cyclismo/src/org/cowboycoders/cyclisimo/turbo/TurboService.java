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
package org.cowboycoders.cyclisimo.turbo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.cowboycoders.ant.Node;
import org.cowboycoders.ant.interfaces.AndroidAntTransceiver;
import org.cowboycoders.ant.interfaces.AntRadioPoweredOffException;
import org.cowboycoders.ant.interfaces.AntRadioServiceNotInstalledException;
import org.cowboycoders.ant.interfaces.ServiceAlreadyClaimedException;
import org.cowboycoders.cyclisimo.Constants;
import org.cowboycoders.cyclisimo.R;
import org.cowboycoders.cyclisimo.TrackEditActivity;
import org.cowboycoders.cyclisimo.content.Bike;
import org.cowboycoders.cyclisimo.content.CyclismoProviderUtils;
import org.cowboycoders.cyclisimo.content.MyTracksProviderUtils;
import org.cowboycoders.cyclisimo.content.User;
import org.cowboycoders.cyclisimo.services.TrackRecordingServiceConnection;
import org.cowboycoders.cyclisimo.util.IntentUtils;
import org.cowboycoders.cyclisimo.util.PreferencesUtils;
import org.cowboycoders.cyclisimo.util.TrackRecordingServiceConnectionUtils;
import org.cowboycoders.cyclisimo.util.UnitConversions;
import org.cowboycoders.location.LatLongAlt;
import org.cowboycoders.turbotrainers.CourseTracker;
import org.cowboycoders.turbotrainers.Mode;
import org.cowboycoders.turbotrainers.Parameters;
import org.cowboycoders.turbotrainers.TooFewAntChannelsAvailableException;
import org.cowboycoders.turbotrainers.TurboCommunicationException;
import org.cowboycoders.turbotrainers.TurboTrainerDataListener;
import org.cowboycoders.turbotrainers.TurboTrainerInterface;
import org.cowboycoders.turbotrainers.bushido.brake.BushidoBrake;
import org.cowboycoders.turbotrainers.bushido.brake.PidBrakeController;
import org.cowboycoders.turbotrainers.bushido.brake.SpeedResistanceMapper;
import org.cowboycoders.turbotrainers.bushido.headunit.BushidoHeadunit;

public class TurboService extends Service {

  private static final int DEFAULT_BIKE_WEIGHT = 7;

  private static final int DEFAULT_USER_WEIGHT = 60;

  private static final int RESULT_ERROR = 0;

  private Binder turboBinder = new TurboBinder();
  
  //relocate to R
  public static final int NOTIFCATION_ID_STARTUP = 0;
  
  public static final int NOTIFCATION_ID_SHUTDOWN = 1;

  public static String TAG = "TurboService";

  public static String COURSE_TRACK_ID = "COURSE_TRACK_ID";

  public static double TARGET_TRACKPOINT_DISTANCE_METRES = 5;
  
  protected AntLoggerImpl antLogger;

  private final static String MOCK_LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;// LocationManager.NETWORK_PROVIDER;

  private static int GPS_ACCURACY = 5; // m

  // private List<LatLongAlt> latLongAlts;

  private boolean running = false;
  
  private float scaleFactor = 1.0f; //multiply positive gradients by this value
  
  private AndroidAntTransceiver transceiver;

  // private double distanceBetweenPoints;

  // private double lastSubmittedDistance = 0;

  // private int currentLatLongIndex = 1;

  private static String WAKE_LOCK = TurboService.class.getSimpleName();

  private boolean mIsBound = false;
  
  private boolean attachAntLogger = false;

  private Node antNode;
  
  // Notification that we are running
  public static int ONGOING_NOTIFICATION = 1786;

  double lastRecordedSpeed = 0.0; // kmh

  private TurboTrainerInterface turboTrainer;
  
  private Lock parameterBuilderLock = new ReentrantLock();
  
  private Parameters.Builder parameterBuilder;
  
  public void setParameterBuilder(Parameters.Builder builder) {
    try {
      parameterBuilderLock.lock();
      parameterBuilder = builder;
    } finally {
      parameterBuilderLock.unlock();
    }
   
  }
  

  TurboTrainerDataListener dataListener = new TurboTrainerDataListener() {

    boolean updating = false;
    Lock updatingLock = new ReentrantLock();

    @Override
    public void onSpeedChange(double speed) { // synchronized to keep speed in
      
      // alignment with distance    
      
      Intent intent = new Intent(getString(R.string.sensor_data_speed_kmh));
      intent.putExtra(getString(R.string.sensor_data_double_value), speed);
      sendBroadcast(intent);

      try {
        updatingLock.lock();
        if (updating)
          return;
        updating = true;
      } finally {
        updatingLock.unlock();
      }

      lastRecordedSpeed = speed;
      Log.v(TAG, "new speed: " + speed);

      try {
        updatingLock.lock();
        updating = false;
      } finally {
        updatingLock.unlock();
      }

    }

    @Override
    public void onPowerChange(double power) {
      Intent intent = new Intent(getString(R.string.sensor_data_power));
      intent.putExtra(getString(R.string.sensor_data_double_value), power);
      sendBroadcast(intent);
    }

    @Override
    public void onCadenceChange(double cadence) {
      Log.d(TAG, "cadence: " + cadence);
      Intent intent = new Intent(getString(R.string.sensor_data_cadence));
      intent.putExtra(getString(R.string.sensor_data_double_value), cadence);
      sendBroadcast(intent);
    }

    @Override
    public void onDistanceChange(double distance) {
      // synchronized to keep speed in alignment with distance : may need
      // changing if threads are queueing and order becomes
      // unpredictable
      try {
        updatingLock.lock();
        if (updating)
          return;
        updating = true;
      } finally {
        updatingLock.unlock();
      }

      Log.d(TAG, "distance:" + distance);
      LatLongAlt currentLocation = courseTracker.getNearestLocation(distance);
      updateLocation(currentLocation);
      double gradient = courseTracker.getCurrentGradient(); // returns 0.0 if
                                                            // finished for warm
                                                            // down
      if (gradient > 0) {
        gradient = gradient * scaleFactor;
      }
      
      try {
        parameterBuilderLock.lock();
        turboTrainer.setParameters(parameterBuilder.buildTargetSlope(gradient));
      } finally {
        parameterBuilderLock.unlock();
      }
      
      Log.d(TAG, "New Gradient: " + gradient);
      if (courseTracker.hasFinished()) {
        doFinish();
      }

      try {
        updatingLock.lock();
        updating = false;
      } finally {
        updatingLock.unlock();
      }

    }

    @Override
    public void onHeartRateChange(double heartRate) {
      if (heartRate > 0.) {
        Intent intent = new Intent(getString(R.string.sensor_data_heart_rate));
        intent.putExtra(getString(R.string.sensor_data_double_value), heartRate);
        sendBroadcast(intent);
      }
    }

  };

  private WakeLock wakeLock;

  private CourseTracker courseTracker;

  private long recordingTrackId;

  private SharedPreferences preferences;

  private String selectedTurboTrainer;

  public void doFinish() {
    preferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    // if (recordingTrackId != PreferencesUtils.RECORDING_TRACK_ID_DEFAULT) {
    Intent intent = IntentUtils.newIntent(getBaseContext(), TrackEditActivity.class)
        .putExtra(TrackEditActivity.EXTRA_TRACK_ID, recordingTrackId)
        .putExtra(TrackEditActivity.EXTRA_NEW_TRACK, true).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    getApplication().startActivity(intent);
    TrackRecordingServiceConnectionUtils
        .stopRecording(this, trackRecordingServiceConnection, false);
    // }
    this.stopSelf();
  }

  void doUnbindService() {
    if (mIsBound) {
      unbindService(mConnection);
      mIsBound = false;
    }
  }

  /*
   * (non-Javadoc)
   * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
   */
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);
    return START_STICKY;
  }

  public synchronized void start(final long trackId, final Context context) {
    if (running) {
      return;
    }
    running = true;
    
    preferences = context.getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
    
    preferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    
    syncScaleFactor();
    
    this.selectedTurboTrainer = preferences.getString(context.getString(R.string.turbotrainer_selected), context.getString(R.string.turbotrainer_tacx_bushido_headunit_value));
    
    Log.d(TAG,selectedTurboTrainer);
    
    
    // accessing database so should be put into a task
    double userWeight;
    double bikeWeight;
    Bike selectedBike;
    
    CyclismoProviderUtils providerUtils = MyTracksProviderUtils.Factory.getCyclimso(context);
    User currentUser = providerUtils.getUser(PreferencesUtils.getLong(context, R.string.settings_select_user_current_selection_key));
    if (currentUser!= null) {
      userWeight = currentUser.getWeight();
      selectedBike = providerUtils.getBike(PreferencesUtils.getLong(context, R.string.settings_select_bike_current_selection_key));
    } else {
      Log.w(TAG, "using default user weight");
      userWeight = DEFAULT_USER_WEIGHT; //kg
      selectedBike = null;
    }
    if (selectedBike != null) {
      bikeWeight = selectedBike.getWeight();
    } else {
      Log.w(TAG, "using default bike weight");
      bikeWeight = DEFAULT_BIKE_WEIGHT; //kg
    }
    setParameterBuilder(new Parameters.Builder(userWeight,bikeWeight));
    Log.d(TAG,"user weight: " + userWeight);
    Log.d(TAG,"bike weight: " + bikeWeight);
    
    attachAntLogger = PreferencesUtils.getBoolean(context,R.string.settings_ant_diagnostic_logging_state_key, false);

    wakeLock.acquire();

    recordingTrackId = PreferencesUtils.getLong(this, R.string.recording_track_id_key);

    List<LatLongAlt> latLongAlts = null;

    context.getClass();
    CourseLoader cl = new CourseLoader(context, trackId);
    try {
      latLongAlts = cl.getLatLongAlts();
    } catch (InterruptedException e) {
      String error = "interrupted whilst loading course";
      Log.e(TAG, error);
      handleException(e, "Error loading course",true,NOTIFCATION_ID_STARTUP);
    }

    latLongAlts = org.cowboycoders.location.LocationUtils.interpolatePoints(latLongAlts,
        TARGET_TRACKPOINT_DISTANCE_METRES);

    this.courseTracker = new CourseTracker(latLongAlts);

    Log.d(TAG, "latlong length: " + latLongAlts.size());

    enableMockLocations();

    // start in background as otherwise it is destroyed in onDestory() before we
    // can disconnect
    startServiceInBackround();

    doBindService();

    registerRecordingReceiver();

    Intent notificationIntent = new Intent(this, TurboService.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

    Notification noti = new NotificationCompat.Builder(this)
        .setContentTitle(getString(R.string.turbo_mode_service_running_notification_title))
        .setContentText(getString(R.string.turbo_mode_service_running_notification_text))
        .setSmallIcon(R.drawable.track_bike).setContentIntent(pendingIntent).setOngoing(true)
        .build(); // build api 16 only ;/

    startForeground(ONGOING_NOTIFICATION, noti);
    
    // currentLatLongIndex = latLongAlts.size() -3; puts in near end so we can
    // test ending

  }
  
  private static interface ErrorProneCall {
    void call() throws Exception;
  }
  
  private static void retryErrorProneCall(ErrorProneCall errorProneCall, int maxRetries) throws Exception {
    int retries = 0;
    while (true) {
      try {
        errorProneCall.call();
        break;
      } catch (Exception e) {
        if (++retries >= maxRetries) {
          throw e;
        }
      }
    }
  }
  
  private ErrorProneCall startTurbo= new ErrorProneCall() {

    @Override
    public void call() throws TurboCommunicationException, InterruptedException, TimeoutException {
      turboTrainer.start();
    }
    
  };



  private ServiceConnection mConnection = new ServiceConnection() {

    public void onServiceConnected(ComponentName className, IBinder binder) {
      AntHubService s = ((AntHubService.LocalBinder) binder).getService();
      antNode = s.getNode();
      if (attachAntLogger) {
        antLogger = new AntLoggerImpl(TurboService.this);
        antNode.registerAntLogger(antLogger);
      }
      transceiver = s.getTransceiver();
      TurboService.this.turboTrainer =  getTurboTrainer();

      new Thread() {
        public void run() {
          try {
            turboTrainer.setMode(Mode.TARGET_SLOPE);
            // slight hack to get around timeout errors on my tablet 
            retryErrorProneCall(startTurbo,10);
            turboTrainer.registerDataListener(dataListener);
            //turboTrainer.start();
            //turboTrainer.registerDataListener(dataListener);
            // if
            // (TrackRecordingServiceConnectionUtils.isRecordingServiceRunning(TurboService.this))
            // {
            // TrackRecordingServiceConnectionUtils.resumeTrack(trackRecordingServiceConnection);
            // }
            // notify receivers that we have started up
            Intent intent = new Intent().setAction(TurboService.this
                .getString(R.string.turbo_service_action_course_start));
            sendBroadcast(intent);
            updateLocation(courseTracker.getNearestLocation(0.0));
          } catch (Exception e) {
            handleException(e, "Error initiliasing turbo trainer",true,NOTIFCATION_ID_STARTUP);
          }
        }
      }.start();

      Toast.makeText(TurboService.this, "Connected to AntHub service", Toast.LENGTH_SHORT).show();
    }

    public void onServiceDisconnected(ComponentName className) {
      Toast.makeText(TurboService.this, "Disconnected from AntHub service", Toast.LENGTH_SHORT)
          .show();
    }

  };

  private TrackRecordingServiceConnection trackRecordingServiceConnection;

  private boolean networkProviderEnabled;


  void doBindService() {
    bindService(new Intent(this, AntHubService.class), mConnection, Context.BIND_AUTO_CREATE);
    mIsBound = true;
  }

  protected TurboTrainerInterface getTurboTrainer() {
    if (selectedTurboTrainer == getString(R.string.turbotrainer_tacx_bushido_headunit_value)) {
      return new BushidoHeadunit(antNode);
    } else if (selectedTurboTrainer == getString(R.string.turbotrainer_tacx_bushido_brake_headunit_simulation_value)) {
      SpeedResistanceMapper mapper = new SpeedResistanceMapper();
      return new BushidoBrake(antNode,mapper);
    } else if (selectedTurboTrainer == getString(R.string.turbotrainer_tacx_bushido_brake_pid_control_value)) {
      PidBrakeController pid = new PidBrakeController();
      return new BushidoBrake(antNode,pid);
    }
    
    return null;
  }

  private void startServiceInBackround() {
    Intent intent = new Intent(this, AntHubService.class);
    this.startService(intent);
  }
  
  private boolean enableLocationProvider(String provider) {
    LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(
        Context.LOCATION_SERVICE);
    if (locationManager.isProviderEnabled(provider)) {
      try {
        locationManager.addTestProvider(provider, "requiresNetwork" == "",
            "requiresSatellite" == "", "requiresCell" == "", "hasMonetaryCost" == "",
            "supportsAltitude" == "", "supportsSpeed" == "", "supportsBearing" == "",
            android.location.Criteria.POWER_LOW, android.location.Criteria.ACCURACY_FINE);

        locationManager.setTestProviderEnabled(provider, true);
        locationManager.setTestProviderStatus(provider, LocationProvider.AVAILABLE,
            null, System.currentTimeMillis());
      } catch (SecurityException e) {
        handleException(e, "Error enabling location provider",true,NOTIFCATION_ID_STARTUP);
        return false;
      }
    } else {
      return false;
    }
    
    return true;
  }

  private void enableMockLocations() {
    enableLocationProvider(MOCK_LOCATION_PROVIDER);
    // enable any alternatives otherwise : could provide a last known location
    networkProviderEnabled = enableLocationProvider(LocationManager.NETWORK_PROVIDER);
  }

  private void disableMockLocations() {
    disableLocationProvider(MOCK_LOCATION_PROVIDER);
    if (networkProviderEnabled) disableLocationProvider(LocationManager.NETWORK_PROVIDER);
  }
  
  private void disableLocationProvider(String provider) {
    LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(
        Context.LOCATION_SERVICE);
    if (locationManager.isProviderEnabled(provider)) {
      try {
        // is this the same as the below? probably
        locationManager.setTestProviderEnabled(provider, false);
        locationManager.clearTestProviderEnabled(provider);
        locationManager.clearTestProviderLocation(provider);
        locationManager.clearTestProviderStatus(provider);
        locationManager.removeTestProvider(provider);
      } catch (SecurityException e) {
        // ignore 
      }
    }
  }

  private synchronized void updateLocation(LatLongAlt pos) {
    LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(
        Context.LOCATION_SERVICE);
    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      try {
        float locSpeed = (float) (lastRecordedSpeed / UnitConversions.MS_TO_KMH);
        final long timestamp = System.currentTimeMillis();
        Log.v(TAG, "location timestamp: " + timestamp);
        Location loc = new Location(MOCK_LOCATION_PROVIDER);
        Log.d(TAG, "alt: " + pos.getAltitude());
        Log.d(TAG, "lat: " + pos.getLatitude());
        Log.d(TAG, "long: " + pos.getLongitude());
        loc.setLatitude(pos.getLatitude());
        loc.setLongitude(pos.getLongitude());
        loc.setAltitude(pos.getAltitude());
        loc.setTime(timestamp);
        loc.setSpeed(locSpeed);
        loc.setAccuracy(GPS_ACCURACY);
        locationManager.setTestProviderLocation(MOCK_LOCATION_PROVIDER, loc);
        Log.e(TAG, "updated location");
      } catch (SecurityException e) {
        handleException(e,"Error updating location",true,NOTIFCATION_ID_STARTUP);
      }

      return;
    }
    Log.e(TAG, "no gps provider");

  }

  @Override
  public synchronized void onDestroy() {
    unregisterRecordingReceiver();
    disableMockLocations();
    running = false;

    new Thread() {
      public void run() {
        shutDownTurbo();
      }
    }.start();

    // no longer need ant
    this.doUnbindService();

    super.onDestroy();
  }

  // this takes time
  private boolean shutDownTurbo() {
    boolean shutDownSuccess = true;
    if (turboTrainer != null) {
      try {
        turboTrainer.unregisterDataListener(dataListener);
        turboTrainer.stop();
      } catch (Exception e) {
        handleException(e,"Error shutting down turbo",false,NOTIFCATION_ID_SHUTDOWN);
        shutDownSuccess = false;
      }

      String shutdownMessage;
      if (shutDownSuccess) {
        shutdownMessage = "Shutdown turbo trainer sucessfully";
      } else {
        shutdownMessage = "Error shutting down turbo trainer";
      }

      // NOU IN UI
      // Toast.makeText(this.getBaseContext(), shutdownMessage,
      // Toast.LENGTH_LONG).show();
      Log.i(TAG, shutdownMessage);

    }
    Intent intent = new Intent().setAction(this.getString(R.string.anthub_action_shutdown));
    sendBroadcast(intent);
    wakeLock.release();
    // trackRecordingServiceConnection.unbind();
    return shutDownSuccess;

  }

  public class TurboBinder extends Binder {
    public TurboService getService() {
      return TurboService.this;
    }
  }

  @Override
  public IBinder onBind(Intent arg0) {
    return turboBinder;
  }

  /*
   * (non-Javadoc)
   * @see android.app.Service#onCreate()
   */
  @Override
  public void onCreate() {
    super.onCreate();
    trackRecordingServiceConnection = new TrackRecordingServiceConnection(this, bindChangedCallback);
    // trackRecordingServiceConnection.startAndBind();
    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    this.wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TurboService.WAKE_LOCK);
  }

  // can th is be null
  private final Runnable bindChangedCallback = new Runnable() {
    @Override
    public void run() {
      // After binding changes (is available), update the total time in
      // trackController.
    }
  };

  // start track controller stuff

  private final BroadcastReceiver receiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (action.equals(TRACK_STOPPED_ACTION)) {
        TurboService.this.doFinish();
      } else if (action.equals(TRACK_PAUSED_ACTION)) {
        // paused
      }
    }
  };

  private static String TRACK_STOPPED_ACTION;
  private static String TRACK_PAUSED_ACTION;

  public void registerRecordingReceiver() {
    TRACK_STOPPED_ACTION = TurboService.this.getString(R.string.track_stopped_broadcast_action);
    TRACK_PAUSED_ACTION = TurboService.this.getString(R.string.track_paused_broadcast_action);
    IntentFilter filter = new IntentFilter();
    filter.addAction(TRACK_STOPPED_ACTION);
    filter.addAction(TRACK_PAUSED_ACTION);

    registerReceiver(receiver, filter);
  }

  public void unregisterRecordingReceiver() {
    unregisterReceiver(receiver);
  }
  
  // probably should show an activity with detail?
  // toDO: move strings to R
  private void handleException(Exception e , String title, boolean finish, int id) {
    title = title == null ? "An Error occured communicating with your turbotrainer" : title;
    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.track_bike)
        .setContentTitle(title);
    Notification notification = null;
    
    Log.e(TAG, "Exception thrown in "+ TurboService.class.getSimpleName() + ": ", e);
    String exceptionMessage = e.getMessage() == null ? "" : e.getMessage();
    if (e instanceof ServiceAlreadyClaimedException) {
      if (transceiver != null) {
        transceiver.requestForceClaimInterface(getString(R.string.app_name));
      }
      mBuilder.setContentText("Someone has already claimed the interface : attempting to force claim");
      notification = mBuilder.build();
      
    } else if (e instanceof AntRadioServiceNotInstalledException) {
      
      mBuilder.setContentText("AntRadioSerivce not installed. Please install.");
      notification = mBuilder.build();
      
    } else if (e instanceof TooFewAntChannelsAvailableException) {
      mBuilder.setContentText("Too few ant channels available");
      notification = mBuilder.build();
      
    }else if (e instanceof AntRadioPoweredOffException) {
      mBuilder.setContentText("Ant radio is powered off");
      notification = mBuilder.build();
      
    } else if (e instanceof Exception) { //too long : just show message?
      StringBuilder message = new StringBuilder();
      Throwable cause = e.getCause();
      message.append("Exception: " + e.getMessage());
      if (exceptionMessage != "") {
        message.append("\n" + "Message : " + exceptionMessage );
      }
      if (cause != null) {
        message.append("\nCaused by: ");
        message.append(cause.toString());
        if (cause.getMessage() != null) {
          message.append(" : " + cause.getMessage());
        }
        while ((cause = cause.getCause()) != null) {
          message.append(", ");
          message.append(cause.toString());
          if (cause.getMessage() != null) {
            message.append(" : " + cause.getMessage());
          }
        }
      }
      mBuilder.setContentText(message);
      notification = mBuilder.build(); 
    }
    
    NotificationManager mNotificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    
    mNotificationManager.notify(id, notification);
    
    if(finish) {
      Intent intent = new Intent().setAction(this.getString(R.string.turbo_service_action_exception_thrown));
      sendBroadcast(intent);
      doFinish();
    }
 
  }
  
  /*
   * Note that sharedPreferenceChangeListenr cannot be an anonymous inner class.
   * Anonymous inner class will get garbage collected.
   */
  private final OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
      if (key == getString(R.string.settings_turbotrainer_generic_scale_factor_key)) {
        syncScaleFactor();
      } else if (key == getString(R.string.turbotrainer_selected)) {
        selectedTurboTrainer = preferences.getString(getString(R.string.turbotrainer_selected),getString(R.string.turbotrainer_tacx_bushido_headunit_value));
      }
      
    }
  
  };
  
  private void syncScaleFactor() {
    scaleFactor = Float.parseFloat(preferences.getString(getString(R.string.settings_turbotrainer_generic_scale_factor_key), "1.0"));
  }
 

  // if
  // (context.getString(R.string.track_paused_broadcast_action).equals(action)
  // ||
  // context.getString(R.string.track_resumed_broadcast_action).equals(action)
  // ||
  // context.getString(R.string.track_started_broadcast_action).equals(action)
  // ||
  // context.getString(R.string.track_stopped_broadcast_action).equals(action)
  // ||
  // context.getString(R.string.track_update_broadcast_action).equals(action))
  //

}
