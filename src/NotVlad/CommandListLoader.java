package NotVlad;

import java.io.File;

import auto.CommandList;
import auto.CommandListRunner;
import comms.SmartWriter;
import comms.XboxController;
import edu.wpi.first.wpilibj.DriverStation;
import robot.Global;
//include notVlad robotDefinition
import robot.Global.TargetSide;
import robot.IControl;

public class CommandListLoader extends IControl {

	private CommandList commandList;
	// Add Controllers
	private DriverStation ds = DriverStation.getInstance();
	private String autoName;
	private CommandListRunner runner;

	public CommandListLoader() {

	}

	public void autonomousInit() {
		String gameData = DriverStation.getInstance().getGameSpecificMessage();
		if (gameData.charAt(0) == 'L') {
			Global.ourSwitchPosition = TargetSide.L;
		} else {
			Global.ourSwitchPosition = TargetSide.R;
		}

		if (gameData.charAt(1) == 'L') {
			Global.scalePosition = TargetSide.L;
		} else {
			Global.scalePosition = TargetSide.R;
		}

		if (gameData.charAt(2) == 'L') {
			Global.opponentSwitchPosition = TargetSide.L;
		} else {
			Global.opponentSwitchPosition = TargetSide.R;
		}

		choosePath();
	}

	public void choosePath() {
		String path = "";

		MiyamotoControl switchboard = (MiyamotoControl) Global.controllers;
		path += switchboard.getStartPosition();

		int pathNum = 1; // Defaults to front approach of the scale

		if (!switchboard.getApproach()) {
			// If we are approaching from the side
			pathNum++;
		}

		if (switchboard.getObjective()) {
			// If we are going for the scale
			pathNum += 4;
			if (Global.scalePosition == TargetSide.L) {
				// If we are going for the left side of the scale
				pathNum += 2;
			}
		} else {
			// If we are going for the switch
			if (Global.ourSwitchPosition == TargetSide.L) {
				// If we are gong for the left side of the switch
				pathNum += 2;
			}
		}

		path += pathNum + "-";

		if (switchboard.getPathType()) {
			// If we take the long path
			path += "2";
		} else {
			// If we take the short path
			path += "1";
		}

		NotVladXMLInterpreter interp = new NotVladXMLInterpreter(new File("Paths.xml"));
		commandList = interp.getPathList(path);
	}

	public void autonomousPeriodic() {
		if (autoName.equals("none")) {
			autonomousInit();
		} else {
			if (runner == null) {
				runner = new CommandListRunner(commandList);
				runner.init();
			} else {
				runner.runList();
			}
		}
	}

	public void teleopInit() {

	}
}
