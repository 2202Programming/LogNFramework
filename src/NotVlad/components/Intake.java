package NotVlad.components;

import NotVlad.MiyamotoControl;
import edu.wpi.first.wpilibj.DigitalInput;
import input.SensorController;
import physicalOutput.motors.IMotor;
import robot.Global;
import robot.IControl;

public class Intake extends IControl {
	private IMotor intakeMotorLeft;
	private IMotor intakeMotorRight;
	private MiyamotoControl controller;
	private DigitalInput sensor;
	private int sensorCount;

	public Intake(IMotor intakeMotorLeft, IMotor intakeMotorRight) {
		this.intakeMotorLeft=intakeMotorLeft;
		this.intakeMotorRight=intakeMotorRight;
	}

	private void init() {
		intakeMotorLeft.set(0);
		intakeMotorRight.set(0);
		controller=(MiyamotoControl)(Global.controllers);
		sensor=(DigitalInput)SensorController.getInstance().getSensor("INTAKE");
		sensorCount = 0;
	}

	public void autonomousInit() {
		init();
	}

	public void teleopInit() {
		init();
	}

	public void intake() {
		intakeMotorLeft.set(0.6);
		intakeMotorRight.set(-0.6);
	}

	public void outtake() {
		intakeMotorLeft.set(-1);
		intakeMotorRight.set(1);
	}

	public void stop() {
		intakeMotorLeft.set(0);
		intakeMotorRight.set(0);
	}

	public void teleopPeriodic() {
		if(sensor.get()){
			sensorCount++;
		}else{
			sensorCount = 0;
		}
		
		if (controller.intake()) {
			if(sensorCount > 100){
				stop();
			}else{
				intake();				
			}
		}else if (controller.outtake()) {
			outtake();
		}
		else {
			stop();
		}
	}
}
