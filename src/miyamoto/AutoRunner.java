package miyamoto;

import java.io.File;
import java.util.ArrayList;

import com.kauailabs.navx.frc.AHRS;

import auto.CommandList;
import auto.CommandListRunner;
import auto.commands.DriveAtAngle;
import auto.commands.PIDDriveAtAngle;
import auto.commands.PIDDriveMode;
import auto.stopConditions.DistanceStopCondition;
import auto.stopConditions.SummativeDistanceStopCondition;
import auto.stopConditions.TimerStopCondition;
import comms.DebugMode;
import comms.SmartWriter;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
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
		long startRead = System.currentTimeMillis();
		File file = new File("/home/lvuser/Paths.xml");
		try {
			XMLInterpreter = new MiyamotoXMLInterpreter(file);
			long interpretEnd = System.currentTimeMillis();
			createCommandList("D");

			System.out.println("**********Successfully made XMLInterpreter**********");
			System.out.println(file.getName());
			System.out.println("File Read Time: " + (interpretEnd - startRead));
		} catch (Exception e) {
			XMLInterpreter = null;
			System.out.println("**********Caught Path File Loading Error**********");
			e.printStackTrace();
		}
	}

	// Parse game data from FMS and set enums for auto switch/scale positions
	public void autonomousInit() {
		long autoInitStart = System.currentTimeMillis();
		runner = null;

		gameData = DriverStation.getInstance().getGameSpecificMessage();

		AHRS navX = (AHRS) SensorController.getInstance().getSensor("NAVX");
		navX.reset();

		if (gameData == null || gameData.length() < 3) {
			useDefaultCommandList();
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

		try {
			String pathChosen = choosePath();

			createCommandList(pathChosen);
		} catch (Exception e) {
			System.out.println("Invalid path construction from pathChosen; will run default path");
			e.printStackTrace();
			useDefaultCommandList();
		}
		long autoInitEnd = System.currentTimeMillis();
		System.out.println("Auto Init run time: " + (autoInitEnd - autoInitStart));
	}

	// Creates list of auto commands to be run
	public void createCommandList(String path) throws Exception {
		if (path == null || path.equals("null") || path.equals("") || runner != null) {
			return;
		}
		finished = false;
		timeCost = System.currentTimeMillis();

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
			return;
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
		int pathNum = 1; // Defaults to front approach of the switch
		
		MiyamotoControl switchboard = (MiyamotoControl) Global.controllers;
		path += switchboard.getStartPosition();
		
		if (!switchboard.getSafeAuto()) {
			System.out.println("Start Position: " + switchboard.getStartPosition() + 
					"\n" + "Approach: " + switchboard.getApproach() + 
					"\n" + "Objective: " + switchboard.getObjective());
			System.out.println(path);

			if (path.equals("null") || path.equals("")) {
				System.out.println("Path is Bad; no switchboard input");
				return null;
			}

			if (path.charAt(0) == 'D') {
				return path;
			}

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

			int pathType = 1;

			// If we can do 2 block auto
			if (Global.scalePosition.toString().equals(switchboard.getStartPosition().toString())
					&& Global.scalePosition.toString().equals(Global.ourSwitchPosition.toString()) && pathNum > 4) {
				pathType += 2;
			}

			path += pathType;

			System.out.println(path);
		}
		else {
			if(switchboard.getObjective() && Global.scalePosition.toString().equals(switchboard.getStartPosition().toString())){
				if(Global.scalePosition == TargetSide.L){
					path = "L8-2";
				}else{
					path = "R6-2";
				}
				
			}else if (Global.ourSwitchPosition.toString().equals(switchboard.getStartPosition().toString())) {
				if (Global.ourSwitchPosition == TargetSide.L) {
					// If going for the left side of the switch
					pathNum += 2;
				}
				
				pathNum++; //always start on side
				
				path += pathNum + "-1";
			}
			else {
				path = "D";
			}
			
			System.out.println(path);
		}
		
		return path;
		
	}

	public void useDefaultCommandList() {
		CommandList defaultPathList = new CommandList();

		ArrayList<Encoder> encoders = new ArrayList<Encoder>();
		SensorController sensorController = SensorController.getInstance();
		encoders.add((Encoder) sensorController.getSensor("ENCODER0"));
		encoders.add((Encoder) sensorController.getSensor("ENCODER1"));

		defaultPathList.addCommand(new DriveAtAngle(new DistanceStopCondition(encoders, 40), 0.55, 0));
		defaultPathList.addCommand(new DriveAtAngle(new SummativeDistanceStopCondition(encoders, 80), 0.3, 0));
		defaultPathList.addCommand(new PIDDriveAtAngle(new TimerStopCondition(1000), encoders, 140, -0.3, 0.3, 2, 0, 0.012, PIDDriveMode.LONG));
		defaultPathList.addCommand(new PIDDriveAtAngle(new TimerStopCondition(1000), encoders, 140, -0.3, 0.3, 2, 0, 0.012, PIDDriveMode.SHORT));
		System.out.println("**********Caught error and will run default path**********");

		runner = new CommandListRunner(defaultPathList);
	}
}
