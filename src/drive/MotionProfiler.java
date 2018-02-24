package drive;

import robot.Global;
import robot.IControl;
import robotDefinitions.RobotDefinitionBase;
import robotDefinitions.controls.ControlBase;
import robotDefinitions.controls.MotionProfileableController;

/**
 * Caps the max acceleration and velocity of the robot so that it can be maneuvered more easily
 * @author Daniel
 *
 */
public class MotionProfiler extends IControl {
	private MotionProfileableController controller;
	private MotionProfile[] profiles;
	private int index;
	private MotionProfileable drive;
	
	public MotionProfiler(IDrive drive){
		profiles = new MotionProfile[2];
		profiles[0] = new MotionProfile (2,1);
		index = 0;
		if(drive instanceof MotionProfileable){
			this.drive = (MotionProfileable)drive;			
		}
		if(Global.controllers instanceof MotionProfileableController){
			controller = (MotionProfileableController)Global.controllers;			
		}
	}
	
	public MotionProfiler(IDrive drive, MotionProfile[] profiles){
		this(drive);
		this.profiles = profiles;
	}
	
	public void teleopInit(){
		index = 0;
	}
	
	public void teleopPeriodic(){
		if(drive != null && controller != null){
			if(controller.cycleMotionProfile()){
				index++;
			}
			index%=profiles.length;
			drive.setMaxAcceleration(profiles[index].getAcceleration());
			drive.setMaxVelocity(profiles[index].getVelocity());			
		}
	}
}
