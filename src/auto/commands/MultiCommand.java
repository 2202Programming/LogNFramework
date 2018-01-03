package auto.commands;

import java.util.ArrayList;
import java.util.List;

import auto.CommandList;
import auto.CommandListRunner;
import auto.ICommand;

public class MultiCommand implements ICommand {
	private CommandListRunner runner;

	/**
	 * Creates a command that runs all of the commands in the array
	 * @param commands the array to run
	 */
	public MultiCommand(ICommand[] commands) {
		ArrayList<ICommand> toAdd = new ArrayList<>();
		for(ICommand x: commands) {
			toAdd.add(x);
		}
		buildRunner(toAdd);
	}
	
	/**
	 * Creates a command that runs all of the commands in the list
	 * @param commands the list of commands to run
	 */
	public MultiCommand(List<ICommand> commands) {
		buildRunner(commands);
	}
	
	/**
	 * Creates the CommandListRunner
	 * @param commands
	 */
	private void buildRunner(List<ICommand> commands) {
		CommandList list = new CommandList();
		for(ICommand x: commands) {
			list.addCommand(x);
		}
		runner = new CommandListRunner(list);
	}
	
	@Override
	public void init() {
		runner.init();
	}

	@Override
	public boolean run() {
		return runner.runList();
	}

	@Override
	public void stop() {
		runner.stop();
	}

}
