package auto;

//Interface for ICommand objects
public interface ICommand {
	/**
	 * initializes the command
	 */
	public void init();
	/**
	 * Runs the command 
	 */
	public void run();
	
	/**
	 * Stops the command
	 */
	public void stop();
}
