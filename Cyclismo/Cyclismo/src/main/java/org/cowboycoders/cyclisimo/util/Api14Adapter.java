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

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.view.MenuItem;
import android.widget.SearchView;

/**
 * API level 14 specific implementation of the {@link ApiAdapter}.
 * 
 * @author Jimmy Shih
 */
@TargetApi(14)
public class Api14Adapter extends Api11Adapter {

  @Override
  public void configureActionBarHomeAsUp(Activity activity) {
    ActionBar actionBar = activity.getActionBar();
    actionBar.setHomeButtonEnabled(true);
    actionBar.setDisplayHomeAsUpEnabled(true);
  }

  @Override
  public void configureSearchWidget(Activity activity, final MenuItem menuItem) {
    super.configureSearchWidget(activity, menuItem);
    SearchView searchView = (SearchView) menuItem.getActionView();
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        menuItem.collapseActionView();
        return false;
      }
      @Override
      public boolean onQueryTextChange(String newText) {
        return false;
      }
    });
    searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
        @Override
      public boolean onSuggestionSelect(int position) {
        return false;
      }
      @Override
      public boolean onSuggestionClick(int position) {
        menuItem.collapseActionView();
        return false;
      }
    });
  }

  @Override
  public boolean handleSearchKey(MenuItem menuItem) {
    menuItem.expandActionView();
    return true;
  }
}
