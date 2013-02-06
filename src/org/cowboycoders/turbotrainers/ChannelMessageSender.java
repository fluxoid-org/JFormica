package org.cowboycoders.turbotrainers;
import org.cowboycoders.ant.messages.ChannelMessage;
import org.cowboycoders.ant.messages.StandardMessage;


public interface ChannelMessageSender {
  public void sendMessage(ChannelMessage msg);

  void sendMessage(ChannelMessage msg, Runnable callback);
  
}
