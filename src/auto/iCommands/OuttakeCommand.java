package auto.iCommands;

import auto.ICommand;
import auto.IStopCondition;
import miyamoto.components.Intake;
import robot.Global;

public class OuttakeCommand implements ICommand {
	private Intake intake;
	private double speed;

	/**
	 * Outtakes at a constant speed until a condition is fulfilled
	 * 
	 * @param stop
	 *            The condition that stops the command
	 */
	public OuttakeCommand(double speed) {
		this.speed = speed;
	}

	public void init() {
		intake = (Intake) Global.controlObjects.get("INTAKE");
	}

	public void run() {
		if (intake == null) {
			init();
		}
		intake.runIntake(-speed);

	}

	public void stop() {
		intake.stop();
	}
}
