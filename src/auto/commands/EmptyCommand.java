package auto.commands;

import auto.ICommand;
import auto.IStopCondition;

public class EmptyCommand implements ICommand {
	private IStopCondition stop;

	/**
	 * Empty command that is at the end of every CommandList
	 * @param stopCondition 
	 */
	public EmptyCommand(IStopCondition stopCondition) {
		stop = stopCondition;
	}
	
	@Override
	public void init() {
		stop.init();
	}
	
	@Override
	public boolean run() {
		return stop.stopNow();
	}
	
	public void stop(){
		
	}
}
