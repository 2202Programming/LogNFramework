package NotVlad;

import java.io.File;

import auto.CommandList;
import auto.CommandListRunner;
import comms.DebugMode;
import comms.SmartWriter;
import robot.IControl;

public class AutoRunner extends IControl{
	NotVladXMLInterpreter XMLInterpreter;
	CommandListRunner runner;
	private double timeCost;
	private boolean finished;
	public AutoRunner(){
	}
	
	public void robotInit(){
	}
	
	public void autonomousInit(){
		finished = false;
		System.out.println("Start" + System.currentTimeMillis());
		File file = new File("/home/lvuser/Paths.xml");
		System.out.println(file.getName());
		XMLInterpreter = new NotVladXMLInterpreter(file);
		CommandList list = XMLInterpreter.getPathList(SmartWriter.getS("Path"));
		System.out.println("Parse End" + System.currentTimeMillis());
		runner = new CommandListRunner(list);
		timeCost = System.currentTimeMillis();
	}
	
	public void autonomousPeriodic(){
		if(!finished) {
			SmartWriter.putD("TimeCost", System.currentTimeMillis()-timeCost);
		}
		finished = runner.runList();
	}
	
	public void teleopInit(){
		runner.stop();
	}
	
	public void disabledInit(){
		SmartWriter.putS("Path", "EnterPath", DebugMode.COMPETITION);
		runner.stop();
	}

}
