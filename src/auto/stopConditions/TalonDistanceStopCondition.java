package auto.stopConditions;

import java.util.List;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import auto.IStopCondition;
import comms.SmartWriter;

/**
 * A stop condition based on Talon Sensors
 *
 * Precondition: The Talons have sensors that are in the correct start position
 */
public class TalonDistanceStopCondition implements IStopCondition {
	private List<TalonSRX> talons;
	private int duration;

	public TalonDistanceStopCondition(List<TalonSRX> talons, int inches) {
		this.talons = talons;
		duration = inches;
	}

	public void init() {
		for (TalonSRX t : talons) {
			t.setIntegralAccumulator(0.0, 0, 0);
			t.setSelectedSensorPosition(0, 0, 0);
		}
	}

	public boolean stopNow() {
		double sum = 0;
		for (TalonSRX t : talons) {
			sum += t.getSelectedSensorPosition(0);
		}
		SmartWriter.putD("AUTO - Talon AVG Encoder Count", sum / talons.size());
		return (sum / talons.size()) > duration;
	}
}
