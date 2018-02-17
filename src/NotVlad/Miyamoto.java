package NotVlad;

import java.util.HashMap;
import java.util.Map;

import com.kauailabs.navx.frc.AHRS;

import NotVlad.components.Climber;
import NotVlad.components.Intake;
import NotVlad.components.Lift;
import comms.SmartWriter;
import drive.IDrive;
import drive.MotionProfile;
import drive.MotionProfiler;
import drive.TwoStickDrive;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SPI.Port;
import input.EncoderMonitor;
import input.SensorController;
import physicalOutput.TurnController;
import physicalOutput.motors.ChainMotor;
import physicalOutput.motors.IMotor;
import physicalOutput.motors.SparkMotor;
import physicalOutput.motors.TalonSRXMotor;
import robot.Global;
import robot.IControl;
import robotDefinitions.RobotDefinitionBase;

/**
 * The Piper implementation of IDefinition.<br>
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

		// Default Motor Pins
		_properties.put("FLMOTORPIN", "3");
		_properties.put("BLMOTORPIN", "2");
		_properties.put("FRMOTORPIN", "1");
		_properties.put("BRMOTORPIN", "0");
		_properties.put("CLIMBMOTORPIN", "4");
		_properties.put("INTAKELEFTPIN", "5");
		_properties.put("INTAKERIGHTPIN", "6");
	}

	/***
	 * 
	 * @return Control object map for Miyamoto
	 */
	public Map<String, IControl> loadControlObjects() {

		SmartWriter.putS("Robot is miyamoto...", "asdf");
		// Create map to store public objects
		Map<String, IControl> iControlMap = super.loadControlObjects();

		Global.controllers = new MiyamotoControl();

		// Encoder stuff
		Encoder encoder0 = new Encoder(10, 11, true); // Right
		Encoder encoder1 = new Encoder(12, 13); // Left
		encoder0.setDistancePerPulse(0.05318);
		encoder1.setDistancePerPulse(0.05321);

		SensorController sensorController = SensorController.getInstance();
		AHRS navX = new AHRS(Port.kMXP);
		System.out.println(navX == null);
		sensorController.registerSensor("NAVX", navX);

		EncoderMonitor encoderMonitor = new EncoderMonitor();
		encoderMonitor.add("ENCODER0", encoder0);
		encoderMonitor.add("ENCODER1", encoder1);
		iControlMap.put("ENCODERMONITOR", encoderMonitor);

		sensorController.registerSensor("ENCODER0", encoder0);
		sensorController.registerSensor("ENCODER1", encoder1);
		sensorController.registerSensor("INTAKE", new DigitalInput(2));

		IMotor FL = new SparkMotor(getInt("FLMOTORPIN"), false);
		IMotor FR = new SparkMotor(getInt("FRMOTORPIN"), true);
		IMotor BL = new SparkMotor(getInt("BLMOTORPIN"), false);
		IMotor BR = new SparkMotor(getInt("BRMOTORPIN"), true);
		
		TurnController turnController = new TurnController(new ChainMotor(FR, BR), new ChainMotor(FL, BL));
		iControlMap.put("TURNCONTROLLER", turnController);
		
		IDrive drive=new TwoStickDrive(new ChainMotor(FR,BR), new ChainMotor(FL, BL),4,false);
		iControlMap.put(RobotDefinitionBase.DRIVENAME, drive);
		MotionProfile[] profiles = {
				new MotionProfile(0.1,1),
				new MotionProfile(0.1,0.6)
				};
		MotionProfiler sneak = new MotionProfiler(drive,profiles);
		ReverseDrive reverse = new ReverseDrive(drive);
		
		IMotor climbMotor = new SparkMotor(getInt("CLIMBMOTORPIN"), true);
		Climber climber = new Climber(climbMotor);

		IMotor intakeLeft = new SparkMotor(getInt("INTAKELEFTPIN"), true);
		IMotor intakeRight = new SparkMotor(getInt("INTAKERIGHTPIN"), true);
		Intake intake = new Intake(intakeLeft, intakeRight);
		iControlMap.put("INTAKE", intake);

		TalonSRXMotor liftMotor = new TalonSRXMotor(11, true, 0.1, 0.0, 0.0, 0.0);
		iControlMap.put("LIFT_TALON", liftMotor);
		Lift lift = new Lift(liftMotor);
		iControlMap.put("LIFT", lift);

		AutoRunner AR = new AutoRunner();
		iControlMap.put("AutoRunner", AR);

		return iControlMap;
	}

}