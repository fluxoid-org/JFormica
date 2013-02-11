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
 * Copyright 2009 Google Inc.
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
package org.cowboycoders.cyclisimo.io.gdata.docs;

import com.google.wireless.gdata.client.GDataClient;
import com.google.wireless.gdata.client.GDataParserFactory;
import com.google.wireless.gdata.client.GDataServiceClient;

/**
 * GDataServiceClient for accessing Google Documents. This is not a full
 * implementation.
 */
public class DocumentsClient extends GDataServiceClient {
  /** The name of the service, dictated to be 'wise' by the protocol. */
  public static final String SERVICE = "writely";

  /**
   * Creates a new DocumentsClient.
   * 
   * @param client The GDataClient that should be used to authenticate requests,
   *        retrieve feeds, etc
   * @param parserFactory The GDataParserFactory that should be used to obtain
   *        GDataParsers used by this client
   */
  public DocumentsClient(GDataClient client, GDataParserFactory parserFactory) {
    super(client, parserFactory);
  }

  @Override
  public String getServiceName() {
    return SERVICE;
  }
}
