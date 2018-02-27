package auto.stopConditions;

import auto.IStopCondition;

public class OrStopCondition implements IStopCondition {
	IStopCondition first;
	IStopCondition second;

	/**
	 * Takes two parameters and stops when either conditions are satisfied
	 * @param firstCondition
	 * @param secondCondition
	 */
	public OrStopCondition(IStopCondition firstCondition, IStopCondition secondCondition) {
		first = firstCondition;
		second = secondCondition;
	}

	public void init() {
		first.init();
		second.init();
	}

	public boolean stopNow() {
		return first.stopNow() || second.stopNow();
	}
}
