package org.cowboycoders.ant.temp;

public interface ProcessVariableProvider {
	
	/**
	 * Supplies the latest value observed (actually observed)
	 * @return observed value
	 */
	double getProcessVariable();

}
