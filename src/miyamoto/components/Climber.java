package miyamoto.components;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import miyamoto.MiyamotoControl;
import physicalOutput.motors.IMotor;
import robot.Global;
import robot.IControl;

public class Climber extends IControl {
	private IMotor climbMotor;
	private MiyamotoControl controller;
	private PowerDistributionPanel pdp;
	
	public Climber(IMotor climbMotor) {
		this.climbMotor=climbMotor;
		//pdp = new PowerDistributionPanel();
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
		else if(controller.climbReverse()){
			//if(pdp.getCurrent(2) < 2){
				climbMotor.set(-0.5);				
			//}
		}
		else{
			climbMotor.set(0);
		}
	}
}
