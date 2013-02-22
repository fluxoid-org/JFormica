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
package org.cowboycoders.ant;

import java.util.LinkedList;

import org.cowboycoders.ant.messages.MessageMetaWrapper;
import org.cowboycoders.ant.messages.StandardMessage;

public class Receipt {

  private LinkedList<MessageMetaWrapper<StandardMessage>> sentMessages = new LinkedList<MessageMetaWrapper<StandardMessage>>();
  private LinkedList<MessageMetaWrapper<StandardMessage>> receivedMessages = new LinkedList<MessageMetaWrapper<StandardMessage>>();
  
  public void addRecieved(MessageMetaWrapper<StandardMessage> msg) {
	  receivedMessages.add(msg);
  }
  
  public void addSent(MessageMetaWrapper<StandardMessage> msg) {
	  sentMessages.add(msg);
  }
  
 public MessageMetaWrapper<StandardMessage> getLastSent() {
	 return sentMessages.getLast();
 }
 
 public MessageMetaWrapper<StandardMessage> getLastRecieved() {
	 return receivedMessages.getLast();
 }

protected LinkedList<MessageMetaWrapper<StandardMessage>> getSentMessages() {
	return sentMessages;
}

protected LinkedList<MessageMetaWrapper<StandardMessage>> getReceivedMessages() {
	return receivedMessages;
}
  
  
  
}
  
  