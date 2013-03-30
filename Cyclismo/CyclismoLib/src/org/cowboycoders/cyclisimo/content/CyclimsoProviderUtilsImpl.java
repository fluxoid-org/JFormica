package org.cowboycoders.cyclisimo.content;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class CyclimsoProviderUtilsImpl extends MyTracksProviderUtilsImpl implements CyclismoProviderUtils {

  public CyclimsoProviderUtilsImpl(ContentResolver contentResolver) {
    super(contentResolver);
  }

  @Override
  public User createUser(Cursor cursor, boolean allFieldsMustBePresent) {
  
    int idIndex = cursor.getColumnIndexOrThrow(UserInfoColumns._ID);
    int nameIndex;
    int weightIndex; 
    int currentBikeIndex;
    int settingsIndex;
    if (allFieldsMustBePresent) {
      nameIndex = cursor.getColumnIndexOrThrow(UserInfoColumns.NAME);
      weightIndex = cursor.getColumnIndexOrThrow(UserInfoColumns.WEIGHT);
      currentBikeIndex = cursor.getColumnIndexOrThrow(UserInfoColumns.CURRENT_BIKE);
      settingsIndex = cursor.getColumnIndexOrThrow(UserInfoColumns.SETTINGS);
    } else {
      nameIndex = cursor.getColumnIndex(UserInfoColumns.NAME);
      weightIndex = cursor.getColumnIndex(UserInfoColumns.WEIGHT);
      currentBikeIndex = cursor.getColumnIndex(UserInfoColumns.CURRENT_BIKE);
      settingsIndex = cursor.getColumnIndex(UserInfoColumns.SETTINGS);
    }
    
    User user = new User();
    if (!cursor.isNull(idIndex)) {
      user.setId(cursor.getLong(idIndex));
    }
    if (nameIndex > 0 && !cursor.isNull(nameIndex)) {
      user.setName(cursor.getString(nameIndex));
    }
    if (weightIndex > 0 && !cursor.isNull(weightIndex)) {
      user.setWeight(cursor.getFloat(weightIndex));
    }
    if (currentBikeIndex > 0 && !cursor.isNull(currentBikeIndex)) {
      user.setCurrentlySelectedBike(cursor.getLong(currentBikeIndex));
    }
    if (settingsIndex > 0 && !cursor.isNull(settingsIndex)) {
      user.setSettings(cursor.getBlob(settingsIndex));
    }

    return user;
  }

  @Override
  public void deleteAllUsers() {
    Cursor cursor = null;
    List<User> users = null;
    try {
      String [] projection = new String[] {UserInfoColumns._ID};
      cursor = getUserCursor(projection, null, null, UserInfoColumns._ID);
      if (cursor != null && cursor.moveToNext()) {
        users = getAllUsers(cursor,false);
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    
    if (users != null) {
      for (User user : users) {
        deleteUser(user.getId());
      }
    }
  }
  


  private void cleanupUserDelete(long userId) {
    Cursor cursor = null;
    List<Track> tracks = null;
    try {
      String selection = TracksColumns.OWNER + "=?";
      String [] projection = new String [] {TracksColumns._ID};
      String [] args = new String[] {Long.toString(userId)};
      cursor = getTrackCursor(projection, selection, args, TracksColumns._ID);
      if (cursor != null && cursor.moveToNext()) {
        tracks = getAllTracks(cursor,false);
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    
    if (tracks != null) {
      for (Track track : tracks) {
        deleteTrack(track.getId());
      }
    }
    
    List<Bike> bikes = null;
    try {
      String selection = BikeInfoColumns.OWNER  + "=?";
      String [] projection = new String [] {BikeInfoColumns._ID};
      String [] args = new String[] {Long.toString(userId)};
      cursor = getBikeCursor(projection, selection, args, BikeInfoColumns._ID);
      if (cursor != null && cursor.moveToNext()) {
        bikes = getAllBikes(cursor,false);
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    
    if (bikes != null) {
      for (Bike bike : bikes) {
        deleteBike(bike.getId());
      }
    }

 
  }

  @Override
  public void deleteUser(long userId) {
    contentResolver.delete(UserInfoColumns.CONTENT_URI, UserInfoColumns._ID + "=?",
        new String[] { Long.toString(userId) });
    cleanupUserDelete(userId);
    
  }
  
  private List<User> getAllUsers(Cursor cursor, boolean allFieldsMustBePresent) {
    ArrayList<User> users = new ArrayList<User>();
    if (cursor != null) {
      try {
        users.ensureCapacity(cursor.getCount());
        if (cursor.moveToFirst()) {
          do {
            users.add(createUser(cursor, allFieldsMustBePresent));
          } while (cursor.moveToNext());
        }
      } finally {
        cursor.close();
      }
    }
    return users;
  }

  @Override
  public List<User> getAllUsers() {
    Cursor cursor = getUserCursor(null, null, null, UserInfoColumns._ID);
    return getAllUsers(cursor, true);
  }

  @Override
  public User getUser(long userId) {
    if (userId < 0) {
      return null;
    }
    Cursor cursor = null;
    try {
      cursor = getUserCursor(null, UserInfoColumns._ID + "=?",
          new String[] { Long.toString(userId) }, UserInfoColumns._ID);
      if (cursor != null && cursor.moveToNext()) {
        return createUser(cursor,true);
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return null;
  }

  @Override
  public Cursor getUserCursor(String selection, String[] selectionArgs, String sortOrder) {
    return getUserCursor(null, selection, selectionArgs, sortOrder);
  }
  
  private Cursor getUserCursor(
      String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    return contentResolver.query(
        UserInfoColumns.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
  }
  
  private ContentValues createContentValues(User user) {
    ContentValues values = new ContentValues();

    // Value < 0 indicates no id is available
    if (user.getId() >= 0) {
      values.put(UserInfoColumns._ID, user.getId());
    }
    values.put(UserInfoColumns.NAME, user.getName());
    values.put(UserInfoColumns.WEIGHT, user.getWeight());
    values.put(UserInfoColumns.CURRENT_BIKE, user.getCurrentlySelectedBike());
    values.put(UserInfoColumns.SETTINGS,user.getSettings());
    
    return values;
  }

  @Override
  public Uri insertUser(User user) {
    validateUser(user);
    return contentResolver.insert(UserInfoColumns.CONTENT_URI, createContentValues(user));
  }
  
  protected void validateUser(User user) {
    if (user.getName() == null) {
      throw new IllegalArgumentException("user must have a name");
    }
    if (user.getWeight() == -1l) {
      throw new IllegalArgumentException("user must have weight set");
    }
  }

  @Override
  public void updateUser(User user) {
    validateUser(user);
    contentResolver.update(UserInfoColumns.CONTENT_URI, createContentValues(user),
        UserInfoColumns._ID + "=?", new String[] { Long.toString(user.getId()) });
  }

  @Override
  public Bike createBike(Cursor cursor, boolean allFieldsMustBePresent) {
    int idIndex = cursor.getColumnIndexOrThrow(BikeInfoColumns._ID);
    int nameIndex;
    int weightIndex; 
    int sharedIndex;
    int ownerIndex;
    if (allFieldsMustBePresent) {
      nameIndex = cursor.getColumnIndexOrThrow(BikeInfoColumns.NAME);
      weightIndex = cursor.getColumnIndexOrThrow(BikeInfoColumns.WEIGHT);
      sharedIndex = cursor.getColumnIndexOrThrow(BikeInfoColumns.SHARED);
      ownerIndex = cursor.getColumnIndexOrThrow(BikeInfoColumns.OWNER);
    } else {
      nameIndex = cursor.getColumnIndex(BikeInfoColumns.NAME);
      weightIndex = cursor.getColumnIndex(BikeInfoColumns.WEIGHT);
      sharedIndex = cursor.getColumnIndex(BikeInfoColumns.SHARED);
      ownerIndex = cursor.getColumnIndex(BikeInfoColumns.OWNER);
    }
    
    Bike bike = new Bike();
    if (!cursor.isNull(idIndex)) {
      bike.setId(cursor.getLong(idIndex));
    }
    if (nameIndex > 0 && !cursor.isNull(nameIndex)) {
      bike.setName(cursor.getString(nameIndex));
    }
    if (weightIndex > 0 && !cursor.isNull(weightIndex)) {
      bike.setWeight(cursor.getFloat(weightIndex));
    }
    
    if (sharedIndex > 0 && !cursor.isNull(sharedIndex)) {
      bike.setShared(cursor.getInt(sharedIndex));
    }
    if (ownerIndex > 0 && !cursor.isNull(ownerIndex)) {
      bike.setOwnerId(cursor.getLong(ownerIndex));
    }

    return bike;
  }

  @Override
  public void deleteAllBikes() {
    contentResolver.delete(BikeInfoColumns.CONTENT_URI, null, null);
  }
  
  @Override
  public void deleteAllBikes(User user) {
    if (user == null) {
      throw new NullPointerException("user cannot be null");
    }
    String selection = BikeInfoColumns.OWNER  + "=?";
    String [] args = new String[] {Long.toString(user.getId())};
    contentResolver.delete(BikeInfoColumns.CONTENT_URI, selection, args);
  }

  @Override
  public void deleteBike(long bikeId) {
    contentResolver.delete(BikeInfoColumns.CONTENT_URI, BikeInfoColumns._ID + "=?",
        new String[] { Long.toString(bikeId) });
    
  }

  @Override
  public List<Bike> getAllBikes() {
    Cursor cursor = getBikeCursor(null, null, null, BikeInfoColumns._ID);
    return getAllBikes(cursor,true);
  }
  
  @Override
  public List<Bike> getAllSharedBikes() {
    String selection = BikeInfoColumns.SHARED  + "=?";
    // 1 true, 0 false
    String [] args = new String[] {Integer.toString(1)};
    Cursor cursor = getBikeCursor(null, selection, args, BikeInfoColumns._ID);
    return getAllBikes(cursor,true);
  }
  
  
  @Override
  public List<Bike> getAllBikes(User user) {
    String selection = BikeInfoColumns.OWNER  + "=?";
    String [] args = new String[] {Long.toString(user.getId())};
    Cursor cursor = getBikeCursor(null, selection, args, BikeInfoColumns._ID);
    return getAllBikes(cursor,true);
  }
  
  
  
  private List<Bike> getAllBikes(Cursor cursor, boolean allFieldsMustBePresent) {
    ArrayList<Bike> bikes = new ArrayList<Bike>();
    if (cursor != null) {
      try {
        bikes.ensureCapacity(cursor.getCount());
        if (cursor.moveToFirst()) {
          do {
            bikes.add(createBike(cursor, allFieldsMustBePresent));
          } while (cursor.moveToNext());
        }
      } finally {
        cursor.close();
      }
    }
    return bikes;
  }
  
  private Cursor getBikeCursor(
      String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    return contentResolver.query(
        BikeInfoColumns.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
  }


  @Override
  public Bike getBike(long bikeId) {
    if (bikeId < 0) {
      return null;
    }
    Cursor cursor = null;
    try {
      cursor = getBikeCursor(null, BikeInfoColumns._ID + "=?",
          new String[] { Long.toString(bikeId) }, BikeInfoColumns._ID);
      if (cursor != null && cursor.moveToNext()) {
        return createBike(cursor,true);
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return null;
  }
  
  private boolean validateBike(Bike bike) {
    // if unowned must be shared
    if (bike.getOwnerId() == -1L) {
       return bike.isShared();
    }
    return true;
  }

  @Override
  public Cursor getBikeCursor(String selection, String[] selectionArgs, String sortOrder) {
    return getBikeCursor(null,selection, selectionArgs, sortOrder);
  }

  @Override
  public Uri insertBike(Bike bike) {
    if (!validateBike(bike)) throw new IllegalArgumentException("Bike did not pass validation");
    return contentResolver.insert(BikeInfoColumns.CONTENT_URI, createContentValues(bike));
  }

  private ContentValues createContentValues(Bike bike) {
    ContentValues values = new ContentValues();

    // Value < 0 indicates no id is available
    if (bike.getId() >= 0) {
      values.put(BikeInfoColumns._ID, bike.getId());
    }
    values.put(BikeInfoColumns.NAME, bike.getName());
    values.put(BikeInfoColumns.WEIGHT, bike.getWeight());
    values.put(BikeInfoColumns.SHARED, bike.isShared() ? 1 : 0);
    values.put(BikeInfoColumns.OWNER, bike.getOwnerId());
    
    return values;
  }

  @Override
  public void updateBike(Bike bike) {
    if (!validateBike(bike)) throw new IllegalArgumentException("Bike did not pass validation");
    contentResolver.update(BikeInfoColumns.CONTENT_URI, createContentValues(bike),
        BikeInfoColumns._ID + "=?", new String[] { Long.toString(bike.getId()) });
  }
  

  @Override
  protected void validateTrack(Track track) {
    super.validateTrack(track);
    // if user doesn't exist
    if (getUser(track.getOwner()) == null) {
      throw new IllegalArgumentException("User doesn't exist");
    }
  }

  @Override
  public List<Track> getAllTracks(User user) {
    String selection = TracksColumns.OWNER  + "=?";
    String [] args = new String[] {Long.toString(user.getId())};
    Cursor cursor = getTrackCursor(null, selection, args, TracksColumns._ID);
    return getAllTracks(cursor,true);
  }

  @Override
  public void deleteAllBikes(long userId) {
    User user = new User();
    user.setId(userId);
    deleteAllBikes(user);
  }


}
