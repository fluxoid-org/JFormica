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
package org.cowboycoders.ant;

import org.cowboycoders.ant.messages.StandardMessage;

public abstract class AntLogger {
  
  public static enum  Direction {
    RECEIVED,
    SENT,
  }
  
  public static class LogDataContainer {
    private Direction direction;
    private Class<? extends StandardMessage> messageClass;
    private long timeStamp;
    private byte[] packet;
    private StandardMessage message;
    
    public LogDataContainer(Direction direction, StandardMessage msg) {
      this.direction = direction;
      this.messageClass = msg.getClass();
      this.packet = msg.encode();
      this.timeStamp = System.currentTimeMillis();
      this.message = msg;
    }

    /**
     * @return the direction
     */
    public Direction getDirection() {
      return direction;
    }

    /**
     * @return the messageClass
     */
    public Class<? extends StandardMessage> getMessageClass() {
      return messageClass;
    }

    /**
     * @return the timeStamp
     */
    public long getTimeStamp() {
      return timeStamp;
    }

    /**
     * @return the packet
     */
    public byte[] getPacket() {
      return packet;
    }

    /**
     * @return the message
     */
    public StandardMessage getMessage() {
      return message;
    }
    
    
  }
  
  
  public abstract void log(LogDataContainer data);

  

}
