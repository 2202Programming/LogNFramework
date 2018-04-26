package comms;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import robot.IControl;

public class LogWriter extends IControl{
	private static Map<String,ILoggable> loggables = new HashMap<>();
	private static Map<String,PrintWriter> fileWriters = new HashMap<>();
	/**
	 * Registers a log rule in the Map that can be ran at any time in the program
	 * @param name the name of the log rule
	 * @param logRule the behavior this log will have
	 */
	public static void registerLoggable(String name, ILoggable logRule){
		loggables.put(name, logRule);
		FileWriter file = null;
		try{
			file = new FileWriter(logRule.getLogFileName());
			System.out.println(logRule.getLogFileName());
		}catch(IOException e){
			SmartWriter.outputError(e, System.currentTimeMillis() + "");
		}
		
		PrintWriter writer = new PrintWriter(file);
		fileWriters.put(name, writer);
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
			writeData(name, item.getLogData() + "\n" + extraData);
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
			writeData(name,item.getLogData());
			return true;
		}
	}
	
	private static void flushLogWriters(){
		for(Entry<String, PrintWriter> key: fileWriters.entrySet()){
			key.getValue().flush();
		}
	}
	
	private static void resetLogFiles(){
		for(Entry<String, PrintWriter> key: fileWriters.entrySet()){
			key.getValue().write("");
			key.getValue().flush();
		}
	}
	
	private static void writeData(String name, String data){
		PrintWriter writer = fileWriters.get(name);
		writer.append(data);
	}
	
	public void autonomousInit(){
		resetLogFiles();
	}
	
	public void disabledInit(){
		flushLogWriters();
	}
}
