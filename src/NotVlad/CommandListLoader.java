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
import robot.Global.TargetSide;

public class CommandListLoader extends IControl {

	private CommandList commandList;
	//Add Controllers
	private DriverStation ds=DriverStation.getInstance();
	private String autoName;
	private CommandListRunner runner;
	
	public CommandListLoader() {
		
	}
	
	public void autonomousInit() {
		String gameData = DriverStation.getInstance().getGameSpecificMessage();
		if (gameData.charAt(0) == 'L') {
			Global.ourSwitchPosition = TargetSide.L;
		}
		else {
			Global.ourSwitchPosition = TargetSide.R;
		}
		
		if (gameData.charAt(1) == 'L') {
			Global.scalePosition = TargetSide.L;
		}
		else {
			Global.scalePosition = TargetSide.R;
		}
		
		if (gameData.charAt(2) == 'L') {
			Global.opponentSwitchPosition = TargetSide.L;
		}
		else {
			Global.opponentSwitchPosition = TargetSide.R;
		}
	}
	
	public void autonomousPeriodic() {
		
	}
	
	public void teleopInit() {
		
	}
}
