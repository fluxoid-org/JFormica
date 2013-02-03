package org.cowboycoders.turbotrainers;

import java.util.concurrent.TimeoutException;

import org.cowboycoders.ant.Node;

public abstract class AntTurboTrainer implements TurboTrainerInterface {
  
  private Node node;

  public AntTurboTrainer(Node node){
    this.node = node;
  }
  
  /**
   * @return the node
   */
  public Node getNode() {
    return node;
  }
  
  public abstract void start() throws TooFewAntChannelsAvailableException, TurboCommunicationException, InterruptedException, TimeoutException;
  
}
