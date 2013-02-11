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
