package comms;

/**
 * Defines the behavior for a loggable item
 * @author Daniel
 *
 */
public interface ILoggable {
	/**
	 * Gets the data to log when this log rule is used
	 * @return A string representing the data to be logged
	 */
	public String getLogData();
	/**
	 * Gets the file that we should log to
	 * @return A string representing the file path to log to
	 */
	public String getLogFileName();
}
