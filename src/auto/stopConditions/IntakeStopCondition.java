package auto.stopConditions;

import auto.IStopCondition;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SensorBase;
import input.SensorController;
import miyamoto.components.Intake;

public class IntakeStopCondition implements IStopCondition {
	private Intake intake;
	private DigitalInput photogate;
	
	public IntakeStopCondition() {
		photogate = (DigitalInput) SensorController.getInstance().getSensor("INTAKE");
	}

	@Override
	public void init() {
	}

	@Override
	public boolean stopNow() {
		return photogate.get();
	}
}
