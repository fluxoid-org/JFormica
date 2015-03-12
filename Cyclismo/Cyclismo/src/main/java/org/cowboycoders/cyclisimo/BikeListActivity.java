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
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.ResourceCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.cowboycoders.cyclisimo.content.Bike;
import org.cowboycoders.cyclisimo.content.BikeInfoColumns;
import org.cowboycoders.cyclisimo.content.CyclismoProviderUtils;
import org.cowboycoders.cyclisimo.content.MyTracksProviderUtils;
import org.cowboycoders.cyclisimo.content.User;
import org.cowboycoders.cyclisimo.fragments.ConfirmationDialogFragment;
import org.cowboycoders.cyclisimo.fragments.ConfirmationDialogFragment.DialogCallback;
import org.cowboycoders.cyclisimo.util.ApiAdapterFactory;
import org.cowboycoders.cyclisimo.util.IntentUtils;
import org.cowboycoders.cyclisimo.util.PreferencesUtils;

/**
 * An activity displaying a list of user.
 * 
 * @author Will Szumski
 */
public class BikeListActivity extends FragmentActivity {

  public static final String TAG = BikeListActivity.class.getSimpleName();

  private static final String[] PROJECTION = new String[] { BikeInfoColumns._ID,
      BikeInfoColumns.NAME };

  // protected static final int COURSE_SETUP_RESPONSE_CODE = 0;

  /*
   * Note that sharedPreferenceChangeListenr cannot be an anonymous inner class.
   * Anonymous inner class will get garbage collected.
   */
  private final OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
      if (key != null) {
        if (key == getString(R.string.settings_select_bike_current_selection_key)) {
          Log.i(TAG," current bike changed");
        }
        else if (key == getString(R.string.settings_select_user_current_selection_key)) {
          resourceCursorAdapter.notifyDataSetChanged();
        }
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            boolean isRecording = recordingTrackId != PreferencesUtils.RECORDING_TRACK_ID_DEFAULT;
            updateMenuItems(isRecording);
            updateUI();
            resourceCursorAdapter.notifyDataSetChanged();
          }
        });
      }
    }
  };

  // Callback when an item is selected in the contextual action mode
  private final ContextualActionModeCallback contextualActionModeCallback = new ContextualActionModeCallback() {
    @Override
    public boolean onClick(int itemId, int position, long id) {
      return handleContextItem(itemId, position);
    }
  };

  // The following are set in onCreate
  private SharedPreferences sharedPreferences;
  private ListView listView;
  private ResourceCursorAdapter resourceCursorAdapter;

  private long recordingTrackId = PreferencesUtils.RECORDING_TRACK_ID_DEFAULT;

  // Menu items
  private MenuItem deleteAllMenuItem;

  private CyclismoProviderUtils providerUtils;

  private Button cancelButton;
  

  private Handler mHandler;
  
  private static void updateUser(Context context, long bikeId) {
    final CyclismoProviderUtils providerUtils = MyTracksProviderUtils.Factory.getCyclimso(context);
    final long currentUserId = PreferencesUtils.getLong(context, R.string.settings_select_user_current_selection_key);
    User user = providerUtils.getUser(currentUserId);
    user.setCurrentlySelectedBike(bikeId);
    providerUtils.updateUser(user);
  }

  @SuppressWarnings("unused")
  private ContextMenu contextMenu;

  public static final class DeleteBikeCallback implements DialogCallback, Parcelable {
    
    private Bike bike;
    
    public DeleteBikeCallback(Parcel in) {
      bike= in.readParcelable(Bike.class.getClassLoader());
    }

    public DeleteBikeCallback(Bike bike) {
      this.bike = bike;
    }

    @Override
    public int describeContents() {
      return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
      dest.writeParcelable(bike,flags);
    }

    @Override
    public void onConfirm(final Context context) {
      final CyclismoProviderUtils providerUtils = MyTracksProviderUtils.Factory.getCyclimso(context);
      
      // don't run on ui thread
      new Thread() {
        public void run() {
          long bikeId = bike.getId();
          providerUtils.deleteBike(bike.getId());
          long currentBike = PreferencesUtils.getLong(context, R.string.settings_select_bike_current_selection_key);
          // if deleting current user, set to invalid user
          if (bikeId == currentBike) {
            updateUser(context,-1L);
            PreferencesUtils.setLong(context, R.string.settings_select_bike_current_selection_key, -1L);
          }
        }
        
      }.start();


    }

    public static final Parcelable.Creator<DeleteBikeCallback> CREATOR = new Parcelable.Creator<DeleteBikeCallback>() {
      public DeleteBikeCallback createFromParcel(Parcel in) {
        return new DeleteBikeCallback(in);
      }

      public DeleteBikeCallback[] newArray(int size) {
        return new DeleteBikeCallback[size];
      }
    };

    @Override
    public CharSequence getConfirmationMessage(Context context) {
      String unformated = context.getString(R.string.bike_list_delete_single_confirmation_message);
      return String.format(unformated, bike.getName());
    }

    @Override
    public void onFinish(Context context) {
      Intent intent = IntentUtils.newIntent(context, BikeListActivity.class);
      context.startActivity(intent);
    }

  };
  
  
public static final class DeleteAllBikesCallback implements DialogCallback,Parcelable {
    
    
    public DeleteAllBikesCallback(@SuppressWarnings("unused") Parcel in) {
    }

    public DeleteAllBikesCallback() {
    }

    @Override
    public int describeContents() {
      return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public void onConfirm(final Context context) {
      final CyclismoProviderUtils providerUtils = MyTracksProviderUtils.Factory.getCyclimso(context);
      
      // don't run on ui thread
      new Thread() {
        public void run() {
          long userId = PreferencesUtils.getLong(context, R.string.settings_select_user_current_selection_key);
          providerUtils.deleteAllBikes(userId);
          long currentBike = PreferencesUtils.getLong(context, R.string.settings_select_bike_current_selection_key);
          if (providerUtils.getBike(currentBike) == null) {
            updateUser(context,-1L);
            PreferencesUtils.setLong(context, R.string.settings_select_bike_current_selection_key, -1L);
          }

        }
        
      }.start();


    }

    public static final Parcelable.Creator<DialogCallback> CREATOR = new Parcelable.Creator<DialogCallback>() {
      public DialogCallback createFromParcel(Parcel in) {
        return new DeleteAllBikesCallback(in);
      }

      public DialogCallback[] newArray(int size) {
        return new DialogCallback[size];
      }
    };

    @Override
    public CharSequence getConfirmationMessage(Context context) {
      return context.getString(R.string.bike_list_delete_all_confirmation_message);
    }

    @Override
    public void onFinish(Context context) {
      Intent intent = IntentUtils.newIntent(context, BikeListActivity.class);
      context.startActivity(intent);
    }

  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setVolumeControlStream(TextToSpeech.Engine.DEFAULT_STREAM);
    setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
    setContentView(R.layout.generic_list_select);
    
    mHandler = new Handler();

    this.providerUtils = MyTracksProviderUtils.Factory.getCyclimso(this);

    sharedPreferences = getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);

    listView = (ListView) findViewById(R.id.generic_list);

    this.setTitle(this.getString(R.string.my_tracks_app_name) + " : "
        + this.getString(R.string.bike_list_title));

    TextView userEmptyListText = (TextView) findViewById(R.id.generic_list_empty_view);
    userEmptyListText.setText(R.string.bike_list_empty_message);

    this.cancelButton = (Button) this.findViewById(R.id.generic_list_left_button);
    cancelButton.setText(getString(R.string.cancel));

    initCancelButton();

    Button addUserButton = (Button) this.findViewById(R.id.generic_list_right_button);
    addUserButton.setText(getString(R.string.bike_create));

    addUserButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        //TODO: make bike edit
        Intent intent = IntentUtils.newIntent(BikeListActivity.this, BikeEditActivity.class)
            .putExtra(BikeEditActivity.EXTRA_NEW_BIKE, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        // intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

      }

    });

    listView.setEmptyView(userEmptyListText);

    resourceCursorAdapter = new ResourceCursorAdapter(this, R.layout.user_list_item, null, 0) {
      
      @Override
      public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = super.newView(context, cursor, parent);
        return view;
      }

      @Override
      public void bindView(View view, Context context, Cursor cursor) {
        final Bike bike = providerUtils.createBike(cursor, false);
        TextView userName = (TextView) view.findViewById(R.id.list_item_name);
        userName.setText(bike.getName());
        userName.setEnabled(!isRecording());
        userName.setVisibility(View.VISIBLE);
        
        
        view.setOnClickListener(new OnClickListener() {

          @Override
          public void onClick(View v) {
            boolean isRecording = recordingTrackId != PreferencesUtils.RECORDING_TRACK_ID_DEFAULT;
            if (isRecording) {
              onUnableToPerformOperation();
            } else {
              
              new Thread() {
                public void run() {
                  // update user first as we count on this being updated on shared change
                  updateUser(BikeListActivity.this,bike.getId());
                  PreferencesUtils.setLong(BikeListActivity.this, R.string.settings_select_bike_current_selection_key, bike.getId());
                }
                
              }.start();
              
              String unformated = getString(R.string.bike_list_new_user_selected);
              Toast.makeText(BikeListActivity.this, String.format(unformated, bike.getName()), Toast.LENGTH_SHORT).show();
              BikeListActivity.this.doFinish();
            }
            
          }
          
        });
        
        registerForContextMenu(view);
        

        
      }
    };
    
    listView.setAdapter(resourceCursorAdapter);
    ApiAdapterFactory.getApiAdapter().configureListViewContextualMenu(this, listView,
        contextualActionModeCallback, R.menu.user_list_context_menu);

    getSupportLoaderManager().initLoader(0, null, new LoaderCallbacks<Cursor>() {
      @Override
      public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        long ownerId = PreferencesUtils.getLong(BikeListActivity.this, R.string.settings_select_user_current_selection_key);
        String selection = BikeInfoColumns.OWNER  + "=? OR " + BikeInfoColumns.SHARED + "=?";
        // one true, 0 false
        String [] args = new String[] {Long.toString(ownerId),Integer.toString(1)};
        
        return new CursorLoader(BikeListActivity.this, BikeInfoColumns.CONTENT_URI, PROJECTION,
            selection, args, "LOWER(" + BikeInfoColumns.NAME + ")");
      }
     

      @Override
      public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        resourceCursorAdapter.swapCursor(cursor);
      }

      @Override
      public void onLoaderReset(Loader<Cursor> loader) {
        resourceCursorAdapter.swapCursor(null);
      }
    });

    updateUI();
    this.contextMenu = (ContextMenu) findViewById(R.menu.user_list_context_menu);
    

  }

  private void initCancelButton() {
    cancelButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        doFinish();
      }

    });
  }

  private void doFinish() {
    BikeListActivity.this.finish();
  }

  private void updateUI() {
    
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
  protected void onResume() {
    super.onResume();

    // Update UI
    boolean isRecording = recordingTrackId != PreferencesUtils.RECORDING_TRACK_ID_DEFAULT;
    updateMenuItems(isRecording);
    resourceCursorAdapter.notifyDataSetChanged();
  }

  @Override
  protected void onPause() {
    super.onPause();

  }

  @Override
  protected void onStop() {
    super.onStop();

    // Unregister shared preferences listener
    sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.user_list, menu);

    deleteAllMenuItem = menu.findItem(R.id.user_list_delete_all);

    updateMenuItems(isRecording());
    return true;
  }
  
  private boolean isRecording() {
    return recordingTrackId != PreferencesUtils.RECORDING_TRACK_ID_DEFAULT;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean isRecording = recordingTrackId != PreferencesUtils.RECORDING_TRACK_ID_DEFAULT;
    switch (item.getItemId()) {
      case R.id.user_list_delete_all:
        if (isRecording) {
          this.onUnableToPerformOperation();
          return true;
        }
        ConfirmationDialogFragment.newInstance(new DeleteAllBikesCallback()).show(
        getSupportFragmentManager(),
        ConfirmationDialogFragment.TAG);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    getMenuInflater().inflate(R.menu.user_list_context_menu, menu);
    MenuItem deleteMenuItem = menu.findItem(R.id.list_context_menu_delete);
    deleteMenuItem.setVisible(isRecording());
    MenuItem editMenuItem = menu.findItem(R.id.list_context_menu_edit);
    editMenuItem.setVisible(isRecording());
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    // ((AdapterContextMenuInfo) item.getMenuInfo()).position)
    if (item != null
        && handleContextItem(item.getItemId(),
            ((AdapterContextMenuInfo) item.getMenuInfo()).position)) {
      return true;
    }
    return super.onContextItemSelected(item);
  }

  /**
   * Updates the menu items.
   * 
   * @param isRecording true if recording
   */
  private void updateMenuItems(boolean isRecording) {
    if (deleteAllMenuItem != null) {
      deleteAllMenuItem.setVisible(!isRecording);
    }
  }

  /**
   * Handles a context item selection.
   * 
   * @param itemId the menu item id
   * @param trackId the track id
   * @return true if handled.
   */
  private boolean handleContextItem(int itemId, int position) {
    Intent intent;
    //listView.getItemAtPosition(position);
    boolean isRecording = recordingTrackId != PreferencesUtils.RECORDING_TRACK_ID_DEFAULT;
    Bike bike = providerUtils.createBike((Cursor) listView.getItemAtPosition(position), false);
    switch (itemId) {
      case R.id.list_context_menu_edit:
        intent = IntentUtils.newIntent(this, BikeEditActivity.class).putExtra(
            BikeEditActivity.EXTRA_BIKE, bike);
        startActivity(intent);
        return true;
      case R.id.list_context_menu_delete:
         if (isRecording) {
           this.onUnableToPerformOperation();
           return true;
         }
         ConfirmationDialogFragment.newInstance(new DeleteBikeCallback(bike)).show(
         getSupportFragmentManager(),
         ConfirmationDialogFragment.TAG);
        return true;
      default:
        return false;
    }
  }

  protected void onUnableToPerformOperation() {
    Context context = getApplicationContext();
    CharSequence text = getString(0);
    int duration = Toast.LENGTH_SHORT;
    Toast toast = Toast.makeText(context, text, duration);
    toast.show();
  }

}
