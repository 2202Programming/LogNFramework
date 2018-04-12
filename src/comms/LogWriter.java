package comms;

import java.util.HashMap;
import java.util.Map;

public class LogWriter {
	private static Map<String,ILoggable> loggables = new HashMap<>();
	
	/**
	 * Registers a log rule in the Map that can be ran at any time in the program
	 * @param name the name of the log rule
	 * @param logRule the behavior this log will have
	 */
	public static void registerLoggable(String name, ILoggable logRule){
		loggables.put(name, logRule);
	}
	
	/**
	 * Logs the data according to the chosen log rule
	 * @param name the name of the log rule that was registered
	 * @param extraData any extra data to append to the end of the log rule's basic text
	 * @return true if the log ran successfully, false otherwise
	 */
	public static boolean runLog(String name, String extraData){
		ILoggable item = loggables.get(name);
		if(item == null){
			return false;
		}else{
			FileLoader.writeToFile(item.getLogFileName(), item.getLogData() + "\n" + extraData);
			return true;
		}
	}
	
	/**
	 * Logs the data according to the chosen log rule
	 * @param name the name of the log rule that was registered
	 * @return true if the log ran successfully, false otherwise
	 */
	public static boolean runLog(String name){
		ILoggable item = loggables.get(name);
		if(item == null){
			return false;
		}else{
			FileLoader.writeToFile(item.getLogFileName(), item.getLogData());
			return true;
		}
	}
}
