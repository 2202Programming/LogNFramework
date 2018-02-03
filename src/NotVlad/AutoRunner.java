package NotVlad;

import auto.CommandList;
import auto.CommandListRunner;
import auto.pathFinder.TankPathFinderCommand;
import comms.DebugMode;
import comms.SmartWriter;
import edu.wpi.first.wpilibj.DriverStation;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Waypoint;
import robot.Global;
import robot.Global.TargetSide;
import robot.IControl;

public class AutoRunner extends IControl {
	NotVladXMLInterpreter XMLInterpreter;
	CommandListRunner runner;
	private double timeCost;
	private boolean finished;

	public AutoRunner() {
	}

	public void robotInit() {
	}

	public void autonomousInit() {
		Waypoint[] path = new Waypoint[] { new Waypoint(-4, -1, Pathfinder.d2r(0)) };
		CommandList list = new CommandList();
		list.addCommand(new TankPathFinderCommand(path, 1.0, 1.0, 1.0, .5842, 0.2032, .0005, .002, .15, 0));
		runner = new CommandListRunner(list);
		timeCost = System.currentTimeMillis();
	}

	public void autonomousPeriodic() {
		if (!finished) {
			SmartWriter.putD("TimeCost", System.currentTimeMillis() - timeCost);
			SmartWriter.putS("Game Data & Path Name",
					DriverStation.getInstance().getGameSpecificMessage() + " " + choosePath());
			SmartWriter.putS("Switch/Scale",
					Global.ourSwitchPosition.toString() + " " + Global.scalePosition.toString());
		}
		finished = runner.runList();
	}

	public void teleopInit() {
		runner.stop();
	}

	public void disabledInit() {
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
