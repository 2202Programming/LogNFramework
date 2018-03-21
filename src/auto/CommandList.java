package auto;

import java.util.ArrayList;

import auto.iCommands.EmptyCommand;
import auto.stopConditions.TimerStopCondition;



public class CommandList {
	// holds the list of iCommands to be run
	private ArrayList<IRunnableCommand> iCommands;

	// default constructor creates an empty command list
	public CommandList() {
		iCommands=new ArrayList<IRunnableCommand>();
	}

	/**
	 * Adds a command to the list <br>
	 * Preconditions: The command must be a valid ICommand object <br>
	 * Postconditions: The command is added to the list<br>
	 * 
	 * @param CommandIn
	 *            the command to be inputed
	 */
	public void addCommand(IRunnableCommand CommandIn) {
		iCommands.add(CommandIn);
	}

	/**
	 * Gets the command at commandNum <br>
	 * Preconditions: commandNum is less or equal to the number of iCommands <br>
	 * Postconditions: returns the command<br>
	 * 
	 * @param commandNum
	 *            the number of the command
	 * @return the type, power, and distance for the command
	 */
	public IRunnableCommand getCommand(int commandNum) {
		if (commandNum<iCommands.size()) {
			return iCommands.get(commandNum);
		}
		return null;
	}
	
	public int size() {
		return iCommands.size();
	}
}
