package drive;

public class MotionProfile {
	private double maxacceleration, maxvelocity;
	public MotionProfile(double accel, double vel){
		maxacceleration = accel;
		maxvelocity = vel;
	}
	public double getAcceleration(){
		return maxacceleration;
	}
	public double getVelocity(){
		return maxvelocity;
	}
}
