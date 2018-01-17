package NotVlad;

import auto.CommandList;
import auto.CommandListRunner;
import comms.DebugMode;
import comms.SmartWriter;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import robot.Global;
import robot.IControl;
//include notVlad robotDefinition

public class CommandListLoader extends IControl {

	private CommandList commandList;
	//Add Controllers
	private DriverStation ds=DriverStation.getInstance();
	private String autoName;
	private CommandListRunner runner;
	
	public CommandListLoader() {
		
	}
	
	public void autonomousInit() {
		
	}
	
	public void autonomousPeriodic() {
		
	}
	
	public void teleopInit() {
		
	}
}
