package NotVlad.components;

import NotVlad.MiyamotoControl;
import physicalOutput.motors.IMotor;
import robot.Global;
import robot.IControl;

public class Intake extends IControl {
	private IMotor intakeMotorLeft;
	private IMotor intakeMotorRight;
	private MiyamotoControl controller;

	public Intake(IMotor intakeMotorLeft, IMotor intakeMotorRight) {
		this.intakeMotorLeft = intakeMotorLeft;
		this.intakeMotorRight = intakeMotorRight;
	}

	private void init() {
		intakeMotorLeft.set(0);
		intakeMotorRight.set(0);
		controller = (MiyamotoControl) (Global.controllers);
	}

	public void autonomousInit() {
		init();
	}

	public void teleopInit() {
		init();
	}

	public void intake() {
		intakeMotorLeft.set(0.2);
		intakeMotorRight.set(-0.2);
	}

	public void outtake() {
		intakeMotorLeft.set(-0.2);
		intakeMotorRight.set(0.2);
	}

	public void stop() {
		intakeMotorLeft.set(0);
		intakeMotorRight.set(0);
	}

	public void teleopPeriodic() {
		if (controller.intake()) {
			intake();
		} else if (controller.outtake()) {
			outtake();
		} else {
			stop();
		}
	}
}
