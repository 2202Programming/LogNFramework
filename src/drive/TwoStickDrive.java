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
public class TwoStickDrive extends IDrive implements Reversible, MotionProfileable{
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
	private double maxAcceleration;
	private double maxVelocity;

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
		maxAcceleration = 2;
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
	 * @param maxAcceleration
	 *            the max acceleration of the robot between 0 and 2
	 */
	public TwoStickDrive(IMotor rightMotors, IMotor leftMotors, double maxAcceleration, boolean invertSticks) {
		this(rightMotors, leftMotors);
		this.maxAcceleration = maxAcceleration;
		this.invertSticks = invertSticks;
	}

	public void setTurnExponent(int exp) {
		turnSmoothingExponent = exp;
	}

	public void invertJoysticks(boolean invert) {
		invertSticks = invert;
	}
	
	public void reverseDrive(boolean reverse){
		backwardsDrive = reverse;
	}
	
	@Override
	public void setMaxAcceleration(double maxAcceleration) {
		this.maxAcceleration = maxAcceleration;
	}
	
	@Override
	public void setMaxVelocity(double maxVelocity) {
		this.maxVelocity = maxVelocity;
	}

	@Override
	protected void teleopUpdate() {
		MotorPowers forward = setForwardSpeed();
		MotorPowers turn = setTurnAmount();
		System.out.println(turn.leftPower + " " + turn.rightPower);
		leftPower = forward.leftPower + turn.leftPower;
		rightPower = forward.rightPower + turn.rightPower;
	}

	private MotorPowers setForwardSpeed() {
		double forwardStick;
		if (invertSticks) {
			forwardStick = controller.getRightJoystickY();
		} else {
			forwardStick = controller.getLeftJoystickY();
		}
		MotorPowers toReturn = new MotorPowers();
		if (Math.abs(forwardStick - lastForward) > maxAcceleration) {
			forwardStick = lastForward + Math.signum(forwardStick - lastForward) * maxAcceleration;
		}
		lastForward = forwardStick;
		toReturn.leftPower = forwardStick;
		toReturn.rightPower = forwardStick;
		if(backwardsDrive){
			toReturn.leftPower = -toReturn.leftPower;
			toReturn.rightPower = -toReturn.rightPower;
		}
		
		if(Math.abs(toReturn.leftPower) > maxVelocity){
			toReturn.leftPower = Math.signum(toReturn.leftPower) * maxVelocity;
		}
		if(Math.abs(toReturn.rightPower) > maxVelocity){
			toReturn.rightPower = Math.signum(toReturn.rightPower) * maxVelocity;
		}
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
//		if (Math.abs(leftPower) > maxVelocity) {
//			leftPower = Math.signum(leftPower) * maxVelocity;
//		}
//		if (Math.abs(rightPower) > maxVelocity) {
//			rightPower = Math.signum(rightPower) * maxVelocity;
//		}
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
		if (super.driveControl == DriveControl.EXTERNAL_CONTROL) {
			leftMotors.set(power);
		}
	}

	@Override
	public void setRightMotors(double power) {
		if (super.driveControl == DriveControl.EXTERNAL_CONTROL) {
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
