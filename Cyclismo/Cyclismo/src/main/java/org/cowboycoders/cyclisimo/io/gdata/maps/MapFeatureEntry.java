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
// Copyright 2010 Google Inc. All Rights Reserved.
package org.cowboycoders.cyclisimo.io.gdata.maps;

import com.google.wireless.gdata.data.Entry;

import java.util.HashMap;
import java.util.Map;

/**
 * GData entry for a map feature.
 */
public class MapFeatureEntry extends Entry {

  private String mPrivacy = null;
  private Map<String, String> mAttributes = new HashMap<String, String>();

  public void setPrivacy(String privacy) {
    mPrivacy = privacy;
  }

  public String getPrivacy() {
    return mPrivacy;
  }

  public void setAttribute(String name, String value) {
    mAttributes.put(name, value);
  }

  public void removeAttribute(String name) {
    mAttributes.remove(name);
  }

  public Map<String, String> getAllAttributes() {
    return mAttributes;
  }
}
