package org.cowboycoders.turbotrainers.bushido.headunit;

import org.cowboycoders.turbotrainers.TurboTrainerDataListener;

/**
 * All bushido events
 * @author will
 *
 */
interface BushidoInternalListener extends TurboTrainerDataListener, BushidoButtonPressListener {
  
  void onRequestPauseStatus();
  
  void onRequestData();
  
  void onRequestKeepAlive();
   

}
