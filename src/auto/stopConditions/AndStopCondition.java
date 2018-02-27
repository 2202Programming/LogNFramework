package auto.stopConditions;

import auto.IStopCondition;

public class AndStopCondition implements IStopCondition {
	IStopCondition first;
	IStopCondition second;

	/**
	 * Takes two parameters and stops when both conditions are satisfied
	 * @param firstCondition 	First StopCondition to be satisfied
	 * @param secondCondition	Second StopCondition to be satisfied
	 */
	public AndStopCondition(IStopCondition firstCondition, IStopCondition secondCondition) {
		first = firstCondition;
		second = secondCondition;
	}

	public void init() {
		first.init();
		second.init();
	}

	public boolean stopNow() {
		return first.stopNow() && second.stopNow();
	}
}
