package auto.commands;

import auto.ICommand;
import auto.IStopCondition;
import drive.DriveControl;
import drive.IDrive;
import robot.Global;
import robotDefinitions.RobotDefinitionBase;
@Deprecated
public class SneakDriveCommand implements ICommand {

	private IStopCondition stopCondition;
	private IDrive drive;
	private double maxAcceleration;
	private double prevSpeed;

	/**
	 * Gradually speeds up at a constant acceleration of motor power; <br>
	 * WARNING: DOES NOT STOP! ALWAYS PAIR THIS WITH A DECELCOMMAND <br>
	 * Precondition: The left and right motor speeds are equal
	 * Postcondition: The left and right motors speeds are nonzero.
	 * 
	 * @param stop
	 *            The stop condition
	 * @param maxAcceleration
	 *            The acceleration of motor power
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
		prevSpeed = drive.getLeftMotorsSpeed();
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
		drive.setDriveControl(DriveControl.DRIVE_CONTROLLED);
	}
}