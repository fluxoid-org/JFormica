package org.cowboycoders.cyclisimo.content;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Constants for the users table.
 * 
 * @author Will Szumski
 */
public interface BikeInfoColumns extends BaseColumns {

  public static final String TABLE_NAME = "bikes";

  /**
   * Tracks provider uri.
   */
  public static final Uri CONTENT_URI = Uri.parse(
      "content://org.cowboycoders.cyclisimo/bikes");

  /**
   * Track content type.
   */
  public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.cowboycoders.bike";

  /**
   * Track id content type.
   */
  public static final String CONTENT_ITEMTYPE = "vnd.android.cursor.item/vnd.cowboycoders.bike";

  /**
   * Tracks table default sort order.
   */
  public static final String DEFAULT_SORT_ORDER = "_id";

  // Columns
  public static final String NAME = "name"; // bike name
  public static final String WEIGHT = "weight"; // weight in kilos
  public static final String SHARED = "shared"; // is the bike shared?
  public static final String OWNER = "owner"; // user_id of owner
  
  
  public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" 
      + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
      + NAME + " STRING, " 
      + SHARED + " INTEGER, "
      + OWNER + " INTEGER REFERENCES " + UserInfoColumns.TABLE_NAME + " ON DELETE CASCADE ON UPDATE CASCADE" + ", "
      + WEIGHT + " FLOAT"
      //+ "FOREIGN KEY(" + OWNER +") REFERENCES " + UserInfoColumns.TABLE_NAME +"(" + UserInfoColumns._ID +") "
      + ");";
  
//  public static final String CREATE_TRIGGER = "CREATE TRIGGER on_delete_user_update_bikes "
//      + "AFTER DELETE ON " + UserInfoColumns.TABLE_NAME + " "
//      + "BEGIN "
//      +     "UPDATE " + TABLE_NAME + " SET " + SHARED + " = TRUE  WHERE " + OWNER + " = old."+ UserInfoColumns._ID  + "; "
//      +     "UPDATE " + TABLE_NAME + " SET " + OWNER + " = NULL WHERE " + OWNER + " = old."+ UserInfoColumns._ID  + "; "
//      + "END;";

      
  public static final String[] COLUMNS = {
      _ID,
      NAME,
      SHARED,
      OWNER,
      WEIGHT,
  };

  public static final byte[] COLUMN_TYPES = {
      ContentTypeIds.LONG_TYPE_ID, // id
      ContentTypeIds.STRING_TYPE_ID, // name
      ContentTypeIds.BOOLEAN_TYPE_ID, // is shared?
      ContentTypeIds.LONG_TYPE_ID, // user_id of owner
      ContentTypeIds.FLOAT_TYPE_ID, // weight
    };
}
