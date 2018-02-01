package NotVlad;

import java.io.File;

import auto.CommandList;
import auto.CommandListRunner;
import comms.DebugMode;
import comms.SmartWriter;
import edu.wpi.first.wpilibj.DriverStation;
import robot.Global;
import robot.IControl;
import robot.Global.TargetSide;

public class AutoRunner extends IControl{
	NotVladXMLInterpreter XMLInterpreter;
	CommandListRunner runner;
	private double timeCost;
	private boolean finished;
	public AutoRunner(){
	}
	
	public void robotInit(){
	}
	
	public void autonomousInit(){
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
		
		finished = false;
		System.out.println("Start" + System.currentTimeMillis());
		File file = new File("/home/lvuser/Paths.xml");
		System.out.println(file.getName());
		XMLInterpreter = new NotVladXMLInterpreter(file);
		CommandList list = XMLInterpreter.getPathList(choosePath());
		System.out.println("Parse End" + System.currentTimeMillis());
		runner = new CommandListRunner(list);
		timeCost = System.currentTimeMillis();
	}
	
	public void autonomousPeriodic(){
		if(!finished) {
			SmartWriter.putD("TimeCost", System.currentTimeMillis()-timeCost);
			SmartWriter.putS("Game Data & Path Name", DriverStation.getInstance().getGameSpecificMessage() + " " + choosePath());
			SmartWriter.putS("Switch/Scale", Global.ourSwitchPosition.toString() + " " + Global.scalePosition.toString());
		}
		finished = runner.runList();
	}
	
	public void teleopInit(){
		runner.stop();
	}
	
	public void disabledInit(){
		SmartWriter.putS("Path", "EnterPath", DebugMode.COMPETITION);
		runner.stop();
	}
	
	public static String choosePath() {
		String path = "";

		MiyamotoControl switchboard = (MiyamotoControl) Global.controllers;
		path += switchboard.getStartPosition();

		int pathNum = 1; // Defaults to front approach of the scale

		if (switchboard.getApproach()) {
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

		return path;
	}

}
