package physicalOutput.motors;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class TalonSRXMotor extends IMotor {
	private TalonSRX part;
	private ControlMode mode;
	private int countsPerRotation;
	private boolean useInches;
	private double countsPerInch;
	
	/**
	 * Constructs a TalonSRX that uses percent power similar to regular motors
	 * @param port
	 * @param reverse
	 */
	public TalonSRXMotor(int port, boolean reverse){
		super(reverse);
		part = new TalonSRX(port);
		mode = ControlMode.PercentOutput;
		countsPerRotation = 0;
		useInches = false;
		countsPerInch = 1;
	}
	
	/**
	 * Constructs a TalonSRX that uses a quad encoder and a closed control loop
	 * @param port the talon id
	 * @param reverse
	 * @param kp
	 * @param ki
	 * @param kd
	 * @param kf
	 */
	public TalonSRXMotor(int port, boolean reverse, double kp, double ki, double kd, double kf){
		this(port,reverse);
		mode = ControlMode.Position;
		part.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
		part.config_kP(0, kp, 0);
		part.config_kI(0, ki, 0);
		part.config_kD(0, kd, 0);
		part.config_kF(0, kf, 0);
		countsPerRotation = 4096;
		part.setIntegralAccumulator(0.0,0,0);
		part.setSelectedSensorPosition(0, 0, 0);
	}
	
	/**
	 * Sets the mode to run the Talon at
	 * @param mode the ControlMode to set
	 */
	public void setMode(ControlMode mode){
		this.mode = mode;
	}
	
	/**
	 * Resets the encoder position and integral accumulator to 0
	 */
	public void reset(){
		part.setIntegralAccumulator(0.0,0,0);
		part.setSelectedSensorPosition(0, 0, 0);
	}
	
	/**
	 * Sets up the motor to use inches as its set instead of encoder counts. Used mostly for position mode
	 * @param useInches true tells the motor to use inches. False will use encoder value
	 * @param circumference the circumference of the wheel
	 */
	public void useInches(boolean useInches, double circumference){
		this.useInches = useInches;
		countsPerInch = countsPerRotation/circumference;
	}
	
	/**
	 * Used to get the sensor so custom setup can be used
	 * @return the motor object
	 */
	public TalonSRX getTalon(){
		return part;
	}
	
	/**
	 * Sets the counts per rotation for the encoder. Default for quadrature is 4096
	 * @param counts the counts per rotation
	 */
	public void setCountsPerRotation(int counts){
		countsPerRotation = counts;
	}
	
	@Override
	protected void setMotor(double x) {
		if(useInches){
			part.set(mode, x*countsPerInch);
		}else{
			part.set(mode, x);
		}
	}

}
