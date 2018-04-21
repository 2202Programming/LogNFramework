package comms;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import robot.IControl;

public class NetworkTables extends IControl{

	NetworkTable table;
	
	public NetworkTables(TableNamesEnum tableKey) {
		table = NetworkTableInstance.getDefault().getTable(tableKey.toString());
	}
	
	public double getDouble(String key){
		return table.getEntry(key).getDouble(0);
	}
	
	public void setDouble(String key, double value){
		table.getEntry(key).setNumber(value);
	}
	
	public String getString(String key){
		return table.getEntry(key).getString("");
	}
	
	public void setString(String key, String value){
		table.getEntry(key).setString(value);
	}
	
	public boolean getBoolean(String key){
		return table.getEntry(key).getBoolean(false);
	}
	
	public boolean getBoolean(String key, boolean defaultValue){
		return table.getEntry(key).getBoolean(defaultValue);
	}
	
	public void setBoolean(String key, boolean value){
		table.getEntry(key).setBoolean(value);
	}
}
