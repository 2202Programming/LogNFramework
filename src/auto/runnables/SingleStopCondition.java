package auto.runnables;

import java.util.ArrayList;
import java.util.List;

import auto.ICommand;
import auto.IRunnableCommand;
import auto.IStopCondition;

public class SingleStopCondition implements IRunnableCommand {
	private List<ICommand> commands;
	private IStopCondition stopCondition;
	
	public SingleStopCondition(ICommand command, IStopCondition stopCondition){
		commands = new ArrayList<ICommand>();
		commands.add(command);
		this.stopCondition = stopCondition;
	}
	
	public SingleStopCondition(List<ICommand> command, IStopCondition stopCondition) {
		this.commands = command;
		this.stopCondition = stopCondition;
	}
	
	@Override
	public void init() {
		for(ICommand item: commands){
			item.init();
		}
		stopCondition.init();
	}

	@Override
	public boolean runCommands() {
		for(ICommand item: commands){
			item.run();
		}
		return stopCondition.stopNow();
	}

	@Override
	public void stopCommands() {
		for(ICommand item: commands){
			item.stop();
		}
	}

}
