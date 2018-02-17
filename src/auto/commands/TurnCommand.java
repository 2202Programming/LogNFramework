package auto.commands;


import com.kauailabs.navx.frc.AHRS;

import auto.ICommand;
import auto.stopConditions.AngleStopCondition;
import comms.SmartWriter;
import drive.DriveControl;
import drive.IDrive;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import input.SensorController;
import physicalOutput.TurnController;
import robot.Global;
import robot.Robot;
import robotDefinitions.RobotDefinitionBase;


public class TurnCommand implements ICommand {
	
	private AngleStopCondition stopCondition;
	private PIDController controller;
	private PIDOutput output;
	private PIDSource source;
	private IDrive drive;
	
	
	public TurnCommand(double degreesToTurn) {
		this(new AngleStopCondition(degreesToTurn, 2, 0.3));
	}
	
	public TurnCommand(double degreesToTurn, double maxError, double timeInRange) {
		this(new AngleStopCondition(degreesToTurn, maxError, timeInRange));
	}
	
	
	public TurnCommand(AngleStopCondition stop) {
		stopCondition=stop;
		loadPIDValues();
	}
	
	public void init() {
		controller=new PIDController(0.0, 0.0, 0.0, source, output);
		source = (AHRS) SensorController.getInstance().getSensor("NAVX");
		output = (TurnController) Global.controlObjects.get("TURNCONTROLLER");
		controller.setInputRange(-180, 180);
		controller.setOutputRange(-1.0, 1.0);
		controller.setPercentTolerance(1.0);
		
		drive = (IDrive)Global.controlObjects.get(RobotDefinitionBase.DRIVENAME);
		stopCondition.init();
		drive.setDriveControl(DriveControl.EXTERNAL_CONTROL);
	}

	public boolean run() {
		
		SmartWriter.putD("TurnCommandAngle", stopCondition.getError());
		//TO-DO figure out how to use the FRC PID Controller for motorValue
		double motorValue = controller.get();
		SmartWriter.putD("PID Turning Motor Power", motorValue);
		drive.setLeftMotors(motorValue);
		drive.setRightMotors(-motorValue);
		boolean stopNow=stopCondition.stopNow();
		SmartWriter.putB("hghjkhjghg", stopNow);
		return stopNow;
	}
	
	public void stop(){
		drive.setLeftMotors(0);
		drive.setRightMotors(0);
		drive.setDriveControl(DriveControl.DRIVE_CONTROLLED);
	}
	
	private void loadPIDValues() {
		switch (Robot.name) {
		case BABBAGE:
			controller.setPID(0.006, 0.0002, .15);
			break;
		case NOTVLAD:
			controller.setPID(0.1185* .60, 0.0, 0.15);//new PIDValues(0.02, 0.0006, 0.15); //(0.03, 0.0, 0.0) works for Piper
			//Ku = .1887 and Tu = .6465 for PID tuning
			break;
		case TIM:
			// TODO setPIDVALUES
			break;
		case MIYAMOTO:
			//With FRC PID values currently in wont work for sure
			controller.setPID(.06, 0.0001, 0.0); //.06 works for just P (for kinda low battery)
			break;
		case UNKNOWN:
			//TODO setPIDVALUES
			break;
		case MECHANUMDRIVE:
			//TODO setPIDVALUES
			break;
		default:
			break;
		}
	}
}
