package org.cowboycoders.pid;

public interface GainController {
	
	GainParameters getGain(OutputControlParameters parameters);

}
