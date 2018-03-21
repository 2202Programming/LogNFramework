package miyamoto;

import java.util.ArrayList;

import com.kauailabs.navx.frc.AHRS;

import auto.CommandList;
import auto.CommandListRunner;
import auto.iCommands.DriveCommand;
import auto.iCommands.LiftCommand;
import auto.runnables.SingleStopCondition;
import auto.stopConditions.DistanceStopCondition;
import auto.stopConditions.TimerStopCondition;
import drive.MotionProfiler;
import edu.wpi.first.wpilibj.Encoder;
import input.SensorController;
import miyamoto.components.Lift;
import miyamoto.components.LiftPosition;
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
	
	public AutomationController(Lift lift, MotionProfiler profiler){
		controller = (MiyamotoControl)Global.controllers;
		gyro = (AHRS)SensorController.getInstance().getSensor("NAVX");
		this.lift = lift;
		this.profiler = profiler;
		tiltAngles = new double[5];
		tiltAngles[4] = 10;
		tiltAngles[3] = 10;
		tiltAngles[2] = 25;
		tiltAngles[1] = 30;
		tiltAngles[0] = 45;
		
		ArrayList<Encoder> encoders = new ArrayList<>();
		encoders.add((Encoder)SensorController.getInstance().getSensor("ENCODER0"));
		encoders.add((Encoder)SensorController.getInstance().getSensor("ENCODER1"));
		CommandList list = new CommandList();
		list.addCommand(new SingleStopCondition(new DriveCommand(0.5),new DistanceStopCondition(encoders, 2)));
		list.addCommand(new SingleStopCondition(new LiftCommand(LiftPosition.CLIMB), new TimerStopCondition(2000)));
		list.addCommand(new DriveCommand(new TimerStopCondition(1000), -0.3));
		list.addCommand(new LiftCommand(LiftPosition.BOTTOM, new TimerStopCondition(10)));
		runner = new CommandListRunner(list);
	}
	
	/**
	 * Gets how severe the height of the lift will effect our robot
	 * @return the severity level
	 */
	private int getLiftSeverity(){
		int liftPosition = lift.getLiftCounts();
		LiftPosition[] positions = {LiftPosition.BOTTOM, LiftPosition.SWITCH, LiftPosition.CLIMB,LiftPosition.LOWSCALE,LiftPosition.HIGHSCALE};
		
		int closestDistance = Math.abs(positions[0].getNumber()-liftPosition);
		int closestIndex = 0;
		for(int i = 1; i < positions.length; i++){
			int tempDistance = Math.abs(positions[i].getNumber() - liftPosition);
			if(tempDistance < closestDistance){
				closestDistance = tempDistance;
				closestIndex = i;
			}
		}
		
		switch(positions[closestIndex]){
			case HIGHSCALE:
				return 4;
			case LOWSCALE:
				return 3;
			case CLIMB:
				return 2;
			case SWITCH:
				return 1;
			case BOTTOM:
				return 0;
			default:
				return 0;
		}
	}
	
	/**
	 * Automatically sets the motion profile based on the severity of the lift
	 * @param severity the severity of the lift
	 */
	private void setMotionProfile(int severity){
		profiler.setProfileIndex(severity);
	}
	
	private void keepLiftSafe(){
		double tiltAngle = Math.abs(gyro.getPitch()+4.5);
		int liftSeverity = getLiftSeverity();		
		if(tiltAngle > tiltAngles[liftSeverity]){
			lift.setLiftPosition(LiftPosition.BOTTOM);
		}
	}
	
	private void autoClimb(){
		if(controller.cancelClimb()){
			runner.stop();
			doneRunning = true;
			lift.setLiftPosition(LiftPosition.BOTTOM);
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
