package input;

import java.util.HashMap;
import java.util.Map.Entry;

import com.kauailabs.navx.frc.AHRS;

import comms.DebugMode;
import comms.SmartWriter;
import robot.IControl;

public class NavXMonitor extends IControl {
	HashMap<String, AHRS> navXs;

	/**
	 * This class will print the values of all NavXs during all modes<br>
	 */
	public NavXMonitor() {
		navXs = new HashMap<String, AHRS>();
	}

	public void add(String name, AHRS navX) {
		navXs.put(name, navX);
	}

	/**
	 * Print the navX values to smart dashboard
	 */
	public void teleopPeriodic() {
		for (Entry<String, AHRS> entry : navXs.entrySet()) {
			AHRS navX = entry.getValue();
			SmartWriter.putD("NavX Roll Angle", navX.getRoll(), DebugMode.DEBUG);
			SmartWriter.putD("NavX Pitch Angle", navX.getPitch(), DebugMode.DEBUG);
			SmartWriter.putD("NavX Yaw Angle", navX.getYaw(), DebugMode.COMPETITION);
		}
	}

	/**
	 * Print the navX values to smart dashboard
	 */
	public void autonomousPeriodic() {
		for (Entry<String, AHRS> entry : navXs.entrySet()) {
			AHRS navX = entry.getValue();
			SmartWriter.putD("NavX Roll Angle", navX.getRoll(), DebugMode.DEBUG);
			SmartWriter.putD("NavX Pitch Angle", navX.getPitch(), DebugMode.DEBUG);
			SmartWriter.putD("NavX Yaw Angle", navX.getYaw(), DebugMode.COMPETITION);
		}
	}

	public HashMap<String, AHRS> getNavXs() {
		return navXs;
	}
}
