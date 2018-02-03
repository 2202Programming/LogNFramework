package auto.commands;

import NotVlad.components.Intake;
import auto.ICommand;
import auto.IStopCondition;
import robot.Global;

public class OuttakeCommand implements ICommand {
	private IStopCondition stopCondition;
	private Intake intake;

	/**
	 * Outtakes at a constant speed until a condition is fulfilled
	 * 
	 * @param stop
	 *            The condition that stops the command
	 */
	public OuttakeCommand(IStopCondition stop) {
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
		intake.outtake();

		return stopCondition.stopNow();
	}

	public void stop() {
		intake.stop();
	}
}
