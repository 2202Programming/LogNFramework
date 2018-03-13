package miyamoto.components;

import comms.SmartWriter;
import miyamoto.MiyamotoControl;
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
	private LiftPosition autoPosition;
	
	public Lift(TalonSRXMotor motor){
		controller = (MiyamotoControl)Global.controllers;
		this.motor = motor;
		this.setPosition = 0;
		positions = new LiftPosition[4];
		positions[0] = LiftPosition.BOTTOM;
		positions[1] = LiftPosition.SWITCH;
		positions[2] = LiftPosition.LOWSCALE;
		positions[3] = LiftPosition.HIGHSCALE;
		index = 0;
		settling = false;
		
		motor.getTalon().overrideLimitSwitchesEnable(false);
		//motor.getTalon().configForwardLimitSwitchSource(LimitSwitchSource.RemoteTalonSRX, LimitSwitchNormal.NormallyOpen, 0);
		//motor.getTalon().configReverseLimitSwitchSource(LimitSwitchSource.RemoteTalonSRX, LimitSwitchNormal.NormallyOpen, 0);
	}

	public void setLiftPosition(LiftPosition position) {
		setPosition = position.getNumber();
		autoPosition = position;
		settling = true;
	}

	public void setLiftPosition(int position) {
		setPosition = position;
	}

	public int getLiftPosition() {
		return setPosition;
	}
	
	/**
	 * Gets the encoder position of the lift
	 * @return
	 */
	public int getLiftCounts(){
		return Math.abs(motor.getTalon().getSelectedSensorPosition(0));
	}
	
	public void teleopInit(){
		//motor.reset();
		index = 0;
		//setLiftPosition(LiftPosition.BOTTOM);
		motor.set(setPosition);
		settling = false;
	}
	
	public void teleopPeriodic(){
		if(controller.raiseLift()){
			settling = true;
			index = Math.min(positions.length-1, index+1);
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
		
		if(controller.resetLift()){
			motor.reset();
		}
		
		SmartWriter.putD("SetPosition", setPosition);
		SmartWriter.putD("LiftPos", motor.getTalon().getSelectedSensorPosition(0));
		SmartWriter.putD("LiftCurrent", motor.getTalon().getOutputCurrent());
		motor.set(setPosition);
	}

	public void autonomousInit() {
		motor.reset();
		setLiftPosition(LiftPosition.BOTTOM.getNumber());
		motor.set(setPosition);
	}

	public void autonomousPeriodic() {
		if(settling){
			settleLift(autoPosition);
		}
		motor.set(setPosition);
	}
	
	public void settleLift(int index){
		int counts = Math.abs(motor.getTalon().getSelectedSensorPosition(0));
		if(counts < positions[index].getNumber()){
			setLiftPosition(LiftPosition.MAX.getNumber());
		}else{
			setLiftPosition(positions[index].getNumber());
			settling = false;
		}
	}
	
	public void settleLift(LiftPosition setPosition){
		int counts = Math.abs(motor.getTalon().getSelectedSensorPosition(0));
		if(counts < setPosition.getNumber()){
			setLiftPosition(LiftPosition.MAX.getNumber());
		}else{
			setLiftPosition(setPosition.getNumber());
			settling = false;
		}
	}
}
