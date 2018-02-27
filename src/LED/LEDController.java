package LED;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Direction;
import edu.wpi.first.wpilibj.Relay.Value;
import robot.IControl;

public class LEDController extends IControl {
	private Relay red = new Relay(0, Direction.kForward);
	private Relay blue = new Relay(1, Direction.kForward);

	private DriverStation ds = DriverStation.getInstance();

	/**
	 * Checks to see if we are on the red alliance
	 * 
	 * @return
	 */
	private boolean isRedTeam() {
		return ds.getAlliance() == Alliance.Red;
	}

	/**
	 * resets all LEDs
	 */
	private void resetLEDs() {
		red.set(Value.kOff);
		blue.set(Value.kOff);
	}

	/**
	 * Activates LEDs based on what team we are on
	 */
	private void activateLEDs() {
		// resetLEDs();
		if (isRedTeam()) {
			blue.set(Value.kOff);
			red.set(Value.kOn);
		} else {
			red.set(Value.kOff);
			blue.set(Value.kOn);
		}
	}

	public void robotInit() {
		resetLEDs();
	}

	public void teleopInit() {
		activateLEDs();
	}

	public void autonomousInit() {
		activateLEDs();
	}

	public void disabledInit() {
		resetLEDs();
	}
}
