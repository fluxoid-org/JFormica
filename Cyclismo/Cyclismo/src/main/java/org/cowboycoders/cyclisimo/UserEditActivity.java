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
import android.widget.EditText;
import android.widget.TextView;

import org.cowboycoders.cyclisimo.content.CyclismoProviderUtils;
import org.cowboycoders.cyclisimo.content.MyTracksProviderUtils;
import org.cowboycoders.cyclisimo.content.User;
import org.cowboycoders.cyclisimo.settings.StatsSettingsActivity;
import org.cowboycoders.cyclisimo.util.IntentUtils;
import org.cowboycoders.cyclisimo.util.PreferencesUtils;
import org.cowboycoders.cyclisimo.util.Units;

/**
 * Edit / create users
 * 
 * @author Will Szumski
 */
public class UserEditActivity extends AbstractMyTracksActivity {

  public static final String EXTRA_NEW_USER = "new_user";
  public static final String EXTRA_USER = "user";

  private static final String TAG = UserEditActivity.class.getSimpleName();
  
  private Object unitsLock = new Object();

  private EditText name;
  private EditText weight;
  
  private boolean newUser = false;
  private User currentUser = null;
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
          PreferencesUtils.getKey(UserEditActivity.this, R.string.metric_units_key))) {
          boolean metricUnits = PreferencesUtils.getBoolean(UserEditActivity.this,
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
    
    this.newUser = getIntent().getBooleanExtra(EXTRA_NEW_USER, false);
    
    this.saveButton = (Button) findViewById(R.id.generic_edit_save);
    
    this.providerUtils = MyTracksProviderUtils.Factory.getCyclimso(this);
    
    // this may be in partial state
    User partialUser = (User) getIntent().getParcelableExtra(EXTRA_USER);
    
    if (!newUser && partialUser == null ) {
      throw new IllegalArgumentException("must either create or pass a user");
    }
    
    name = (EditText) findViewById(R.id.user_edit_name);
    
    weight = (EditText) findViewById(R.id.user_edit_weight);
    
    weightLabel = (TextView) findViewById(R.id.user_edit_weight_label);
    
    if (partialUser != null) {
      currentUser = providerUtils.getUser(partialUser.getId());
      name.setText(currentUser.getName());
      String weightString = String.format("%.2f",currentUser.getWeight());
      weight.setText(weightString);
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
        User user;
        if (currentUser != null) {
          user = currentUser;
        } else {
          user = new User();
        }
        user.setName(name.getText().toString());
        
        double weightOfUser;
        
        synchronized(unitsLock) {
          weightOfUser = Double.parseDouble(weight.getText().toString());
          weightOfUser = Units.METRIC.convertWeight(currentUnits, weightOfUser);
        }
        
        user.setWeight(weightOfUser);
        
        if (currentUser == null) {
          providerUtils.insertUser(user);
        } else {
          providerUtils.updateUser(user);
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
    
    TextView title = (TextView) findViewById(R.id.user_edit_title);
    
    if (currentUser == null) {
      title.setText(getString(R.string.user_create));
    } else {
      title.setText(getString(R.string.user_edit));
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
      final double weightOfUser = Double.parseDouble(weight.getText().toString());
      final double convertedWeight = targetUnits.convertWeight(currentUnits, weightOfUser);
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
    return R.layout.user_edit;
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
