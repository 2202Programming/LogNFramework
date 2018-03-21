package auto.runnables;

import java.util.ArrayList;
import java.util.List;

import auto.ICommand;
import auto.IRunnableCommand;
import auto.IStopCondition;

public class MultiStopConditionAnd implements IRunnableCommand {
	private List<ICommand> commandsToRun;
	private List<IStopCondition> conditionsToCheck;
	
	public MultiStopConditionAnd(ICommand command, List<IStopCondition> stopConditions){
		commandsToRun = new ArrayList<>();
		commandsToRun.add(command);
		conditionsToCheck = stopConditions;
	}
	
	public MultiStopConditionAnd(List<ICommand> commands, List<IStopCondition> stopConditions){
		commandsToRun = commands;
		this.conditionsToCheck = stopConditions;
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
		boolean finished = true;
		for(IStopCondition item: conditionsToCheck){
			if(!item.stopNow()){
				finished = false;
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
