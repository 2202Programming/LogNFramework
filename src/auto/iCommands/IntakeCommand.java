package auto.iCommands;

import auto.ICommand;
import auto.IStopCondition;
import miyamoto.components.Intake;
import robot.Global;

public class IntakeCommand implements ICommand {
	private Intake intake;
	private double speed;
	private double holdSpeed;

	/**
	 * Intakes at a constant speed until a condition is fulfilled
	 * 
	 * @param stop
	 *            The condition that stops the command
	 */
	public IntakeCommand(double speed, double holdSpeed) {
		this.speed = speed;
		this.holdSpeed = holdSpeed;
	}

	public void init() {
		intake = (Intake) Global.controlObjects.get("INTAKE");
	}

	public void run() {
		if (intake == null) {
			init();
		}
		intake.runIntake(speed);

	}

	public void stop() {
		intake.runIntake(holdSpeed);
	}
}
