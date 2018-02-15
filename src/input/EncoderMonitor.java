package input;

import java.util.HashMap;
import java.util.Map.Entry;

import com.kauailabs.navx.frc.AHRS;

import comms.DebugMode;
import comms.SmartWriter;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Ultrasonic;
import robot.IControl;

public class EncoderMonitor extends IControl {

	HashMap<String, Encoder> encoders;
	AHRS navX;

	/**
	 * This class will print the values of all encoders during all modes<br>
	 * Also it will reset during init phases
	 */
	public EncoderMonitor() {
		encoders = new HashMap<String, Encoder>();
		navX = (AHRS) (SensorController.getInstance().getSensor("NAVX"));
	}

	public void add(String name, Encoder e) {
		encoders.put(name, e);
	}

	/**
	 * Print the encoder values to smart dashboard
	 */
	public void teleopPeriodic() {
		// Ultrasonic dist =
		// (Ultrasonic)SensorController.getInstance().getSensor("DISTANCESENSOR");
		// SmartWriter.putD("DistanceSensorDistance", dist.getRangeInches());
		// SmartWriter.putD("DistanceInMilli", dist.getRangeMM());
		for (Entry<String, Encoder> entry : encoders.entrySet()) {
			SmartWriter.putD(entry.getKey(), entry.getValue().get(), DebugMode.COMPETITION);
			SmartWriter.putD(entry.getKey() + " DISTANCE", entry.getValue().getDistance(), DebugMode.COMPETITION);
		}

		SmartWriter.putD("NavXAngleTeleop", navX.getYaw(), DebugMode.DEBUG);
		System.out.println("navX Angle: " + navX.getYaw());
	}

	/**
	 * Print the encoder values to smart dashboard
	 */
	public void autonomousPeriodic() {
		for (Entry<String, Encoder> entry : encoders.entrySet()) {
			SmartWriter.putD(entry.getKey(), entry.getValue().get(), DebugMode.COMPETITION);
			SmartWriter.putD(entry.getKey() + " DISTANCE", entry.getValue().getDistance(), DebugMode.COMPETITION);
		}
		SmartWriter.putD("NavXAngleAuto", navX.getYaw(), DebugMode.DEBUG);
		System.out.println("navX Angle: " + navX.getYaw());
	}

	/**
	 * Reset all of the encoders
	 */
	public void teleopInit() {
		for (Entry<String, Encoder> entry : encoders.entrySet()) {
			entry.getValue().reset();
		}
		navX.reset();
	}

	/**
	 * Reset all of the encoders
	 */
	public void autonomousInit() {
		for (Entry<String, Encoder> entry : encoders.entrySet()) {
			entry.getValue().reset();
		}
		navX.reset();
	}

	/**
	 * Reset all of the encoders
	 */
	public void disabledInit() {
		for (Entry<String, Encoder> entry : encoders.entrySet()) {
			entry.getValue().reset();
		}
	}

	public HashMap<String, Encoder> getEncoders() {
		return encoders;
	}
}
