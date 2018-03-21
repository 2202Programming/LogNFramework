package auto.runnables;

import java.util.List;

import auto.ICommand;
import auto.IRunnableCommand;
import auto.IStopCondition;

public class MultiStopConditionOr implements IRunnableCommand {
	private List<ICommand> commandsToRun;
	private List<IStopCondition> conditionsToCheck;
	
	public MultiStopConditionOr(List<ICommand> commands, List<IStopCondition> stopConditions){
		commandsToRun = commands;
		conditionsToCheck = stopConditions;
	}
	
	@Override
	public void init() {
		for(ICommand item: commandsToRun){
			item.init();
		}
		for(IStopCondition item: conditionsToCheck){
			item.init();
		}
	}

	@Override
	public boolean runCommands() {
		for(ICommand item: commandsToRun){
			item.run();
		}
		boolean finished = false;
		for(IStopCondition item: conditionsToCheck){
			if(item.stopNow()){
				finished = true;
			}
		}
		return finished;
	}

	@Override
	public void stopCommands() {
		for(ICommand item: commandsToRun){
			item.stop();
		}
	}

}
