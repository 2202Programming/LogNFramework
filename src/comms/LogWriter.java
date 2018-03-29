package comms;

import java.util.HashMap;
import java.util.Map;

public class LogWriter {
	private static Map<String,ILoggable> loggables = new HashMap<>();
	
	public static void registerLoggable(String name, ILoggable logRule){
		loggables.put(name, logRule);
	}
	
	public static boolean runLog(String name){
		
	}
}

class LogItem{
	public String logFile;
	public ILoggable log;
	
	public LogItem(String logFileName, ILoggable logRule){
		logFile = logFileName;
		log = logRule;
	}
}
