package auto.commands;

import auto.ICommand;
import comms.SmartWriter;
import drive.DriveControl;
import drive.IDrive;
import robot.Global;
import robotDefinitions.RobotDefinitionBase;

@Deprecated
public class DecelCommand implements ICommand {

	private IDrive drive;
	private double maxAcceleration;
	private double prevSpeed;

	/**
	 * Gradually slows down at a constant acceleration of motor power;
	 * Precondition: The left and right motors speed are equal and nonzero
	 * Postcondition: The left and right motor speeds are zero
	 * 
	 * @param stop
	 *            The stop condition
	 * @param maxAcceleration
	 *            The acceleration of motor power
	 */
	public DecelCommand(double maxAcceleration) {
		this.maxAcceleration = maxAcceleration;
		prevSpeed = 1.0;
	}

	public void init() {
		drive = (IDrive) Global.controlObjects.get(RobotDefinitionBase.DRIVENAME);
		drive.setDriveControl(DriveControl.EXTERNAL_CONTROL);
		prevSpeed = drive.getLeftMotorsSpeed();
		SmartWriter.putD("Starting Decel Speed", prevSpeed);
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