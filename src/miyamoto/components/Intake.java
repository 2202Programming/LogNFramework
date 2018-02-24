package miyamoto.components;

import edu.wpi.first.wpilibj.DigitalInput;
import input.SensorController;
import miyamoto.MiyamotoControl;
import physicalOutput.motors.IMotor;
import robot.Global;
import robot.IControl;

public class Intake extends IControl {
	private IMotor intakeMotorLeft;
	private IMotor intakeMotorRight;
	private MiyamotoControl controller;
	private DigitalInput sensor;
	private int sensorCount;

	/**
	 * Self-evident intake class
	 * @param intakeMotorLeft 
	 * @param intakeMotorRight
	 */
	public Intake(IMotor intakeMotorLeft, IMotor intakeMotorRight) {
		this.intakeMotorLeft = intakeMotorLeft;
		this.intakeMotorRight = intakeMotorRight;
	}

	private void init() {
		intakeMotorLeft.set(0);
		intakeMotorRight.set(0);
		controller = (MiyamotoControl) (Global.controllers);
		sensor = (DigitalInput) SensorController.getInstance().getSensor("INTAKE");
		sensorCount = 0;
	}

	public void autonomousInit() {
		init();
	}

	public void teleopInit() {
		init();
	}
	
	public void runIntake(double speed){
		intakeMotorLeft.set(speed);
		intakeMotorRight.set(-speed);
	}

	public void intake() {
		intakeMotorLeft.set(0.6);
		intakeMotorRight.set(-0.6);
	}

	public void outtake() {
		intakeMotorLeft.set(-0.6);
		intakeMotorRight.set(0.6);
	}

	public void rotate() {
		intakeMotorLeft.set(-0.3);
		intakeMotorRight.set(-0.3);
	}

	public void stop() {
		intakeMotorLeft.set(0);
		intakeMotorRight.set(0);
	}

	public void teleopPeriodic() {
		if (sensor.get()) {
			sensorCount++;
		} else {
			sensorCount = 0;
		}

		if (controller.overrideIntake()) {
			intake();
		} else {
			if (controller.intake()) {
				if (sensorCount > 100) {
					stop();
				} else {
					sensorCount = 0;
					intake();
				}
			} else if (controller.outtake()) {
				outtake();
			} else if (controller.rotateIntake()) {
				rotate();
			}else {
				stop();
			}
		}
	}
}