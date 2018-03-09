package auto.commands;

import auto.ICommand;
import auto.IStopCondition;
import miyamoto.components.Intake;
import robot.Global;

public class IntakeCommand implements ICommand {
	private IStopCondition stopCondition;
	private Intake intake;
	private double speed;

	/**
	 * Intakes at a constant speed until a condition is fulfilled
	 * @param stop
	 *            The condition that stops the command
	 */
	public IntakeCommand(double speed, IStopCondition stop) {
		stopCondition = stop;
		this.speed = speed;
	}

	public void init() {
		stopCondition.init();
		intake = (Intake) Global.controlObjects.get("INTAKE");
	}

	public boolean run() {
		if (intake == null) {
			init();
		}
		intake.runIntake(speed);

		return stopCondition.stopNow();
	}

	public void stop() {
		intake.holdBlock();
	}
}
