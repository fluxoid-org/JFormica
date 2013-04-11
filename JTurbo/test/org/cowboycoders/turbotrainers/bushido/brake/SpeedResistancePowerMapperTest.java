package org.cowboycoders.turbotrainers.bushido.brake;
import static org.junit.Assert.*;

import org.cowboycoders.turbotrainers.bushido.brake.BrakeModel;
import org.cowboycoders.turbotrainers.bushido.brake.SpeedResistanceMapper;
import org.cowboycoders.turbotrainers.bushido.brake.TargetSlopeModel;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */

/**
 * @author doug
 * 
 * Basic test of polynomial speed-resistance map using some known values
 *
 */
public class SpeedResistancePowerMapperTest {

	//Container for brake data
	private final BrakeModel dataModel = new TargetSlopeModel();
	private final SpeedResistanceMapper speedResistanceMapper = new SpeedResistanceMapper();
	
	/**
	 * Test method for {@link org.cowboycoders.turbotrainers.bushido.brake.SpeedResistanceMapper#getBrakeResistanceFromPolynomialFit()}.
	 */
	@Test
	public void testGetBrakeResistanceFromPolynomialFit() {
		
		this.speedResistanceMapper.start(dataModel);
		dataModel.setVirtualSpeed(46);
		dataModel.setSlope(0.0);
		dataModel.setTotalWeight(70);
		double resistance = speedResistanceMapper.getBrakeResistanceFromPolynomialFit();
		assertEquals(27.6, resistance, 10);
		
		dataModel.setVirtualSpeed(0);
		dataModel.setSlope(0.0);
		dataModel.setTotalWeight(70);
		resistance = speedResistanceMapper.getBrakeResistanceFromPolynomialFit();
		assertEquals(30, resistance, 10);
		
		dataModel.setVirtualSpeed(60);
		dataModel.setSlope(20.0);
		dataModel.setTotalWeight(70);
		resistance = speedResistanceMapper.getBrakeResistanceFromPolynomialFit();
		assertEquals(1546, resistance, 10);
		
	}

}
