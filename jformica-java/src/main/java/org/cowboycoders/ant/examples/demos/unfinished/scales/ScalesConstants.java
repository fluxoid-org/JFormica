package org.cowboycoders.ant.examples.demos.unfinished.scales;


import org.cowboycoders.ant.utils.ByteUtils;

public class ScalesConstants {
	
	public static final int WEIGHT_INVALID = 65535;
	public static final int WEIGHT_COMPUTING = 65534;
	
	/**
	 * Extracts weight from a broadcast packet (sent from scales)
	 * @param dataPacket containing data
	 * @return the user's weight in kg
	 * @throws DataNotReadyException if packet indicates that weight is being measured
	 * @throws InvalidWeightException if scales indicate an invalid weight was measured
	 */
	public static double extractWeight(Byte [] dataPacket ) throws DataNotReadyException, InvalidWeightException {
		int [] data  = ByteUtils.unsignedBytesToInts(dataPacket);
		int weightAsInt = ((int) (data[6] << 8  + data[7] ));
		if (weightAsInt == WEIGHT_INVALID) {
			throw new InvalidWeightException();
		} else if (weightAsInt == WEIGHT_COMPUTING) {
			throw new DataNotReadyException();
		}
		double weight = (double) weightAsInt / 100.;
		return weight;
	}

}
