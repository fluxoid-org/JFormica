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
package org.cowboycoders.ant.temp;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Formatter;
import java.util.logging.Logger;

import org.cowboycoders.ant.AntLogger;
import org.cowboycoders.pid.PidParameterController;
import org.cowboycoders.pid.PidUpdateListener;
import org.cowboycoders.utils.Conversions;



public class SimpleSpeedLogger {

  private static final String DIRECTORY_NAME = "logs";
  private static final String FILE_NAME = "speedlog"; 
  private static final Logger LOGGER = Logger.getLogger(SimpleSpeedLogger.class.getSimpleName());
  private static final String HEADINGS = "time/seconds;speed";
  
  private Long timeOffset;
  
  private File directory;
  private File file;
  private boolean setupOk = false;
private boolean writeHeadings;
  
  public SimpleSpeedLogger() {
    directory = new File(DIRECTORY_NAME);
    if (!directory.exists()) {
      directory.mkdirs();
    }
    file = new File(directory, FILE_NAME);
    file.delete();
    //setupOk = true;
    //write(HEADINGS);
  }

  
  /**
   * Creates a new output stream to write to the given filename.
   * @throws IOException 
   */
  protected PrintWriter newPrintWriter()
      throws IOException {
    file = new File(directory, FILE_NAME);
    return new PrintWriter(new FileWriter(file,true));
  }

public synchronized void onSpeedUpdate(double speed) {
    if (!setupOk) {
    	LOGGER.warning("newLog not called");
        return;
      }
    
      if(writeHeadings) {
    	  writeHeadings = false;
    	  writeHeadings();
      }
    
    
      if (timeOffset == null) {
    	  timeOffset = System.nanoTime();
      }
      
      double currentTimeStamp = (System.nanoTime() -timeOffset) / Math.pow(10, 9);
    
      StringBuilder outputText = new StringBuilder();
      outputText.append(currentTimeStamp);
      outputText.append(";");
      outputText.append(speed);
      outputText.append("\n");
      
      write(outputText);
	
}

public synchronized void newLog() {
	//we need to write headings on next update as getters could refer to stale values
	writeHeadings = true;
    setupOk = true;
}

private void writeHeadings() {
    StringBuilder outputText = new StringBuilder();
    outputText.append("\n");
    outputText.append(HEADINGS);
    outputText.append("\n");
    write(outputText);
}

private void write(CharSequence string) {
    PrintWriter writer = null;
    try {
      writer = newPrintWriter();
      writer.append(string);
      writer.flush();
    } catch (FileNotFoundException e) {
    	e.printStackTrace();
    } catch (IOException e) {
    	e.printStackTrace();
    } finally {
      if (writer!= null) {
        writer.close();
      }
    }
}

}