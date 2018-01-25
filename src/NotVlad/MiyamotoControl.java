package NotVlad;

import comms.XboxController;
import robot.Global.StartPosition;
import robotDefinitions.ControlBase;

public class MiyamotoControl extends ControlBase {

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
		if (controllers[2].getAHeld()) {
			return StartPosition.L;
		} else if (controllers[2].getBHeld()) {
			return StartPosition.M;
		} else if (controllers[2].getXHeld()) {
			return StartPosition.R;
		} else {
			return StartPosition.L;
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
	 * Returns the approach option- true is from front and false is from the side
	 * from the driver's perspective
	 * 
	 * @return The approach option
	 */
	public boolean getApproach() {
		return controllers[2].getLeftBumperHeld();
	}

	/**
	 * Returns the path type- true is the long path and false is the short path
	 * 
	 * @return The path type
	 */
	public boolean getPathType() {
		return controllers[2].getRightBumperHeld();
	}

	// speeds up intake
	public boolean intakeSpeed() {
		return controllers[0].getAHeld();
	}

	// starts shooting
	public boolean startShooting() {
		return controllers[0].getRightTriggerPressed();
	}

	// stops shooting
	public boolean stopShooting() {
		return controllers[0].getBPressed();
	}

	// toggles the height of the shooter
	public boolean toggleShooterHeight() {
		return controllers[0].getRightBumperPressed();
	}

	// engage high-goal vision
	public boolean hgVision() {
		return controllers[0].getYPressed();
	}

	// cancel high-goal vision
	public boolean cancelHg() {
		return controllers[0].getStartPressed();
	}

	@Override
	public XboxController[] getControllers() {
		XboxController[] controllers = new XboxController[1];
		controllers[0] = XboxController.getXboxController(0);
		return controllers;
	}

}
