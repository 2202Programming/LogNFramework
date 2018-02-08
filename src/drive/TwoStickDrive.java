package drive;

import physicalOutput.motors.IMotor;
import robot.Global;
import robotDefinitions.ControlBase;

/**
 * A class that uses one joystick as forward/backwards movement and another for turning.
 * @author Daniel
 *
 */
public class TwoStickDrive extends IDrive {
	private IMotor leftMotors;
	private IMotor rightMotors;
	private ControlBase controller;
	
	private double leftPower;
	private double rightPower;
	//Smooths the turning of the robot by applying an exponential curve to the joystick input. Very useful to make turning easier while driving fast.
	private int turnSmoothingExponent;
	private boolean invertSticks;
	private double lastForward;
	
	/**
	 * A class that uses one joystick as forward/backwards movement and another for turning.
	 * @param rightMotors all of the right motors
	 * @param leftMotors all of the left motors
	 */
	public TwoStickDrive(IMotor rightMotors, IMotor leftMotors) {
		controller = Global.controllers;
		this.leftMotors = leftMotors;
		this.rightMotors = rightMotors;
		maxAcceleration = 2;
		turnSmoothingExponent = 1;
		invertSticks = false;
		lastForward = 0;
	}
	
	/**
	 * A class that uses one joystick as forward/backwards movement and another for turning.
	 * @param rightMotors all of the right motors
	 * @param leftMotors all of the left motors
	 * @param maxAcceleration the max acceleration of the robot between 0 and 2
	 */
	public TwoStickDrive(IMotor rightMotors, IMotor leftMotors, double maxAcceleration, boolean invertSticks){
		this(rightMotors,leftMotors);
		this.maxAcceleration = maxAcceleration;
		this.invertSticks = invertSticks;
	}
	
	public void setTurnExponent(int exp){
		turnSmoothingExponent = exp;
	}
	
	public void invertJoysticks(boolean invert){
		invertSticks = invert;
	}

	@Override
	protected void teleopUpdate() {
		MotorPowers forward = setForwardSpeed();
		MotorPowers turn = setTurnAmount();
		leftPower = forward.leftPower+turn.leftPower;
		rightPower = forward.rightPower+turn.rightPower;
	}
	
	private MotorPowers setForwardSpeed(){
		double forwardStick;
		if(invertSticks){
			forwardStick = controller.getRightJoystickY();
		}else{
			forwardStick = controller.getLeftJoystickY();
		}
		MotorPowers toReturn = new MotorPowers();
		if(Math.abs(forwardStick-lastForward) > maxAcceleration){
			forwardStick = lastForward+Math.signum(forwardStick-lastForward)*maxAcceleration;
		}
		lastForward = forwardStick;
		toReturn.leftPower = forwardStick;
		toReturn.rightPower = forwardStick;
		return toReturn;
	}
	
	private MotorPowers setTurnAmount(){
		double turnStick;
		if(invertSticks){
			turnStick = controller.getLeftJoystickX();
		}else{
			turnStick = controller.getRightJoystickX();
		}
		turnStick = turnStick*Math.abs(Math.pow(turnStick, turnSmoothingExponent-1));
		MotorPowers toReturn = new MotorPowers();
		toReturn.leftPower = turnStick;
		toReturn.rightPower = -turnStick;
		return toReturn;
	}

	@Override
	protected void setMotors() {
		leftMotors.set(leftPower);
		rightMotors.set(rightPower);
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

}

class MotorPowers{
	public double leftPower;
	public double rightPower;
	
	public MotorPowers(){
		leftPower = 0;
		rightPower = 0;
	}
}
