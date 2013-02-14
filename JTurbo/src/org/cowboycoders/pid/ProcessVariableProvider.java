package org.cowboycoders.pid;

public interface ProcessVariableProvider {
	
	/**
	 * Supplies the latest value observed (actually observed)
	 * @return observed value
	 */
	double getProcessVariable();

}
