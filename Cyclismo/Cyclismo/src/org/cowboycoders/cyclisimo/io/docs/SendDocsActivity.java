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
package org.cowboycoders.cyclisimo.io.docs;

import org.cowboycoders.cyclisimo.R;

import android.content.Intent;

import org.cowboycoders.cyclisimo.io.sendtogoogle.AbstractSendActivity;
import org.cowboycoders.cyclisimo.io.sendtogoogle.AbstractSendAsyncTask;
import org.cowboycoders.cyclisimo.io.sendtogoogle.SendRequest;
import org.cowboycoders.cyclisimo.io.sendtogoogle.UploadResultActivity;
import org.cowboycoders.cyclisimo.util.IntentUtils;

/**
 * An activity to send a track to Google Docs.
 *
 * @author Jimmy Shih
 */
public class SendDocsActivity extends AbstractSendActivity {

  @Override
  protected AbstractSendAsyncTask createAsyncTask() {
    return new SendDocsAsyncTask(this, sendRequest.getTrackId(), sendRequest.getAccount());
  }

  @Override
  protected String getServiceName() {
    return getString(R.string.send_google_docs);
  }

  @Override
  protected void startNextActivity(boolean success, boolean isCancel) {
    sendRequest.setDocsSuccess(success);
    Intent intent = IntentUtils.newIntent(this, UploadResultActivity.class)
        .putExtra(SendRequest.SEND_REQUEST_KEY, sendRequest);
    startActivity(intent);
    finish();
  }
}
