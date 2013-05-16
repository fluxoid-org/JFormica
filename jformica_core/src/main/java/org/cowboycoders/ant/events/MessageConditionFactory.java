/**
 *     Copyright (c) 2012, Will Szumski
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
package org.cowboycoders.ant.events;

import java.util.logging.Logger;

import org.cowboycoders.ant.messages.ChannelMessage;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.StandardMessage;
import org.cowboycoders.ant.messages.responses.ChannelResponse;
import org.cowboycoders.ant.messages.responses.ResponseCode;

/**
 * Commonly used conditions
 * @author will
 *
 */
public class MessageConditionFactory {
  
  public final static Logger LOGGER = Logger.getLogger(MessageConditionFactory.class .getName()); 
  
  /**
   * wait for a message with class equal clazz
   * 
   * @param clazz

   */
  public static <V extends StandardMessage> MessageCondition newEqualsClassCondition(final Class<V> clazz)
  {
    MessageCondition condition = new MessageCondition () {
      @Override
      public boolean test(StandardMessage msg) {
        LOGGER.finer(msg.getClass().toString());
        LOGGER.finer(clazz.toString());
        if (msg.getClass().equals(clazz)) return true;
        return false;
      }
      
    };
    
    return condition;
  }
  
  /**
   * wait for a message that is an instance of clazz
   * 
   * @param clazz

   */
  public static <V extends StandardMessage> MessageCondition newInstanceOfCondition(final Class<V> clazz)
  {
    MessageCondition condition = new MessageCondition () {
      @Override
      public boolean test(StandardMessage msg) {
        LOGGER.finer(msg.getClass().toString());
        LOGGER.finer(clazz.toString());
        if (clazz.isInstance(msg)) return true;
        return false;
      }
      
    };
    return condition;
  }
  
  
  private static class ResponseCondition implements MessageCondition {
    
    private ResponseCode responseCode;
    private MessageId id;

    public ResponseCondition(MessageId id, ResponseCode responseCode) {
      this.id = id;
      this.responseCode = responseCode;
    }

    @Override
    public boolean test(StandardMessage testMsg) {
      if (!(testMsg instanceof ChannelResponse)) return false;
      ChannelResponse response = (ChannelResponse) testMsg;
      if (id != null) {
        if (!response.getMessageId().equals(id)) return false;
      }
      
      if (responseCode != null) {
        if (!(response.getResponseCode().getCode() == responseCode.getCode())) return false;
      }
      
      return true;
    }
    
  }
  

  /**
   * 
   * @param message
   * @param responseCode
   * @param id overrides message.getID()
   * @return
   */
  public static <V extends StandardMessage> MessageCondition newResponseCondition(MessageId id, 
      ResponseCode responseCode)
  {
    
    MessageCondition condition = new ResponseCondition(id, responseCode);
  
    return condition;
  }
  
  
  /**
   * Chains conditions together - you should only use this once
   * @param conditions to chain
   * @return chained condition
   */
  public static MessageCondition newChainedCondition(final MessageCondition ... conditions) {
    
    MessageCondition condition = new MessageCondition () {
      private int currentIndex = 0;
      private boolean success = false;
      
      @Override
      public boolean test(StandardMessage msg) {
    	// if we are re-testing after all conditions have returned true  
    	if (!(currentIndex < conditions.length)) {
    		return success;
    	}
        if (conditions[currentIndex].test(msg)) {
          currentIndex++;
          if (currentIndex >= conditions.length)  {
        	success = true;
            return true;
          }
        }
        return false;
      }
      
    };
    
    return condition;
  }
  
  

}
