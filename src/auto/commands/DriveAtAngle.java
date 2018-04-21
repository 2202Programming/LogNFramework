package auto.commands;

import com.kauailabs.navx.frc.AHRS;

import auto.ICommand;
import auto.IStopCondition;
import comms.SmartWriter;
import drive.DriveControl;
import drive.IDrive;
import drive.MotionProfiler;
import edu.wpi.first.wpilibj.Encoder;
import input.SensorController;
import robot.Global;
import robotDefinitions.RobotDefinitionBase;

public class DriveAtAngle implements ICommand {

	private IStopCondition stopCondition;
	private IDrive drive;
	private double slowSpeed, fastSpeed;
	private double angle;
	private AHRS navX;
	private boolean usePID;
	private double lastMotorPower;
	private MotionProfiler powerRamp;

	/**
	 * Drives straight at a specified angle using PID (proportions)
	 * 
	 * @param stop
	 *            The stop condition
	 * @param speed
	 *            The speed at which to drive
	 * @param angle
	 *            The maintained angel
	 */
	public DriveAtAngle(IStopCondition stop, double speed, double angle) {
		usePID = true;
		// these will most likely be small as the value needs to be under 1.0/
		// -1.0
		navX = (AHRS) SensorController.getInstance().getSensor("NAVX");
		stopCondition = stop;
		this.angle = angle;
		slowSpeed = speed;
	}

	/**
	 * Drives straight at a specified angle using two speeds
	 * 
	 * @param stop
	 *            The stop condition
	 * @param slowSpeed
	 *            The slow driving speed
	 * @param fastSpeed
	 *            The fast driving speed
	 * @param angle
	 *            The maintained angle
	 */
	public DriveAtAngle(IStopCondition stop, double slowSpeed, double fastSpeed, double angle) {
		usePID = false;
		stopCondition = stop;
		this.slowSpeed = slowSpeed;
		this.fastSpeed = fastSpeed;
		this.angle = angle;
	}

	/**
	 * Sets the main speed for PID
	 * 
	 * @param speed
	 */
	public void setSpeed(double speed) {
		this.slowSpeed = speed;
	}

	/**
	 * Sets the slow and fast speed for the alternate method of driving
	 * 
	 * @param slowSpeed
	 * @param fastSpeed
	 */
	public void setSpeed(double slowSpeed, double fastSpeed) {
		this.slowSpeed = slowSpeed;
		this.fastSpeed = fastSpeed;
	}

	public void init() {
		stopCondition.init();
		drive = (IDrive) Global.controlObjects.get(RobotDefinitionBase.DRIVENAME);
		drive.setDriveControl(DriveControl.EXTERNAL_CONTROL);
		lastMotorPower = 0;
		powerRamp = (MotionProfiler) Global.controlObjects.get("PROFILER");
	}

	public boolean run() {
		SmartWriter.putS("TargetAngle driveAtAngle ", getError() + ", NavXAngle: " + navX.getYaw());
		if (usePID) {
			withGyro();
		} else {
			nonGyro();
		}

		return stopCondition.stopNow();
	}

	/**
	 * Sets the motor speeds based on a proportion PID
	 */
	private void withGyro() {
		double Kp = .012;
		double change = getError() * Kp;
		// System.out.println("PID error: " + getError());
		// System.out.println("Base motor speed: " + slowSpeed);
		double baseSpeed = powerRamp.capAcceleration(lastMotorPower, slowSpeed);
		lastMotorPower = baseSpeed;
		if (Math.abs(getError()) < 1) {
			drive.setLeftMotors(baseSpeed);
			drive.setRightMotors(baseSpeed);
		} else {
			drive.setLeftMotors(baseSpeed + change);
			drive.setRightMotors(baseSpeed - change);
			// System.out.println("PID offset: " + change);
		}
	}

	/**
	 * Set's the motor speeds based on the slow and fast speed
	 */
	private void nonGyro() {
		if (getError() > 0) {
			drive.setLeftMotors(slowSpeed);
			drive.setRightMotors(fastSpeed);
		} else {
			drive.setLeftMotors(fastSpeed);
			drive.setRightMotors(slowSpeed);
		}
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
		drive.setLeftMotors(0);
		drive.setRightMotors(0);
		drive.setDriveControl(DriveControl.DRIVE_CONTROLLED);
		SensorController sensorController = SensorController.getInstance();
		Encoder encoder0 = (Encoder) sensorController.getSensor("ENCODER0");
		Encoder encoder1 = (Encoder) sensorController.getSensor("ENCODER1");
		System.out.println("DriveAtAngle Command Finished" + "\n" + "Encoder0 Distance| Counts: " + encoder0.get()
				+ "\t" + "Inches: " + encoder0.getDistance() + "\n" + "Encoder1 Distance| Counts: " + encoder1.get()
				+ "\t" + "Inches: " + encoder1.getDistance());
	}
	
	public String toString() {
		return "DriveAtAngle";
	}
}
