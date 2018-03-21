package auto;

public interface IRunnableCommand {
	/**
	 * Initializes all commands 
	 */
	public void init();
	
	/**
	 * Runs all commands
	 * @return if the stop conditions are met
	 */
	public boolean runCommands();
	
	/**
	 * Stops all commands immediately
	 */
	public void stopCommands();
}
