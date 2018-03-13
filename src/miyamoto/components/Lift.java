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
	private int utilityIndex;
	private boolean settling;
	private LiftPosition settlePosition;
	
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
		
		utilityPositions = new LiftPosition[4];
		utilityPositions[0] = LiftPosition.BOTTOM;
		utilityPositions[1] = LiftPosition.EXCHANGE;
		utilityPositions[2] = LiftPosition.PORTAL;
		utilityPositions[3] = LiftPosition.CLIMB;
		
		mainIndex = 0;
		settling = false;
		
		motor.getTalon().overrideLimitSwitchesEnable(false);
		//motor.getTalon().configForwardLimitSwitchSource(LimitSwitchSource.RemoteTalonSRX, LimitSwitchNormal.NormallyOpen, 0);
		//motor.getTalon().configReverseLimitSwitchSource(LimitSwitchSource.RemoteTalonSRX, LimitSwitchNormal.NormallyOpen, 0);
	}

	public void setLiftPosition(LiftPosition position) {
		setPosition = position.getNumber();
		settlePosition = position;
		settling = true;
	}

	public void setLiftPosition(int position) {
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
		utilityIndex = 0;
		//setLiftPosition(LiftPosition.BOTTOM);
		motor.set(setPosition);
		settling = false;
	}
	
	public void teleopPeriodic(){
		if(controller.raiseLift()){
			utilityIndex = 0;
			mainIndex = Math.min(mainPositions.length-1, mainIndex+1);
			setLiftPosition(mainPositions[mainIndex]);
		}
		if(controller.lowerLift()){
			utilityIndex = 0;
			mainIndex = Math.max(0, mainIndex-1);
			setLiftPosition(mainPositions[mainIndex]);
		}
		
		if(controller.raiseLiftUtility()){
			mainIndex = 0;
			utilityIndex = Math.min(utilityPositions.length-1, utilityIndex+1);
			setLiftPosition(utilityPositions[utilityIndex]);
		}
		if(controller.lowerLiftUtility()){
			mainIndex = 0;
			utilityIndex = Math.max(0, utilityIndex-1);
			setLiftPosition(utilityPositions[utilityIndex]);
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
			settleLift(settlePosition);
		}
		motor.set(setPosition);
	}
	
	public void settleLift(int index){
		int counts = Math.abs(motor.getTalon().getSelectedSensorPosition(0));
		if(counts < mainPositions[index].getNumber()){
			setLiftPosition(LiftPosition.MAX.getNumber());
		}else{
			setLiftPosition(mainPositions[index].getNumber());
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
