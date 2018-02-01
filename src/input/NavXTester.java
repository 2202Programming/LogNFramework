package input;

import com.kauailabs.navx.frc.AHRS;

import comms.DebugMode;
import comms.SmartWriter;
import robot.IControl;

public class NavXTester extends IControl {
	
	public NavXTester(){
		super();
	}
	
	public void teleopInit() {
		
	}
	
	public void autonomousPeriodic() {
		teleopPeriodic();
	}
	
	public void teleopPeriodic() {
		AHRS navx = (AHRS)SensorController.getInstance().getSensor("NAVX");
//		double navxAngle = navx.getRoll();
		SmartWriter.putD("NavXPitch", navx.getPitch(), DebugMode.FULL);
		SmartWriter.putD("NavXYaw", navx.getYaw(), DebugMode.FULL);
		SmartWriter.putD("NavXRoll", navx.getRoll(), DebugMode.FULL);
		
		
	}


}
