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
package org.cowboycoders.cyclisimo.io.maps;


import android.test.AndroidTestCase;

import org.cowboycoders.cyclisimo.io.docs.SendDocsActivity;
import org.cowboycoders.cyclisimo.io.fusiontables.SendFusionTablesActivity;
import org.cowboycoders.cyclisimo.io.maps.SendMapsActivity;
import org.cowboycoders.cyclisimo.io.sendtogoogle.SendRequest;
import org.cowboycoders.cyclisimo.io.sendtogoogle.UploadResultActivity;

/**
 * Tests the {@link SendMapsActivity}.
 * 
 * @author Youtao Liu
 */
public class SendMapsActivityTest extends AndroidTestCase {

  private SendMapsActivity sendMapsActivity;
  private SendRequest sendRequest;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    sendRequest = new SendRequest(1L);
    sendMapsActivity = new SendMapsActivity();
  }

  /**
   * Tests the method
   * {@link SendMapsActivity#getNextClass(SendRequest, boolean)}. Sets the flags
   * of "sendFusionTables","sendDocs" and "cancel" to true, true and false.
   */
  public void testGetNextClass_notCancelSendFusionTables() {
    sendRequest.setSendFusionTables(true);
    sendRequest.setSendDocs(true);
    Class<?> next = sendMapsActivity.getNextClass(sendRequest, false);
    assertEquals(SendFusionTablesActivity.class, next);
  }

  /**
   * Tests the method
   * {@link SendMapsActivity#getNextClass(SendRequest, boolean)}. Sets the flags
   * of "sendFusionTables","sendDocs" and "cancel" to false, true and false.
  */
  public void testGetNextClass_notCancelSendDocs() {
    sendRequest.setSendFusionTables(false);
    sendRequest.setSendDocs(true);
    Class<?> next = sendMapsActivity.getNextClass(sendRequest, false);
    assertEquals(SendDocsActivity.class, next);
  }
  
  /**
   * Tests the method
   * {@link SendMapsActivity#getNextClass(SendRequest, boolean)}. Sets the flags
   * of "sendFusionTables","sendDocs" and "cancel" to false, false and false.
   */
  public void testGetNextClass_notCancelNotSend() {
    sendRequest.setSendFusionTables(false);
    sendRequest.setSendDocs(false);
    Class<?> next = sendMapsActivity.getNextClass(sendRequest, false);
    assertEquals(UploadResultActivity.class, next);
  }

  /**
   * Tests the method
   * {@link SendMapsActivity#getNextClass(SendRequest, boolean)}. Sets the flags
   * of "sendFusionTables","sendDocs" and "cancel" to true, true and true.
   */
  public void testGetNextClass_cancelSendDocs() {
    sendRequest.setSendFusionTables(true);
    sendRequest.setSendDocs(true);
    Class<?> next = sendMapsActivity.getNextClass(sendRequest, true);
    assertEquals(UploadResultActivity.class, next);
  }

}