package org.cowboycoders.cyclisimo.content;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Constants for the users table.
 * 
 * @author Will Szumski
 */
public interface UserInfoColumns extends BaseColumns {

  public static final String TABLE_NAME = "users";

  /**
   * Tracks provider uri.
   */
  public static final Uri CONTENT_URI = Uri.parse(
      "content://org.cowboycoders.cyclisimo/users");

  /**
   * Track content type.
   */
  public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.cowboycoders.user";

  /**
   * Track id content type.
   */
  public static final String CONTENT_ITEMTYPE = "vnd.android.cursor.item/vnd.cowboycoders.user";

  /**
   * Tracks table default sort order.
   */
  public static final String DEFAULT_SORT_ORDER = "_id";

  // Columns
  public static final String NAME = "name"; // user name
  public static final String WEIGHT = "weight"; // weight in kilos
  public static final String CURRENT_BIKE = "current_bike"; // currently selected bike
  public static final String SETTINGS = "settings"; // currently selected bike
  
  
  
  public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" 
      + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
      + NAME + " STRING, " 
      + WEIGHT + " FLOAT, "
      + CURRENT_BIKE + " INTEGER, "
      + SETTINGS + " BLOB"
      + ");";

  public static final String[] COLUMNS = {
      _ID,
      NAME,
      WEIGHT,
      CURRENT_BIKE,
      SETTINGS,
  };

  public static final byte[] COLUMN_TYPES = {
      ContentTypeIds.LONG_TYPE_ID, // id
      ContentTypeIds.STRING_TYPE_ID, // name
      ContentTypeIds.FLOAT_TYPE_ID, // weight
      ContentTypeIds.LONG_TYPE_ID, // currently selected bike
      ContentTypeIds.BLOB_TYPE_ID, // user settings
    };
}
