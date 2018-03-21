package auto;

import auto.iCommands.EmptyCommand;

public class CommandListRunner {
	public int commandNum;
	private int prevCommandNum;
	private CommandList commands;
	private IRunnableCommand curCommand;

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
		prevCommandNum = -1;
	}

	/**
	 * Runs the current list of commands
	 * 
	 * @return if the list has finished
	 */
	public boolean runList() {
		curCommand = commands.getCommand(commandNum);
		if (curCommand == null)
			return true;
		if (prevCommandNum != commandNum) {
			prevCommandNum = commandNum;
			curCommand.init();
		}
		if (curCommand.runCommands()) {
			curCommand.stopCommands();
			commandNum++;
		}
		return false;
	}

	public void stop() {
		commandNum = commands.size();
		if (curCommand != null) {
			curCommand.stopCommands();
		}
	}
}
