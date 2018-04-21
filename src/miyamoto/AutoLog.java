package miyamoto;

import com.kauailabs.navx.frc.AHRS;

import comms.ILoggable;
import edu.wpi.first.wpilibj.Encoder;
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

	public void autonomousInit() {
		timeStamp = System.currentTimeMillis() + "";
	}

	@Override
	public String getLogData() {
		Encoder encoder0 = (Encoder) sensors.getSensor("ENCODER0");
		Encoder encoder1 = (Encoder) sensors.getSensor("ENCODER1");
		AHRS navX = (AHRS) sensors.getSensor("NAVX");
		Lift lift = (Lift) Global.controlObjects.get("LIFT");
		return "Command Finished" + "\n" + "Encoder0 Distance| Counts: " + encoder0.get() + "\t" + "Inches: "
				+ encoder0.getDistance() + "\n" + "Encoder1 Distance| Counts: " + encoder1.get() + "\t" + "Inches: "
				+ encoder1.getDistance() + "\n" + "NAVX Final Angle: " + navX.getYaw() + "\n" + "Lift| SetPosition: "
				+ lift.getLiftPosition() + "\t" + "Lift Counts: " + lift.getLiftCounts();
	}

	@Override
	public String getLogFileName() {
		return "/home/lvuser/RobotLog" + timeStamp + ".txt";
	}

}
