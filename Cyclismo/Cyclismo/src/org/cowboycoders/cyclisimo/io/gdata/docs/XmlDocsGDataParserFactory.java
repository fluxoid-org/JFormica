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
 * Copyright 2010 Google Inc.
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

import com.google.wireless.gdata.client.GDataParserFactory;
import com.google.wireless.gdata.data.Entry;
import com.google.wireless.gdata.parser.GDataParser;
import com.google.wireless.gdata.parser.ParseException;
import com.google.wireless.gdata.parser.xml.XmlGDataParser;
import com.google.wireless.gdata.parser.xml.XmlParserFactory;
import com.google.wireless.gdata.serializer.GDataSerializer;
import com.google.wireless.gdata.serializer.xml.XmlEntryGDataSerializer;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParserException;

/**
 * Factory of Xml parsers for gdata maps data.
 */
public class XmlDocsGDataParserFactory implements GDataParserFactory {
  private XmlParserFactory xmlFactory;

  public XmlDocsGDataParserFactory(XmlParserFactory xmlFactory) {
    this.xmlFactory = xmlFactory;
  }

  @Override
  public GDataParser createParser(InputStream is) throws ParseException {
    try {
      return new XmlGDataParser(is, xmlFactory.createParser());
    } catch (XmlPullParserException e) {
      e.printStackTrace();
      return null;
    }
  }

  @SuppressWarnings("rawtypes")
  @Override
  public GDataParser createParser(Class cls, InputStream is)
      throws ParseException {
    try {
      return createParserForClass(is);
    } catch (XmlPullParserException e) {
      e.printStackTrace();
      return null;
    }
  }

  private GDataParser createParserForClass(InputStream is)
      throws ParseException, XmlPullParserException {
    return new XmlGDataParser(is, xmlFactory.createParser());
  }

  @Override
  public GDataSerializer createSerializer(Entry en) {
    return new XmlEntryGDataSerializer(xmlFactory, en);
  }
}