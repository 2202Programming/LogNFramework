package physicalOutput;

import java.util.HashMap;
import java.util.Map;

import comms.SmartWriter;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SolenoidBase;

public class SolenoidController {
	private static SolenoidController controller;
	private Map<String, SolenoidBase> solenoids;

	private SolenoidController(){
		solenoids  = new HashMap<String, SolenoidBase>();
	}
	
	//Use this to get the instance of SolenoidController
	public static SolenoidController getInstance(){
		if(controller == null){
			controller = new SolenoidController();
		}
		return controller;
	}
	/**
	 * registers a new solenoid in the controller<br>
	 * Preconditions: toRegister is a valid solenoid and the key is a string<br>
	 * Postconditions: registers the solenoid<br>
	 *
	 * @param key
	 *            the string identifier of the solenoid
	 * @param toRegister
	 *            the solenoid to register
	 */
	public void registerSolenoid(String key, SolenoidBase toRegister) {
		solenoids.put(key, toRegister);
	}

	/**
	 * get a DoubleSolenoid from the Map<br>
	 * Preconditions: the name is a valid key and is tied to a DoubleSolenoid<br>
	 * Postconditions: returns the solenoid<br>
	 *
	 * @param name
	 *            the name of the solenoid
	 * @return the requested solenoid
	 * @throws Exception
	 */
	public DoubleSolenoid getDoubleSolenoid(String name) throws Exception {
		SolenoidBase temp = solenoids.get(name);

		if (temp == null) throw new Exception("No such value in dictionary");

		if ( !(temp instanceof DoubleSolenoid)) throw new Exception(name + " is not of type double solioid");

		return (DoubleSolenoid)temp;
	}

	/**
	 * get a DoubleSolenoid from the Map<br>
	 * Preconditions: the name is a valid key and is tied to a Solenoid<br>
	 * Postconditions: returns the solenoid<br>
	 *
	 * @param name
	 *            the name of the solenoid
	 * @return the requested solenoid
	 * @throws Exception
	 */
	public Solenoid getSolenoid(String name) throws Exception {
		SolenoidBase temp = solenoids.get(name);

		if (temp == null) throw new Exception("No such value in dictionary");

		if ( !(temp instanceof Solenoid)) throw new Exception(name + " is not of type solioid");

		return (Solenoid)temp;
	}
	
	/**
	 * Gets the value of a single valve solenoid
	 * @param name the name of the solenoid
	 * @return true if the solenoid is open. False if the solenoid is closed.
	 * @throws Exception if the solenoid is not a single valve or does not exist.
	 */
	public boolean getSolenoidValue(String name) throws Exception {
		Solenoid solenoid = getSolenoid(name);
		return solenoid.get();
	}
	
	/**
	 * Gets the value of a double valve solenoid
	 * @param name the name of the solenoid
	 * @return true if the solenoid is open. False if the solenoid is closed.
	 * @throws Exception if the solenoid is not a double valve or does not exist.
	 */
	public DoubleSolenoid.Value getDoubleSolenoidValue(String name) throws Exception {
		DoubleSolenoid solenoid = getDoubleSolenoid(name);
		return solenoid.get();
	}
	
	/**
	 * Sets the value of a single valve solenoid
	 * @param name the name of the solenoid
	 * @param value the value to set the solenoid
	 */
	public void setSolenoid(String name, boolean value) {
		try{
			Solenoid solenoid = getSolenoid(name);			
			solenoid.set(value);
		}catch(Exception e){
			SmartWriter.outputError(e, System.currentTimeMillis()+"");
		}
	}
	
	/**
	 * Sets the value of a single valve solenoid
	 * @param name the name of the solenoid
	 * @param value the value to set the solenoid
	 */
	public void setDoubleSolenoid(String name, DoubleSolenoid.Value value) {
		try{
			DoubleSolenoid solenoid = getDoubleSolenoid(name);			
			solenoid.set(value);
		}catch(Exception e){
			SmartWriter.outputError(e, System.currentTimeMillis()+"");
		}
	}
}
