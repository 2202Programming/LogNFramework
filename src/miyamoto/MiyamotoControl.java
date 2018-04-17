package miyamoto;

import comms.XboxController;
import robot.Global.StartPosition;
import robotDefinitions.controls.ControlBase;
import robotDefinitions.controls.MotionProfileableController;
import robotDefinitions.controls.ReversibleController;

public class MiyamotoControl extends ControlBase implements ReversibleController, MotionProfileableController {

	// Controllers 0 and 1 are xbox controllers used to control the robot
	// Controller 2 is the switchboard that controls auto

	public MiyamotoControl() {
		super(true);
	}

	/**
	 * Returns the start position of the robot
	 * 
	 * @return The Start Position- defaults to Left side
	 */
	public StartPosition getStartPosition() {
		if (controllers[2].getAHeld() && controllers[2].getBHeld() && controllers[2].getXHeld()) {
			return StartPosition.D;
		} else if (controllers[2].getAHeld()) {
			return StartPosition.L;
		} else if (controllers[2].getBHeld()) {
			return StartPosition.M;
		} else if (controllers[2].getXHeld()) {
			return StartPosition.R;
		} else {
			return null;
		}
	}

	/**
	 * Get's the objective of auto- true is the scale and false is the switch
	 * 
	 * @return The objective
	 */
	public boolean getObjective() {
		return controllers[2].getYHeld();
	}

	/**
	 * Returns the approach option- true is from front and false is from the
	 * side from the driver's perspective
	 * 
	 * @return The approach option
	 */
	public boolean getApproach() {
		return controllers[2].getLeftBumperHeld();
	}

	/**
	 * Returns the choice of strat - safe if held, normal if not
	 * 
	 * @return The path type
	 */
	public boolean getSafeAuto() {
		return controllers[2].getRightBumperHeld();
	}

	/**
	 * returns if the climber should climb fast
	 * 
	 * @return if the climber should climb fast
	 */
	public boolean climbFast() {
		return controllers[2].getStartHeld();
	}

	/**
	 * returns if the climber should climb slow
	 * 
	 * @return if the climber should climb slow
	 */
	public boolean climbSlow() {
		return controllers[2].getBackHeld();
	}

	public boolean climbReverse() {
		return controllers[2].getL3Held() && controllers[2].getR3Held() && controllers[1].getL3Held()
				&& controllers[1].getR3Held();
	}

	/**
	 * returns if the robot should intake
	 * 
	 * @return if the robot should intake
	 */
	public boolean outtakeSlow() {
		return controllers[0].getLeftBumperHeld();
	}

	/**
	 * returns if the robot should outtake
	 * 
	 * @return if the robot should outtake
	 */
	public boolean outtake() {
		return controllers[0].getLeftTriggerHeld();
	}

	public boolean overrideIntake() {
		return controllers[0].getRightTriggerHeld();
	}

	public boolean rotateIntake() {
		return controllers[0].getRightBumperHeld();
	}

	public boolean raiseLift() {
		return controllers[1].getRightBumperPressed();
	}

	public boolean lowerLift() {
		return controllers[1].getLeftBumperPressed();
	}
	
	public boolean exchangeLift(){
		return controllers[1].getAHeld();
	}
	
	public boolean portalLift(){
		return controllers[1].getBHeld();
	}
	
	public boolean secondBlockLift(){
		return controllers[1].getYHeld();
	}
	
	public boolean climbLift(){
		return controllers[1].getXHeld();
	}

	public boolean cycleMotionProfile() {
		return controllers[0].getRightBumperPressed();
	}

	public boolean reverseDrive() {
		return controllers[0].getXPressed();
	}

	public boolean manualLiftUp() {
		return controllers[1].getRightTriggerHeld();
	}

	public boolean manualLiftDown() {
		return controllers[1].getLeftTriggerHeld();
	}

	public boolean resetLift() {
		return controllers[1].getStartPressed();
	}

	public boolean autoClimb() {
		return controllers[0].getAPressed();
	}

	public boolean cancelClimb() {
		return controllers[0].getBPressed();
	}

	@Override
	public XboxController[] getControllers() {
		XboxController[] controllers = new XboxController[3];
		controllers[0] = XboxController.getXboxController(0);
		controllers[1] = XboxController.getXboxController(1);
		controllers[2] = XboxController.getXboxController(2);
		return controllers;
	}

}
