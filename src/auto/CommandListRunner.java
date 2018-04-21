package auto;

import auto.commands.EmptyCommand;
import comms.LogWriter;

public class CommandListRunner {
	public int commandNum;
	private int prevCommandNum;
	private CommandList commands;
	private ICommand curCommand;
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
			LogWriter.runLog("AutonomousLog", "Command Name: " + curCommand.toString() + "\nCommand run time: " + (System.currentTimeMillis()-commandTime) + "\n\n");
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
