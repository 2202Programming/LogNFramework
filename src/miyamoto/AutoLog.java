package miyamoto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.kauailabs.navx.frc.AHRS;

import comms.ILoggable;
import edu.wpi.first.wpilibj.Encoder;
import input.EncoderMonitor;
import input.SensorController;
import miyamoto.components.Lift;
import robot.Global;
import robot.IControl;

public class AutoLog extends IControl implements ILoggable {
	private SensorController sensors;
	private String timeStamp;

	public AutoLog() {
		sensors = SensorController.getInstance();
	}

	public void setTimeStamp() {
		// Time zone is Central Time (CST)
		timeStamp = LocalDateTime.now(ZoneId.of("CST", ZoneId.SHORT_IDS))
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
	}

	@Override
	public String getLogData() {
		AHRS navX = (AHRS) sensors.getSensor("NAVX");
		Lift lift = (Lift) Global.controlObjects.get("LIFT");
		return "Command Finished" + getEncoderData() + "NAVX Final Angle: " + navX.getYaw() + "\n"
				+ "Lift| SetPosition: " + lift.getLiftPosition() + "\t" + "Lift Counts: " + lift.getLiftCounts();
	}

	public String getEncoderData() {
		String data = "";

		EncoderMonitor monitor = (EncoderMonitor) (Global.controlObjects.get("ENCODERMONITOR"));
		HashMap<String, Encoder> encoders = monitor.getEncoders();

		for (Map.Entry<String, Encoder> entry : encoders.entrySet()) {
			String encoderName = entry.getKey();
			Encoder encoder = entry.getValue();

			data += encoderName + " Distance| Counts: " + encoder.get() + "\t" + "Inches: " + encoder.getDistance()
					+ "\n";
		}

		return data;
	}

	@Override
	public String getLogFileName() {
		return "/home/lvuser/RobotLog" + timeStamp + ".txt";
	}

}
