package miyamoto;

import com.kauailabs.navx.frc.AHRS;

import comms.ILoggable;
import edu.wpi.first.wpilibj.Encoder;
import input.SensorController;
import robot.IControl;

public class AutoLog extends IControl implements ILoggable {
	private SensorController sensors;
	private String timeStamp;

	public AutoLog() {
		sensors = SensorController.getInstance();
	}

	public void autonomousInit() {
		timeStamp = System.currentTimeMillis() + "";
	}

	@Override
	public String getLogData() {
		Encoder encoder0 = (Encoder) sensors.getSensor("ENCODER0");
		Encoder encoder1 = (Encoder) sensors.getSensor("ENCODER1");
		AHRS navX = (AHRS) sensors.getSensor("NAVX");
		return "DriveAtAngle Command Finished" + "\n" + "Encoder0 Distance| Counts: " + encoder0.get() + "\t"
				+ "Inches: " + encoder0.getDistance() + "\n" + "Encoder1 Distance| Counts: " + encoder1.get() + "\t"
				+ "Inches: " + encoder1.getDistance() + "\n" + "Final Angle: " + navX.getYaw();
	}

	@Override
	public String getLogFileName() {
		return "/home/lvuser/RobotLog" + timeStamp + ".txt";
	}

}
