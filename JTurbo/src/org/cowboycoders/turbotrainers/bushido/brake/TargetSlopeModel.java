package org.cowboycoders.turbotrainers.bushido.brake;

import java.math.BigInteger;

import org.cowboycoders.turbotrainers.Parameters;
import org.cowboycoders.turbotrainers.Parameters.CommonParametersInterface;

public class TargetSlopeModel  extends BrakeModel{
	
	@Override
	public void setParameters(CommonParametersInterface parameters)
			throws IllegalArgumentException {
		Parameters.TargetSlope castParameters = null;
		try {
			castParameters = (Parameters.TargetSlope) parameters;
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("Expecting target slope",e);
		}
		setTotalWeight(castParameters.getTotalWeight());
		setSlope(castParameters.getSlope());
		
	}

	@Override
	public double getTarget() {
		return getSlope();
	}
	
	public static void main(String[] args) {
		BigInteger bi = new BigInteger(new byte[] { (byte) 254, 98 });
		System.out.println("resistance low: " + bi);
		bi = new BigInteger(new byte[] { (byte) 0x0c, (byte) 0xff });
		System.out.println("resistance high: " + bi);
		System.out.println();
		TargetSlopeModel bd = new TargetSlopeModel();
		bd.setResistance(100);
		System.out.println(bd.getAbsoluteResistance());
		System.out.println(bd.getResistance());
		bd.setResistance(50);
		System.out.println(bd.getAbsoluteResistance());
		System.out.println(bd.getResistance());
		bd.setResistance(0);
		System.out.println(bd.getAbsoluteResistance());
		System.out.println(bd.getResistance());
	}

}
