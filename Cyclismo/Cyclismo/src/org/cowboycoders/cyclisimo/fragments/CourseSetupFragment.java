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
package org.cowboycoders.cyclisimo.fragments;

import org.cowboycoders.cyclisimo.content.MyTracksCourseProviderUtils;
import org.cowboycoders.cyclisimo.content.Track;
import org.cowboycoders.cyclisimo.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

import org.cowboycoders.cyclisimo.Constants;
import org.cowboycoders.cyclisimo.settings.AbstractSettingsFragment;
import org.cowboycoders.cyclisimo.util.PreferencesUtils;

public class CourseSetupFragment extends AbstractSettingsFragment {
    private final static String TAG = "CourseSetupFragment";
    public static final int PICK_COURSE_REQUEST = 1567;
    //private Long trackId = null;
    //private String mode = null;
    //private OnPreferenceChangeListener listener;
    private OnSharedPreferenceChangeListener sharedListener;
    private SharedPreferences sharedPreferences;
    private UpdateSummaryCaller updateTrackIdSummarryCaller;
    private CourseSetupObserver observer;
    private Preference courseTrackIdPreference;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = this.getActivity();
        Log.i(TAG, "fragment onCreate");
        addPreferencesFromResource(R.xml.course_settings);
        
        //this is set early as it required for updateUiByCourseMode
        this.courseTrackIdPreference = (Preference) findPreference(
            getString(R.string.course_track_id));
        
        ListPreference courseModeListPreference = (ListPreference) findPreference(
            getString(R.string.course_mode));
        OnPreferenceChangeListener listener = new OnPreferenceChangeListener() {
            @Override
          public boolean onPreferenceChange(Preference preference, Object newValue) {
            updateUiByCourseMode((String) newValue);
            return true;
          }
        };
        String courseModeValue = PreferencesUtils.getString(
            this.getActivity(), R.string.course_mode, PreferencesUtils.COURSE_MODE_DEFAULT);
        //mode = courseModeValue;
        this.
        configurePreference(courseModeListPreference,
            getResources().getStringArray(R.array.course_mode_select_options),
            getResources().getStringArray(R.array.course_mode_select_values),
            R.string.settings_courses_mode_summary, courseModeValue, listener,null);
        
        
        Long courseTrackIdValue = PreferencesUtils.getLong(context, R.string.course_track_id);
        
        
        //listener = new OnPreferenceChangeListener() {
        //  @Override
        //public boolean onPreferenceChange(Preference preference, Object newValue) {
        //  updateUiByCourseTrackId((Long)newValue);
        //  return true;
        //}
        //};
        
        courseTrackIdPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
          @Override
          public boolean onPreferenceClick(Preference preference) {
            startActivityForResult(preference.getIntent(), PICK_COURSE_REQUEST);
            return true;
          }
        });
        
        
        //trackId = courseTrackIdValue;
        
        PreferencesUtils.SettingsSelectionSummarizer summarizer = new PreferencesUtils.SettingsSelectionSummarizer() {

          @Override
          public String summarize(Object value) {
            return summarizeTrackIdSelection(value);
          }
          
        };
        
        //configurePreference(courseTrackIdPreference,
         //   null,
         //   null,
          //  R.string.settings_courses_route_summary, courseTrackIdValue, null, summarizer);
        
        this.updateTrackIdSummarryCaller = new UpdateSummaryCaller(courseTrackIdPreference, null, null,R.string.settings_courses_route_summary , summarizer);
        
        // initial trackId update
        updateUiByCourseTrackId(courseTrackIdValue);
        
        this.sharedPreferences = this.getActivity().getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
        
        this.sharedListener = new OnSharedPreferenceChangeListener() {

          @Override
          public void onSharedPreferenceChanged(SharedPreferences sharedPreferencesIn, String key) {
            if (key == PreferencesUtils.getKey(getActivity(), R.string.course_track_id)) {
              Long newValue = sharedPreferences.getLong(key, -1l);
              Log.i(TAG,"detected new track: " + newValue);
              updateUiByCourseTrackId(newValue);
            }
            
          }
          
        };
        
        if(observer != null) {
          observer.onCourseModeUpdate(courseModeValue);
          observer.onTrackIdUpdate(courseTrackIdValue);
        }
        
        
    }
    
    public CourseSetupFragment() {
      super();
    }
    
    public CourseSetupFragment(CourseSetupObserver observer) {
      this();
      this.observer = observer;
    }
    
    /* (non-Javadoc)
     * @see android.preference.PreferenceFragment#onStart()
     */
    @Override
    public void onStart() {
      super.onStart();
      this.sharedPreferences.registerOnSharedPreferenceChangeListener(sharedListener);
    }
    
    @Override
    public void onResume() {
      super.onResume();
      //this.sharedPreferences.registerOnSharedPreferenceChangeListener(sharedListener);
    }
    
    @Override
    public void onStop() {
      super.onStop();
      //sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedListener);
    }
    
    @Override
    public void onDestroy() {
      super.onStop();
      sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedListener);
    }
    


    protected String summarizeTrackIdSelection(Object value) {
      Long trackId = (Long) value;
      String noneSelected = this.getString(R.string.course_not_selected);
      if (trackId == -1) {
        return noneSelected;
      }
      Track track = new MyTracksCourseProviderUtils(this.getActivity().getContentResolver()).getTrack(trackId);
      if (track == null) {
        Log.w(TAG, "track invalid : shared preference mismatch");
        PreferencesUtils.setLong(this.getActivity(), R.string.course_track_id, -1L);
        return noneSelected;
      }
      return track.getName();
    }


    private void updateUiByCourseTrackId(Long newValue) {
      Log.i(TAG, "new track selected with id: " + newValue);
      this.updateTrackIdSummarryCaller.updateSummary(newValue);
      if (observer != null) {
        observer.onTrackIdUpdate(newValue);
      }
    }

    private void updateUiByCourseMode(String newValue) {
      Log.i(TAG,"course mode: " + newValue);
      if (observer != null) {
        observer.onCourseModeUpdate(newValue);
      }
      if (!newValue.equals(getString(R.string.settings_courses_mode_simulation_value))) {
        courseTrackIdPreference.setEnabled(false);
      }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_COURSE_REQUEST ) {
            if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED ){
              if (data != null) {
                Long trackId = data.getLongExtra(getString(R.string.course_track_id), -1L);
                updateUiByCourseTrackId(trackId);
              } else {
                Log.d(TAG,"onActivityResult : data was null");
                updateUiByCourseTrackId(-1L);
              }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    //private void redraw() {
    //  Log.i(TAG,"redrawing");
    //  Fragment newFragment = new CourseSetupFragment();
    //  FragmentTransaction transaction = getFragmentManager().beginTransaction();
    //  transaction.replace(R.id.course_select_preferences, newFragment);
    //  transaction.addToBackStack(null);
    //  transaction.commitAllowingStateLoss();
   // }
    
    public static interface CourseSetupObserver {
      
      /**
       * @param trackId the new trackID
       */
      public void onTrackIdUpdate(Long trackId);
      
      public void onCourseModeUpdate(String modeString);
      
    }


}