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
/*
 * Copyright 2008 Google Inc.
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

package org.cowboycoders.cyclisimo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.cowboycoders.cyclisimo.content.Bike;
import org.cowboycoders.cyclisimo.content.CyclismoProviderUtils;
import org.cowboycoders.cyclisimo.content.MyTracksProviderUtils;
import org.cowboycoders.cyclisimo.settings.StatsSettingsActivity;
import org.cowboycoders.cyclisimo.util.IntentUtils;
import org.cowboycoders.cyclisimo.util.PreferencesUtils;
import org.cowboycoders.cyclisimo.util.Units;

/**
 * Edit / create bikes
 * 
 * @author Will Szumski
 */
public class BikeEditActivity extends AbstractMyTracksActivity {

  public static final String EXTRA_NEW_BIKE = "new_bike";
  public static final String EXTRA_BIKE = "bike";

  private static final String TAG = BikeEditActivity.class.getSimpleName();
  
  private Object unitsLock = new Object();

  private EditText name;
  private EditText weight;
  
  private boolean newBike = false;
  private Bike currentBike = null;
  
  private CheckBox shared;
  
  private boolean validName = false;
  private boolean validWeight = false;

  private CyclismoProviderUtils providerUtils;
  private Button saveButton;
  
  private Units currentUnits = Units.METRIC;
  private Units targetUnits = Units.METRIC;
  
  private SharedPreferences sharedPreferences;
  
  private final OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
      if (key == null || key.equals(
          PreferencesUtils.getKey(BikeEditActivity.this, R.string.metric_units_key))) {
          boolean metricUnits = PreferencesUtils.getBoolean(BikeEditActivity.this,
            R.string.metric_units_key, PreferencesUtils.METRIC_UNITS_DEFAULT);
          if (metricUnits) {
            targetUnits = Units.METRIC;
          } else {
            targetUnits = Units.IMPERIAL;
          }
          updateWeightUI();
      }
      }
    
  };
  private TextView weightLabel;
  
  
  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    
    sharedPreferences = getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
    
    this.newBike = getIntent().getBooleanExtra(EXTRA_NEW_BIKE, false);
    
    this.saveButton = (Button) findViewById(R.id.generic_edit_save);
    
    this.providerUtils = MyTracksProviderUtils.Factory.getCyclimso(this);
    
    // this may be in partial state
    Bike partialBike = (Bike) getIntent().getParcelableExtra(EXTRA_BIKE);
    
    if (!newBike && partialBike == null ) {
      throw new IllegalArgumentException("must either create or pass a user");
    }
    
    name = (EditText) findViewById(R.id.bike_edit_name);
    
    weight = (EditText) findViewById(R.id.bike_edit_weight);
    
    weightLabel = (TextView) findViewById(R.id.bike_edit_weight_label);
    
    this.shared = (CheckBox) findViewById(R.id.bike_shared_check_box);
    
    if (partialBike != null) {
      currentBike = providerUtils.getBike(partialBike.getId());
      name.setText(currentBike.getName());
      String weightString = String.format("%.2f",currentBike.getWeight());
      weight.setText(weightString);
      shared.setChecked(currentBike.isShared());
    }
    
    validateName();
    validateWeight();
    updateItems();
    
    name.addTextChangedListener(new TextWatcher() {


      @Override
      public void afterTextChanged(Editable s) {
        // do nothing
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        validateName();
        updateItems();
      }

      
      
    });
    
    weight.addTextChangedListener(new TextWatcher() {


      @Override
      public void afterTextChanged(Editable s) {
        // do nothing
        
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
        
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        validateWeight();
        updateItems();
      }

    });
    
    
    saveButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //FIXME: database / shared preference calls should be in an sync task
        Bike bike;
        if (currentBike != null) {
          bike = currentBike;
        } else {
          bike = new Bike();
        }
        bike.setName(name.getText().toString());
        
        double weightOfUser;
        
        synchronized(unitsLock) {
          weightOfUser = Double.parseDouble(weight.getText().toString());
          weightOfUser = Units.METRIC.convertWeight(currentUnits, weightOfUser);
        }
        
        bike.setWeight(weightOfUser);
        bike.setShared(shared.isChecked());
        
        if (bike.isShared()) {
          bike.setOwnerId(-1L);
        } else {
          long ownerId = PreferencesUtils.getLong(BikeEditActivity.this, R.string.settings_select_user_current_selection_key);
          bike.setOwnerId(ownerId);
        }
        
        if (currentBike == null) {
          providerUtils.insertBike(bike);
        } else {
          providerUtils.updateBike(bike);
        }
        finish();
      }
    });

    Button cancel = (Button) findViewById(R.id.generic_edit_cancel);
    cancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    
    TextView title = (TextView) findViewById(R.id.bike_edit_title);
    
    if (currentBike == null) {
      title.setText(getString(R.string.bike_create));
    } else {
      title.setText(getString(R.string.bike_edit));
    }
    

  }
  
  protected void updateWeightUI() {
    this.runOnUiThread(new Runnable() {

      @Override
      public void run() {
        if (targetUnits == Units.METRIC) {
          weightLabel.setText(R.string.generic_weight_kg);
        } else {
          weightLabel.setText(R.string.generic_weight_lbs);
        }
        updateWeight();
      }
      
      
      
    });
    
  }
  
  private void updateWeight() {
    
    if (!validateWeight()) return;
    
    synchronized(unitsLock) {
      final double wegihtOfBike = Double.parseDouble(weight.getText().toString());
      final double convertedWeight = targetUnits.convertWeight(currentUnits, wegihtOfBike);
      String weightString = String.format("%.2f",convertedWeight);
      weight.setText(weightString);
      currentUnits = targetUnits;
    }
    
  }
  

  private void updateItems() {
    saveButton.setEnabled(validName && validWeight);
  }

  private void validateName() {
    boolean validLength = name.getText().length() > 0;

    validName = validLength;
    
    if (validName) {
      name.setError(null);
    } else {
      name.setError(getString(R.string.user_edit_name_error));
    }
  }
  
  private boolean validateWeight() {
    boolean validLength = weight.getText().length() > 0;

    validWeight = validLength;
    
    if (validWeight) {
      weight.setError(null);
    } else {
      weight.setError(getString(R.string.user_edit_weight_error));
    }
    
    return validWeight;
  }

  @Override
  protected int getLayoutResId() {
    return R.layout.bike_edit;
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.user_edit_list, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.user_edit_units:
        Intent intent = IntentUtils.newIntent(this, StatsSettingsActivity.class);
        startActivity(intent);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
  
  @Override
  protected void onStart() {
    super.onStart();

    // Register shared preferences listener
    sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

    // Update shared preferences
    sharedPreferenceChangeListener.onSharedPreferenceChanged(null, null);

  }
  
  @Override
  protected void onStop() {
    super.onStop();

    // Unregister shared preferences listener
    sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

  }
  
  


}
