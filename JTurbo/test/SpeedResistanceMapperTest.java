import static org.junit.Assert.*;

import org.cowboycoders.turbotrainers.bushido.brake.BrakeModel;
import org.cowboycoders.turbotrainers.bushido.brake.SpeedResistanceMapper;
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
public class SpeedResistanceMapperTest {

	//Container for brake data
	private BrakeModel dataModel = new BrakeModel();
	private SpeedResistanceMapper speedResistanceMapper = new SpeedResistanceMapper(dataModel);
	
	/**
	 * Test method for {@link org.cowboycoders.turbotrainers.bushido.brake.SpeedResistanceMapper#getBrakeResistanceFromPolynomialFit()}.
	 */
	@Test
	public void testGetBrakeResistanceFromPolynomialFit() {
		
		dataModel.setVirtualSpeed(46);
		dataModel.setSlope(0.0);
		dataModel.setTotalWeight(70);
		double resistance = speedResistanceMapper.getBrakeResistanceFromPolynomialFit();
		assertEquals(278, resistance, 10);
		
		dataModel.setVirtualSpeed(0);
		dataModel.setSlope(0.0);
		dataModel.setTotalWeight(70);
		resistance = speedResistanceMapper.getBrakeResistanceFromPolynomialFit();
		assertEquals(30, resistance, 10);
		
		dataModel.setVirtualSpeed(60);
		dataModel.setSlope(20.0);
		dataModel.setTotalWeight(70);
		resistance = speedResistanceMapper.getBrakeResistanceFromPolynomialFit();
		assertEquals(1982, resistance, 10);
		
	}

}
