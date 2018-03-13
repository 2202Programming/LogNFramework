package miyamoto;

import java.io.File;

import com.kauailabs.navx.frc.AHRS;

import auto.CommandList;
import auto.CommandListRunner;
import comms.DebugMode;
import comms.SmartWriter;
import edu.wpi.first.wpilibj.DriverStation;
import input.SensorController;
import robot.Global;
import robot.Global.TargetSide;
import robot.IControl;

public class AutoRunner extends IControl {
	MiyamotoXMLInterpreter XMLInterpreter;
	CommandListRunner runner;
	private double timeCost;
	private boolean finished;
	private String gameData;

	public AutoRunner() {
	}

	public void robotInit() {
	}

	// Parse game data from FMS and set enums for auto switch/scale positions
	public void autonomousInit() {
		runner = null;

		gameData = DriverStation.getInstance().getGameSpecificMessage();

		AHRS navX = (AHRS) SensorController.getInstance().getSensor("NAVX");
		navX.reset();

		if (gameData == null || gameData.length() != 3) {
			createCommandList("D");
			return;
		}

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

		String pathChosen = choosePath();
		createCommandList(pathChosen);
	}

	// Creates list of auto commands to be run
	public void createCommandList(String path) {
		if (path == null || path.equals("null") || path.equals("")) {
			return;
		}
		finished = false;
		timeCost = System.currentTimeMillis();
		long startRead = System.currentTimeMillis();
		File file = new File("/home/lvuser/Paths.xml");
		XMLInterpreter = new MiyamotoXMLInterpreter(file);
		long interpretEnd = System.currentTimeMillis();

		
		System.out.println(file.getName());
		System.out.println("File read and Parse Time Only: " + (interpretEnd - startRead));

		long commandListBuildingStart = System.currentTimeMillis();
		CommandList list = XMLInterpreter.getPathList(path);
		long commandListBuildingEnd = System.currentTimeMillis();
	
		System.out.println("CommandList creation time ONLY): " + (commandListBuildingEnd - commandListBuildingStart));
		runner = new CommandListRunner(list);
		SmartWriter.putS("Game Data & Path Name", gameData + " " + path);
	}

	public void autonomousPeriodic() {
		if (runner == null) {
			autonomousInit();
			System.out.println("Runner is null");
		}

		if (!finished) {
			SmartWriter.putD("TimeCost", System.currentTimeMillis() - timeCost);
			SmartWriter.putS("Switch/Scale",
					Global.ourSwitchPosition.toString() + " " + Global.scalePosition.toString());
		}
		finished = runner.runList();
	}

	public void teleopInit() {
		if (runner != null)
			runner.stop();
	}

	public void disabledInit() {
		SmartWriter.putS("Path", "EnterPath", DebugMode.COMPETITION);
		if (runner != null) {
			runner.stop();
		}
	}

	// 2017-18 specific; construct the correct path from switchboard input,
	// robot
	// field position, and switch/scale position enums
	public static String choosePath() {
		String path = "";

		MiyamotoControl switchboard = (MiyamotoControl) Global.controllers;
		path += switchboard.getStartPosition();

		System.out.println("Start Position: " + switchboard.getStartPosition());
		System.out.println("Approach: " + switchboard.getApproach());
		System.out.println("Objective: " + switchboard.getObjective());
		System.out.println("Path Type: " + switchboard.getPathType());
		System.out.println(path);

		if (path.equals("null") || path.equals("")) {
			return null;
		}
		
		if(path.charAt(0)=='D'){
			return path;
		}

		int pathNum = 1; // Defaults to front approach of the switch

		if (switchboard.getObjective()) {
			// If we are going for the scale
			System.out.println("Going for scale");
			pathNum += 4;
			if (Global.scalePosition == TargetSide.L) {
				// If we are going for the left side of the scale
				pathNum += 2;
			}
			if (Global.scalePosition.toString().equals(switchboard.getStartPosition().toString())) {
				// If scale is same side
				pathNum++;
			}
		} else {
			// If we are going for the switch
			System.out.println("Going for switch");
			if (Global.ourSwitchPosition == TargetSide.L) {
				// If going for the left side of the switch
				pathNum += 2;
			}
			if (path.charAt(0) != 'M') {
				// If not starting mid
				pathNum++;
			}
		}

		path += pathNum + "-";
		System.out.println(path);

		int pathType = 0;

		// Determines primary (optimal) path or alternate path
		if (switchboard.getPathType()) {
			// If we take the alternate path
			pathType += 2;
		} else {
			// If we take the primary path
			pathType += 1;
		}

		// If we can do 2 block auto
		if (Global.scalePosition.toString().equals(switchboard.getStartPosition().toString())
				&& Global.scalePosition.toString().equals(Global.ourSwitchPosition.toString()) && pathNum > 4) {
			pathType += 2;
		}

		path += pathType;

		System.out.println(path);
		return path;
	}
}
