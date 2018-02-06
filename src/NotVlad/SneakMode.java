package NotVlad;

import drive.TwoStickDrive;
import robot.Global;
import robot.IControl;
import robotDefinitions.RobotDefinitionBase;

public class SneakMode extends IControl {
	private MiyamotoControl controller;
	private double[] accelerationValues;
	private int index;
	private TwoStickDrive drive;
	
	public SneakMode(){
		controller = (MiyamotoControl)Global.controllers;
		accelerationValues = new double[2];
		accelerationValues[0] = 2;
		accelerationValues[1] = 0.2;
		index = 0;
		drive = (TwoStickDrive)Global.controlObjects.get(RobotDefinitionBase.DRIVENAME);
	}
	
	public void teleopPeriodic(){
		if(controller.sneakMode()){
			index++;
		}
		index%=accelerationValues.length;
		drive.setMaxAcceleration(accelerationValues[index]);
	}
}
