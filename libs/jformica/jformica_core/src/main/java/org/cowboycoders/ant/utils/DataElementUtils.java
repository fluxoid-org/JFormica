/**
 *     Copyright (c) 2013, Will Szumski
 *
 *     This file is part of formicidae.
 *
 *     formicidae is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     formicidae is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with formicidae.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package org.cowboycoders.ant.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cowboycoders.ant.messages.FatalMessageException;
import org.cowboycoders.ant.messages.Constants.DataElement;

/**
 * @author will
 *
 */
public class DataElementUtils {
  
  private DataElementUtils() {
    
  }
  
  /**
   * Sets a DataElement in a payload
   * @param payload to modify 
   * @param messageElements describe the composition of the payload
   * @param element to modify
   * @param value to set {@code element} to
   * @param offset to messageElements description
   * @param skip how many identical elements to skip before insertion
   * @return true, if successful otherwise false
   */
  public static boolean setDataElement(
      ArrayList<Byte> payload,
      DataElement [] messageElements,      
      DataElement element, 
      Integer value,
      int offset,
      int skip
      ) {
    if (!Arrays.asList(messageElements).contains(element)) {
      throw new FatalMessageException("Arg, element, not in expected list");
    }
    
    boolean completed = false;
    List<Byte> insertionBytes = ByteUtils.lsbSplit(value, element.getLength());
    
    completed = insertElementBytes(payload, messageElements, element, insertionBytes, offset,skip);
    
    if (!completed) {
      throw new FatalMessageException("Byte insertion failed");
    }
    
    return completed;
    
  }
  
  /**
   * Inserts data, given as  a {@code List} of Bytes, into a payload
   * @param payload the payload to insert bytes into
   * @param messageElements description of the composition
   * @param element element we are targeting
   * @param bytesToInsert new bytes to insert
   * @param offset to messageElements description
   * @param skip how many identical elements to skip before insertion
   * @return true if successful, otherwise false
   */
  private static boolean insertElementBytes(
      ArrayList<Byte> payload,
      DataElement [] messageElements,
      DataElement element,
      List<Byte> bytesToInsert,
      int offset,
      int skip
      ) {
    assert bytesToInsert.size() == element.getLength() : 
      "Number of bytes to insert doesn't match expected element length";
    
    boolean completed = false;
    int elementCount = 0;
    int index = offset;
    for (DataElement e : messageElements) {
      if (e == element) {
        if (elementCount == skip) {
          for (byte b : bytesToInsert) {
            payload.set(index, b);
            index++;
          }
          completed = true;
          break;
        }
        elementCount++;

      }
      index += e.getLength();
    }
    
    return completed;
  }
  
  /**
   * Gets the data associated with a given {@code DataElement}
   * @param payload to get the data from
   * @param messageElements describes the composition of the payload
   * @param element to get data for
   * @param offset in payload to start of messageElements
   * @return
   */
  public static Integer getDataElement(
      ArrayList<Byte> payload,
      DataElement [] messageElements,
      DataElement element, 
      int offset,
      int skip) {
    
    if (!Arrays.asList(messageElements).contains(element)) {
      throw new FatalMessageException("Arg, element, not in expected list");
    }
    
    Integer rtn = null;
    int elementCount = 0;
    int index = offset;
    for (DataElement e : messageElements) {
      if (e == element) {
        if (elementCount == skip) {
          rtn = ByteUtils.lsbMerge(payload.subList(index, index += e.getLength()));
          break;
        }
        elementCount++;
      }
      index += e.getLength();
    }
    
    return rtn;
  }


}
