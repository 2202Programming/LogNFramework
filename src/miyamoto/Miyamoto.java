package miyamoto;

import java.util.HashMap;
import java.util.Map;

import com.kauailabs.navx.frc.AHRS;

import LED.LEDController;
import comms.LogWriter;
import comms.SmartWriter;
import drive.IDrive;
import drive.MotionProfile;
import drive.MotionProfiler;
import drive.TwoStickDrive;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SPI.Port;
import input.EncoderMonitor;
import input.NavXMonitor;
import input.SensorController;
import miyamoto.components.Climber;
import miyamoto.components.Intake;
import miyamoto.components.Lift;
import physicalOutput.TurnController;
import physicalOutput.motors.ChainMotor;
import physicalOutput.motors.IMotor;
import physicalOutput.motors.SparkMotor;
import physicalOutput.motors.TalonSRXMotor;
import robot.Global;
import robot.IControl;
import robotDefinitions.RobotDefinitionBase;

/**
 * The Miyamoto implementation of IDefinition.<br>
 * <br>
 * Comments are in IDefinition
 */
public class Miyamoto extends RobotDefinitionBase {

	protected boolean useXML() {
		return false;
	}

	protected String loadDefinitionName() {
		return "MIYAMOTO";
	}

	protected void loadManualDefinitions() {
		_properties = new HashMap<String, String>();

		// Old Motor Pins
		_properties.put("FLMOTORPIN", "3");
		_properties.put("BLMOTORPIN", "2");
		_properties.put("FRMOTORPIN", "1");
		_properties.put("BRMOTORPIN", "0");
		_properties.put("CLIMBMOTORPIN", "4");
		_properties.put("INTAKELEFTPIN", "5");
		_properties.put("INTAKERIGHTPIN", "6");

		// New Motor Pins
		// _properties.put("FLMOTORPIN", "0");
		// _properties.put("BLMOTORPIN", "1");
		// _properties.put("FRMOTORPIN", "3");
		// _properties.put("BRMOTORPIN", "2");
		// _properties.put("CLIMBMOTORPIN", "4");
		// _properties.put("INTAKELEFTPIN", "5");
		// _properties.put("INTAKERIGHTPIN", "6");
	}

	/***
	 * 
	 * @return Control object map for Miyamoto
	 */
	public Map<String, IControl> loadControlObjects() {

		SmartWriter.putS("Robot is miyamoto...", "2018");
		// Create map to store public objects
		Map<String, IControl> iControlMap = super.loadControlObjects();

		Global.controllers = new MiyamotoControl();

		CameraServer.getInstance().startAutomaticCapture();
		// Create sensor map
		SensorController sensorController = SensorController.getInstance();
		// Create the NavX
		AHRS navX = new AHRS(Port.kMXP);
		sensorController.registerSensor("NAVX", navX);
		NavXMonitor navXMonitor = new NavXMonitor();
		navXMonitor.add("NAVX", navX);

		// Encoder stuff
		double CPI = 19.04; // use same calibration for left/right.Sensors are very very close. - DPL/TG
							// 3/28
		Encoder encoder0 = new Encoder(0, 1, true, EncodingType.k4X);// Right Changed from 0,1
		Encoder encoder0b = new Encoder(7, 8, true, EncodingType.k4X);
		Encoder encoder1 = new Encoder(2, 3, true, EncodingType.k4X); // Left Changed from 2,3
		Encoder encoder1b = new Encoder(5, 6, true, EncodingType.k4X); // Alternate Left encoder
		encoder0.setDistancePerPulse(1.0 / CPI); // was 0.05318);
		encoder0b.setDistancePerPulse(3.0 / CPI);
		encoder1.setDistancePerPulse(1.0 / CPI); // was 0.05321);
		encoder1b.setDistancePerPulse(3.0 / CPI);
		EncoderMonitor encoderMonitor = new EncoderMonitor();
		encoderMonitor.add("ENCODER0", encoder0);
		encoderMonitor.add("ENCODER0B", encoder0b);
		encoderMonitor.add("ENCODER1", encoder1);
		encoderMonitor.add("ENCODER1B", encoder1b);
		iControlMap.put("ENCODERMONITOR", encoderMonitor);

		sensorController.registerSensor("ENCODER0", encoder0);
		sensorController.registerSensor("ENCODER0B", encoder0b);
		sensorController.registerSensor("ENCODER1", encoder0);
		sensorController.registerSensor("ENCODER1B", encoder1b);
		sensorController.registerSensor("INTAKE", new DigitalInput(4));

		IMotor FL = new SparkMotor(getInt("FLMOTORPIN"), false);
		IMotor FR = new SparkMotor(getInt("FRMOTORPIN"), true);
		IMotor BL = new SparkMotor(getInt("BLMOTORPIN"), false);
		IMotor BR = new SparkMotor(getInt("BRMOTORPIN"), true);

		ChainMotor left = new ChainMotor(FR, BR);
		ChainMotor right = new ChainMotor(FL, BL);

		TurnController turnController = new TurnController(left, right);
		iControlMap.put("TURNCONTROLLER", turnController);

		IDrive drive = new TwoStickDrive(left, right, 1, false);
		iControlMap.put(RobotDefinitionBase.DRIVENAME, drive);

		MotionProfile[] profiles = { new MotionProfile(0.08, 1), new MotionProfile(0.05, 1),
				new MotionProfile(0.05, 0.6), new MotionProfile(0.03, 0.4), new MotionProfile(0.03, 0.3) };
		MotionProfiler driveProfiler = new MotionProfiler(profiles);
		iControlMap.put("PROFILER", driveProfiler);

		IMotor climbMotor = new SparkMotor(getInt("CLIMBMOTORPIN"), false);
		Climber climber = new Climber(climbMotor);

		IMotor intakeLeft = new SparkMotor(getInt("INTAKELEFTPIN"), true);
		IMotor intakeRight = new SparkMotor(getInt("INTAKERIGHTPIN"), true);
		Intake intake = new Intake(intakeLeft, intakeRight);
		iControlMap.put("INTAKE", intake);

		TalonSRXMotor liftMotor = new TalonSRXMotor(11, true, 0.2, 0.0, 0.0, 0.0);
		iControlMap.put("LIFT_TALON", liftMotor);
		Lift lift = new Lift(liftMotor);
		iControlMap.put("LIFT", lift);

		LEDController LED = new LEDController();

		AutomationController AC = new AutomationController(lift, driveProfiler);
		iControlMap.put("AC", AC);

		AutoRunner AR = new AutoRunner();
		iControlMap.put("AutoRunner", AR);

		LogWriter.registerLoggable("AutonomousLog", new AutoLog());

		return iControlMap;
	}

}