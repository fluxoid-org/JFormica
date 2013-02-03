package org.cowboycoders.turbotrainers.bushido.headunit;

public interface BushidoButtonPressListener {

  public abstract void onButtonPressFinished(
      BushidoButtonPressDescriptor descriptor);

  public abstract void onButtonPressActive(
      BushidoButtonPressDescriptor descriptor);

}