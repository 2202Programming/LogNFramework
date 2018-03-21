package auto.iCommands;

import auto.ICommand;
import auto.IStopCondition;

public class EmptyCommand implements ICommand {

	/**
	 * Empty command that is at the end of every CommandList
	 * @param stopCondition 
	 */
	public EmptyCommand() {
	}
	
	@Override
	public void init() {
	}
	
	@Override
	public void run() {
	}
	
	public void stop(){
	}
}
