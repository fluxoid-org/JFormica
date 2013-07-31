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
package org.cowboycoders.ant.events;

import java.util.logging.Logger;

import org.cowboycoders.ant.TransferException;
import org.cowboycoders.ant.messages.MessageId;
import org.cowboycoders.ant.messages.StandardMessage;
import org.cowboycoders.ant.messages.responses.Response;
import org.cowboycoders.ant.messages.responses.ResponseCode;

/**
 * Commonly used conditions
 * @author will
 *
 */
public class MessageConditionFactory {
  
  public final static Logger LOGGER = Logger.getLogger(MessageConditionFactory.class .getName());
  
  private static MessageCondition GENERIC_RESPONSE_CONDITION = new ResponseCondition();
  
  private static MessageCondition RESPONSE_FILTER_CONDITION = new MessageCondition() {

	@Override
	public boolean test(StandardMessage msg) {
		if (!GENERIC_RESPONSE_CONDITION.test(msg)) return false;
		Response response = (Response) msg;
		if (response.getMessageId().equals(MessageId.EVENT)) return false;
		return true;
	}
	  
  };
  
  private static MessageCondition EVENT_FILTER_CONDITION = new MessageCondition() {

	@Override
	public boolean test(StandardMessage msg) {
		if (!GENERIC_RESPONSE_CONDITION.test(msg)) return false;
		Response response = (Response) msg;
		if (!response.getMessageId().equals(MessageId.EVENT)) return false;
		return true;
	}
	  
  };
  
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
    
    /**
     * Matches any {@link Response}
     */
    public ResponseCondition() {
    	this(null,null);
      }

    @Override
    public boolean test(StandardMessage testMsg) {
      if (!(testMsg instanceof Response)) return false;
      Response response = (Response) testMsg;
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
   * Matches a generic response, see {@link Response}
   * @return
   */
  public static MessageCondition newResponseCondition()
  {
    return RESPONSE_FILTER_CONDITION;
  }
  
  /**
   * Matches a generic event i.e something that is sent from the ant chip
   * that isn't in reply to a message you have sent, see {@link Response}
   * @return
   */
  public static MessageCondition newEventCondition()
  {
    return EVENT_FILTER_CONDITION;
  }
  
  
  /**
   * Checks for {@code ResponseCode.EVENT_TRANSFER_TX_COMPLETED}, throws a
   * {@link TransferException} on {@code ResponseCode.EVENT_TRANSFER_TX_FAILED}.
   * 
   * Used for sending {@link AcknowledgedDataMessage}s
   * 
   * @return the condition satisfying the above
   */
  public static MessageCondition newAcknowledgedCondition()
  {
		final MessageCondition complete = MessageConditionFactory.newResponseCondition(MessageId.EVENT,ResponseCode.EVENT_TRANSFER_TX_COMPLETED);
		final MessageCondition failed = MessageConditionFactory.newResponseCondition(MessageId.EVENT,ResponseCode.EVENT_TRANSFER_TX_FAILED);
		MessageCondition condition = new MessageCondition() {

			@Override
			public boolean test(StandardMessage msg) {
				if (complete.test(msg)) return true;
				if (failed.test(msg)) {
					Response response = (Response)msg;
					throw new TransferException(response.getId(),  response.getResponseCode(), "didn't receive an acknowledgement");
				}
				return false;
			}
			
		};
		
		return condition;
  }
  
  
  /**
   * Chains conditions together - you should only use this once. All conditions must be 
   * satisfied (think and!)
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
