package NotVlad;

import java.util.HashMap;
import java.util.Map;

import com.kauailabs.navx.frc.AHRS;

import NotVlad.components.Climber;
import NotVlad.components.Intake;
import NotVlad.components.Lift;
import comms.SmartWriter;
import drive.IDrive;
import drive.SneakMode;
import drive.TwoStickDrive;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SerialPort;
import input.EncoderMonitor;
import input.SensorController;
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
		Map<String, IControl> iControlMap=super.loadControlObjects();
		
		Global.controllers = new MiyamotoControl();

		//Encoder stuff
		Encoder encoder0 = new Encoder(10, 11);	//Right
		Encoder encoder1 =  new Encoder(12, 13); //Left
		encoder0.setDistancePerPulse(0.06265);
		encoder1.setDistancePerPulse(0.06265);
		EncoderMonitor encoderMonitor = new EncoderMonitor();
		encoderMonitor.add("ENCODER0", encoder0);
		encoderMonitor.
		
		add("ENCODER1", encoder1);

		SensorController sensorController = SensorController.getInstance();
		sensorController.registerSensor("ENCODER0", encoder0);
		sensorController.registerSensor("ENCODER1", encoder1);
		sensorController.registerSensor("NAVX", new AHRS(SerialPort.Port.kMXP));
		
		IMotor FL=new SparkMotor(getInt("FLMOTORPIN"), false);
		IMotor FR=new SparkMotor(getInt("FRMOTORPIN"), true);
		IMotor BL=new SparkMotor(getInt("BLMOTORPIN"), false);
		IMotor BR=new SparkMotor(getInt("BRMOTORPIN"), true);
		
		IDrive drive=new TwoStickDrive(new ChainMotor(FR,BR), new ChainMotor(FL, BL),4,false);
		iControlMap.put(RobotDefinitionBase.DRIVENAME, drive);
		SneakMode sneak = new SneakMode(drive);
		
		IMotor climbMotor = new SparkMotor(getInt("CLIMBMOTORPIN"), true);
		Climber climber = new Climber(climbMotor);
		
		IMotor intakeLeft = new SparkMotor(getInt("INTAKELEFTPIN"),true);
		IMotor intakeRight = new SparkMotor(getInt("INTAKERIGHTPIN"),true);
		Intake intake = new Intake(intakeLeft,intakeRight);
		
		TalonSRXMotor liftMotor = new TalonSRXMotor(11,true,0.1,0.0,0.0,0.0);
		Lift lift = new Lift(liftMotor);
		
		AutoRunner AR = new AutoRunner();
		iControlMap.put("AutoRunner", AR);

		return iControlMap;
	}

}