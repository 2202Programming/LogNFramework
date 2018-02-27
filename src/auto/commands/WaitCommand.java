package auto.commands;

import auto.ICommand;
import auto.IStopCondition;

public class WaitCommand implements ICommand {
	IStopCondition stop;
	/**
	 * Creates a command that waits
	 * @param stop The stop condition, usually a TimerStopCondition
	 */
	public WaitCommand(IStopCondition stop) {
		this.stop = stop;
	}

	
	@Override
	public void init() {
		stop.init();
	}

	@Override
	public boolean run() {
		return stop.stopNow();
	}

	@Override
	public void stop() {
	}

}
