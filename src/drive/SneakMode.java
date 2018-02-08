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
	private double[] accelerationValues;
	private int index;
	private IDrive drive;
	
	public SneakMode(IDrive drive){
		controller = Global.controllers;
		accelerationValues = new double[2];
		accelerationValues[0] = 0.05;
		accelerationValues[1] = 2;
		index = 0;
		this.drive = drive;
	}
	
	public SneakMode(IDrive drive, double[] accelerationValues){
		this(drive);
		this.accelerationValues = accelerationValues;
	}
	
	public void teleopPeriodic(){
		if(controller.sneakMode()){
			index++;
		}
		index%=accelerationValues.length;
		drive.setMaxAcceleration(accelerationValues[index]);
	}
}
