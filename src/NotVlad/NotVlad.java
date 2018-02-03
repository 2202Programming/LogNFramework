package NotVlad;

import java.util.HashMap;
import java.util.Map;

import com.kauailabs.navx.frc.AHRS;

import NotVlad.components.Climber;
import NotVlad.components.Intake;
import comms.SmartWriter;
import drive.ArcadeDrive;
import drive.IDrive;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SerialPort;
import input.EncoderMonitor;
import input.NavXTester;
import input.SensorController;
import physicalOutput.motors.IMotor;
import physicalOutput.motors.SparkMotor;
import robot.Global;
import robot.IControl;
import robotDefinitions.RobotDefinitionBase;

/**
 * The Piper implementation of IDefinition.<br>
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
		_properties=new HashMap<String, String>();

		// Default Motor Pins
		_properties.put("FLMOTORPIN", "3");// PWM3
		_properties.put("BLMOTORPIN", "2");// PWM4
		_properties.put("FRMOTORPIN", "1");// PWM1
		_properties.put("BRMOTORPIN", "0");// PWM2
		_properties.put("CLIMBMOTORPIN", "4");
		_properties.put("INTAKELEFTPIN", "5");// Shooter front left
		_properties.put("INTAKERIGHTPIN", "6");// Shooter back left
	}
	/***
	 * 
	 * @return Control object map for Tim
	 */
	public Map<String, IControl> loadControlObjects() {

		SmartWriter.putS("Robot is notvlad...", "asdf");
		// Create map to store public objects
		Map<String, IControl> iControlMap=super.loadControlObjects();
		
		Global.controllers = new PiperControl();

		// TODO add the sensors here
		/*
		 * // Creates the global solenoid controller SolenoidController SO =
		 * SolenoidController.getInstance(); SO.registerSolenoid("TRIGGER", new
		 * DoubleSolenoid(1,1)); //TODO register the solenoids here
		 */

		// Create IMotors for Arcade Drive
		IMotor FL=new SparkMotor(getInt("FLMOTORPIN"), true);
		IMotor FR=new SparkMotor(getInt("FRMOTORPIN"), true);
		IMotor BL=new SparkMotor(getInt("BLMOTORPIN"), true);
		IMotor BR=new SparkMotor(getInt("BRMOTORPIN"), true);

		// Create IDrive arcade drive I dont know why we cast it as a IDrive
		// though
		IDrive arcadeDrive=new ArcadeDrive(FL, FR, BL, BR);
		iControlMap.put(RobotDefinitionBase.DRIVENAME, arcadeDrive);

		//Encoder stuff
		Encoder encoder0 = new Encoder(0, 1);
		Encoder encoder1 =  new Encoder(2, 3);
		encoder0.setDistancePerPulse(0.06265);
		encoder1.setDistancePerPulse(0.06265);
		EncoderMonitor encoderMonitor = new EncoderMonitor();
		encoderMonitor.add("ENCODER0", encoder0);
		encoderMonitor.add("ENCODER1", encoder1);
		
		SensorController sensorController=SensorController.getInstance();
		sensorController.registerSensor("ENCODER0", encoder0);
		sensorController.registerSensor("ENCODER1", encoder1);
		sensorController.registerSensor("NAVX", new AHRS(SerialPort.Port.kMXP));
		
		AutoRunner AR = new AutoRunner();
		iControlMap.put("AutoRunner", AR);
		
		IMotor climbMotor = new SparkMotor(getInt("CLIMBMOTORPIN"), true);
		Climber climber = new Climber(climbMotor);
		
		IMotor intakeLeft = new SparkMotor(getInt("INTAKELEFTPIN"),true);
		IMotor intakeRight = new SparkMotor(getInt("INTAKERIGHTPIN"),true);
		Intake intake = new Intake(intakeLeft,intakeRight);
		
//		SolenoidController solenoidController = SolenoidController.getInstance();
//		solenoidController.registerSolenoid("intakeSolenoid", new DoubleSolenoid(4,5));
		
//		IMotor[] shooterMotors = {new SparkMotor(getInt("SFLMOTORPIN"),true),new SparkMotor(getInt("SFRMOTORPIN"),true)};
//		ChainMotor shootMotors = new ChainMotor(shooterMotors);
//		
//		DoubleSolenoid heightSolenoid = new DoubleSolenoid(0, 1);
//		DoubleSolenoid trigger = new DoubleSolenoid(2, 3);
//		Shooter shooter = new Shooter(shootMotors, heightSolenoid,trigger);
//		
//		
//		IMotor intakeMotor = new SparkMotor(getInt("INTAKEMOTOR"),false);
//		Intake intake = new Intake(intakeMotor);

		new NavXTester();
		//new NavXPIDTunable();
		//new CommandListRunnerDoNotKeepItSucks();
		
		
		// Create the autonomous command list maker, and command runner
		// CommandListMaker CLM = new CommandListMaker(AD);
		// CommandListRunner CR = new CommandListRunner(CLM.makeList1(),"PIPER"); //
		// makes list one for the TIM robot

		// Create the IMotors for the Shooter class
		// IMotor SL = new SparkMotor(getInt("SLMOTORPIN"),false);
		// IMotor SR = new SparkMotor(getInt("SRMOTORPIN"),false);

		// temp.put("AD", AD);
		// temp.put("CR", CR);

		return iControlMap;
	}

}
