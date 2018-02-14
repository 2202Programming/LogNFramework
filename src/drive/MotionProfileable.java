package drive;

public interface MotionProfileable {
	/**
	 * Sets the max acceleration of the robot. 0 is no acceleration. 2 is max acceleration.
	 * @param maxAcceleration the max acceleration of the robot
	 */
	public void setMaxAcceleration(double maxAcceleration);
	/**
	 * Sets the max velocity of the robot between 0 and 1
	 * @param maxAcceleration the max velocity of the robot
	 */
	public void setMaxVelocity(double maxVelocity);
}
