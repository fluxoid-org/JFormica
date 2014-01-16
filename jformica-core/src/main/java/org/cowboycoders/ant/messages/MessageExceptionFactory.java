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
package org.cowboycoders.ant.messages;

import org.cowboycoders.ant.utils.ValidationUtils;
import org.cowboycoders.ant.utils.ValidationUtils.MaxMinExceptionProducer;

/**
 * Creates commonly used exceptions
 * @author will
 *
 */
public class MessageExceptionFactory {
  
  /**
   * Creates a MessageException with a message suitable for max-min validation
   * @param identifer the name of the variable to be tested
   * @param min minimum allowable value of variable
   * @param max allowable value of variable
   * @param value value being checked
   * @return the {@code ValidationException}
   */
  public static ValidationException createMaxMinException(String identifer, int min, int max, int value) {
    return new ValidationException(identifer + " must be between " + min + " and " +
        max +". Actual value: " + value);
  }
  
  /**
   * A factory which produces {@code ValidationUtils.MaxMinExceptionProducable}s
   * @param identifier variableName being tested
   * @return the created factory
   */
  public static ValidationUtils.MaxMinExceptionProducer<ValidationException> 
    createMaxMinExceptionProducable(final String identifier) {
    return new MaxMinExceptionProducer<ValidationException> () {
      
      @Override
      public ValidationException getMaxMinException(int min, int max, int value) {
        return createMaxMinException(identifier,min,max,value);
      }
      
    };
  }
  
  
  private MessageExceptionFactory() {}
  
  
  
}
