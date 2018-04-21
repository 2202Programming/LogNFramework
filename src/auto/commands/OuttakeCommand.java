package auto.commands;

import auto.ICommand;
import auto.IStopCondition;
import miyamoto.components.Intake;
import robot.Global;

public class OuttakeCommand implements ICommand {
	private IStopCondition stopCondition;
	private Intake intake;
	private double speed;

	/**
	 * Outtakes at a constant speed until a condition is fulfilled
	 * 
	 * @param stop
	 *            The condition that stops the command
	 */
	public OuttakeCommand(double speed, IStopCondition stop) {
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
		intake.runIntake(-speed);

		return stopCondition.stopNow();
	}

	public void stop() {
		intake.stop();
	}
	
	public String toString() {
		return "OuttakeCommand";
	}
}
