package auto.stopConditions;

import java.util.List;

import auto.IStopCondition;
import comms.SmartWriter;
import edu.wpi.first.wpilibj.Encoder;

public class PIDDistanceStopCondition implements IStopCondition {
	private List<Encoder> enc;
	private int distance;
	private double marginOfError;
	long initialTargetTime;
	double millisecInRange;

	/**
	 * StopCondition that uses an encoder count to inches conversion to stop
	 * 
	 * @param encoder
	 *            List of drive encoders
	 * @param inches
	 *            Distance (in inches) at which to be stopped at
	 * @param marginOfErrorInInches
	 *            The margin of error in inches
	 * @param millisecondsInRange
	 *            The time the robot has to be within the margin of error before
	 *            stopping
	 */
	public PIDDistanceStopCondition(List<Encoder> encoder, int inches, double marginOfErrorInInches,
			double millisecondsInRange) {
		enc = encoder;
		distance = inches;
		marginOfError = marginOfErrorInInches;
		millisecInRange = millisecondsInRange;
	}

	public void init() {
		for (Encoder x : enc) {
			x.reset();
		}
		initialTargetTime = Long.MAX_VALUE;
	}

	public boolean stopNow() {
		double sum = 0;
		for (Encoder x : enc) {
			sum += x.getDistance();
		}
		SmartWriter.putD("Current Distance Per Pulse", enc.get(0).getDistancePerPulse());
		SmartWriter.putD("AUTO - AVG Encoder Count", sum / enc.size());
		SmartWriter.putD("AUTO - AVG Encoder Error Count", distance - sum / enc.size());

		double error = sum / enc.size() - distance;
		boolean onTarget = Math.abs(error) <= marginOfError;

		if (onTarget) {
			long curTime = System.currentTimeMillis();

			if (curTime < initialTargetTime) {
				initialTargetTime = curTime;
			} else {
				return Math.abs(curTime - initialTargetTime) >= millisecInRange;
			}
		} else {
			initialTargetTime = Long.MAX_VALUE;
		}
		return false;
	}
}
