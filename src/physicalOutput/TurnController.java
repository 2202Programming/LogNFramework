package physicalOutput;

import edu.wpi.first.wpilibj.PIDOutput;
import physicalOutput.motors.IMotor;

public class TurnController implements PIDOutput {
	IMotor left;
	IMotor right;

	/**
	 * Provides an interface for PIDController to turn with motors
	 * @param leftMotor The left IMotor
	 * @param rightMotor The right IMotor
	 */
	public TurnController(IMotor leftMotor, IMotor rightMotor) {
		left = leftMotor;
		right = rightMotor;
	}

	@Override
	public void pidWrite(double output) {
		left.set(output);
		right.set(-output);
	}
}
