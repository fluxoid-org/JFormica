import static org.junit.Assert.*;

import org.cowboycoders.turbotrainers.bushido.brake.BrakeModel;
import org.cowboycoders.turbotrainers.bushido.brake.SpeedResistancePowerMapper;
import org.cowboycoders.turbotrainers.bushido.brake.TargetSlopeModel;
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
	private BrakeModel dataModel = new TargetSlopeModel();
	private SpeedResistancePowerMapper speedResistancePowerMapper = new SpeedResistancePowerMapper();
	
	/**
	 * Test method for {@link org.cowboycoders.turbotrainers.bushido.brake.SpeedResistanceMapper#getBrakeResistanceFromPolynomialFit()}.
	 */
	@Test
	public void testGetBrakeResistanceFromPolynomialFit() {
		
		this.speedResistancePowerMapper.start(dataModel);
		double resistance;
		
		//Big Mig is back and he's doing a time trial; we expect a very hard resistance (500)
		dataModel.setPower(600);
		dataModel.setVirtualSpeed(45);
		resistance = speedResistancePowerMapper.getBrakeResistanceFromSurfaceFit();
		assertEquals(500, resistance, 10);
		
		//Big Mig is powering up a hill. Should be even harder to force the wheel speed down (750)
		dataModel.setPower(600);
		dataModel.setVirtualSpeed(25);
		resistance = speedResistancePowerMapper.getBrakeResistanceFromSurfaceFit();
		assertEquals(750, resistance, 10);
		
		//Big Mig is descending. Should be easier than the time trail to get wheel speed up (322)
		dataModel.setPower(600);
		dataModel.setVirtualSpeed(65);
		resistance = speedResistancePowerMapper.getBrakeResistanceFromSurfaceFit();
		assertEquals(322, resistance, 10);
		
		//Sir Chris Hoy. Should be at the upper limit (1000)
		dataModel.setPower(2483);
		dataModel.setVirtualSpeed(65);
		resistance = speedResistancePowerMapper.getBrakeResistanceFromSurfaceFit();
		assertEquals(1000, resistance, 10);
		
		//A feeble effort. Should be at the lower limit (250)
		dataModel.setPower(50);
		dataModel.setVirtualSpeed(10);
		resistance = speedResistancePowerMapper.getBrakeResistanceFromSurfaceFit();
		assertEquals(250, resistance, 10);
		
		//Chris Hoy going backwards on a fixie. Should be at the upper limit (1000)
		dataModel.setPower(2483);
		dataModel.setVirtualSpeed(-10);
		resistance = speedResistancePowerMapper.getBrakeResistanceFromSurfaceFit();
		assertEquals(1000, resistance, 10);
		
		//Pre-ride faffing. The lowest limit.
		dataModel.setPower(0);
		dataModel.setVirtualSpeed(0);
		resistance = speedResistancePowerMapper.getBrakeResistanceFromSurfaceFit();
		assertEquals(250, resistance, 10);
		
		//Going slowly up a very steep hill - should default to lower limit
		dataModel.setPower(200);
		dataModel.setVirtualSpeed(8);
		resistance = speedResistancePowerMapper.getBrakeResistanceFromSurfaceFit();
		assertEquals(250, resistance, 10);
		
		//Going fast up a hill - should be hard but 
		//FIXME Is this off the surface fit? It is very hard.
		dataModel.setPower(350);
		dataModel.setVirtualSpeed(8);
		resistance = speedResistancePowerMapper.getBrakeResistanceFromSurfaceFit();
		assertEquals(1000, resistance, 10);
		
	}

}
