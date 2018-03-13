package auto.stopConditions;

import auto.IStopCondition;
import miyamoto.components.Lift;
import miyamoto.components.LiftPosition;

/**
 * A stop condition based on the Miyamoto Lift component
 *
 * Precondition: The Lift component is calibrated
 */
public class LiftStopCondition implements IStopCondition {
	private Lift lift;
	private int stopPosition;

	public LiftStopCondition(Lift liftComponent, int finalPosition) {
		lift = liftComponent;
		stopPosition = finalPosition;
	}

	public LiftStopCondition(Lift liftComponent, LiftPosition finalPosition) {
		lift = liftComponent;
		stopPosition = finalPosition.getNumber();
	}

	@Override
	public void init() {
		return;
	}

	@Override
	public boolean stopNow() {
		return Math.abs(stopPosition - lift.getLiftCounts()) <= 100;
	}

}
