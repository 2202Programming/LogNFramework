package drive;

import physicalOutput.motors.IMotor;
import robot.Global;
import robotDefinitions.controls.ControlBase;

/**
 * A class that uses one joystick as forward/backwards movement and another for
 * turning.
 * 
 * @author Daniel
 *
 */
public class TwoStickDrive extends IDrive implements Reversible{
	private IMotor leftMotors;
	private IMotor rightMotors;
	private ControlBase controller;

	private double leftPower;
	private double rightPower;
	// Smooths the turning of the robot by applying an exponential curve to the
	// joystick input. Very useful to make turning easier while driving fast.
	private int turnSmoothingExponent;
	private boolean invertSticks;
	private double lastForward;
	private boolean backwardsDrive;
	private MotionProfiler powerControl;

	/**
	 * A class that uses one joystick as forward/backwards movement and another for
	 * turning.
	 * 
	 * @param rightMotors
	 *            all of the right motors
	 * @param leftMotors
	 *            all of the left motors
	 */
	public TwoStickDrive(IMotor rightMotors, IMotor leftMotors) {
		controller = Global.controllers;
		this.leftMotors = leftMotors;
		this.rightMotors = rightMotors;
		turnSmoothingExponent = 1;
		invertSticks = false;
		lastForward = 0;
		backwardsDrive = false;
	}

	/**
	 * A class that uses one joystick as forward/backwards movement and another for
	 * turning.
	 * 
	 * @param rightMotors
	 *            all of the right motors
	 * @param leftMotors
	 *            all of the left motors
	 * @param turnSmootingExponent
	 *            the exponential smoothing amount of turning
	 */
	public TwoStickDrive(IMotor rightMotors, IMotor leftMotors, int turnSmoothingExponent, boolean invertSticks) {
		this(rightMotors, leftMotors);
		this.turnSmoothingExponent = turnSmoothingExponent;
		this.invertSticks = invertSticks;
	}
	
	public void robotInit() {
		powerControl = (MotionProfiler) Global.controlObjects.get("PROFILER");
	}

	public void setTurnExponent(int exp) {
		turnSmoothingExponent = exp;
	}

	public void invertJoysticks(boolean invert) {
		invertSticks = invert;
	}

	public void reverseDrive(boolean reverse) {
		backwardsDrive = reverse;
	}

	@Override
	protected void teleopUpdate() {
		if(driveControl == DriveControl.DRIVE_CONTROLLED){
			MotorPowers forward = setForwardSpeed();
			MotorPowers turn = setTurnAmount();
			leftPower = forward.leftPower + turn.leftPower;
			rightPower = forward.rightPower + turn.rightPower;
		}
	}

	private MotorPowers setForwardSpeed() {
		double forwardStick;
		if (invertSticks) {
			forwardStick = controller.getRightJoystickY();
		} else {
			forwardStick = controller.getLeftJoystickY();
		}
		MotorPowers toReturn = new MotorPowers();
		forwardStick = powerControl.capAcceleration(lastForward, forwardStick);
		lastForward = forwardStick;
		toReturn.leftPower = forwardStick;
		toReturn.rightPower = forwardStick;
		if (backwardsDrive) {
			toReturn.leftPower = -toReturn.leftPower;
			toReturn.rightPower = -toReturn.rightPower;
		}
		
		leftPower = powerControl.capVelocity(leftPower);
		rightPower = powerControl.capVelocity(rightPower);
		return toReturn;
	}

	private MotorPowers setTurnAmount() {
		double turnStick;
		if (invertSticks) {
			turnStick = controller.getLeftJoystickX();
		} else {
			turnStick = controller.getRightJoystickX();
		}
		turnStick = turnStick * Math.abs(Math.pow(turnStick, turnSmoothingExponent - 1));
		MotorPowers toReturn = new MotorPowers();
		toReturn.leftPower = turnStick;
		toReturn.rightPower = -turnStick;
		return toReturn;
	}

	@Override
	protected void setMotors() {
		// if (Math.abs(leftPower) > maxVelocity) {
		// leftPower = Math.signum(leftPower) * maxVelocity;
		// }
		// if (Math.abs(rightPower) > maxVelocity) {
		// rightPower = Math.signum(rightPower) * maxVelocity;
		// }
		rightMotors.set(rightPower);
		leftMotors.set(leftPower);
	}

	@Override
	protected void disableMotors() {
		leftMotors.set(0);
		rightMotors.set(0);
	}

	@Override
	public boolean hasEncoders() {
		return false;
	}

	@Override
	public void setLeftMotors(double power) {
		if(super.driveControl == DriveControl.EXTERNAL_CONTROL){
			leftMotors.set(power);
		}
	}

	@Override
	public void setRightMotors(double power) {
		if(super.driveControl == DriveControl.EXTERNAL_CONTROL){
			rightMotors.set(power);
		}
	}

	@Override
	/**
	 * Returns the motor speed/power of the left motors
	 */
	public double getLeftMotorsSpeed() {
		return leftMotors.getSpeed();
	}

	@Override
	/**
	 * Returns the motor speed/power of the right motors
	 */
	public double getRightMotorsSpeed() {
		return rightMotors.getSpeed();
	}
}

class MotorPowers {
	public double leftPower;
	public double rightPower;

	public MotorPowers() {
		leftPower = 0;
		rightPower = 0;
	}
}
