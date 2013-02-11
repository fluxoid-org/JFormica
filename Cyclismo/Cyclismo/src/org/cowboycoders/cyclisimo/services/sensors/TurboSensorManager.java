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
package org.cowboycoders.cyclisimo.services.sensors;

import org.cowboycoders.cyclisimo.content.Sensor;
import org.cowboycoders.cyclisimo.content.Sensor.SensorData;
import org.cowboycoders.cyclisimo.content.Sensor.SensorDataSet;
import org.cowboycoders.cyclisimo.content.Sensor.SensorState;
import org.cowboycoders.cyclisimo.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class TurboSensorManager extends SensorManager {
  
  SensorDataSet sensorDataSet;
  
  private static final String TAG = TurboSensorManager.class.getSimpleName();
  
  private static String NEW_CADENCE_ACTION;
  private static String NEW_SPEED_ACTION;
  private static String NEW_HEART_RATE_ACTION;
  private static String NEW_POWER_ACTION;
  private static String DATA_ID;
  
  private Context context;
  
  public TurboSensorManager(Context context) {
    this.context = context;
    sensorDataSet = Sensor.SensorDataSet.getDefaultInstance();
    NEW_CADENCE_ACTION = context.getString(R.string.sensor_data_cadence);
    NEW_SPEED_ACTION = context.getString(R.string.sensor_data_speed_kmh);
    NEW_HEART_RATE_ACTION = context.getString(R.string.sensor_data_heart_rate);
    NEW_POWER_ACTION = context.getString(R.string.sensor_data_power);
    DATA_ID = context.getString(R.string.sensor_data_double_value);
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  protected void setUpChannel() {
    setSensorState(SensorState.CONNECTING);
    registerTurboReceiver();
    setSensorState(SensorState.CONNECTED);
  }

  @Override
  protected void tearDownChannel() {
    unregisterTurboReceiver();
    setSensorState(SensorState.DISCONNECTED);
  }

  @Override
  public synchronized SensorDataSet getSensorDataSet() {
    return sensorDataSet;
  }
  
  public synchronized void setSensorDataSet(SensorDataSet newSet) {
    this.sensorDataSet = newSet;
  }
  
  private final BroadcastReceiver receiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        setSensorState(SensorState.SENDING);
       String action = intent.getAction();
       double value = intent.getDoubleExtra(DATA_ID, -1);
       SensorData sd = SensorData.newBuilder()
           .setValue((int) value)
           .setState(Sensor.SensorState.SENDING)
           .build();
       if(action.equals(NEW_CADENCE_ACTION)){
         synchronized (TurboSensorManager.this) {
           Log.d(TAG,"cadence raw: " + value);
           SensorDataSet sds = getSensorDataSet();
           sds = sds.toBuilder()
               .setCadence(sd)
               .setCreationTime(System.currentTimeMillis())
               .build();
           setSensorDataSet(sds);
           Log.d(TAG,"sensorDataSet has cadence: " + sds.hasCadence());
           Log.d(TAG,"cadence in sensorDataSet: " + sds.getCadence());
         }
       }
       else if(action.equals(NEW_SPEED_ACTION)){
         //can't handle this atm
       }
       else if(action.equals(NEW_HEART_RATE_ACTION)){
         synchronized (TurboSensorManager.this) {
           SensorDataSet sds = getSensorDataSet();
           sds = sds.toBuilder()
               .setHeartRate(sd)
               .setCreationTime(System.currentTimeMillis())
               .build();
           setSensorDataSet(sds);
         }
       }
       else if(action.equals(NEW_POWER_ACTION)){
         synchronized (TurboSensorManager.this) {
           SensorDataSet sds = getSensorDataSet();
           sds = sds.toBuilder()
               .setPower(sd)
               .setCreationTime(System.currentTimeMillis())
               .build();
           setSensorDataSet(sds);
         }
       }
    }
  };



  public void registerTurboReceiver() {
    IntentFilter filter = new IntentFilter();
    filter.addAction(NEW_POWER_ACTION);
    filter.addAction(NEW_HEART_RATE_ACTION);
    filter.addAction(NEW_SPEED_ACTION);
    filter.addAction(NEW_CADENCE_ACTION);

    context.registerReceiver(receiver, filter);
  }

  public void unregisterTurboReceiver() {
    context.unregisterReceiver(receiver);
  }

}
