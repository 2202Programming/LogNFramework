package miyamoto;

import java.util.HashMap;
import java.util.Map;

import com.kauailabs.navx.frc.AHRS;

import comms.LogWriter;
import comms.SmartWriter;
import drive.IDrive;
import drive.MotionProfiler;
import drive.TwoStickDrive;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.SerialPort;
import input.EncoderMonitor;
import input.NavXMonitor;
import input.SensorController;
import miyamoto.components.Intake;
import physicalOutput.motors.ChainMotor;
import physicalOutput.motors.IMotor;
import physicalOutput.motors.SparkMotor;
import robot.Global;
import robot.IControl;
import robotDefinitions.RobotDefinitionBase;

/**
 * The NotVlad (basically Piper) implementation of IDefinition.<br>
 * <br>
 * Comments are in IDefinition
 */
public class NotVlad extends RobotDefinitionBase {

	protected boolean useXML() {
		return false;
	}

	protected String loadDefinitionName() {
		return "NOTVLAD";
	}

	protected void loadManualDefinitions() {
		_properties = new HashMap<String, String>();

		// Default Motor Pins
		_properties.put("FLMOTORPIN", "3");
		_properties.put("BLMOTORPIN", "4");
		_properties.put("FRMOTORPIN", "1");
		_properties.put("BRMOTORPIN", "2");
		// _properties.put("CLIMBMOTORPIN", "4");
		_properties.put("INTAKELEFTPIN", "6");
		_properties.put("INTAKERIGHTPIN", "7");
	}

	/***
	 * 
	 * @return Control object map for Miyamoto
	 */
	public Map<String, IControl> loadControlObjects() {

		SmartWriter.putS("Robot is notvlad...", "asdf");
		// Create map to store public objects
		Map<String, IControl> iControlMap = super.loadControlObjects();

		Global.controllers = new MiyamotoControl();

		// Encoder stuff
		Encoder encoder0 = new Encoder(0, 1, true);
		Encoder encoder1 = new Encoder(2, 3, false);
		encoder0.setDistancePerPulse(0.062875);
		encoder1.setDistancePerPulse(0.063685);
		EncoderMonitor encoderMonitor = new EncoderMonitor();
		encoderMonitor.add("ENCODER0", encoder0);
		encoderMonitor.add("ENCODER1", encoder1);
		iControlMap.put("ENCODERMONITOR", encoderMonitor);

		SensorController sensorController = SensorController.getInstance();
		sensorController.registerSensor("ENCODER0", encoder0);
		sensorController.registerSensor("ENCODER1", encoder1);

		// Create NavX
		AHRS navX = new AHRS(Port.kMXP);
		sensorController.registerSensor("NAVX", navX);
		NavXMonitor navXMonitor = new NavXMonitor();
		navXMonitor.add("NAVX", navX);

		IMotor FL = new SparkMotor(getInt("FLMOTORPIN"), false);
		IMotor FR = new SparkMotor(getInt("FRMOTORPIN"), true);
		IMotor BL = new SparkMotor(getInt("BLMOTORPIN"), false);
		IMotor BR = new SparkMotor(getInt("BRMOTORPIN"), true);

		IDrive drive = new TwoStickDrive(new ChainMotor(FR, BR), new ChainMotor(FL, BL), 4, false);
		iControlMap.put(RobotDefinitionBase.DRIVENAME, drive);
		MotionProfiler sneak = new MotionProfiler();

		// IMotor climbMotor = new SparkMotor(getInt("CLIMBMOTORPIN"), true);
		// Climber climber = new Climber(climbMotor);

		IMotor intakeLeft = new SparkMotor(getInt("INTAKELEFTPIN"), true);
		IMotor intakeRight = new SparkMotor(getInt("INTAKERIGHTPIN"), true);
		Intake intake = new Intake(intakeLeft, intakeRight);

		iControlMap.put("INTAKE", intake);

		// TalonSRXMotor liftMotor = new TalonSRXMotor(11,true,0.1,0.0,0.0,0.0);
		// Lift lift = new Lift(liftMotor);
		

		AutoRunner AR = new AutoRunner();
		iControlMap.put("AutoRunner", AR);
		
		MotionProfiler profiler = new MotionProfiler();
		iControlMap.put("PROFILER", profiler);
		
		AutoLog AL = new AutoLog();
		LogWriter.registerLoggable("AutonomousLog", AL);

		return iControlMap;
	}

}
