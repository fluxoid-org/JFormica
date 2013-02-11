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
 * Metadata about a maps feature.
 */
class MapsFeatureMetadata {

  private static final String BLUE_DOT_URL =
      "http://maps.google.com/mapfiles/ms/micons/blue-dot.png";
  private static final int DEFAULT_COLOR = 0x800000FF;
  private static final int DEFAULT_FILL_COLOR = 0xC00000FF;

  private String title;
  private String description;
  private int type;
  private int color;
  private int lineWidth;
  private int fillColor;
  private String iconUrl;

  public MapsFeatureMetadata() {
    title = "";
    description = "";
    type = MapsFeature.MARKER;
    color = DEFAULT_COLOR;
    lineWidth = 5;
    fillColor = DEFAULT_FILL_COLOR;
    iconUrl = BLUE_DOT_URL;
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

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public int getLineWidth() {
    return lineWidth;
  }

  public void setLineWidth(int width) {
    lineWidth = width;
  }

  public int getFillColor() {
    return fillColor;
  }

  public void setFillColor(int color) {
    fillColor = color;
  }

  public String getIconUrl() {
    return iconUrl;
  }

  public void setIconUrl(String url) {
    iconUrl = url;
  }
}
