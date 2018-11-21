package physicalOutput.motors;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

//import com.ctre.CANTalon;
//import com.ctre.CANTalon.FeedbackDevice;
//import com.ctre.CANTalon.TalonControlMode;

import comms.DebugMode;
import comms.SmartWriter;

public class ShooterTalonSRX extends IMotor {

	//old CANTalon part;
	TalonSRX part;
	private boolean hasEncoder;
	int slotIdx =0;     //same as profile 0
	int timeoutMS = 0;  //no blocking or checking on SRX config

	public ShooterTalonSRX(int id, boolean reverse) {
		super(reverse);
		part = new TalonSRX(id);
		//was part.setPID(0.001, 0, 0);
		part.config_kF(slotIdx, 0.0, timeoutMS);  
		part.config_kP(slotIdx, 0.001, timeoutMS);
		part.config_kI(slotIdx, 0.0, timeoutMS);  
		part.config_kD(slotIdx, 0.0, timeoutMS); 
		part.set(ControlMode.PercentOutput, 0.0);    //was  PercentVbus);
		hasEncoder = false;
	}

	public ShooterTalonSRX(int x, boolean reverse, boolean hasEncoder) {
		this(x, reverse);
		
		if (hasEncoder) {
			
			// part.config_Profile(0);  use slotID now
			part.config_kF(slotIdx, 0.025, timeoutMS);   // was setF(0.025);//0.001);
			part.config_kP(slotIdx, 0.3, timeoutMS);     // was setP(0.3);//0.1);
			part.config_kI(slotIdx, 0.00, timeoutMS);    //0.0001); Dont use this
			part.config_kD(slotIdx, 0.0000, timeoutMS);  //0
			part.configAllowableClosedloopError(slotIdx, 0);   //dpl 11/20/18 ### zero not a good idea

			part.configPeakOutputForward(1.0);      //configPeakOutputVoltage(0, -12);
			part.configPeakOutputReverse(0.0);      //configPeakOutputVoltage(0, -12);
			
			// waspart.setFeedbackDevice(FeedbackDevice.QuadEncoder);
			part.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
			//uses sensor units now //was part.configEncoderCodesPerRev(4096);
			// 1024 for analog sensors *4 for quadrature edge
			
			part.setInverted(!reverse);                         //was  reverseSensor(!reverse);
			//new TalonSRX uses set with a ControlMode and value to avoid confusion.
			part.set(ControlMode.Velocity, 0.0);      //was  changeControlMode(cm.Speed);

			// limit the error wind up, greater than IZone, zeros the integrator.
			//
			// err= 500RPM (50r/min)*(4096U/rev)*(1/60 min/s)*(1/10 s/100ms)
			// = 3410 U/100ms (U is pulses or native units)
			//
			//part.setIZone(0); // should be in native-units (pulses/100ms)
			// Izone seems to break the CL control by zeroing the iAcc when the err gets to zero
			// this feels like a bug in the SRX. - Derek
			
			part.getSensorCollection().setQuadraturePosition(0, timeoutMS); //was part.setEncPosition(0);
			part.setIntegralAccumulator(0.0);  //was ClearIaccum();
		}

		this.hasEncoder = hasEncoder;
	}

	public void teleopInit() {
		super.teleopInit();
		if (hasEncoder) {
			part.getSensorCollection().setQuadraturePosition(0, timeoutMS); //was part.setEncPosition(0);
			part.setIntegralAccumulator(0.0);  //was ClearIaccum();
			part.clearStickyFaults();
			part.configAllowableClosedloopError(slotIdx, 0);
		}
	}

	@Override
	protected void setMotor(double speed) {
		part.set(ControlMode.Velocity, speed);
		SmartWriter.putD("ShooterSpeed", this.getSpeed(),DebugMode.COMPETITION);
		SmartWriter.putD("Shooter Error", speed - this.getSpeed(), DebugMode.COMPETITION);
		
	}

	public double getSpeed() {
		//  600/sensorunitsperrotation --> rpm
		return (double)(part.getSelectedSensorVelocity()*600)/4096.0;   //return int now  (units/100ms),  was double getSpeed();
	}
}
