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
// Copyright 2009 Google Inc. All Rights Reserved.
package org.cowboycoders.cyclisimo.io.gdata.maps;

/**
 * Metadata about a Google Maps map.
 */
public class MapsMapMetadata {

  private String title;
  private String description;
  private String gdataEditUri;
  private boolean searchable;

  public MapsMapMetadata() {
    title = "";
    description = "";
    gdataEditUri = "";
    searchable = false;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean getSearchable() {
    return searchable;
  }

  public void setSearchable(boolean searchable) {
    this.searchable = searchable;
  }

  public String getGDataEditUri() {
    return gdataEditUri;
  }

  public void setGDataEditUri(String editUri) {
    this.gdataEditUri = editUri;
  }
}
