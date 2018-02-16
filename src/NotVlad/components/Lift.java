package NotVlad.components;

import NotVlad.MiyamotoControl;
import comms.SmartWriter;
import physicalOutput.motors.TalonSRXMotor;
import robot.Global;
import robot.IControl;

public class Lift extends IControl {
	private MiyamotoControl controller;
	private TalonSRXMotor motor;
	private int setPosition;
	private LiftPosition[] positions;
	private int index;
	private boolean settling;
	
	public Lift(TalonSRXMotor motor){
		controller = (MiyamotoControl)Global.controllers;
		this.motor = motor;
		this.setPosition = 0;
		positions = new LiftPosition[4];
		positions[0] = LiftPosition.BOTTOM;
		positions[1] = LiftPosition.SWITCH;
		positions[2] = LiftPosition.SCALE;
		positions[3] = LiftPosition.CLIMB;
		index = 0;
		settling = false;
	}
	
	public void setLiftPosition(LiftPosition position){
		setPosition = position.getNumber();
	}
	
	public void setLiftPosition(int position){
		setPosition = position;
	}
	
	public int getLiftPosition(){
		return setPosition;
	}
	
	public void teleopInit(){
		motor.reset();
		index = 0;
		setLiftPosition(LiftPosition.BOTTOM);
		motor.set(setPosition);
		settling = false;
	}
	
	public void teleopPeriodic(){
		if(controller.raiseLift()){
			settling = true;
			index = Math.min(positions.length, index+1);
		}
		if(controller.lowerLift()){
			settling = true;
			index = Math.max(0, index-1);
		}
		
		if(settling){
			settleLift(index);
		}
		
		if(controller.manualLiftUp()){
			setPosition += 100;
		}
		if(controller.manualLiftDown()){
			setPosition -= 100;
		}
		
		SmartWriter.putD("SetPosition", setPosition);
		SmartWriter.putD("LiftPos", motor.getTalon().getSelectedSensorPosition(0));
		SmartWriter.putD("LiftCurrent", motor.getTalon().getOutputCurrent());
		motor.set(setPosition);
	}
	
	public void autonomousInit(){
		motor.reset();
		setLiftPosition(LiftPosition.BOTTOM);
		motor.set(setPosition);
	}
	
	public void autonomousPeriodic(){
		motor.set(setPosition);
	}
	
	public void settleLift(int index){
		int counts = Math.abs(motor.getTalon().getSelectedSensorPosition(0));
		if(counts < positions[index].getNumber()){
			setLiftPosition(LiftPosition.MAX);
		}else{
			setLiftPosition(positions[index]);
			settling = false;
		}
	}
}
