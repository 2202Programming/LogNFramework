package NotVlad.components;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import NotVlad.MiyamotoControl;
import comms.SmartWriter;
import physicalOutput.motors.TalonSRXMotor;
import robot.Global;
import robot.IControl;

public class Lift extends IControl {
	private MiyamotoControl controller;
	private TalonSRXMotor motor;
	private int setPosition;
	
	public Lift(TalonSRX talon) {
		controller = (MiyamotoControl)Global.controllers;
		talon.setIntegralAccumulator(0.0,0,0);
		talon.setSelectedSensorPosition(0, 0, 0);
	}
	
	public Lift(TalonSRXMotor motor){
		controller = (MiyamotoControl)Global.controllers;
		this.motor = motor;
		this.setPosition = 0;
	}
	
	public void setLiftPosition(LiftPosition position){
		setPosition = position.getNumber();
	}
	
	private LiftPosition getCurrentPosition(){
		int current = motor.getTalon().getSelectedSensorPosition(0);
		int[] distances = new int[4];
		distances[0] = Math.abs(current-LiftPosition.BOTTOM.getNumber());
		distances[1] = Math.abs(current-LiftPosition.SWITCH.getNumber());
		distances[2] = Math.abs(current-LiftPosition.SCALE.getNumber());
		distances[3] = Math.abs(current-LiftPosition.CLIMB.getNumber());
		
		int minDistance = distances[0];
		int minIndex = 0;
		for(int i = 1; i <  distances.length; i++){
			if(distances[i] < minDistance){
				minDistance = distances[i];
				minIndex = i;
			}
		}
		
		switch(minIndex){
			case 0:
				return LiftPosition.BOTTOM;
			case 1:
				return LiftPosition.SWITCH;
			case 2:
				return LiftPosition.SCALE;
			case 3:
				return LiftPosition.CLIMB;
		}
		return LiftPosition.BOTTOM;
	}
	
	public void teleopInit(){
		motor.reset();
		setLiftPosition(LiftPosition.BOTTOM);
		motor.set(setPosition);
	}
	
	public void teleopPeriodic(){
		if(controller.raiseLift()){
			switch(getCurrentPosition()){
				case BOTTOM:
					setLiftPosition(LiftPosition.SWITCH);
					break;
				case SWITCH:
					setLiftPosition(LiftPosition.SCALE);
					break;
				case SCALE:
					setLiftPosition(LiftPosition.CLIMB);
					break;
				default:
					setLiftPosition(LiftPosition.BOTTOM);
					break;
			}
		}
		if(controller.lowerLift()){
			switch(getCurrentPosition()){
				case SWITCH:
					setLiftPosition(LiftPosition.BOTTOM);
					break;
				case SCALE:
					setLiftPosition(LiftPosition.SWITCH);
					break;
				case CLIMB:
					setLiftPosition(LiftPosition.SCALE);
					break;
				default:
					setLiftPosition(LiftPosition.BOTTOM);
					break;
			}
		}
		SmartWriter.putD("SetPosition", setPosition);
//		if(controller.lowerLift()){
//			setPosition-=1000;
//		}else if(controller.raiseLift()){
//			setPosition+=1000;
//		}
		motor.set(setPosition);
	}
}
