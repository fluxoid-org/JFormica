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
 * @author Will
 * 
 * Tests for {@link org.cowboycoders.turbotrainers.bushido.brake.SpeedResistancePowerMapper}
 *
 */
public class SpeedResistanceMapperTest {

	//Container for brake data
	private final BrakeModel dataModel = new TargetSlopeModel();
	private final SpeedResistancePowerMapper speedResistanceMapper = new SpeedResistancePowerMapper();
	
	/**
	 * Test method for {@link org.cowboycoders.turbotrainers.bushido.brake.SpeedResistanceMapper#getBrakeResistanceFromPolynomialFit()}.
	 */
	@Test
	public void testGetBrakeResistanceFromSurfaceFit() {
		
		this.speedResistanceMapper.start(dataModel);
		dataModel.setVirtualSpeed(39);
		dataModel.setSlope(0.0);
		dataModel.setTotalWeight(70);
		dataModel.setPower(645);
		double resistance = speedResistanceMapper.getBrakeResistanceFromSurfaceFit();
		System.out.println(resistance);
		//assertEquals(27.6, resistance, 10);
		
	}

}
