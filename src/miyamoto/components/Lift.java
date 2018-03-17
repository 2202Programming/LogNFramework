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
	private LiftPosition[] mainPositions;
	private LiftPosition[] utilityPositions;
	private int mainIndex;
	private boolean settling;
	private int settlePosition;
	
	public Lift(TalonSRXMotor motor){
		controller = (MiyamotoControl)Global.controllers;
		this.motor = motor;
		this.setPosition = 0;
		
		mainPositions = new LiftPosition[5];
		mainPositions[0] = LiftPosition.BOTTOM;
		mainPositions[1] = LiftPosition.SWITCH;
		mainPositions[2] = LiftPosition.LOWSCALE;
		mainPositions[3] = LiftPosition.MIDSCALE;
		mainPositions[4] = LiftPosition.HIGHSCALE;
		
		mainIndex = 0;
		settling = false;
		
		motor.getTalon().overrideLimitSwitchesEnable(false);
		//motor.getTalon().configForwardLimitSwitchSource(LimitSwitchSource.RemoteTalonSRX, LimitSwitchNormal.NormallyOpen, 0);
		//motor.getTalon().configReverseLimitSwitchSource(LimitSwitchSource.RemoteTalonSRX, LimitSwitchNormal.NormallyOpen, 0);
	}

	public void setLiftPosition(LiftPosition position) {
		setPosition = position.getNumber();
		settlePosition = position.getNumber();
		settling = true;
	}

	public void setLiftPosition(int position) {
		setPosition = position;
		settlePosition = position;
		settling = true;
	}
	
	private void setLiftNoSettle(int position) {
		setPosition = position;
	}

	/**
	 * Gets the set position of the lift
	 * @return
	 */
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
		mainIndex = 0;
		//setLiftPosition(LiftPosition.BOTTOM);
		motor.set(setPosition);
		settling = false;
	}
	
	public void teleopPeriodic(){
		if(controller.raiseLift()){
			mainIndex = Math.min(mainPositions.length-1, mainIndex+1);
			setLiftPosition(mainPositions[mainIndex]);
		}
		if(controller.lowerLift()){
			mainIndex = Math.max(0, mainIndex-1);
			setLiftPosition(mainPositions[mainIndex]);
		}
		
		if(controller.exchangeLift()){
			mainIndex = 0;
			setLiftPosition(LiftPosition.EXCHANGE);
		}
		if(controller.portalLift()){
			mainIndex = 0;
			setLiftPosition(LiftPosition.PORTAL);
		}
		if(controller.secondBlockLift()) {
			mainIndex = 0;
			setLiftPosition(LiftPosition.SECONDBLOCK);
		}
		if(controller.climbLift()){
			mainIndex = 0;
			setLiftPosition(LiftPosition.CLIMB);
		}
		
		if(settling){
			settleLift(settlePosition);
		}
		
		if(controller.manualLiftUp()){
			setPosition += 100;
		}
		if(controller.manualLiftDown()){
			setPosition -= 100;
		}
		
//		if(controller.resetLift()){
//			motor.reset();
//		}
//		
		SmartWriter.putD("SetPosition", setPosition);
		SmartWriter.putD("LiftPos", motor.getTalon().getSelectedSensorPosition(0));
		SmartWriter.putD("LiftCurrent", motor.getTalon().getOutputCurrent());
		//System.out.println("LiftPower: " + motor.getTalon().getSelectedSensorVelocity(0));
		motor.set(setPosition);
	}

	public void autonomousInit() {
		motor.reset();
		setLiftPosition(LiftPosition.BOTTOM.getNumber());
		motor.set(setPosition);
	}

	public void autonomousPeriodic() {
		if(settling){
			settleLift(settlePosition);
		}
		motor.set(setPosition);
	}
	
	public void settleLift(int setPosition){
		int counts = Math.abs(motor.getTalon().getSelectedSensorPosition(0));
		if(counts < setPosition){
			setLiftNoSettle(LiftPosition.MAX.getNumber());
		}else{
			setLiftNoSettle(setPosition);
			settling = false;
		}
	}
}
