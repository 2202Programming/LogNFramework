package miyamoto;

import java.util.ArrayList;

import com.kauailabs.navx.frc.AHRS;

import auto.CommandList;
import auto.CommandListRunner;
import auto.ICommand;
import auto.commands.DriveCommand;
import auto.commands.LiftCommand;
import auto.commands.TurnCommand;
import auto.commands.WaitCommand;
import auto.stopConditions.AngleStopCondition;
import auto.stopConditions.DistanceStopCondition;
import auto.stopConditions.OrStopCondition;
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
	private ArrayList<Encoder> encoders;
	
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
		
		encoders = new ArrayList<>();
		encoders.add((Encoder)SensorController.getInstance().getSensor("ENCODER0"));
		encoders.add((Encoder)SensorController.getInstance().getSensor("ENCODER1"));
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
		double tiltAngle = Math.abs(gyro.getPitch());
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
			if(controller.autoClimb()){
				CommandList list = new CommandList();
				list.addCommand(new DriveCommand(new DistanceStopCondition(encoders, 2), 0.5));
				list.addCommand(new LiftCommand(LiftPosition.CLIMB, new TimerStopCondition(2000)));
				list.addCommand(new DriveCommand(new TimerStopCondition(1000), -0.3));
				list.addCommand(new LiftCommand(LiftPosition.BOTTOM, new TimerStopCondition(2000)));
				if(Math.abs(controller.getLeftJoystickX(0)) > 0.2){
					list.addCommand(new DriveCommand(new DistanceStopCondition(encoders, 1), 0.5));
					list.addCommand(new WaitCommand(new TimerStopCondition(200)));
					if(controller.getLeftJoystickX(0) > 0){
						list.addCommand(new TurnCommand(new OrStopCondition(new TimerStopCondition(1000), new AngleStopCondition(-90, 3, .5)),-90));
					}else{
						list.addCommand(new TurnCommand(new OrStopCondition(new TimerStopCondition(1000), new AngleStopCondition(90, 3, .5)),90));
					}
					list.addCommand(new DriveCommand(new DistanceStopCondition(encoders, 15), 0.5));
				}
				runner = new CommandListRunner(list);
				runner.init();
				doneRunning = false;
			}
		}
	}
	
	public void teleopInit(){
		doneRunning = true;
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
