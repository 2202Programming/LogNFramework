package NotVlad.components;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import NotVlad.MiyamotoControl;
import robot.Global;
import robot.IControl;

public class Lift extends IControl {
	private MiyamotoControl controller;
	private TalonSRX talon;
	
	public Lift(TalonSRX talon) {
		this.talon = talon;
		controller = (MiyamotoControl)Global.controllers;
	}
	
	public void teleopInit(){
		talon.set(ControlMode.Position, 0);
	}
	
	public void teleopPeriodic(){
		double position = controller.getLeftJoystickX();
		talon.set(ControlMode.Position, position);
	}
}
