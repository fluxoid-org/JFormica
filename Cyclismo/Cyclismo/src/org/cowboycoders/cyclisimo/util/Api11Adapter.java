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

package org.cowboycoders.cyclisimo.util;

import org.cowboycoders.cyclisimo.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.List;

import org.cowboycoders.cyclisimo.ContextualActionModeCallback;

/**
 * API level 11 specific implementation of the {@link ApiAdapter}.
 * 
 * @author Jimmy Shih
 */
@TargetApi(11)
public class Api11Adapter extends Api10Adapter {

  @Override
  public void hideTitle(Activity activity) {
    // Do nothing
  }

  @Override
  public void configureActionBarHomeAsUp(Activity activity) {
    activity.getActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @Override
  public void configureListViewContextualMenu(final Activity activity, ListView listView,
      final ContextualActionModeCallback contextualActionModeCallback) {
    listView.setOnItemLongClickListener(new OnItemLongClickListener() {
      ActionMode actionMode;
      @Override
      public boolean onItemLongClick(
          AdapterView<?> parent, View view, final int position, final long id) {
        if (actionMode != null) {
          return false;
        }
        actionMode = activity.startActionMode(new ActionMode.Callback() {
          @Override
          public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.list_context_menu, menu);
            return true;
          }
          @Override
          public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // Return false to indicate no change.
            return false;
          }
          @Override
          public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
          }
          @Override
          public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            mode.finish();
            return contextualActionModeCallback.onClick(item.getItemId(), position, id);
          }
        });
        TextView textView = (TextView) view.findViewById(R.id.list_item_name);
        if (textView != null) {
          actionMode.setTitle(textView.getText());
        }
        view.setSelected(true);
        return true;
      }
    });
  };

  @Override
  public void configureSearchWidget(Activity activity, final MenuItem menuItem) {
    SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
    SearchView searchView = (SearchView) menuItem.getActionView();
    searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
    searchView.setQueryRefinementEnabled(true);
  }

  @Override
  public boolean handleSearchMenuSelection(Activity activity) {
    // Returns false to allow the platform to expand the search widget.
    return false;
  }

  @Override
  public <T> void addAllToArrayAdapter(ArrayAdapter<T> arrayAdapter, List<T> items) {
    arrayAdapter.addAll(items);
  }

  @Override
  public void invalidMenu(Activity activity) {
    activity.invalidateOptionsMenu();
  }
}
