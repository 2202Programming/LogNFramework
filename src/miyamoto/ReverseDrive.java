package miyamoto;

import drive.IDrive;
import drive.Reversible;
import robot.Global;
import robot.IControl;
import robotDefinitions.controls.ReversibleController;

public class ReverseDrive extends IControl {
	private Reversible drive;
	private ReversibleController controller;
	private boolean reverse;

	/**
	 * Uses reversible interface to reverse input (mainly used in teleop for human
	 * drivers' convenience
	 * @param drive
	 */
	public ReverseDrive(){
		
	}
	
	public void robotInit(){
		if (Global.controlObjects.get("DRIVE") instanceof Reversible) {
			drive = (Reversible)Global.controlObjects.get("DRIVE");
		}
		if (Global.controllers instanceof ReversibleController) {
			controller = (ReversibleController) Global.controllers;
		}
	}

	public void autonomousInit() {
		if (drive != null && controller != null) {
			drive.reverseDrive(false);
			reverse = false;
		}
	}

	public void teleopInit() {
		if (drive != null && controller != null) {
			drive.reverseDrive(false);
			reverse = false;
		}
	}

	public void teleopPeriodic() {
		if (drive != null && controller != null) {
			if (controller.reverseDrive()) {
				reverse = !reverse;
			}
			drive.reverseDrive(reverse);
		}
	}
}
