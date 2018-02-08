package drive;

import robot.Global;
import robot.IControl;
import robotDefinitions.ControlBase;
import robotDefinitions.RobotDefinitionBase;

/**
 * Caps the max acceleration of the robot so that it can be maneuvered more easily
 * @author Daniel
 *
 */
public class SneakMode extends IControl {
	private ControlBase controller;
	private MotionProfile[] profiles;
	private int index;
	private IDrive drive;
	
	public SneakMode(IDrive drive){
		controller = Global.controllers;
		profiles = new MotionProfile[2];
		profiles[0] = new MotionProfile (0.05,1);
		profiles[1] = new MotionProfile (0.05,0.5);
		index = 0;
		this.drive = drive;
	}
	
	public SneakMode(IDrive drive, MotionProfile[] profiles){
		this(drive);
		this.profiles = profiles;
	}
	
	public void teleopPeriodic(){
		if(controller.sneakMode()){
			index++;
		}
		index%=profiles.length;
		drive.setMaxAcceleration(profiles[index].getAcceleration());
		drive.setMaxVelocity(profiles[index].getVelocity());
	}
}
