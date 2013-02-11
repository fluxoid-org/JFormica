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
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Formatter;

import org.cowboycoders.ant.AntLogger;
import org.cowboycoders.cyclisimo.R;
import org.cowboycoders.cyclisimo.util.FileUtils;


public class AntLoggerImpl extends AntLogger {

  private static final String DIRECTORY_NAME = "logs";
  public static final String TAG = AntLoggerImpl.class.getSimpleName();
  private String fileName;  
  private File directory;
  private File file;
  private boolean setupOk = false;
  
  public AntLoggerImpl(Context context) {
    fileName = context.getString(R.string.settings_bushido_antlog_filename);
    if(!canWriteFile()) {
      return;
    }
    file = new File(directory, fileName);
    file.delete();
    setupOk = true;
  }

  @Override
  public void log(LogDataContainer data) {
    if (!setupOk) {
      Log.w(TAG,"Unable to write to log file : setup of Logger failed");
      return;
    }
    StringBuilder outputText = new StringBuilder();
    outputText.append(data.getTimeStamp());
    outputText.append(";");
    outputText.append(data.getDirection());
    outputText.append(";");
    outputText.append(data.getMessageClass().getSimpleName());
    outputText.append(";");
    
    Formatter formatter = new Formatter(outputText);
    byte [] packet = data.getPacket();
    for (int i = 0 ; i< packet.length ; i++) {
      if (i == 0) {
        formatter.format("%02x", packet[i]);
        continue;
      } 
      formatter.format(":%02x", packet[i]);
    }
    
    outputText.append("\n");
    
    PrintWriter writer = null;
    try {
      writer = newPrintWriter();
      writer.append(outputText);
      writer.flush();
    } catch (FileNotFoundException e) {
      Log.i(TAG, "Unable to log: file not found");
    } catch (IOException e) {
      Log.i(TAG, "Unable to log: IOException");
    } finally {
      if (writer!= null) {
        writer.close();
      }
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
          FileUtils.buildExternalDirectoryPath(AntLoggerImpl.DIRECTORY_NAME);
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
  protected PrintWriter newPrintWriter()
      throws IOException {
    file = new File(directory, fileName);
    return new PrintWriter(new FileWriter(file,true));
  }

}
