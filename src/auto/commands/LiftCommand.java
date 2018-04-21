package auto.commands;

import auto.ICommand;
import auto.IStopCondition;
import miyamoto.components.Lift;
import miyamoto.components.LiftPosition;
import robot.Global;

public class LiftCommand implements ICommand {
	private IStopCondition stopCondition;
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

	public LiftCommand(int finalPosition, IStopCondition stop) {
		stopCondition = stop;
		this.finalPosition = finalPosition;
	}

	public LiftCommand(LiftPosition finalPosition, IStopCondition stop) {
		stopCondition = stop;
		this.finalPosition = finalPosition.getNumber();
	}

	public void init() {
		stopCondition.init();
		lift = (Lift) Global.controlObjects.get("LIFT");
	}

	public boolean run() {
		if (lift == null) {
			init();
		}

		lift.setLiftPosition(finalPosition);

		return stopCondition.stopNow();
	}

	public void stop() {
		lift.setLiftPosition(finalPosition);
	}
	
	public String toString() {
		return "LiftCommand";
	}
}
