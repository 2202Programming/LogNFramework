package auto.commands;

import NotVlad.components.Intake;
import auto.ICommand;
import auto.IStopCondition;
import robot.Global;

public class IntakeCommand implements ICommand {
	private IStopCondition stopCondition;
	private Intake intake;

	/**
	 * Intakes at a constant speed until a condition is fulfilled
	 * @param stop
	 *            The condition that stops the command
	 */
	public IntakeCommand(IStopCondition stop) {
		stopCondition = stop;
	}

	public void init() {
		stopCondition.init();
		intake = (Intake) Global.controlObjects.get("INTAKE");
	}

	public boolean run() {
		if (intake == null) {
			init();
		}
		intake.intake();

		return stopCondition.stopNow();
	}

	public void stop() {
		intake.stop();
	}
}
