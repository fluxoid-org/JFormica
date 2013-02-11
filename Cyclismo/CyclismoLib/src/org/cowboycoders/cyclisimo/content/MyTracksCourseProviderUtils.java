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
 * Copyright 2008 Google Inc.
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

package org.cowboycoders.cyclisimo.content;

import com.google.protobuf.InvalidProtocolBufferException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.cowboycoders.cyclisimo.content.Sensor.SensorDataSet;
import org.cowboycoders.cyclisimo.stats.TripStatistics;

/**
 * {@link MyTracksProviderUtils} implementation.
 * 
 * @author Leif Hendrik Wilden
 */
public class MyTracksCourseProviderUtils implements MyTracksProviderUtils {

  private static final String TAG = MyTracksCourseProviderUtils.class.getSimpleName();

  private static final int MAX_LATITUDE = 90000000;
  
  /**
   * The authority (the first part of the URI) for the My Tracks content
   * provider.
   */
  public static final String AUTHORITY = "org.cowboycoders.cyclisimo.courses";
  
  /**
   * The authority (the first part of the URI) for the My Tracks content
   * provider.
   */
  public static final String TABLE_PREFIX = "course_";

  private final ContentResolver contentResolver;
  private int defaultCursorBatchSize = 2000;

  public MyTracksCourseProviderUtils(ContentResolver contentResolver) {
    this.contentResolver = contentResolver;
  }

  @Override
  public Track createTrack(Cursor cursor) {
    int idIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns._ID);
    int nameIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.NAME);
    int descriptionIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.DESCRIPTION);
    int categoryIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.CATEGORY);
    int startIdIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.STARTID);
    int stopIdIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.STOPID);
    int startTimeIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.STARTTIME);
    int stopTimeIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.STOPTIME);
    int numPointsIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.NUMPOINTS);
    int totalDistanceIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.TOTALDISTANCE);
    int totalTimeIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.TOTALTIME);
    int movingTimeIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.MOVINGTIME);
    int minLatIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.MINLAT);
    int maxLatIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.MAXLAT);
    int minLonIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.MINLON);
    int maxLonIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.MAXLON);
    int maxSpeedIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.MAXSPEED);
    int minElevationIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.MINELEVATION);
    int maxElevationIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.MAXELEVATION);
    int elevationGainIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.ELEVATIONGAIN);
    int minGradeIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.MINGRADE);
    int maxGradeIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.MAXGRADE);
    int mapIdIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.MAPID);
    int tableIdIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.TABLEID);
    int iconIndex = cursor.getColumnIndexOrThrow(CourseTracksColumns.ICON);

    Track track = new Track();
    TripStatistics tripStatistics = track.getTripStatistics();
    if (!cursor.isNull(idIndex)) {
      track.setId(cursor.getLong(idIndex));
    }
    if (!cursor.isNull(nameIndex)) {
      track.setName(cursor.getString(nameIndex));
    }
    if (!cursor.isNull(descriptionIndex)) {
      track.setDescription(cursor.getString(descriptionIndex));
    }
    if (!cursor.isNull(categoryIndex)) {
      track.setCategory(cursor.getString(categoryIndex));
    }
    if (!cursor.isNull(startIdIndex)) {
      track.setStartId(cursor.getLong(startIdIndex));
    }
    if (!cursor.isNull(stopIdIndex)) {
      track.setStopId(cursor.getLong(stopIdIndex));
    }
    if (!cursor.isNull(startTimeIndex)) {
      tripStatistics.setStartTime(cursor.getLong(startTimeIndex));
    }
    if (!cursor.isNull(stopTimeIndex)) {
      tripStatistics.setStopTime(cursor.getLong(stopTimeIndex));
    }
    if (!cursor.isNull(numPointsIndex)) {
      track.setNumberOfPoints(cursor.getInt(numPointsIndex));
    }
    if (!cursor.isNull(totalDistanceIndex)) {
      tripStatistics.setTotalDistance(cursor.getFloat(totalDistanceIndex));
    }
    if (!cursor.isNull(totalTimeIndex)) {
      tripStatistics.setTotalTime(cursor.getLong(totalTimeIndex));
    }
    if (!cursor.isNull(movingTimeIndex)) {
      tripStatistics.setMovingTime(cursor.getLong(movingTimeIndex));
    }
    if (!cursor.isNull(minLatIndex) && !cursor.isNull(maxLatIndex) && !cursor.isNull(minLonIndex)
        && !cursor.isNull(maxLonIndex)) {
      int bottom = cursor.getInt(minLatIndex);
      int top = cursor.getInt(maxLatIndex);
      int left = cursor.getInt(minLonIndex);
      int right = cursor.getInt(maxLonIndex);
      tripStatistics.setBounds(left, top, right, bottom);
    }
    if (!cursor.isNull(maxSpeedIndex)) {
      tripStatistics.setMaxSpeed(cursor.getFloat(maxSpeedIndex));
    }
    if (!cursor.isNull(minElevationIndex)) {
      tripStatistics.setMinElevation(cursor.getFloat(minElevationIndex));
    }
    if (!cursor.isNull(maxElevationIndex)) {
      tripStatistics.setMaxElevation(cursor.getFloat(maxElevationIndex));
    }
    if (!cursor.isNull(elevationGainIndex)) {
      tripStatistics.setTotalElevationGain(cursor.getFloat(elevationGainIndex));
    }
    if (!cursor.isNull(minGradeIndex)) {
      tripStatistics.setMinGrade(cursor.getFloat(minGradeIndex));
    }
    if (!cursor.isNull(maxGradeIndex)) {
      tripStatistics.setMaxGrade(cursor.getFloat(maxGradeIndex));
    }
    if (!cursor.isNull(mapIdIndex)) {
      track.setMapId(cursor.getString(mapIdIndex));
    }
    if (!cursor.isNull(tableIdIndex)) {
      track.setTableId(cursor.getString(tableIdIndex));
    }
    if (!cursor.isNull(iconIndex)) {
      track.setIcon(cursor.getString(iconIndex));
    }
    return track;
  }

  @Override
  public void deleteAllTracks() {
    contentResolver.delete(CourseTrackPointsColumns.CONTENT_URI, null, null);
    contentResolver.delete(CourseWaypointsColumns.CONTENT_URI, null, null);
    // Delete tracks last since it triggers a database vaccum call
    contentResolver.delete(CourseTracksColumns.CONTENT_URI, null, null);
  }

  @Override
  public void deleteTrack(long trackId) {
    Track track = getTrack(trackId);
    if (track != null) {
      String where = CourseTrackPointsColumns._ID + ">=? AND " + CourseTrackPointsColumns._ID + "<=?";
      String[] selectionArgs = new String[] {
          Long.toString(track.getStartId()), Long.toString(track.getStopId()) };
      contentResolver.delete(CourseTrackPointsColumns.CONTENT_URI, where, selectionArgs);
    }
    contentResolver.delete(CourseWaypointsColumns.CONTENT_URI, CourseWaypointsColumns.TRACKID + "=?",
        new String[] { Long.toString(trackId) });
    // Delete tracks last since it triggers a database vaccum call
    contentResolver.delete(CourseTracksColumns.CONTENT_URI, CourseTracksColumns._ID + "=?",
        new String[] { Long.toString(trackId) });
  }

  @Override
  public List<Track> getAllTracks() {
    Cursor cursor = getTrackCursor(null, null, null, CourseTracksColumns._ID);
    ArrayList<Track> tracks = new ArrayList<Track>();
    if (cursor != null) {
      tracks.ensureCapacity(cursor.getCount());
      if (cursor.moveToFirst()) {
        do {
          tracks.add(createTrack(cursor));
        } while (cursor.moveToNext());
      }
      cursor.close();
    }
    return tracks;
  }

  @Override
  public Track getLastTrack() {
    Cursor cursor = null;
    try {
      String selection = CourseTracksColumns._ID + "=(select max(" + CourseTracksColumns._ID + ") from "
          + CourseTracksColumns.TABLE_NAME + ")";
      cursor = getTrackCursor(null, selection, null, CourseTracksColumns._ID);
      if (cursor != null && cursor.moveToNext()) {
        return createTrack(cursor);
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return null;
  }

  @Override
  public Track getTrack(long trackId) {
    if (trackId < 0) {
      return null;
    }
    Cursor cursor = null;
    try {
      cursor = getTrackCursor(null, CourseTracksColumns._ID + "=?",
          new String[] { Long.toString(trackId) }, CourseTracksColumns._ID);
      if (cursor != null && cursor.moveToNext()) {
        return createTrack(cursor);
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return null;
  }

  @Override
  public Cursor getTrackCursor(String selection, String[] selectionArgs, String sortOrder) {
    return getTrackCursor(null, selection, selectionArgs, sortOrder);
  }

  @Override
  public Uri insertTrack(Track track) {
    return contentResolver.insert(CourseTracksColumns.CONTENT_URI, createContentValues(track));
  }

  @Override
  public void updateTrack(Track track) {
    contentResolver.update(CourseTracksColumns.CONTENT_URI, createContentValues(track),
        CourseTracksColumns._ID + "=?", new String[] { Long.toString(track.getId()) });
  }

  private ContentValues createContentValues(Track track) {
    ContentValues values = new ContentValues();
    TripStatistics tripStatistics = track.getTripStatistics();

    // Value < 0 indicates no id is available
    if (track.getId() >= 0) {
      values.put(CourseTracksColumns._ID, track.getId());
    }
    values.put(CourseTracksColumns.NAME, track.getName());
    values.put(CourseTracksColumns.DESCRIPTION, track.getDescription());
    values.put(CourseTracksColumns.CATEGORY, track.getCategory());
    values.put(CourseTracksColumns.STARTID, track.getStartId());
    values.put(CourseTracksColumns.STOPID, track.getStopId());
    values.put(CourseTracksColumns.STARTTIME, tripStatistics.getStartTime());
    values.put(CourseTracksColumns.STOPTIME, tripStatistics.getStopTime());
    values.put(CourseTracksColumns.NUMPOINTS, track.getNumberOfPoints());
    values.put(CourseTracksColumns.TOTALDISTANCE, tripStatistics.getTotalDistance());
    values.put(CourseTracksColumns.TOTALTIME, tripStatistics.getTotalTime());
    values.put(CourseTracksColumns.MOVINGTIME, tripStatistics.getMovingTime());
    values.put(CourseTracksColumns.MINLAT, tripStatistics.getBottom());
    values.put(CourseTracksColumns.MAXLAT, tripStatistics.getTop());
    values.put(CourseTracksColumns.MINLON, tripStatistics.getLeft());
    values.put(CourseTracksColumns.MAXLON, tripStatistics.getRight());
    values.put(CourseTracksColumns.AVGSPEED, tripStatistics.getAverageSpeed());
    values.put(CourseTracksColumns.AVGMOVINGSPEED, tripStatistics.getAverageMovingSpeed());
    values.put(CourseTracksColumns.MAXSPEED, tripStatistics.getMaxSpeed());
    values.put(CourseTracksColumns.MINELEVATION, tripStatistics.getMinElevation());
    values.put(CourseTracksColumns.MAXELEVATION, tripStatistics.getMaxElevation());
    values.put(CourseTracksColumns.ELEVATIONGAIN, tripStatistics.getTotalElevationGain());
    values.put(CourseTracksColumns.MINGRADE, tripStatistics.getMinGrade());
    values.put(CourseTracksColumns.MAXGRADE, tripStatistics.getMaxGrade());
    values.put(CourseTracksColumns.MAPID, track.getMapId());
    values.put(CourseTracksColumns.TABLEID, track.getTableId());
    values.put(CourseTracksColumns.ICON, track.getIcon());
    return values;
  }

  /**
   * Gets a track cursor.
   * 
   * @param projection the projection
   * @param selection the selection
   * @param selectionArgs the selection arguments
   * @param sortOrder the sort oder
   */
  private Cursor getTrackCursor(
      String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    return contentResolver.query(
        CourseTracksColumns.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
  }

  @Override
  public Waypoint createWaypoint(Cursor cursor) {
    int idIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns._ID);
    int nameIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.NAME);
    int descriptionIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.DESCRIPTION);
    int categoryIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.CATEGORY);
    int iconIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.ICON);
    int trackIdIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.TRACKID);
    int typeIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.TYPE);
    int lengthIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.LENGTH);
    int durationIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.DURATION);
    int startTimeIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.STARTTIME);
    int startIdIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.STARTID);
    int stopIdIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.STOPID);
    int longitudeIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.LONGITUDE);
    int latitudeIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.LATITUDE);
    int timeIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.TIME);
    int altitudeIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.ALTITUDE);
    int accuracyIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.ACCURACY);
    int speedIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.SPEED);
    int bearingIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.BEARING);
    int totalDistanceIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.TOTALDISTANCE);
    int totalTimeIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.TOTALTIME);
    int movingTimeIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.MOVINGTIME);
    int maxSpeedIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.MAXSPEED);
    int minElevationIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.MINELEVATION);
    int maxElevationIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.MAXELEVATION);
    int elevationGainIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.ELEVATIONGAIN);
    int minGradeIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.MINGRADE);
    int maxGradeIndex = cursor.getColumnIndexOrThrow(CourseWaypointsColumns.MAXGRADE);

    Waypoint waypoint = new Waypoint();

    if (!cursor.isNull(idIndex)) {
      waypoint.setId(cursor.getLong(idIndex));
    }
    if (!cursor.isNull(nameIndex)) {
      waypoint.setName(cursor.getString(nameIndex));
    }
    if (!cursor.isNull(descriptionIndex)) {
      waypoint.setDescription(cursor.getString(descriptionIndex));
    }
    if (!cursor.isNull(categoryIndex)) {
      waypoint.setCategory(cursor.getString(categoryIndex));
    }
    if (!cursor.isNull(iconIndex)) {
      waypoint.setIcon(cursor.getString(iconIndex));
    }
    if (!cursor.isNull(trackIdIndex)) {
      waypoint.setTrackId(cursor.getLong(trackIdIndex));
    }
    if (!cursor.isNull(typeIndex)) {
      waypoint.setType(cursor.getInt(typeIndex));
    }
    if (!cursor.isNull(lengthIndex)) {
      waypoint.setLength(cursor.getFloat(lengthIndex));
    }
    if (!cursor.isNull(durationIndex)) {
      waypoint.setDuration(cursor.getLong(durationIndex));
    }
    if (!cursor.isNull(startIdIndex)) {
      waypoint.setStartId(cursor.getLong(startIdIndex));
    }
    if (!cursor.isNull(stopIdIndex)) {
      waypoint.setStopId(cursor.getLong(stopIdIndex));
    }

    Location location = new Location("");
    if (!cursor.isNull(longitudeIndex) && !cursor.isNull(latitudeIndex)) {
      location.setLongitude(((double) cursor.getInt(longitudeIndex)) / 1E6);
      location.setLatitude(((double) cursor.getInt(latitudeIndex)) / 1E6);
    }
    if (!cursor.isNull(timeIndex)) {
      location.setTime(cursor.getLong(timeIndex));
    }
    if (!cursor.isNull(altitudeIndex)) {
      location.setAltitude(cursor.getFloat(altitudeIndex));
    }
    if (!cursor.isNull(accuracyIndex)) {
      location.setAccuracy(cursor.getFloat(accuracyIndex));
    }
    if (!cursor.isNull(speedIndex)) {
      location.setSpeed(cursor.getFloat(speedIndex));
    }
    if (!cursor.isNull(bearingIndex)) {
      location.setBearing(cursor.getFloat(bearingIndex));
    }
    waypoint.setLocation(location);

    TripStatistics tripStatistics = new TripStatistics();
    boolean hasTripStatistics = false;
    if (!cursor.isNull(startTimeIndex)) {
      tripStatistics.setStartTime(cursor.getLong(startTimeIndex));
      hasTripStatistics = true;
    }
    if (!cursor.isNull(totalDistanceIndex)) {
      tripStatistics.setTotalDistance(cursor.getFloat(totalDistanceIndex));
      hasTripStatistics = true;
    }
    if (!cursor.isNull(totalTimeIndex)) {
      tripStatistics.setTotalTime(cursor.getLong(totalTimeIndex));
      hasTripStatistics = true;
    }
    if (!cursor.isNull(movingTimeIndex)) {
      tripStatistics.setMovingTime(cursor.getLong(movingTimeIndex));
      hasTripStatistics = true;
    }
    if (!cursor.isNull(maxSpeedIndex)) {
      tripStatistics.setMaxSpeed(cursor.getFloat(maxSpeedIndex));
      hasTripStatistics = true;
    }
    if (!cursor.isNull(minElevationIndex)) {
      tripStatistics.setMinElevation(cursor.getFloat(minElevationIndex));
      hasTripStatistics = true;
    }
    if (!cursor.isNull(maxElevationIndex)) {
      tripStatistics.setMaxElevation(cursor.getFloat(maxElevationIndex));
      hasTripStatistics = true;
    }
    if (!cursor.isNull(elevationGainIndex)) {
      tripStatistics.setTotalElevationGain(cursor.getFloat(elevationGainIndex));
      hasTripStatistics = true;
    }
    if (!cursor.isNull(minGradeIndex)) {
      tripStatistics.setMinGrade(cursor.getFloat(minGradeIndex));
      hasTripStatistics = true;
    }
    if (!cursor.isNull(maxGradeIndex)) {
      tripStatistics.setMaxGrade(cursor.getFloat(maxGradeIndex));
      hasTripStatistics = true;
    }

    if (hasTripStatistics) {
      waypoint.setTripStatistics(tripStatistics);
    }
    return waypoint;
  }

  @Override
  public void deleteWaypoint(long waypointId, DescriptionGenerator descriptionGenerator) {
    final Waypoint waypoint = getWaypoint(waypointId);
    if (waypoint != null && waypoint.getType() == Waypoint.TYPE_STATISTICS) {
      final Waypoint nextWaypoint = getNextStatisticsWaypointAfter(waypoint);
      if (nextWaypoint == null) {
        Log.d(TAG, "Unable to find the next statistics marker after deleting one.");
      } else {
        nextWaypoint.getTripStatistics().merge(waypoint.getTripStatistics());
        nextWaypoint.setDescription(
            descriptionGenerator.generateWaypointDescription(nextWaypoint.getTripStatistics()));
        if (!updateWaypoint(nextWaypoint)) {
          Log.e(TAG, "Unable to update the next statistics marker after deleting one.");
        }
      }
    }
    contentResolver.delete(CourseWaypointsColumns.CONTENT_URI, CourseWaypointsColumns._ID + "=?",
        new String[] { Long.toString(waypointId) });
  }

  @Override
  public long getFirstWaypointId(long trackId) {
    if (trackId < 0) {
      return -1L;
    }
    Cursor cursor = null;
    try {
      cursor = getWaypointCursor(new String[] { CourseWaypointsColumns._ID },
          CourseWaypointsColumns.TRACKID + "=?", new String[] { Long.toString(trackId) },
          CourseWaypointsColumns._ID, 1);
      if (cursor != null && cursor.moveToFirst()) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(CourseWaypointsColumns._ID));
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return -1L;
  }

  @Override
  public Waypoint getLastStatisticsWaypoint(long trackId) {
    if (trackId < 0) {
      return null;
    }
    Cursor cursor = null;
    try {
      String selection = CourseWaypointsColumns.TRACKID + "=? AND " + CourseWaypointsColumns.TYPE + "="
          + Waypoint.TYPE_STATISTICS;
      String[] selectionArgs = new String[] { Long.toString(trackId) };
      cursor = getWaypointCursor(null, selection, selectionArgs, CourseWaypointsColumns._ID + " DESC", 1);
      if (cursor != null && cursor.moveToFirst()) {
        return createWaypoint(cursor);
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return null;
  }

  @Override
  public int getNextWaypointNumber(long trackId, boolean statistics) {
    if (trackId < 0) {
      return -1;
    }
    Cursor cursor = null;
    try {
      String[] projection = { CourseWaypointsColumns._ID };
      String selection = CourseWaypointsColumns.TRACKID + "=?  AND " + CourseWaypointsColumns.TYPE + "=?";
      int type = statistics ? Waypoint.TYPE_STATISTICS : Waypoint.TYPE_WAYPOINT;
      String[] selectionArgs = new String[] { Long.toString(trackId), Integer.toString(type) };
      cursor = getWaypointCursor(projection, selection, selectionArgs, CourseWaypointsColumns._ID, 0);
      if (cursor != null) {
        int count = cursor.getCount();
        /*
         * For statistics markers, the first marker is for the track statistics,
         * so return the count as the next user visible number.
         */
        return statistics ? count : count + 1;
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return -1;
  }

  @Override
  public Waypoint getWaypoint(long waypointId) {
    if (waypointId < 0) {
      return null;
    }
    Cursor cursor = null;
    try {
      cursor = getWaypointCursor(null, CourseWaypointsColumns._ID + "=?",
          new String[] { Long.toString(waypointId) }, CourseWaypointsColumns._ID, 0);
      if (cursor != null && cursor.moveToFirst()) {
        return createWaypoint(cursor);
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return null;
  }

  @Override
  public Cursor getWaypointCursor(
      String selection, String[] selectionArgs, String sortOrder, int maxWaypoints) {
    return getWaypointCursor(null, selection, selectionArgs, sortOrder, maxWaypoints);
  }

  @Override
  public Cursor getWaypointCursor(long trackId, long minWaypointId, int maxWaypoints) {
    if (trackId < 0) {
      return null;
    }

    String selection;
    String[] selectionArgs;
    if (minWaypointId >= 0) {
      selection = CourseWaypointsColumns.TRACKID + "=? AND " + CourseWaypointsColumns._ID + ">=?";
      selectionArgs = new String[] { Long.toString(trackId), Long.toString(minWaypointId) };
    } else {
      selection = CourseWaypointsColumns.TRACKID + "=?";
      selectionArgs = new String[] { Long.toString(trackId) };
    }
    return getWaypointCursor(null, selection, selectionArgs, CourseWaypointsColumns._ID, maxWaypoints);
  }

  @Override
  public Uri insertWaypoint(Waypoint waypoint) {
    waypoint.setId(-1L);
    return contentResolver.insert(CourseWaypointsColumns.CONTENT_URI, createContentValues(waypoint));
  }

  @Override
  public boolean updateWaypoint(Waypoint waypoint) {
    int rows = contentResolver.update(CourseWaypointsColumns.CONTENT_URI, createContentValues(waypoint),
        CourseWaypointsColumns._ID + "=?", new String[] { Long.toString(waypoint.getId()) });
    return rows == 1;
  }

  ContentValues createContentValues(Waypoint waypoint) {
    ContentValues values = new ContentValues();

    // Value < 0 indicates no id is available
    if (waypoint.getId() >= 0) {
      values.put(CourseWaypointsColumns._ID, waypoint.getId());
    }
    values.put(CourseWaypointsColumns.NAME, waypoint.getName());
    values.put(CourseWaypointsColumns.DESCRIPTION, waypoint.getDescription());
    values.put(CourseWaypointsColumns.CATEGORY, waypoint.getCategory());
    values.put(CourseWaypointsColumns.ICON, waypoint.getIcon());
    values.put(CourseWaypointsColumns.TRACKID, waypoint.getTrackId());
    values.put(CourseWaypointsColumns.TYPE, waypoint.getType());
    values.put(CourseWaypointsColumns.LENGTH, waypoint.getLength());
    values.put(CourseWaypointsColumns.DURATION, waypoint.getDuration());
    values.put(CourseWaypointsColumns.STARTID, waypoint.getStartId());
    values.put(CourseWaypointsColumns.STOPID, waypoint.getStopId());

    Location location = waypoint.getLocation();
    if (location != null) {
      values.put(CourseWaypointsColumns.LONGITUDE, (int) (location.getLongitude() * 1E6));
      values.put(CourseWaypointsColumns.LATITUDE, (int) (location.getLatitude() * 1E6));
      values.put(CourseWaypointsColumns.TIME, location.getTime());
      if (location.hasAltitude()) {
        values.put(CourseWaypointsColumns.ALTITUDE, location.getAltitude());
      }
      if (location.hasAccuracy()) {
        values.put(CourseWaypointsColumns.ACCURACY, location.getAccuracy());
      }
      if (location.hasSpeed()) {
        values.put(CourseWaypointsColumns.SPEED, location.getSpeed());
      }
      if (location.hasBearing()) {
        values.put(CourseWaypointsColumns.BEARING, location.getBearing());
      }
    }

    TripStatistics tripStatistics = waypoint.getTripStatistics();
    if (tripStatistics != null) {
      values.put(CourseWaypointsColumns.STARTTIME, tripStatistics.getStartTime());
      values.put(CourseWaypointsColumns.TOTALDISTANCE, tripStatistics.getTotalDistance());
      values.put(CourseWaypointsColumns.TOTALTIME, tripStatistics.getTotalTime());
      values.put(CourseWaypointsColumns.MOVINGTIME, tripStatistics.getMovingTime());
      values.put(CourseWaypointsColumns.AVGSPEED, tripStatistics.getAverageSpeed());
      values.put(CourseWaypointsColumns.AVGMOVINGSPEED, tripStatistics.getAverageMovingSpeed());
      values.put(CourseWaypointsColumns.MAXSPEED, tripStatistics.getMaxSpeed());
      values.put(CourseWaypointsColumns.MINELEVATION, tripStatistics.getMinElevation());
      values.put(CourseWaypointsColumns.MAXELEVATION, tripStatistics.getMaxElevation());
      values.put(CourseWaypointsColumns.ELEVATIONGAIN, tripStatistics.getTotalElevationGain());
      values.put(CourseWaypointsColumns.MINGRADE, tripStatistics.getMinGrade());
      values.put(CourseWaypointsColumns.MAXGRADE, tripStatistics.getMaxGrade());
    }
    return values;
  }

  private Waypoint getNextStatisticsWaypointAfter(Waypoint waypoint) {
    Cursor cursor = null;
    try {
      String selection = CourseWaypointsColumns._ID + ">?  AND " + CourseWaypointsColumns.TRACKID + "=? AND "
          + CourseWaypointsColumns.TYPE + "=" + Waypoint.TYPE_STATISTICS;
      String[] selectionArgs = new String[] {
          Long.toString(waypoint.getId()), Long.toString(waypoint.getTrackId()) };
      cursor = getWaypointCursor(null, selection, selectionArgs, CourseWaypointsColumns._ID, 1);
      if (cursor != null && cursor.moveToFirst()) {
        return createWaypoint(cursor);
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return null;
  }

  /**
   * Gets a waypoint cursor.
   * 
   * @param projection the projection
   * @param selection the selection
   * @param selectionArgs the selection args
   * @param sortOrder the sort order
   * @param maxWaypoints the maximum number of waypoints
   */
  private Cursor getWaypointCursor(String[] projection, String selection, String[] selectionArgs,
      String sortOrder, int maxWaypoints) {
    if (sortOrder == null) {
      sortOrder = CourseWaypointsColumns._ID;
    }
    if (maxWaypoints > 0) {
      sortOrder += " LIMIT " + maxWaypoints;
    }
    return contentResolver.query(
        CourseWaypointsColumns.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
  }

  @Override
  public int bulkInsertTrackPoint(Location[] locations, int length, long trackId) {
    if (length == -1) {
      length = locations.length;
    }
    ContentValues[] values = new ContentValues[length];
    for (int i = 0; i < length; i++) {
      values[i] = createContentValues(locations[i], trackId);
    }
    return contentResolver.bulkInsert(CourseTrackPointsColumns.CONTENT_URI, values);
  }

  @Override
  public Location createTrackPoint(Cursor cursor) {
    Location location = new MyTracksLocation("");
    fillTrackPoint(cursor, new CachedTrackPointsIndexes(cursor), location);
    return location;
  }

  @Override
  public long getFirstTrackPointId(long trackId) {
    if (trackId < 0) {
      return -1L;
    }
    Cursor cursor = null;
    try {
      String selection = CourseTrackPointsColumns._ID + "=(select min(" + CourseTrackPointsColumns._ID
          + ") from " + CourseTrackPointsColumns.TABLE_NAME + " WHERE " + CourseTrackPointsColumns.TRACKID
          + "=?)";
      String[] selectionArgs = new String[] { Long.toString(trackId) };
      cursor = getTrackPointCursor(new String[] { CourseTrackPointsColumns._ID }, selection,
          selectionArgs, CourseTrackPointsColumns._ID);
      if (cursor != null && cursor.moveToFirst()) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(CourseTrackPointsColumns._ID));
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return -1L;
  }

  @Override
  public long getLastTrackPointId(long trackId) {
    if (trackId < 0) {
      return -1L;
    }
    Cursor cursor = null;
    try {
      String selection = CourseTrackPointsColumns._ID + "=(select max(" + CourseTrackPointsColumns._ID
          + ") from " + CourseTrackPointsColumns.TABLE_NAME + " WHERE " + CourseTrackPointsColumns.TRACKID
          + "=?)";
      String[] selectionArgs = new String[] { Long.toString(trackId) };
      cursor = getTrackPointCursor(new String[] { CourseTrackPointsColumns._ID }, selection,
          selectionArgs, CourseTrackPointsColumns._ID);
      if (cursor != null && cursor.moveToFirst()) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(CourseTrackPointsColumns._ID));
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return -1L;
  }

  @Override
  public Location getLastValidTrackPoint(long trackId) {
    if (trackId < 0) {
      return null;
    }
    String selection = CourseTrackPointsColumns._ID + "=(select max(" + CourseTrackPointsColumns._ID + ") from "
        + CourseTrackPointsColumns.TABLE_NAME + " WHERE " + CourseTrackPointsColumns.TRACKID + "=? AND "
        + CourseTrackPointsColumns.LATITUDE + "<=" + MAX_LATITUDE + ")";
    String[] selectionArgs = new String[] { Long.toString(trackId) };
    return findTrackPointBy(selection, selectionArgs);
  }

  @Override
  public Location getLastValidTrackPoint() {
    String selection = CourseTrackPointsColumns._ID + "=(select max(" + CourseTrackPointsColumns._ID + ") from "
        + CourseTrackPointsColumns.TABLE_NAME + " WHERE " + CourseTrackPointsColumns.LATITUDE + "<="
        + MAX_LATITUDE + ")";
    return findTrackPointBy(selection, null);
  }

  @Override
  public Cursor getTrackPointCursor(
      long trackId, long startTrackPointId, int maxLocations, boolean descending) {
    if (trackId < 0) {
      return null;
    }

    String selection;
    String[] selectionArgs;
    if (startTrackPointId >= 0) {
      String comparison = descending ? "<=" : ">=";
      selection = CourseTrackPointsColumns.TRACKID + "=? AND " + CourseTrackPointsColumns._ID + comparison
          + "?";
      selectionArgs = new String[] { Long.toString(trackId), Long.toString(startTrackPointId) };
    } else {
      selection = CourseTrackPointsColumns.TRACKID + "=?";
      selectionArgs = new String[] { Long.toString(trackId) };
    }

    String sortOrder = CourseTrackPointsColumns._ID;
    if (descending) {
      sortOrder += " DESC";
    }
    if (maxLocations > 0) {
      sortOrder += " LIMIT " + maxLocations;
    }
    return getTrackPointCursor(null, selection, selectionArgs, sortOrder);
  }

  @Override
  public LocationIterator getTrackPointLocationIterator(final long trackId,
      final long startTrackPointId, final boolean descending,
      final LocationFactory locationFactory) {
    if (locationFactory == null) {
      throw new IllegalArgumentException("locationFactory is null");
    }
    return new LocationIterator() {
      private long lastTrackPointId = -1L;
      private Cursor cursor = getCursor(startTrackPointId);
      private final CachedTrackPointsIndexes
          indexes = cursor != null ? new CachedTrackPointsIndexes(cursor)
              : null;

      /**
       * Gets the track point cursor.
       * 
       * @param trackPointId the starting track point id
       */
      private Cursor getCursor(long trackPointId) {
        return getTrackPointCursor(trackId, trackPointId, defaultCursorBatchSize, descending);
      }

      /**
       * Advances the cursor to the next batch. Returns true if successful.
       */
      private boolean advanceCursorToNextBatch() {
        long trackPointId = lastTrackPointId == -1L ? -1L
            : lastTrackPointId + (descending ? -1 : 1);
        Log.d(TAG, "Advancing track point id: " + trackPointId);
        cursor.close();
        cursor = getCursor(trackPointId);
        return cursor != null;
      }

        @Override
      public long getLocationId() {
        return lastTrackPointId;
      }

        @Override
      public boolean hasNext() {
        if (cursor == null) {
          return false;
        }
        if (cursor.isAfterLast()) {
          return false;
        }
        if (cursor.isLast()) {
          if (cursor.getCount() != defaultCursorBatchSize) {
            return false;
          }
          return advanceCursorToNextBatch() && !cursor.isAfterLast();
        }
        return true;
      }

        @Override
      public Location next() {
        if (cursor == null) {
          throw new NoSuchElementException();
        }
        if (!cursor.moveToNext()) {
          if (!advanceCursorToNextBatch() || !cursor.moveToNext()) {
            throw new NoSuchElementException();
          }
        }
        lastTrackPointId = cursor.getLong(indexes.idIndex);
        Location location = locationFactory.createLocation();
        fillTrackPoint(cursor, indexes, location);
        return location;
      }

        @Override
      public void close() {
        if (cursor != null) {
          cursor.close();
          cursor = null;
        }
      }

        @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override
  public Uri insertTrackPoint(Location location, long trackId) {
    return contentResolver.insert(
        CourseTrackPointsColumns.CONTENT_URI, createContentValues(location, trackId));
  }

  /**
   * Creates the {@link ContentValues} for a {@link Location}.
   * 
   * @param location the location
   * @param trackId the track id
   */
  private ContentValues createContentValues(Location location, long trackId) {
    ContentValues values = new ContentValues();
    values.put(CourseTrackPointsColumns.TRACKID, trackId);
    values.put(CourseTrackPointsColumns.LONGITUDE, (int) (location.getLongitude() * 1E6));
    values.put(CourseTrackPointsColumns.LATITUDE, (int) (location.getLatitude() * 1E6));

    // Hack for Samsung phones that don't properly populate the time field
    long time = location.getTime();
    if (time == 0) {
      time = System.currentTimeMillis();
    }
    values.put(CourseTrackPointsColumns.TIME, time);
    if (location.hasAltitude()) {
      values.put(CourseTrackPointsColumns.ALTITUDE, location.getAltitude());
    }
    if (location.hasAccuracy()) {
      values.put(CourseTrackPointsColumns.ACCURACY, location.getAccuracy());
    }
    if (location.hasSpeed()) {
      values.put(CourseTrackPointsColumns.SPEED, location.getSpeed());
    }
    if (location.hasBearing()) {
      values.put(CourseTrackPointsColumns.BEARING, location.getBearing());
    }

    if (location instanceof MyTracksLocation) {
      MyTracksLocation myTracksLocation = (MyTracksLocation) location;
      if (myTracksLocation.getSensorDataSet() != null) {
        values.put(CourseTrackPointsColumns.SENSOR, myTracksLocation.getSensorDataSet().toByteArray());
      }
    }
    return values;
  }

  /**
   * Fills a track point from a cursor.
   * 
   * @param cursor the cursor pointing to a location.
   * @param indexes the cached track points indexes
   * @param location the track point
   */
  private void fillTrackPoint(Cursor cursor, CachedTrackPointsIndexes indexes, Location location) {
    location.reset();

    if (!cursor.isNull(indexes.longitudeIndex)) {
      location.setLongitude(((double) cursor.getInt(indexes.longitudeIndex)) / 1E6);
    }
    if (!cursor.isNull(indexes.latitudeIndex)) {
      location.setLatitude(((double) cursor.getInt(indexes.latitudeIndex)) / 1E6);
    }
    if (!cursor.isNull(indexes.timeIndex)) {
      location.setTime(cursor.getLong(indexes.timeIndex));
    }
    if (!cursor.isNull(indexes.altitudeIndex)) {
      location.setAltitude(cursor.getFloat(indexes.altitudeIndex));
    }
    if (!cursor.isNull(indexes.accuracyIndex)) {
      location.setAccuracy(cursor.getFloat(indexes.accuracyIndex));
    }
    if (!cursor.isNull(indexes.speedIndex)) {
      location.setSpeed(cursor.getFloat(indexes.speedIndex));
    }
    if (!cursor.isNull(indexes.bearingIndex)) {
      location.setBearing(cursor.getFloat(indexes.bearingIndex));
    }
    if (location instanceof MyTracksLocation && !cursor.isNull(indexes.sensorIndex)) {
      MyTracksLocation myTracksLocation = (MyTracksLocation) location;
      try {
        myTracksLocation.setSensorDataSet(
            SensorDataSet.parseFrom(cursor.getBlob(indexes.sensorIndex)));
      } catch (InvalidProtocolBufferException e) {
        Log.w(TAG, "Failed to parse sensor data.", e);
      }
    }
  }

  private Location findTrackPointBy(String selection, String[] selectionArgs) {
    Cursor cursor = null;
    try {
      cursor = getTrackPointCursor(null, selection, selectionArgs, CourseTrackPointsColumns._ID);
      if (cursor != null && cursor.moveToNext()) {
        return createTrackPoint(cursor);
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return null;
  }

  /**
   * Gets a track point cursor.
   * 
   * @param projection the projection
   * @param selection the selection
   * @param selectionArgs the selection arguments
   * @param sortOrder the sort order
   */
  private Cursor getTrackPointCursor(
      String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    return contentResolver.query(
        CourseTrackPointsColumns.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
  }

  /**
   * A cache of track points indexes.
   */
  private static class CachedTrackPointsIndexes {
    public final int idIndex;
    public final int longitudeIndex;
    public final int latitudeIndex;
    public final int timeIndex;
    public final int altitudeIndex;
    public final int accuracyIndex;
    public final int speedIndex;
    public final int bearingIndex;
    public final int sensorIndex;

    public CachedTrackPointsIndexes(Cursor cursor) {
      idIndex = cursor.getColumnIndex(CourseTrackPointsColumns._ID);
      longitudeIndex = cursor.getColumnIndexOrThrow(CourseTrackPointsColumns.LONGITUDE);
      latitudeIndex = cursor.getColumnIndexOrThrow(CourseTrackPointsColumns.LATITUDE);
      timeIndex = cursor.getColumnIndexOrThrow(CourseTrackPointsColumns.TIME);
      altitudeIndex = cursor.getColumnIndexOrThrow(CourseTrackPointsColumns.ALTITUDE);
      accuracyIndex = cursor.getColumnIndexOrThrow(CourseTrackPointsColumns.ACCURACY);
      speedIndex = cursor.getColumnIndexOrThrow(CourseTrackPointsColumns.SPEED);
      bearingIndex = cursor.getColumnIndexOrThrow(CourseTrackPointsColumns.BEARING);
      sensorIndex = cursor.getColumnIndexOrThrow(CourseTrackPointsColumns.SENSOR);
    }
  }

  /**
   * Sets the default cursor batch size. For testing purpose.
   * 
   * @param defaultCursorBatchSize the default cursor batch size
   */
  void setDefaultCursorBatchSize(int defaultCursorBatchSize) {
    this.defaultCursorBatchSize = defaultCursorBatchSize;
  }
  
  
  
  /**
   * A factory which can produce instances of {@link MyTracksProviderUtils}, and
   * can be overridden for testing.
   */
  public static class Factory extends MyTracksProviderUtils.Factory {

    /**
     * Creates an instance of {@link MyTracksProviderUtils}. Allows subclasses
     * to override for testing.
     * 
     * @param context the context
     */
    public MyTracksProviderUtils newForContext(Context context) {
      return new MyTracksCourseProviderUtils(context.getContentResolver());
    }
  }

  @Override
  public boolean shouldSetPreference(int keyId) {
    //TODO: add preferences associated with this provider course_id etc
    // in the current use case this doesn't matter.
    return false;
    
  }
}