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
 * Copyright 2012 Google Inc.
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

package org.cowboycoders.cyclisimo.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import org.cowboycoders.cyclisimo.CourseListActivity;
import org.cowboycoders.cyclisimo.R;
import org.cowboycoders.cyclisimo.TrackListActivity;
import org.cowboycoders.cyclisimo.content.MyTracksCourseProviderUtils;
import org.cowboycoders.cyclisimo.content.MyTracksProviderUtils;
import org.cowboycoders.cyclisimo.services.TrackRecordingServiceConnection;
import org.cowboycoders.cyclisimo.util.DialogUtils;
import org.cowboycoders.cyclisimo.util.IntentUtils;
import org.cowboycoders.cyclisimo.util.PreferencesUtils;
import org.cowboycoders.cyclisimo.util.TrackRecordingServiceConnectionUtils;

/**
 * A DialogFragment to delete one track.
 * 
 * @author Jimmy Shih
 */
public class DeleteOneTrackDialogFragment extends DialogFragment {

  public static final String DELETE_ONE_TRACK_DIALOG_TAG = "deleteOneTrackDialog";
  private static final String KEY_TRACK_ID = "trackId";
  private static final String KEY_USE_COURSE_PROVIDER = "useCourseProvider";
  private int messageId = R.string.track_detail_delete_confirm_message;

  /**
   * Interface for caller of this dialog fragment.
   * 
   * @author Jimmy Shih
   */
  public interface DeleteOneTrackCaller {
    public TrackRecordingServiceConnection getTrackRecordingServiceConnection();
  }

  public static DeleteOneTrackDialogFragment newInstance(long trackId) {
    Bundle bundle = new Bundle();
    bundle.putLong(KEY_TRACK_ID, trackId);

    DeleteOneTrackDialogFragment deleteOneTrackDialogFragment = new DeleteOneTrackDialogFragment();
    deleteOneTrackDialogFragment.setArguments(bundle);
    return deleteOneTrackDialogFragment;
  }

  private FragmentActivity activity;
  private DeleteOneTrackCaller caller;

  @Override
  public void onAttach(Activity anActivity) {
    super.onAttach(anActivity);
    try {
      caller = (DeleteOneTrackCaller) anActivity;
    } catch (ClassCastException e) {
      throw new ClassCastException(anActivity.toString() + " must implement DeleteOneTrackCaller");
    }
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    activity = getActivity();
    return DialogUtils.createConfirmationDialog(activity,
        messageId, new DialogInterface.OnClickListener() {
            private boolean mUseCourseProvider = false;

            @Override
          public void onClick(DialogInterface dialog, int which) {
            final long trackId = getArguments().getLong(KEY_TRACK_ID);
            this.mUseCourseProvider  = getArguments().getBoolean(KEY_USE_COURSE_PROVIDER);
            final Context context = activity;
            if (trackId == PreferencesUtils.getLong(context, R.string.recording_track_id_key)) {
              if (!mUseCourseProvider) {
              TrackRecordingServiceConnectionUtils.stopRecording(
                  context, caller.getTrackRecordingServiceConnection(), false);
              }
            }
            new Thread(new Runnable() {
              @Override
              public void run() {
                getProviderUtils(context).deleteTrack(trackId);
              }

              private MyTracksProviderUtils getProviderUtils(final Context c) {
                if (mUseCourseProvider) {
                  return new MyTracksCourseProviderUtils(c.getContentResolver());
                }
                return MyTracksProviderUtils.Factory.get(c);
              }
            }).start();
            Intent intent = null;
            if (mUseCourseProvider) {
              intent = IntentUtils.newIntent(context, CourseListActivity.class);
            } else {
              intent = IntentUtils.newIntent(context, TrackListActivity.class);
            }
            
            startActivity(intent);
            // Close the activity since its content can change after delete
            activity.finish();

          }
        });
  }

  public static DialogFragment newInstance(long trackId, boolean useCourseProvider) {
    Bundle bundle = new Bundle();
    bundle.putLong(KEY_TRACK_ID, trackId);
    if (useCourseProvider) {
      bundle.putBoolean(DeleteOneTrackDialogFragment.KEY_USE_COURSE_PROVIDER, true);
    }
    DeleteOneTrackDialogFragment deleteOneTrackDialogFragment = new DeleteOneTrackDialogFragment();
    deleteOneTrackDialogFragment.setArguments(bundle);
    return deleteOneTrackDialogFragment;
  }
  
  public static DialogFragment newInstance(long trackId, boolean useCourseProvider, int messageId) {
    DeleteOneTrackDialogFragment rtn  = (DeleteOneTrackDialogFragment) newInstance(trackId,useCourseProvider);
    rtn.messageId = messageId;
    return rtn;
  }
}