package auto.stopConditions;

import java.util.List;
import auto.IStopCondition;
import comms.SmartWriter;
import edu.wpi.first.wpilibj.Encoder;

public class DistanceStopCondition implements IStopCondition {
	private List<Encoder> enc;
	private int duration;

	public DistanceStopCondition(List<Encoder> encoder, int inches) {
		enc = encoder;
		duration = inches;
	}

	public void init() {
		for (Encoder x : enc) {
			x.reset();
		}
	}

	public boolean stopNow() {
		double sum = 0;
		for (Encoder x : enc) {
			sum += x.getDistance();
		}
		SmartWriter.putD("Current Distance Per Pulse", enc.get(0).getDistancePerPulse());
		SmartWriter.putD("AUTO - AVG Encoder Count", sum / enc.size());
		return (sum / enc.size()) > duration;
	}

	public boolean stopNow1() { // Used for one encoder
		double sum = 0;

		sum += enc.get(1).getDistance();

		SmartWriter.putD("Current Distance Per Pulse", enc.get(1).getDistancePerPulse());
		SmartWriter.putD("AUTO - AVG Encoder Count", sum);
		return (sum) > duration;
	}

}
