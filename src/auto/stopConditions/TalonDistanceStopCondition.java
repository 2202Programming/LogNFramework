package auto.stopConditions;

import java.util.List;

import auto.IStopCondition;
import comms.SmartWriter;
import miyamoto.components.LiftPosition;
import physicalOutput.motors.TalonSRXMotor;

/**
 * A stop condition based on Talon Motors
 *
 * Precondition: The Talon Motors have sensors that are in the correct start
 * position
 */
public class TalonDistanceStopCondition implements IStopCondition {
	private List<TalonSRXMotor> talons;
	private int stopPosition;

	public TalonDistanceStopCondition(List<TalonSRXMotor> talons, int finalPosition) {
		this.talons = talons;
		stopPosition = finalPosition;
	}

	public TalonDistanceStopCondition(List<TalonSRXMotor> talons, LiftPosition finalPosition) {
		this.talons = talons;
		stopPosition = finalPosition.getNumber();
	}

	public void init() {
		for (TalonSRXMotor t : talons) {
			t.reset();
		}
	}

	public boolean stopNow() {
		double sum = 0;
		for (TalonSRXMotor t : talons) {
			sum += t.getTalon().getSelectedSensorPosition(0);
		}
		SmartWriter.putD("AUTO - Talon AVG Encoder Count", sum / talons.size());
		return ((sum / talons.size()) - stopPosition) <= 1e-7;
	}
}
