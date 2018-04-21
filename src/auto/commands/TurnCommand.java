package auto.commands;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.kauailabs.navx.frc.AHRS;

import auto.ICommand;
import auto.IStopCondition;
import auto.stopConditions.AngleStopCondition;
import comms.SmartWriter;
import drive.DriveControl;
import drive.IDrive;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import input.SensorController;
import physicalOutput.TurnController;
import robot.Global;
import robot.Robot;
import robotDefinitions.RobotDefinitionBase;

public class TurnCommand implements ICommand {
	private double degreesToTurn;
	private IStopCondition stopCondition;
	private PIDController controller;
	private PIDOutput output;
	private PIDSource source;
	private IDrive drive;

	/**
	 * Turns to an angle
	 * 
	 * @param degreesToTurn
	 *            The angle to turn to where positive is clockwise
	 */
	public TurnCommand(double degreesToTurn) {
		this(new AngleStopCondition(degreesToTurn, 2, 0.3), degreesToTurn, -180.0, 180.0, -1.0, 1.0, 1.0);
	}

	/**
	 * Turns to an angle
	 * 
	 * @param degreesToTurn
	 *            The angle to turn to where positive is clockwise
	 * @param maxError
	 *            The range of error in degrees for which the bot is considered at
	 *            the correct target angle
	 * @param timeInRange
	 *            The time, in seconds, that the bot has to be facing at the correct
	 *            angle to be considered on target
	 */
	public TurnCommand(double degreesToTurn, double maxError, double timeInRange) {
		this(new AngleStopCondition(degreesToTurn, maxError, timeInRange), degreesToTurn, -180.0, 180.0, -1.0, 1.0,
				maxError / 180.0);
	}

	/**
	 * Turns to an angle
	 * 
	 * @param turnDegrees
	 *            The angle to turn to where positive is clockwise
	 * @param minInput
	 *            The minimum angle
	 * @param maxInput
	 *            The maximum angle
	 * @param minOutput
	 *            The minimum motor power
	 * @param maxOutput
	 *            The maximum motor power
	 * @param percentTolerance
	 *            The range of error in percentage for which the bot is considered
	 *            at the correct target angle
	 */

	public TurnCommand(double degreesToTurn, double maxError, double timeInRange, double minInput, double maxInput,
			double minOutput, double maxOutput, double percentTolerance) {
		this(new AngleStopCondition(degreesToTurn, maxError, timeInRange), degreesToTurn, minInput, maxInput, minOutput,
				maxOutput, percentTolerance);
	}

	/**
	 * Turns to an angle
	 * 
	 * @param stop
	 *            A specified stop condition
	 * @param degreesToTurn
	 *            The angle to turn to where positive is clockwise
	 */
	public TurnCommand(IStopCondition stop, double degreesToTurn) {
		this(stop, degreesToTurn, -180.0, 180.0, -1.0, 1.0, 1.0);
	}

	/**
	 * Turns to an angle
	 * 
	 * @param stop
	 *            A specified stop condition
	 * @param turnDegrees
	 *            The angle to turn to where positive is clockwise
	 * @param minInput
	 *            The minimum angle
	 * @param maxInput
	 *            The maximum angle
	 * @param minOutput
	 *            The minimum motor power
	 * @param maxOutput
	 *            The maximum motor power
	 * @param percentTolerance
	 *            The range of error in percentage for which the bot is considered
	 *            at the correct target angle
	 */
	public TurnCommand(IStopCondition stop, double turnDegrees, double minInput, double maxInput, double minOutput,
			double maxOutput, double percentTolerance) {
		stopCondition = stop;
		degreesToTurn = turnDegrees;
		output = (TurnController) Global.controlObjects.get("TURNCONTROLLER");
		source = (AHRS) SensorController.getInstance().getSensor("NAVX");
		controller = new PIDController(0.0, 0.0, 0.0, source, output, 0.02);
		controller.setInputRange(minInput, maxInput);
		controller.setOutputRange(minOutput, maxOutput);
		controller.setPercentTolerance(percentTolerance);
		controller.setContinuous(true);
		try {
			loadPIDValues();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void init() {
		controller.reset();
		controller.setSetpoint(degreesToTurn);
		controller.enable();
		drive = (IDrive) Global.controlObjects.get(RobotDefinitionBase.DRIVENAME);
		stopCondition.init();
		drive.setDriveControl(DriveControl.EXTERNAL_CONTROL);
	}

	public boolean run() {
		// System.out.println("End Point: " + controller.getSetpoint());
		// System.out.println("Motor Power: " + controller.get());
		// System.out.println("Error: " + controller.getError());

		boolean stopNow = stopCondition.stopNow();
		SmartWriter.putB("hghjkhjghg", stopNow);
		return stopNow;
	}

	public void stop() {
		controller.reset();
		AHRS navX = (AHRS) (SensorController.getInstance().getSensor("NAVX"));
		System.out.println("Turn Command Finished at: " + navX.getYaw());
		drive.setLeftMotors(0);
		drive.setRightMotors(0);
		drive.setDriveControl(DriveControl.DRIVE_CONTROLLED);
	}

	private void loadPIDValues() throws IOException {
		switch (Robot.name) {
		case BABBAGE:
			controller.setPID(0.006, 0.0002, .15);
			break;
		case NOTVLAD:
			controller.setPID(0.1185 * .60, 0.0, 0.15);
			// new PIDValues(0.02, 0.0006, 0.15);
			// (0.03, 0.0, 0.0) works for Piper
			// Ku = .1887 and Tu = .6465 for PID tuning
			break;
		case TIM:
			// TODO setPIDVALUES
			break;
		case MIYAMOTO:
			// Old Pid was (.045, 0.0, 0.1)
			// With FRC PID values are (.055, 0.0, 0.5);
			// BufferedReader in = new BufferedReader(new
			// FileReader("/home/lvuser/MiyamotoPIDValues.txt"));
			// Double Kp = Double.parseDouble(in.readLine());
			// Double Ki = Double.parseDouble(in.readLine());
			// Double Kd = Double.parseDouble(in.readLine());
			//
			// in.close();
			controller.setPID(.055, 0.0, .5);
			break;
		case UNKNOWN:
			// TODO setPIDVALUES
			break;
		case MECHANUMDRIVE:
			// TODO setPIDVALUES
			break;
		default:
			break;
		}
	}
	
	public String toString() {
		return "TurnCommand";
	}
}
