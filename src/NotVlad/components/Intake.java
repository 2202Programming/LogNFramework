package NotVlad.components;

import NotVlad.MiyamotoControl;
import physicalOutput.motors.IMotor;
import robot.Global;
import robot.IControl;

public class Intake extends IControl {
	private IMotor intakeMotorLeft;
	private IMotor intakeMotorRight;
	private MiyamotoControl controller;
	
	public Intake(IMotor intakeMotorLeft, IMotor intakeMotorRight){
		this.intakeMotorLeft=intakeMotorLeft;
		this.intakeMotorRight=intakeMotorRight;
	}
	private void init(){
		intakeMotorLeft.set(0);
		intakeMotorRight.set(0);
		controller=(MiyamotoControl)(Global.controllers);
	}
	public void autonomousInit(){
		init();
	}
	public void teleopInit(){
		init();
	}
	public void intake(){
		if(controller.intake()){
			intakeMotorLeft.set(0.8);
			intakeMotorRight.set(-0.8);
		}
		else{
			intakeMotorLeft.set(0);
			intakeMotorRight.set(0);
		}
	}
	public void outtake(){
		if(controller.outtake()){
			intakeMotorLeft.set(-0.8);
			intakeMotorRight.set(0.8);
		}
		else{
			intakeMotorLeft.set(0);
			intakeMotorRight.set(0);
		}
	}
	public void teleopPeriodic() {
		if(controller.intake()){
			intakeMotorLeft.set(0.6);
			intakeMotorRight.set(-0.6);
		}else if(controller.outtake()){
			intakeMotorLeft.set(-0.6);
			intakeMotorRight.set(0.6);
		}else{
			intakeMotorLeft.set(0);
			intakeMotorRight.set(0);
		}
	}
	
}
