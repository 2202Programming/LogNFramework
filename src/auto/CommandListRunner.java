package auto;

import auto.commands.EmptyCommand;
import comms.FileLoader;
import edu.wpi.first.wpilibj.Encoder;
import input.SensorController;

public class CommandListRunner {
	public int commandNum;
	private int prevCommandNum;
	private CommandList commands;
	private ICommand curCommand;
	private SensorController sensors;
	private long commandTime;

	/**
	 * Constructor for CommandListRunner<br>
	 * 
	 * @param xCommands
	 *            The commandList to be run
	 * @param robotName
	 *            Name of the robot for unique commands
	 */
	public CommandListRunner(CommandList xCommands) {
		sensors = SensorController.getInstance();
		commands = xCommands;
		init();
	}

	/**
	 * Initializes to first command
	 */
	public void init() {
		commandNum = 0;
		commandTime = 0;
		prevCommandNum = -1;
	}

	/**
	 * Runs the current list of commands
	 * 
	 * @return if the list has finished
	 */
	public boolean runList() {
		curCommand = commands.getCommand(commandNum);
		if (curCommand instanceof EmptyCommand)
			return true;
		if (prevCommandNum != commandNum) {
			prevCommandNum = commandNum;
			curCommand.init();
			commandTime = System.currentTimeMillis();
		}
		if (curCommand.run()) {
			curCommand.stop();
			commandNum++;
			long logTime = System.currentTimeMillis();
			String fileName = "/RobotLog" + logTime;
			String contents = "Encoder 0: " + ((Encoder)sensors.getSensor("ENCODER0")).get() + "/nEncoder 1: " + ((Encoder)sensors.getSensor("ENCODER1")) + "/nTime to run: " + (logTime-commandTime);
			FileLoader.writeToFile(fileName, contents);
			FileLoader.writeToFile(fileName, "Logging took: " + (System.currentTimeMillis()-logTime));
		}
		return false;
	}

	public void stop() {
		commandNum = commands.size();
		if (curCommand != null) {
			curCommand.stop();
		}
	}
}
