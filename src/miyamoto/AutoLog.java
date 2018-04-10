package miyamoto;
import comms.ILoggable;
import edu.wpi.first.wpilibj.Encoder;
import input.SensorController;
import robot.IControl;

public class AutoLog extends IControl implements ILoggable {
	private SensorController sensors;
	private String timeStamp;
	
	public AutoLog(){
		sensors = SensorController.getInstance();
	}
	
	public void autonomousInit(){
		timeStamp = System.currentTimeMillis() + "";
	}
	
	@Override
	public String getLogData() {
		return "Encoder 0: " + ((Encoder)sensors.getSensor("ENCODER0")).get() + "/nEncoder 1: " + ((Encoder)sensors.getSensor("ENCODER1"));
	}

	@Override
	public String getLogFileName() {
		return "/RobotLog" + timeStamp;
	}

}
