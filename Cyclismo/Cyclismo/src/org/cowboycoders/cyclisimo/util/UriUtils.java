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
 * Copyright 2011 Google Inc.
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

import android.net.Uri;

import java.util.List;

/**
 * Utilities for dealing with content and other types of URIs.
 *
 * @author Rodrigo Damazio
 */
public class UriUtils {

  public static boolean matchesContentUri(Uri uri, Uri baseContentUri) {
    if (uri == null) {
      return false;
    }

    // Check that scheme and authority are the same.
    if (!uri.getScheme().equals(baseContentUri.getScheme()) ||
        !uri.getAuthority().equals(baseContentUri.getAuthority())) {
      return false;
    }

    // Checks that all the base path components are in the URI.
    List<String> uriPathSegments = uri.getPathSegments();
    List<String> basePathSegments = baseContentUri.getPathSegments();
    if (basePathSegments.size() > uriPathSegments.size()) {
      return false;
    }
    for (int i = 0; i < basePathSegments.size(); i++) {
      if (!uriPathSegments.get(i).equals(basePathSegments.get(i))) {
        return false;
      }
    }

    return true;
  }

  public static boolean isFileUri(Uri uri) {
    return "file".equals(uri.getScheme());
  }

  private UriUtils() {}
}
