package NotVlad;

import java.util.ArrayList;

import com.kauailabs.navx.frc.AHRS;

import NotVlad.components.Lift;
import NotVlad.components.LiftPosition;
import auto.CommandList;
import auto.CommandListRunner;
import auto.commands.DriveCommand;
import auto.commands.LiftCommand;
import auto.stopConditions.DistanceStopCondition;
import auto.stopConditions.TimerStopCondition;
import drive.MotionProfiler;
import edu.wpi.first.wpilibj.Encoder;
import input.SensorController;
import robot.Global;
import robot.IControl;

public class AutomationController extends IControl{
	private AHRS gyro;
	private Lift lift;
	private MotionProfiler profiler;
	private double[] tiltAngles;
	private MiyamotoControl controller;
	private CommandListRunner runner;
	private boolean doneRunning;
	
	public AutomationController(){
		controller = (MiyamotoControl)Global.controllers;
		gyro = (AHRS)SensorController.getInstance().getSensor("NAVX");
		lift = (Lift)Global.controlObjects.get("LIFT");
		profiler = (MotionProfiler)Global.controlObjects.get("PROFILER");
		tiltAngles = new double[5];
		tiltAngles[0] = 10;
		tiltAngles[1] = 20;
		tiltAngles[2] = 25;
		tiltAngles[3] = 30;
		tiltAngles[4] = 45;
		
		ArrayList<Encoder> encoders = new ArrayList<>();
		encoders.add((Encoder)SensorController.getInstance().getSensor("ENCODER0"));
		encoders.add((Encoder)SensorController.getInstance().getSensor("ENCODER1"));
		CommandList list = new CommandList();
		list.addCommand(new DriveCommand(new DistanceStopCondition(encoders, -4), 0.3));
		list.addCommand(new LiftCommand(LiftPosition.CLIMB, new TimerStopCondition(1000)));
		list.addCommand(new DriveCommand(new DistanceStopCondition(encoders, 4), 0.3));
		runner = new CommandListRunner(list);
	}
	
	/**
	 * Gets how severe the height of the lift will effect our robot
	 * @return the severity level
	 */
	private int getLiftSeverity(){
		int liftPosition = lift.getLiftPosition();
		if(liftPosition > LiftPosition.HIGHSCALE.getNumber()){
			return 4;
		}else if(liftPosition > LiftPosition.LOWSCALE.getNumber()){
			return 3;
		}else if(liftPosition > LiftPosition.CLIMB.getNumber()){
			return 2;
		}else if(liftPosition > LiftPosition.SWITCH.getNumber()){
			return 1;
		}else if(liftPosition > LiftPosition.BOTTOM.getNumber()){			
			return 0;
		}
		return 0;
	}
	
	/**
	 * Automatically sets the motion profile based on the severity of the lift
	 * @param severity the severity of the lift
	 */
	private void setMotionProfile(int severity){
		profiler.setProfileIndex(severity);
	}
	
	private void keepLiftSafe(){
		double tiltAngle = gyro.getPitch();
		int liftSeverity = getLiftSeverity();
		if(tiltAngle > tiltAngles[liftSeverity]){
			lift.setLiftPosition(LiftPosition.BOTTOM);
		}
	}
	
	private void autoClimb(){
		if(controller.cancelClimb()){
			runner.stop();
			doneRunning = true;
		}
		if(!doneRunning){
			doneRunning = runner.runList();
		}else{
			runner.init();
			if(controller.autoClimb()){
				doneRunning = false;
			}
		}
	}
	
	public void teleopInit(){
		doneRunning = true;
		runner.init();
	}
	
	public void teleopPeriodic(){
		setMotionProfile(getLiftSeverity());
		keepLiftSafe();
		autoClimb();
	}
	
	public void autonomousPeriodic(){
		keepLiftSafe();
	}
}
