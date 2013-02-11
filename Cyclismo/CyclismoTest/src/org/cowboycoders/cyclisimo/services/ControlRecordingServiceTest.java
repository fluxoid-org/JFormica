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
package org.cowboycoders.cyclisimo.services;

import org.cowboycoders.cyclisimo.services.ITrackRecordingService;
import org.cowboycoders.cyclisimo.R;
import com.google.android.testing.mocking.UsesMocks;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.test.ServiceTestCase;

import org.cowboycoders.cyclisimo.services.ControlRecordingService;
import org.easymock.EasyMock;

/**
 * Tests {@link ControlRecordingService}.
 * 
 * @author Youtao Liu
 */
public class ControlRecordingServiceTest extends ServiceTestCase<ControlRecordingService> {

  private Context context;
  private ControlRecordingService controlRecordingService;

  public ControlRecordingServiceTest() {
    super(ControlRecordingService.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    context = getContext();
  }

  /**
   * Tests the start of {@link ControlRecordingService} and tests the method
   * {@link ControlRecordingService#onHandleIntent(Intent, ITrackRecordingService)}
   * to start a track recording.
   */
  @UsesMocks(ITrackRecordingService.class)
  public void testStartRecording() {
    assertNull(controlRecordingService);
    Intent intent = startControlRecordingService(context.getString(R.string.track_action_start));
    assertNotNull(controlRecordingService);

    ITrackRecordingService iTrackRecordingServiceMock = EasyMock
        .createStrictMock(ITrackRecordingService.class);
    try {
      EasyMock.expect(iTrackRecordingServiceMock.startNewTrack()).andReturn(1L);
      EasyMock.replay(iTrackRecordingServiceMock);
      controlRecordingService.onHandleIntent(intent, iTrackRecordingServiceMock);
      EasyMock.verify(iTrackRecordingServiceMock);
    } catch (RemoteException e) {
      fail();
    }
  }

  /**
   * Tests the method
   * {@link ControlRecordingService#onHandleIntent(Intent, ITrackRecordingService)}
   * to stop a track recording.
   */
  @UsesMocks(ITrackRecordingService.class)
  public void testStopRecording() {
    Intent intent = startControlRecordingService(context.getString(R.string.track_action_end));
    
    ITrackRecordingService iTrackRecordingServiceMock = EasyMock
        .createStrictMock(ITrackRecordingService.class);
    try {
      iTrackRecordingServiceMock.endCurrentTrack();
      EasyMock.replay(iTrackRecordingServiceMock);
      controlRecordingService.onHandleIntent(intent, iTrackRecordingServiceMock);
      EasyMock.verify(iTrackRecordingServiceMock);
    } catch (RemoteException e) {
      fail();
    }
  }
  
  /**
   * Starts a ControlRecordingService with a specified action.
   * 
   * @param action the action string in the start intent
   */
  private Intent startControlRecordingService(String action) {
    Intent intent = new Intent(context, ControlRecordingService.class);
    intent.setAction(action);
    startService(intent);
    controlRecordingService = getService();
    return intent;
  }

}
