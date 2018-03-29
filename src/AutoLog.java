import comms.ILoggable;
import edu.wpi.first.wpilibj.Encoder;
import input.SensorController;

public class AutoLog implements ILoggable {
	private SensorController sensors;
	
	public AutoLog(){
		sensors = SensorController.getInstance();
	}
	
	@Override
	public String getLogData() {
		return "Encoder 0: " + ((Encoder)sensors.getSensor("ENCODER0")).get() + "/nEncoder 1: " + ((Encoder)sensors.getSensor("ENCODER1"));
	}

	@Override
	public String getLogFileName() {
		return "/RobotLog";
	}

}
