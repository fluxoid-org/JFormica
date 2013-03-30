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
package org.cowboycoders.cyclisimo.turbo;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.cowboycoders.cyclisimo.Constants;
import org.cowboycoders.cyclisimo.io.backup.PreferenceBackupHelper;
import org.cowboycoders.cyclisimo.util.FileUtils;

/**
 * For testing size of sahred prefs : should they be stored as blob in database?
 * @author will
 *
 */
public class SharedPrefTest{

  private static final String DIRECTORY_NAME = "logs";
  public static final String TAG = SharedPrefTest.class.getSimpleName();
  private String fileName;  
  private File directory;
  private File file;
  private boolean setupOk = false;
  private Context context;
  
  public SharedPrefTest(Context context) {
    this.context = context;
    fileName = "sharedPrefs";
    if(!canWriteFile()) {
      return;
    }
    file = new File(directory, fileName);
    setupOk = true;
  }
  
  private byte [] dumpPreferences(SharedPreferences preferences) throws IOException {
    PreferenceBackupHelper preferenceDumper = createPreferenceBackupHelper();
    byte[] dumpedContents = preferenceDumper.exportPreferences(preferences);
    return dumpedContents;
  }
  
  protected PreferenceBackupHelper createPreferenceBackupHelper() {
    return new PreferenceBackupHelper(context);
  }

  public void write() {
    if (!setupOk) {
      Log.w(TAG,"Unable to write to log file : setup of Logger failed");
      return;
    }
    
    SharedPreferences preferences = context.getSharedPreferences(
        Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
    
    byte[] dump;
    try {
      dump = dumpPreferences(preferences);
    } catch (IOException e) {
      Log.e(TAG, "IOException dumping preferences");
      return;
    }
    
    FileOutputStream stream = null;
    
    try {
      stream = newOutputStream();
      stream.write(dump);
      stream.flush();
    } catch (FileNotFoundException e) {
      Log.i(TAG, "Unable to log: file not found");
    } catch (IOException e) {
      Log.i(TAG, "Unable to log: IOException");
    } finally {
      if (stream!= null) {
        closeOutputStream(stream);
      }
    }
    


  }
  
  public void restore() {
    if (!setupOk || !file.exists()) {
      Log.w(TAG,"Unable to read from log file : setup of Logger failed");
      return;
    }
    
    SharedPreferences preferences = context.getSharedPreferences(
        Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
    
    
    FileInputStream stream = null;
    byte [] dump = new byte[(int) file.length()];
    try {
      stream = newInputStream();
      int result = stream.read(dump);
      if (result != file.length()) {
        Log.e(TAG,"Length mismatch : cancelling restore");
        return;
      }
    } catch (FileNotFoundException e) {
      Log.i(TAG, "Unable to log: file not found");
    } catch (IOException e) {
      Log.i(TAG, "Unable to log: IOException");
    } finally {
      if (stream!= null) {
        closeInputStream(stream);
      }
    }
    
    PreferenceBackupHelper importer = createPreferenceBackupHelper();
    try {
      importer.importPreferences(dump, preferences);
    } catch (IOException e) {
      Log.e(TAG,"error restoring preferences", e);
    }
    
  }
  
  private void closeOutputStream(OutputStream os) {
    try {
      os.close();
    } catch (IOException e) {
      Log.e(TAG, "IOEXception closing stream",e);
    }
  }
  
  private void closeInputStream(InputStream is) {
    try {
      is.close();
    } catch (IOException e) {
      Log.e(TAG, "IOEXception closing stream",e);
    }
  }
  
  
  /**
   * Checks and returns whether we're ready to create the output file.
   */
  protected boolean canWriteFile() {
    if (!FileUtils.isSdCardAvailable()) {
      Log.i(TAG, "Could not find SD card.");
      return false;
    }
    
    if (directory == null) {
      String dirName =
          FileUtils.buildExternalDirectoryPath(SharedPrefTest.DIRECTORY_NAME);
      directory = newFile(dirName);
    }
    if (!FileUtils.ensureDirectoryExists(directory)) {
      Log.i(TAG, "Could not create export directory.");
      return false;
    }
    return true;
  }
  
  /**
   * Creates a new file object for the given path.
   */
  protected File newFile(String path) {
    return new File(path);
  }

  /**
   * Creates a new output stream to write to the given filename.
   * @throws IOException 
   */
  protected FileOutputStream newOutputStream()
      throws IOException {
    file = new File(directory, fileName);
    // do not append
    FileOutputStream stream = new FileOutputStream(file,false);
    return stream;
  }
  
  protected FileInputStream newInputStream()
      throws IOException {
    file = new File(directory, fileName);
    FileInputStream stream = new FileInputStream(file);
    return stream;
  }


}
