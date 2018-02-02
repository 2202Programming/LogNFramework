package NotVlad.components;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import NotVlad.MiyamotoControl;
import comms.SmartWriter;
import robot.Global;
import robot.IControl;

public class Lift extends IControl {
	private MiyamotoControl controller;
	private TalonSRX talon;
	
	public Lift(TalonSRX talon) {
		this.talon = talon;
		talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
		
		controller = (MiyamotoControl)Global.controllers;
	}
	
	public void teleopInit(){
		talon.set(ControlMode.Position, 0);
	}
	
	public void teleopPeriodic(){
		double position = controller.getLeftJoystickX();
		position*=4096*5;
		SmartWriter.putD("Position", position);
		talon.set(ControlMode.Position, position);
		SmartWriter.putD("TalonSpeed", talon.getMotorOutputPercent());
		SmartWriter.putD("TalonEncoder",talon.getSelectedSensorPosition(0));
	}
}
