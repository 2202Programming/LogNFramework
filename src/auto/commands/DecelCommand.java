package auto.commands;

import auto.ICommand;
import auto.IStopCondition;
import drive.DriveControl;
import drive.IDrive;
import robot.Global;
import robotDefinitions.RobotDefinitionBase;

public class DecelCommand implements ICommand {

	private IDrive drive;
	private double maxAcceleration;
	private double prevSpeed;

	/**
	 * Drives at the constant speed for a given number of seconds at a given motor
	 * power
	 * 
	 * @param secondsToDrive
	 *            The number of seconds to drive
	 * @param speed
	 *            The speed to drive at, between 0 and 1
	 */
	public DecelCommand(double startPower, double maxAcceleration) {
		this.maxAcceleration = maxAcceleration;
		prevSpeed = startPower;
	}

	public void init() {
		drive = (IDrive) Global.controlObjects.get(RobotDefinitionBase.DRIVENAME);
		drive.setDriveControl(DriveControl.EXTERNAL_CONTROL);
	}

	public boolean run() {
		if (drive == null) {
			init();
		}
		setDriveSpeed();
		return (prevSpeed <= 1e-7);
	}

	private void setDriveSpeed() {
		double speed = 0.0;
		if (Math.abs(speed - prevSpeed) > maxAcceleration) {
			speed = prevSpeed + Math.signum(speed - prevSpeed) * maxAcceleration;
		}
		prevSpeed = speed;

		drive.setLeftMotors(speed);
		drive.setRightMotors(speed);
	}

	public void stop() {
		drive.setLeftMotors(0);
		drive.setRightMotors(0);
		drive.setDriveControl(DriveControl.DRIVE_CONTROLLED);
	}
}