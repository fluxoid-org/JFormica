package org.cowboycoders.cyclisimo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;

import org.cowboycoders.cyclisimo.Constants;
import org.cowboycoders.cyclisimo.R;
import org.cowboycoders.cyclisimo.content.CyclismoProviderUtils;
import org.cowboycoders.cyclisimo.content.MyTracksProviderUtils;
import org.cowboycoders.cyclisimo.content.User;
import org.cowboycoders.cyclisimo.io.backup.PreferenceBackupHelper;

public class SettingsUtils {
  
  public static final String TAG = "settingsUtils";
  
  private SettingsUtils() {
    
  }
  
  public static void restoreSettings(Context context, User user) {
    
    SharedPreferences preferences = context.getSharedPreferences(
        Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
    
    // clear preferences
    preferences.edit().clear().apply();
    
    
    byte [] settings;
    if ((settings = user.getSettings()) == null) {
      Log.e(TAG,"settings are null : ignorning");
      preferences.edit().putLong(context.getString(R.string.settings_select_user_current_selection_key), user.getId()).apply();
      return;
    }
    
    
    PreferenceBackupHelper importer = createPreferenceBackupHelper(context);
    try {
      importer.importPreferences(settings, preferences);
    } catch (IOException e) {
      Log.e(TAG,"error restoring preferences", e);
    }
    
    
    preferences.edit().putLong(context.getString(R.string.settings_select_user_current_selection_key), user.getId()).apply();
    
  }
  
  public static byte [] dumpPreferences(Context context) throws IOException {
    SharedPreferences preferences = context.getSharedPreferences(
        Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
    PreferenceBackupHelper preferenceDumper = createPreferenceBackupHelper(context);
    byte[] dumpedContents = preferenceDumper.exportPreferences(preferences);
    return dumpedContents;
  }
  
  protected static PreferenceBackupHelper createPreferenceBackupHelper(Context context) {
    return new PreferenceBackupHelper(context);
  }
  
  public static void saveSettings(Context context, User user) {
    
    byte[] dump;
    try {
      dump = dumpPreferences(context);
    } catch (IOException e) {
      Log.e(TAG, "IOException dumping preferences");
      return;
    }
    
    user.setSettings(dump);
    
    CyclismoProviderUtils providerUtils = MyTracksProviderUtils.Factory.getCyclimso(context);
    
    providerUtils.updateUser(user);
    
  }
  
  
  
}
