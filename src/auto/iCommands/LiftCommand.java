package auto.iCommands;

import auto.ICommand;
import auto.IStopCondition;
import miyamoto.components.Lift;
import miyamoto.components.LiftPosition;
import robot.Global;

public class LiftCommand implements ICommand {
	private Lift lift;
	private int finalPosition;

	/**
	 * Lifts the lifter to a set position
	 * 
	 * @param finalPosition
	 *            The final position the lift should be in encoder counts
	 * @param stop
	 *            The stop condition
	 */

	public LiftCommand(int finalPosition) {
		this.finalPosition = finalPosition;
	}

	public LiftCommand(LiftPosition finalPosition) {
		this.finalPosition = finalPosition.getNumber();
	}

	public void init() {
		lift = (Lift) Global.controlObjects.get("LIFT");
	}

	public void run() {
		if (lift == null) {
			init();
		}

		lift.setLiftPosition(finalPosition);

	}

	public void stop() {
		lift.setLiftPosition(finalPosition);
	}
}
