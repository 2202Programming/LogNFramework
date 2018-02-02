package NotVlad.components;

import NotVlad.MiyamotoControl;
import physicalOutput.motors.IMotor;
import robot.Global;
import robot.IControl;

public class Climber extends IControl {
	private IMotor climbMotor;
	private MiyamotoControl controller;
	public Climber(IMotor climbMotor) {
		this.climbMotor=climbMotor;
	}
	private void init(){
		climbMotor.set(0);
		controller=(MiyamotoControl)(Global.controllers);
	}
	public void autonomousInit(){
		init();
	}
	public void teleopInit(){
		init();
	}
	public void teleopPeriodic(){
		if(controller.climbFast()){
			climbMotor.set(1);
		}
		else if(controller.climbSlow()){
			climbMotor.set(0.5);
		}
		else{
			climbMotor.set(0);
		}
	}
}
