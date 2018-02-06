package drive;

import robot.Global;
import robot.IControl;
import robotDefinitions.ControlBase;
import robotDefinitions.RobotDefinitionBase;

public class SneakMode extends IControl {
	private ControlBase controller;
	private double[] accelerationValues;
	private int index;
	private IDrive drive;
	
	public SneakMode(){
		controller = Global.controllers;
		accelerationValues = new double[2];
		accelerationValues[0] = 2;
		accelerationValues[1] = 0.2;
		index = 0;
		drive = (IDrive)Global.controlObjects.get(RobotDefinitionBase.DRIVENAME);
	}
	
	public void teleopPeriodic(){
		if(controller.sneakMode()){
			index++;
		}
		index%=accelerationValues.length;
		drive.setMaxAcceleration(accelerationValues[index]);
	}
}
