package auto.stopConditions;

import java.util.List;
import auto.IStopCondition;
import comms.SmartWriter;
import physicalOutput.motors.TalonSRXMotor;

/**
 * A stop condition based on Talon Motors
 *
 * Precondition: The Talon Motors have sensors that are in the correct start
 * position
 */
public class TalonDistanceStopCondition implements IStopCondition {
	private List<TalonSRXMotor> talons;
	private int duration;

	public TalonDistanceStopCondition(List<TalonSRXMotor> talons, int inches) {
		this.talons = talons;
		duration = inches;
	}

	public void init() {
		for (TalonSRXMotor t : talons) {
			t.getTalon().setIntegralAccumulator(0.0, 0, 0);
			t.getTalon().setSelectedSensorPosition(0, 0, 0);
		}
	}

	public boolean stopNow() {
		double sum = 0;
		for (TalonSRXMotor t : talons) {
			sum += t.getTalon().getSelectedSensorPosition(0);
		}
		SmartWriter.putD("AUTO - Talon AVG Encoder Count", sum / talons.size());
		return (sum / talons.size()) > duration;
	}
}
