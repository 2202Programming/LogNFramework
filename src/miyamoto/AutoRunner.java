package miyamoto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Map;

import com.kauailabs.navx.frc.AHRS;

import auto.CommandList;
import auto.CommandListRunner;
import comms.DebugMode;
import comms.SmartWriter;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import input.EncoderMonitor;
import input.SensorController;
import robot.Global;
import robot.Global.TargetSide;
import robot.IControl;

public class AutoRunner extends IControl {
	MiyamotoXMLInterpreter XMLInterpreter;
	CommandListRunner runner;
	private double timeCost;
	private boolean finished;
	private PrintWriter writer;
	private File distanceLogs;

	public AutoRunner() {
		distanceLogs = new File("/home/lvuser/distanceLogs.txt");
		try {
			writer = new PrintWriter(new FileOutputStream(distanceLogs, false));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void robotInit() {
	}

	// Parse game data from FMS and set enums for auto switch/scale positions
	public void autonomousInit() {
		String gameData = DriverStation.getInstance().getGameSpecificMessage();

		if (gameData == null || gameData.length() != 0) {
			createCommandList("D");
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

		AHRS navX = (AHRS) SensorController.getInstance().getSensor("NAVX");
		navX.reset();

		createCommandList(choosePath());

	}

	// Creates list of auto commands to be run
	public void createCommandList(String path) {
		finished = false;
		long start = System.currentTimeMillis();
		System.out.println("Start: " + start);
		File file = new File("/home/lvuser/Paths.xml");
		System.out.println(file.getName());
		XMLInterpreter = new MiyamotoXMLInterpreter(file);
		CommandList list = XMLInterpreter.getPathList(path);
		long end = System.currentTimeMillis();
		System.out.println("Parse End: " + end);
		System.out.println("Parse Time: " + (end - start));
		runner = new CommandListRunner(list);
		timeCost = System.currentTimeMillis();
		SmartWriter.putS("Game Data & Path Name",
				DriverStation.getInstance().getGameSpecificMessage() + " " + choosePath());
	}

	public void autonomousPeriodic() {
		if (runner == null) {
			System.out.println("Runner is null");
			return;
		}

		EncoderMonitor encoderMonitor = (EncoderMonitor) Global.controlObjects.get("ENCODERMONITOR");
		Map<String, Encoder> encoders = encoderMonitor.getEncoders();

		writer.print("Encoder0 Counts: " + encoders.get("ENCODER0").get() + "\t" + "Encoder0 Distance: "
				+ encoders.get("ENCODER0").getDistance() + "\t");
		writer.print("Encoder1 Counts: " + encoders.get("ENCODER1").get() + "\t" + "Encoder1 Distance: "
				+ encoders.get("ENCODER1").getDistance() + "\t");
		writer.print("Time: " + (System.currentTimeMillis() - timeCost) + "\t");
		writer.println("Command Number: " + runner.commandNum);

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

	// 2017-18 specific; construct the correct path from switchboard input, robot
	// field position, and switch/scale position enums
	public static String choosePath() {
		String path = "";

		MiyamotoControl switchboard = (MiyamotoControl) Global.controllers;
		path += switchboard.getStartPosition();

		if (path.equals("D")) {
			return path;
		}

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
