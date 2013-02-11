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

import org.cowboycoders.cyclisimo.content.MyTracksProviderUtils;
import org.cowboycoders.cyclisimo.R;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import org.cowboycoders.cyclisimo.MarkerListActivity;
import org.cowboycoders.cyclisimo.content.DescriptionGeneratorImpl;
import org.cowboycoders.cyclisimo.util.DialogUtils;
import org.cowboycoders.cyclisimo.util.IntentUtils;

/**
 * A DialogFragment to delete one marker.
 *
 * @author Jimmy Shih
 */
public class DeleteOneMarkerDialogFragment extends DialogFragment {

  public static final String DELETE_ONE_MARKER_DIALOG_TAG = "deleteOneMarkerDialog";
  private static final String KEY_MARKER_ID = "markerId";
  private static final String KEY_TRACK_ID = "trackId";

  public static DeleteOneMarkerDialogFragment newInstance(long markerId, long trackId) {
    Bundle bundle = new Bundle();
    bundle.putLong(KEY_MARKER_ID, markerId);
    bundle.putLong(KEY_TRACK_ID, trackId);

    DeleteOneMarkerDialogFragment deleteOneMarkerDialogFragment = new DeleteOneMarkerDialogFragment();
    deleteOneMarkerDialogFragment.setArguments(bundle);
    return deleteOneMarkerDialogFragment;
  }

  private FragmentActivity activity;
  
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    activity = getActivity();
    return DialogUtils.createConfirmationDialog(activity,
        R.string.marker_delete_one_marker_confirm_message, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            new Thread(new Runnable() {
              @Override
              public void run() {
                MyTracksProviderUtils.Factory.get(activity).deleteWaypoint(
                    getArguments().getLong(KEY_MARKER_ID),
                    new DescriptionGeneratorImpl(activity));
              }
            }).start();
            Intent intent = IntentUtils.newIntent(activity, MarkerListActivity.class)
                .putExtra(MarkerListActivity.EXTRA_TRACK_ID, getArguments().getLong(KEY_TRACK_ID));
            startActivity(intent);
            // Close the activity since its content can change after delete.
            activity.finish();
          }
        });
  }
}