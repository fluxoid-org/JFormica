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
 * Copyright 2012 Google Inc.
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
package org.cowboycoders.cyclisimo.endtoendtest;


import android.os.Bundle;
import android.test.InstrumentationTestRunner;
import android.util.Log;

import org.cowboycoders.cyclisimo.endtoendtest.others.BigTestUtils;

/**
 * A test runner can be used to run end-to-end test.
 * 
 * @author Youtao Liu
 */
public class EndToEndTestRunner extends InstrumentationTestRunner {
  
  /**
   * Gets the parameter port which is used to fix GPS signal to emulators.
   */
  @Override
  public void onCreate(Bundle arguments) {
    String portNumber = arguments.getString("port");
    if (portNumber != null) {
      try {
        EndToEndTestUtils.emulatorPort = Integer.parseInt(portNumber);
      } catch (Exception e) {
        Log.e(EndToEndTestUtils.LOG_TAG, "Unable to get port parameter, use default value."
            + EndToEndTestUtils.emulatorPort, e);
      }
    }

    String isStressTest = arguments.getString("stress");
    if (isStressTest != null && isStressTest.equalsIgnoreCase("true")) {
      BigTestUtils.runStressTest = true;
    } else {
      BigTestUtils.runStressTest = false;
    }

    String isSensorTest = arguments.getString("sensor");
    if (isSensorTest != null && isSensorTest.equalsIgnoreCase("true")) {
      BigTestUtils.runSensorTest = true;
    } else {
      BigTestUtils.runSensorTest = false;
    }

    Log.d(EndToEndTestUtils.LOG_TAG, "Use port number when run test on emulator:"
        + EndToEndTestUtils.emulatorPort);
    Log.i(EndToEndTestUtils.LOG_TAG, "Run stress test:" + BigTestUtils.runStressTest);
    Log.i(EndToEndTestUtils.LOG_TAG, "Run sensor test:" + BigTestUtils.runSensorTest);

    super.onCreate(arguments);
  }

}
