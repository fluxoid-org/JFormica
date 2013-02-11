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
 * Copyright 2010 Google Inc.
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
package org.cowboycoders.cyclisimo.io.gdata;

import com.google.wireless.gdata.client.QueryParams;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Simple implementation of the QueryParams interface.
 */
// TODO: deal with categories
public class QueryParamsImpl extends QueryParams {

  private final Map<String, String> mParams = new HashMap<String, String>();

  @Override
  public void clear() {
    setEntryId(null);
    mParams.clear();
  }

  @Override
  public String generateQueryUrl(String feedUrl) {

    if (TextUtils.isEmpty(getEntryId()) && mParams.isEmpty()) {
      // nothing to do
      return feedUrl;
    }

    // handle entry IDs
    if (!TextUtils.isEmpty(getEntryId())) {
      if (!mParams.isEmpty()) {
        throw new IllegalStateException("Cannot set both an entry ID "
            + "and other query paramters.");
      }
      return feedUrl + '/' + getEntryId();
    }

    // otherwise, append the querystring params.
    StringBuilder sb = new StringBuilder();
    sb.append(feedUrl);
    Set<String> params = mParams.keySet();
    boolean first = true;
    if (feedUrl.contains("?")) {
      first = false;
    } else {
      sb.append('?');
    }
    for (String param : params) {
      if (first) {
        first = false;
      } else {
        sb.append('&');
      }
      sb.append(param);
      sb.append('=');
      String value = mParams.get(param);
      String encodedValue = null;

      try {
        encodedValue = URLEncoder.encode(value, "UTF-8");
      } catch (UnsupportedEncodingException uee) {
        // Should not happen
        throw new IllegalStateException("Cannot encode " + value, uee);
      }
      sb.append(encodedValue);
    }
    return sb.toString();
  }

  @Override
  public String getParamValue(String param) {
    if (!(mParams.containsKey(param))) {
      return null;
    }
    return mParams.get(param);
  }

  @Override
  public void setParamValue(String param, String value) {
    mParams.put(param, value);
  }
}
