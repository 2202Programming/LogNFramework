package robot;

import java.util.Map;

import robotDefinitions.controls.ControlBase;

/**
 * A class for global variables that are not part of any specific class
 */
public class Global {
	// to get an instance of Sensor or Solenoid Controllers, use
	// SomeController.getInstance()

	// don't set!!!!
	public static Map<String, IControl> controlObjects;

	public static ControlBase controllers;

	public enum TargetSide {
		L {
			public String toString() {
				return "Left";
			}
		}, R {
			public String toString() {
				return "Right";
			}
		}
	};

	public enum StartPosition {
		L {
			public String toString() {
				return "Left";
			}
		}, M {
			public String toString() {
				return "Middle";
			}
		}, R {
			public String toString() {
				return "Right";
			}
		}, D {
			public String toString() {
				return "Default";
			}
		}
	};
	
	public static TargetSide ourSwitchPosition;
	public static TargetSide scalePosition;
	public static TargetSide opponentSwitchPosition;

}