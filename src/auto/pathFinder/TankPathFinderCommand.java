package auto.pathFinder;

import com.kauailabs.navx.frc.AHRS;

import auto.ICommand;
import drive.DriveControl;
import drive.IDrive;
import edu.wpi.first.wpilibj.Encoder;
import input.SensorController;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;
import robot.Global;
import robotDefinitions.RobotDefinitionBase;

public class TankPathFinderCommand implements ICommand {
	private final int ENCODER_TICKS_PER_REVOLUTION = 360;
	private final double REFRESH_RATE = .02;
	private TankModifier path;
	private Encoder leftEncoder;
	private Encoder rightEncoder;
	private EncoderFollower left;
	private EncoderFollower right;
	private IDrive drive;
	private AHRS navX;

	/**
	 * 
	 * @param points
	 *            The points on the field that represent the path. It includes the
	 *            position in meters and the angle in degrees
	 * @param wheelDistance
	 *            The outside distance between the left and right wheels
	 * @param wheelDiameter
	 *            Wheel diameter in meters
	 */
	public TankPathFinderCommand(Waypoint[] points, double maxVelocity, double maxAcceleration, double maxJerk,
			double wheelDistance, double wheelDiameter, double proportionalGain, double integralGain,
			double dervativeGain, double accelerationGain) {
		// Create the Trajectory Configuration
		//
		// Arguments:
		// Fit Method: HERMITE_CUBIC or HERMITE_QUINTIC
		// Sample Count: SAMPLES_HIGH (100 000)
		// SAMPLES_LOW (10 000)
		// SAMPLES_FAST (1 000)
		// Time Step: 0.02 Seconds
		// Max Velocity: 1.7 m/s
		// Max Acceleration: 2.0 m/s/s
		// Max Jerk: 60.0 m/s/s/s
		Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC,
				Trajectory.Config.SAMPLES_HIGH, REFRESH_RATE, maxVelocity, maxAcceleration, maxJerk);
		Trajectory trajectory = Pathfinder.generate(points, config);
		path = new TankModifier(trajectory).modify(wheelDistance);
		left = new EncoderFollower(path.getLeftTrajectory());
		right = new EncoderFollower(path.getRightTrajectory());

		// TODO: Make the encoders dynamic with the robot
		leftEncoder = (Encoder) (SensorController.getInstance().getSensor("ENCODER0"));
		rightEncoder = (Encoder) (SensorController.getInstance().getSensor("ENCODER1"));
		left.configureEncoder(leftEncoder.get(), ENCODER_TICKS_PER_REVOLUTION, wheelDiameter);
		right.configureEncoder(rightEncoder.get(), ENCODER_TICKS_PER_REVOLUTION, wheelDiameter);

		// Configure the PID tuning
		left.configurePIDVA(proportionalGain, integralGain, dervativeGain, 1 / maxVelocity, 0);
		right.configurePIDVA(proportionalGain, integralGain, dervativeGain, 1 / maxVelocity, 0);
		System.out.println("PathFinder command created");
	}

	@Override
	public void init() {
		// Get the drive
		drive = (IDrive) Global.controlObjects.get(RobotDefinitionBase.DRIVENAME);
		drive.setDriveControl(DriveControl.EXTERNAL_CONTROL);

		// Get the gyroscope
		navX = (AHRS) (SensorController.getInstance().getSensor("NAVX"));
		navX.reset();
	}

	@Override
	public boolean run() {
		double l = left.calculate(leftEncoder.get());
		double r = right.calculate(rightEncoder.get());

		// Assuming the gyro is giving a value in degrees (-180 to 180)
		double gyro_heading = navX.getAngle() > 360 ? navX.getAngle() - 180 : navX.getAngle();
		double desired_heading = Pathfinder.r2d(left.getHeading()); // Should also be in degrees

		// boundHalfDegrees makes the angle be from -180 to 180
		double angleDifference = Pathfinder.boundHalfDegrees(desired_heading - gyro_heading);
		double turn = 0.8 * (-1.0 / 80.0) * angleDifference;
		
		//Sets the motor speeds
		drive.setLeftMotors(l + turn);
		System.out.println("Left Motor set to: " + (l + turn));
		drive.setRightMotors(r - turn);
		System.out.println("Right Motor set to: " + (r - turn));

		return false;
	}

	@Override
	public void stop() {
		drive.setLeftMotors(0);
		drive.setRightMotors(0);
		drive.setDriveControl(DriveControl.DRIVE_CONTROLLED);
	}

}
