package drive;

public class MotionProfile {
	private double maxacceleration, maxvelocity;
	
	/**
	 * Motion profile to be used for capping acceleration and velocity
	 * @param accel	Max acceleration to be capped at
	 * @param vel	Max velocity to be capped at
	 */
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
