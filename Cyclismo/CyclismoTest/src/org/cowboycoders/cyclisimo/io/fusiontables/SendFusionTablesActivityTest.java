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
package org.cowboycoders.cyclisimo.io.fusiontables;


import android.test.AndroidTestCase;

import org.cowboycoders.cyclisimo.io.docs.SendDocsActivity;
import org.cowboycoders.cyclisimo.io.fusiontables.SendFusionTablesActivity;
import org.cowboycoders.cyclisimo.io.sendtogoogle.SendRequest;
import org.cowboycoders.cyclisimo.io.sendtogoogle.UploadResultActivity;

/**
 * Tests the {@link SendFusionTablesActivity}.
 * 
 * @author Youtao Liu
 */
public class SendFusionTablesActivityTest extends AndroidTestCase {

  private SendFusionTablesActivity sendFusionTablesActivity;
  private SendRequest sendRequest;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    sendRequest = new SendRequest(1L);
    sendFusionTablesActivity = new SendFusionTablesActivity();
  }

  /**
   * Tests the method
   * {@link SendFusionTablesActivity#getNextClass(SendRequest, boolean)}. Sets
   * the flags of "sendDocs" and "cancel" to false and false.
   */
  public void testGetNextClass_notCancelNotSendDocs() {
    sendRequest.setSendDocs(false);
    Class<?> next = sendFusionTablesActivity.getNextClass(sendRequest, false);
    assertEquals(UploadResultActivity.class, next);
  }

  /**
   * Tests the method
   * {@link SendFusionTablesActivity#getNextClass(SendRequest, boolean)}. Sets
   * the flags of "sendDocs" and "cancel" to true and false.
   */
  public void testGetNextClass_notCancelSendDocs() {
    sendRequest.setSendDocs(true);
    Class<?> next = sendFusionTablesActivity.getNextClass(sendRequest, false);
    assertEquals(SendDocsActivity.class, next);
  }

  /**
   * Tests the method
   * {@link SendFusionTablesActivity#getNextClass(SendRequest,boolean)}. Sets
   * the flags of "sendDocs" and "cancel" to true and true.
   */
  public void testGetNextClass_cancelSendDocs() {
    sendRequest.setSendDocs(true);
    Class<?> next = sendFusionTablesActivity.getNextClass(sendRequest, true);
    assertEquals(UploadResultActivity.class, next);
  }

  /**
   * Tests the method
   * {@link SendFusionTablesActivity#getNextClass(SendRequest,boolean)}. Sets
   * the flags of "sendDocs" and "cancel" to false and true.
   */
  public void testGetNextClass_cancelNotSendDocs() {
    sendRequest.setSendDocs(false);
    Class<?> next = sendFusionTablesActivity.getNextClass(sendRequest, true);
    assertEquals(UploadResultActivity.class, next);
  }

}
