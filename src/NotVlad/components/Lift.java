package NotVlad.components;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import NotVlad.MiyamotoControl;
import comms.SmartWriter;
import physicalOutput.motors.TalonSRXMotor;
import robot.Global;
import robot.IControl;

public class Lift extends IControl {
	private MiyamotoControl controller;
	private TalonSRX talon;
	
	private TalonSRXMotor motor;
	private int currentPosition;
	
	public Lift(TalonSRX talon) {
		this.talon = talon;
		talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
		
		controller = (MiyamotoControl)Global.controllers;
		talon.setIntegralAccumulator(0.0,0,0);
		talon.setSelectedSensorPosition(0, 0, 0);
	}
	
	public Lift(TalonSRXMotor motor){
		this.motor = motor;
		this.currentPosition = 0;
	}
	
	private int convertPosition(LiftPosition position){
		int toReturn = 0;
		switch(position){
			case BOTTOM:
				toReturn = 0;
				break;
			case SWITCH:
				toReturn = 1;
				break;
			case SCALE:
				toReturn = 2;
				break;
			case CLIMB:
				toReturn = 3;
				break;
		}
		return toReturn;
	}
	
	private void setLiftPosition(LiftPosition position){
		motor.set(convertPosition(position));
	}
	
	public void teleopInit(){
		talon.set(ControlMode.Position, 0);
		talon.setIntegralAccumulator(0.0, 0, 0);
		talon.setSelectedSensorPosition(0, 0, 0);
//		talon.configForwardSoftLimitEnable(true, 0);
//		talon.configForwardSoftLimitThreshold(30000, 0);
//		talon.configReverseSoftLimitEnable(true, 0);
//		talon.configReverseSoftLimitThreshold(-30000, 0);
		
		motor.reset();
		
	}
	
	public void teleopPeriodic(){
		double position = controller.getLeftJoystickX();
		position*= 4096*5*3;
		//position=Math.max(-30000, position);
		//position=Math.min(30000, position);
		SmartWriter.putD("Position", position);
		talon.set(ControlMode.Position, position);
		SmartWriter.putD("TalonSpeed", talon.getMotorOutputPercent());
		SmartWriter.putD("TalonEncoder",talon.getSelectedSensorPosition(0));
	}
}
