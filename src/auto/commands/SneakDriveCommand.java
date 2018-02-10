package auto.commands;

import auto.ICommand;
import auto.IStopCondition;
import drive.DriveControl;
import drive.IDrive;
import robot.Global;
import robotDefinitions.RobotDefinitionBase;

public class SneakDriveCommand implements ICommand {

	private IStopCondition stopCondition;
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
	public SneakDriveCommand(IStopCondition stop, double maxAcceleration) {
		stopCondition = stop;
		this.maxAcceleration = maxAcceleration;
		prevSpeed = 0.0;
	}

	public void init() {
		stopCondition.init();
		drive = (IDrive) Global.controlObjects.get(RobotDefinitionBase.DRIVENAME);
		drive.setDriveControl(DriveControl.EXTERNAL_CONTROL);
	}

	public boolean run() {
		if (drive == null) {
			init();
		}
		setDriveSpeed();
		return stopCondition.stopNow();
	}

	private void setDriveSpeed() {
		double speed = 1.0;
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