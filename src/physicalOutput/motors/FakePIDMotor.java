package physicalOutput.motors;

import edu.wpi.first.wpilibj.PIDOutput;

public class FakePIDMotor implements PIDOutput{

	//A fake motor to get PID values 
	public FakePIDMotor() {
		
	}
	@Override
	public void pidWrite(double output) {
		return;
	}
	
}
