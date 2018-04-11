package robotDefinitions;

import java.util.Map;

import robot.IControl;

public interface IRobotDefinition {

	/**
	 * Loads the control objects defined from the values in the properties, this
	 * should only need, to be called once in a lifetime, duplicate objects may
	 * not function as intended.
	 * 
	 * @return Control Object List for robot class's main cycle
	 */
	Map<String, IControl> loadControlObjects();
	
	/**
	 * Called to load control objects that are used for all robots. 
	 * This should be called after the loadControl objects method.
	 * 
	 * @return a Map of the default control objects
	 */
	Map<String, IControl> loadDefaultControlObjects();

}