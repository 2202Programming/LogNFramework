package auto.commands;

import com.kauailabs.navx.frc.AHRS;
import auto.ICommand;
import auto.IStopCondition;
import comms.SmartWriter;
import drive.DriveControl;
import drive.IDrive;
import edu.wpi.first.wpilibj.PIDController;
import input.SensorController;
import physicalOutput.motors.FakePIDMotor;
import robot.Global;
import robotDefinitions.RobotDefinitionBase;

public class DriveAtAngle implements ICommand {

	private IStopCondition stopCondition;
	private IDrive drive;
	private double slowSpeed, fastSpeed;
	private double angle;
	private AHRS navX;
	private boolean usePID;

	/**
	 * Creates the command using pid controlled angle
	 * 
	 * @param stop
	 * @param speed
	 * @param angle
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
	 * Creates the command that waddles back and forth
	 * 
	 * @param stop
	 * @param slowSpeed
	 * @param fastSpeed
	 * @param angle
	 */
	public DriveAtAngle(IStopCondition stop, double slowSpeed, double fastSpeed, double angle) {
		usePID = false;
		stopCondition = stop;
		this.slowSpeed = slowSpeed;
		this.fastSpeed = fastSpeed;
		this.angle = angle;
	}

	public void setSpeed(double speed) {
		this.slowSpeed = speed;
	}

	public void setSpeed(double slowSpeed, double fastSpeed) {
		this.slowSpeed = slowSpeed;
		this.fastSpeed = fastSpeed;
	}

	public void init() {
		stopCondition.init();
		drive = (IDrive) Global.controlObjects.get(RobotDefinitionBase.DRIVENAME);
		drive.setDriveControl(DriveControl.EXTERNAL_CONTROL);
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

	private void withGyro() {
		double Kp = .012;
		double change = getError() * Kp;
		System.out.println("PID error: " + getError());
		System.out.println("Base motor speed: " + slowSpeed);
		if (Math.abs(getError()) < 1) {
			drive.setLeftMotors(slowSpeed);
			drive.setRightMotors(slowSpeed);
		} else {
			drive.setLeftMotors(slowSpeed + change);
			drive.setRightMotors(slowSpeed - change);
			System.out.println("PID offset: " + change);
		}
	}

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
	}
}
