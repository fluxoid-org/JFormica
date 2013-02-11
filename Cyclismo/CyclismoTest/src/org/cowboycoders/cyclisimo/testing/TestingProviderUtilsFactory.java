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
// Copyright 2010 Google Inc. All Rights Reserved.

package org.cowboycoders.cyclisimo.testing;

import org.cowboycoders.cyclisimo.content.MyTracksProviderUtils;
import org.cowboycoders.cyclisimo.content.MyTracksProviderUtils.Factory;

import android.content.Context;

/**
 * A fake factory for {@link MyTracksProviderUtils} which always returns a
 * predefined instance.
 *
 * @author Rodrigo Damazio
 */
public class TestingProviderUtilsFactory extends Factory {
  private MyTracksProviderUtils instance;

  public TestingProviderUtilsFactory(MyTracksProviderUtils instance) {
    this.instance = instance;
  }

  @Override
  protected MyTracksProviderUtils newForContext(Context context) {
    return instance;
  }

  public static Factory installWithInstance(MyTracksProviderUtils instance) {
    Factory oldFactory = Factory.getInstance();
    Factory factory = new TestingProviderUtilsFactory(instance);
    MyTracksProviderUtils.Factory.overrideInstance(factory);
    return oldFactory;
  }

  public static void restoreOldFactory(Factory factory) {
    MyTracksProviderUtils.Factory.overrideInstance(factory);
  }
}
