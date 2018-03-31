package drive;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import robot.Global;
import robot.IControl;
import robotDefinitions.controls.MotionProfileableController;

/**
 * Caps the max acceleration and velocity of the robot so that it can be
 * maneuvered more easily
 * 
 * @author Daniel
 *
 */
public class MotionProfiler extends IControl {
	private MotionProfileableController controller;
	private MotionProfile[] profiles;
	private int index;
	private MotionProfileable drive;

	public MotionProfiler(IDrive drive) {
		profiles = new MotionProfile[2];
		profiles[0] = new MotionProfile(2, 1);
		index = 0;
		if (drive instanceof MotionProfileable) {
			this.drive = (MotionProfileable) drive;
		}
		if (Global.controllers instanceof MotionProfileableController) {
			controller = (MotionProfileableController) Global.controllers;
		}
	}

	public MotionProfiler(IDrive drive, MotionProfile[] profiles) {
		this(drive);
		this.profiles = profiles;
	}

	/**
	 * Sets the index for which motion profile to use
	 * 
	 * @param index
	 *            the index of the profile
	 */
	public void setProfileIndex(int index) {
		if (index >= 0 && index < profiles.length) {
			this.index = index;
		}
	}
	
	/**
	 * Gets the current motion profile that is being used
	 * @return A MotionProfile object that contains a maxAcceleration and maxVelocity
	 */
	public MotionProfile getCurrentProfile(){
		return profiles[index];
	}
	
	/**
	 * Returns a new set value given a current set value and the last set value
	 * @param lastValue the previous set value
	 * @param setValue the target value
	 * @return a new value in the direction of setValue that is no farther from the lastValue than the max acceleration of the current profile
	 */
	public double capAcceleration(double lastValue, double setValue){
		double maxAcceleration = profiles[index].getAcceleration();
		if(Math.abs(setValue-lastValue) > maxAcceleration){
			return lastValue + Math.signum(setValue-lastValue) * maxAcceleration;
		}else{
			return setValue;
		}
	}
	
	/**
	 * Makes sure that the setValue is lower than the max velocity
	 * @param setValue the target value
	 * @return a new value in the direction of setValue that is less than or equal to the max velocity of the current profile
	 */
	public double capVelocity(double setValue){
		double maxVelocity = profiles[index].getVelocity();
		if(Math.abs(setValue) > maxVelocity){
			return Math.signum(setValue) * maxVelocity;
		}else{
			return setValue;
		}
	}

	public void teleopInit() {
		index = 0;
	}

	public void teleopPeriodic() {
		if (drive != null && controller != null) {
			if (controller.cycleMotionProfile()) {
				index++;
			}
			index %= profiles.length;
			drive.setMaxAcceleration(profiles[index].getAcceleration());
			drive.setMaxVelocity(profiles[index].getVelocity());
		}
	}
	
	public void autonomousInit(){
		index = 0;
	}
	
	public void autonomousPeriodic(){
		if (drive != null) {
			drive.setMaxAcceleration(profiles[0].getAcceleration());
			drive.setMaxVelocity(profiles[0].getVelocity());
		}
	}
}
