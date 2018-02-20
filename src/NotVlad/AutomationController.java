package NotVlad;

import com.kauailabs.navx.frc.AHRS;

import NotVlad.components.Lift;
import NotVlad.components.LiftPosition;
import drive.MotionProfiler;
import input.SensorController;
import robot.Global;
import robot.IControl;

public class AutomationController extends IControl{
	private AHRS gyro;
	private Lift lift;
	private MotionProfiler profiler;
	private double[] tiltAngles;
	private LiftPosition[] positions;
	
	public AutomationController(){
		gyro = (AHRS)SensorController.getInstance().getSensor("NAVX");
		lift = (Lift)Global.controlObjects.get("LIFT");
		profiler = (MotionProfiler)Global.controlObjects.get("PROFILER");
		tiltAngles = new double[5];
		tiltAngles[0] = 0;
		tiltAngles[1] = 5;
		tiltAngles[2] = 10;
		tiltAngles[3] = 15;
		tiltAngles[4] = 20;
		positions = new LiftPosition[5];
		positions[0] = LiftPosition.BOTTOM;
		positions[1] = LiftPosition.SWITCH;
		positions[2] = LiftPosition.CLIMB;
		positions[3] = LiftPosition.LOWSCALE;
		positions[4] = LiftPosition.HIGHSCALE;
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
		int tiltSeverity = 0;
		for(int i = 0; i < tiltAngles.length; i++){
			if(tiltAngle > tiltAngles[i]){
				tiltSeverity = i;
			}
		}
		
		if(getLiftSeverity() > )
	}
}
