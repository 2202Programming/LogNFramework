package LED;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Direction;
import edu.wpi.first.wpilibj.Relay.Value;
import miyamoto.MiyamotoControl;
import robot.Global;
import robot.IControl;

public class LEDController extends IControl {
	private Relay red = new Relay(0, Direction.kForward);
	private Relay blue = new Relay(1, Direction.kForward);

	private DriverStation ds = DriverStation.getInstance();
	private MiyamotoControl controller = (MiyamotoControl)Global.controllers;
	private boolean flashing = false;
	private boolean on = true;
	private int timer = 0;

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
		on = true;
		timer = 0;
		flashing = false;
	}
	
	public void teleopPeriodic(){
		if(controller.reverseDrive()){
			flashing = !flashing;
			timer = 0;
			on = true;
		}
		
		if(flashing){
			if(timer > 5){
				on = !on;
				timer = 0;
			}else{
				timer++;
			}
		}
		
		if(on){
			activateLEDs();			
		}else{
			resetLEDs();
		}
	}

	public void autonomousInit() {
		activateLEDs();
	}

	public void disabledInit() {
		resetLEDs();
	}
}
