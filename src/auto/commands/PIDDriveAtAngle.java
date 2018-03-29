package auto.commands;

import java.io.IOException;
import java.util.List;

import com.kauailabs.navx.frc.AHRS;

import auto.ICommand;
import auto.IStopCondition;
import comms.SmartWriter;
import drive.DriveControl;
import drive.IDrive;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import input.DriveEncoders;
import input.SensorController;
import physicalOutput.motors.FakePIDMotor;
import robot.Global;
import robot.Robot;
import robotDefinitions.RobotDefinitionBase;

public class PIDDriveAtAngle implements ICommand {

	private IStopCondition stopCondition;
	private IDrive drive;
	private double angle;
	private double distance;
	private DriveEncoders driveEncoders;
	private PIDController controller;
	private AHRS navX;
	private double Kp;

	/**
	 * Drives straight at a specified angle using PID for distance and P for Angle
	 * 
	 * @param stop
	 *            The stop condition
	 * @param distanceInCounts
	 *            The distance to drive in encoder counts
	 * @param angle
	 *            The angle to drive at (1 degree tolerance)
	 * @param minOutput
	 *            The minimum motor power
	 * @param maxOutput
	 *            The maximum motor power
	 * @param percentTolerance
	 *            The percent tolerance
	 */
	public PIDDriveAtAngle(IStopCondition stop, List<Encoder> encoders, double distanceInCounts, double minOutput, double maxOutput,
			double percentTolerance, double angle, double Kp) {
		navX = (AHRS) SensorController.getInstance().getSensor("NAVX");
		stopCondition = stop;
		this.angle = angle;
		this.Kp = Kp;

		this.distance = distanceInCounts;

		driveEncoders = new DriveEncoders(encoders);
		PIDSource source = driveEncoders;
		PIDOutput output = new FakePIDMotor();
		controller = new PIDController(0.0, 0.0, 0.0, source, output, 0.02);
		controller.setOutputRange(minOutput, maxOutput);
		controller.setPercentTolerance(percentTolerance);

		try {
			loadPIDValues();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void init() {
		stopCondition.init();
		controller.reset();
		controller.setSetpoint(distance);
		controller.enable();
		drive = (IDrive) Global.controlObjects.get(RobotDefinitionBase.DRIVENAME);
		drive.setDriveControl(DriveControl.EXTERNAL_CONTROL);
		driveEncoders.reset();
	}

	public boolean run() {
		SmartWriter.putS("TargetAngle driveAtAngle ", getError() + ", NavXAngle: " + navX.getYaw());

		// Result from Distance PID Controller
		double baseSpeed = controller.get();

		// System.out.println("PID error: " + getError());
		// System.out.println("Base motor speed: " + slowSpeed);

		// Self-made Angle P Controller
		double Kp = this.Kp;
		double change = getError() * Kp;
		if (Math.abs(getError()) < 1) {
			drive.setLeftMotors(baseSpeed);
			drive.setRightMotors(baseSpeed);
		} else {
			drive.setLeftMotors(baseSpeed + change);
			drive.setRightMotors(baseSpeed - change);
			// System.out.println("PID offset: " + change);
		}
		return stopCondition.stopNow();
	}

	/**
	 * Returns the angle off the set point from -180 to 180
	 * 
	 * @return error
	 */
	public double getError() {
		return angle - navX.getYaw();
	}

	/**
	 * Returns the angle the robot is at
	 * 
	 * @return The yaw angle
	 */
	public double getAngle() {
		return navX.getYaw();
	}

	/**
	 * sets the new angle for the robot to drive to<br>
	 * must be -180 to 180 at this point
	 */
	public void setAngle(double angleIn) {
		angle = angleIn;
	}

	public void stop() {
		controller.reset();
		drive.setLeftMotors(0);
		drive.setRightMotors(0);
		drive.setDriveControl(DriveControl.DRIVE_CONTROLLED);
		SensorController sensorController = SensorController.getInstance();
		Encoder encoder0 = (Encoder) sensorController.getSensor("ENCODER0");
		Encoder encoder1 = (Encoder) sensorController.getSensor("ENCODER1");
		System.out.println("DriveAtAngle Command Finished" + "\n" + "Encoder0 Distance| Counts: " + encoder0.get()
				+ "\t" + "Inches: " + encoder0.getDistance() + "\n" + "Encoder1 Distance| Counts: " + encoder1.get()
				+ "\t" + "Inches: " + encoder1.getDistance() + "\n" + "Final Angle: " + navX.getYaw());
	}

	private void loadPIDValues() throws IOException {
		switch (Robot.name) {
		case BABBAGE:
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
}
