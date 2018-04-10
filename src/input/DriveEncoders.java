package input;

import java.util.List;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class DriveEncoders implements PIDSource {
	private List<Encoder> encoders;

	public DriveEncoders(List<Encoder> encoders) {
		this.encoders = encoders;
	}

	public void reset() {
		for (Encoder e : encoders) {
			e.reset();
		}
	}

	public List<Encoder> getEncoders() {
		return encoders;
	}

	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {
		for (Encoder e : encoders) {
			e.setPIDSourceType(pidSource);
		}
	}

	@Override
	public PIDSourceType getPIDSourceType() {
		return encoders.get(0).getPIDSourceType();
	}

	@Override
	public double pidGet() {
		double sum = 0;
		for (Encoder e : encoders) {
			sum += e.pidGet();
		}

		return sum / encoders.size();
	}
}
