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
	
	public AutoRunner(){
		File file = new File("/Paths.xml");
		XMLInterpreter = new NotVladXMLInterpreter(file);
	}
	
	public void robotInit(){
		SmartWriter.putS("Path", "", DebugMode.COMPETITION);
	}
	
	public void autonomousInit(){
		CommandList list = XMLInterpreter.getPathList(SmartWriter.getS("Path"));
		runner = new CommandListRunner(list);
	}
	
	public void autonomousPeriodic(){
		runner.runList();
	}
	
	public void teleopInit(){
		runner.stop();
	}
	
	public void disabledInit(){
		runner.stop();
	}

}
