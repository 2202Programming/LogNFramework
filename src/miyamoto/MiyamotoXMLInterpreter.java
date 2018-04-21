package miyamoto;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import auto.CommandList;
import auto.ICommand;
import auto.IStopCondition;
import auto.commands.DecelCommand;
import auto.commands.DriveAtAngle;
import auto.commands.DriveCommand;
import auto.commands.IntakeCommand;
import auto.commands.LiftCommand;
import auto.commands.OuttakeCommand;
import auto.commands.PIDDriveAtAngle;
import auto.commands.PIDDriveMode;
import auto.commands.TurnCommand;
import auto.commands.WaitCommand;
import auto.stopConditions.AngleStopCondition;
import auto.stopConditions.DistanceStopCondition;
import auto.stopConditions.IntakeStopCondition;
import auto.stopConditions.LiftStopCondition;
import auto.stopConditions.OrStopCondition;
import auto.stopConditions.PIDDistanceStopCondition;
import auto.stopConditions.SummativeDistanceStopCondition;
import auto.stopConditions.TimerStopCondition;
import edu.wpi.first.wpilibj.Encoder;
import input.SensorController;
import miyamoto.components.Lift;
import miyamoto.components.LiftPosition;
import robot.Global;

public class MiyamotoXMLInterpreter {

	private Document xmlFile;

	/**
	 * Loads xml file into memory
	 * 
	 * @param f
	 *            xml file path
	 * @throws BadXMLReadException
	 */
	public MiyamotoXMLInterpreter(File f) throws BadXMLReadException {
		readFile(f);
	}

	/**
	 * Reads the xml file
	 * 
	 * @param f
	 *            xml file path
	 * @throws BadXMLReadException
	 */
	public void readFile(File f) throws BadXMLReadException {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			dbFactory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			xmlFile = dBuilder.parse(f);
			xmlFile.getDocumentElement().normalize();
		} catch (Exception e) {
			System.out.println("Reading of file broke");

			throw new BadXMLReadException();
		}
	}

	/**
	 * Searches for a specific path in the xml file and returns a command list
	 * of the commands in the xml file
	 * 
	 * @param id
	 *            path id 1st character: starting position 2nd character: target
	 *            location 3rd character: path option
	 * @return command list of specified path
	 * 
	 */
	public CommandList getPathList(String id) throws Exception {
		// Searches the paths for the correct one
		Node xmlPath = null;
		NodeList paths = xmlFile.getElementsByTagName("Path");
		for (int i = 0; i < paths.getLength(); i++) {
			Node currentNode = paths.item(i);
			// If there is an id attribute on the path
			if (currentNode.getAttributes().item(0).getNodeName().equalsIgnoreCase("Id")) {
				// If the id matches the one we are looking for
				if (currentNode.getAttributes().item(0).getNodeValue().equalsIgnoreCase(id)) {
					System.out.println("Found Path: " + id);
					xmlPath = currentNode;
				}
			}
		}

		// if (xmlPath == null) {
		// xmlPath = paths.item(0); // Defaults to the first path in the xml
		// file
		// }

		NodeList xmlCommands = xmlPath.getChildNodes();

		CommandList path = new CommandList();
		for (int i = 0; i < xmlCommands.getLength(); i++) {
			Node currentNode = xmlCommands.item(i);
			// System.out.println("nodeName:" + currentNode.getNodeName());
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				// calls this method for all the children that are command lists
				path.addCommand(getCommand(currentNode));
			}
		}

		return path;
	}

	/**
	 * Converts the xml command into an ICommand to be put in a CommandList
	 * 
	 * @param n
	 *            the Node created of the command within a path
	 * @return ICommand version of the command
	 */
	public ICommand getCommand(Node n) {
		NamedNodeMap attributes = n.getAttributes();
		String commandName = n.getNodeName();

		switch (commandName) {
		case ("TurnCommand"): {
			double turnDegrees = Double.parseDouble(attributes.getNamedItem("Angle").getNodeValue());
			double maxPower = Math.abs(Double.parseDouble(attributes.getNamedItem("MaxPower").getNodeValue()));
			AngleStopCondition angleStop = new AngleStopCondition(turnDegrees, 2, 0.1);
			TimerStopCondition timeStop = new TimerStopCondition(1500);
			// return new TurnCommand(angleStop, turnDegrees);
			// return new TurnCommand(new OrStopCondition(angleStop, timeStop),
			// turnDegrees);
			// Turning stalls at .25 power
			return new TurnCommand(new OrStopCondition(angleStop, timeStop), turnDegrees, -180, 180, -maxPower,
					maxPower, 1);
		}

		case ("DriveCommand"): {
			// double power =
			// Double.parseDouble(attributes.getNamedItem("Power").getNodeValue());

			// return new SneakDriveCommand(getStopCondition(n), .01);

			double power = Double.parseDouble(attributes.getNamedItem("Power").getNodeValue());
			double angle = Double.parseDouble(attributes.getNamedItem("Angle").getNodeValue());
			return new DriveAtAngle(getStopCondition(n), power, angle);
		}

		case ("PIDDriveAtAngleCommand"): {
			double power = Double.parseDouble(attributes.getNamedItem("Power").getNodeValue());
			double angle = Double.parseDouble(attributes.getNamedItem("Angle").getNodeValue());
			ArrayList<Encoder> encoders = new ArrayList<Encoder>();
			SensorController sensorController = SensorController.getInstance();
			encoders.add((Encoder) sensorController.getSensor("ENCODER0"));
			encoders.add((Encoder) sensorController.getSensor("ENCODER1"));
			int stopDistance = Integer.parseInt(attributes.getNamedItem("Dist_Inches").getNodeValue());

			if (power < 0) {
				power *= -1;
			}

			PIDDriveMode mode = PIDDriveMode.valueOf(attributes.getNamedItem("Mode").getNodeValue().toUpperCase());

			return new PIDDriveAtAngle(getStopCondition(n), encoders, stopDistance, -power, power, 2, angle, 0.012,
					mode);
		}
		case ("DecelCommand"): {
			double maxAcceleration = Double.parseDouble(attributes.getNamedItem("MaxAcceleration").getNodeValue());
			return new DecelCommand(maxAcceleration);
		}

		case ("LiftCommand"): {
			int targetPosition = 0;

			String height = attributes.getNamedItem("Height").getNodeValue();
			switch (height) {
			case ("BOTTOM"): {
				targetPosition = LiftPosition.BOTTOM.getNumber();
				break;
			}
			case ("SWITCH"): {
				targetPosition = LiftPosition.SWITCH.getNumber();
				break;
			}
			case ("LOWSCALE"): {
				targetPosition = LiftPosition.LOWSCALE.getNumber();
				break;
			}
			case ("MIDSCALE"): {
				targetPosition = LiftPosition.MIDSCALE.getNumber();
				break;
			}
			case ("HIGHSCALE"): {
				targetPosition = LiftPosition.HIGHSCALE.getNumber();
				break;
			}
			default: {
				targetPosition = Integer.parseInt(height);
				break;
			}
			}
			System.out.println("Scale Target: " + targetPosition);
			return new LiftCommand(targetPosition,
					new OrStopCondition(new TimerStopCondition(4000), getStopCondition(n)));
		}

		case ("OuttakeCommand"): {
			double speed = Double.parseDouble(attributes.getNamedItem("Power").getNodeValue());
			return new OuttakeCommand(speed, getStopCondition(n));
		}

		case ("IntakeCommand"): {
			double speed = Double.parseDouble(attributes.getNamedItem("Power").getNodeValue());
			Node holdSpeedNode = attributes.getNamedItem("HoldPower");
			IStopCondition stop = getStopCondition(n);
			if (holdSpeedNode == null) {
				return new IntakeCommand(speed, 0.2, stop);
			} else {
				double holdSpeed = Double.parseDouble(holdSpeedNode.getNodeValue());
				return new IntakeCommand(speed, holdSpeed, stop);
			}
		}

		case ("WaitCommand"): {
			long waitTime = Long.parseLong(attributes.getNamedItem("Time").getNodeValue());
			return new WaitCommand(new TimerStopCondition(waitTime));
		}
		}

		return new DriveCommand(new TimerStopCondition(0), 0.0);
	}

	/**
	 * Gets the stop condition of a command
	 * 
	 * Precondition: The stop condition is a child node of the command
	 * 
	 * @param parentNode
	 *            The parent node of the stop condition/ command
	 * @return The stop condition for the command
	 */
	public IStopCondition getStopCondition(Node parentNode) {
		Node stopConditionNode = null;
		for (int i = 0; i < parentNode.getChildNodes().getLength(); i++) {
			// System.out.println(n.getChildNodes().item(i));
			if (parentNode.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
				stopConditionNode = parentNode.getChildNodes().item(i);
			}
		}
		// System.out.println(stopConditionNode);
		NamedNodeMap attributes = stopConditionNode.getAttributes();
		String stopConditionType = stopConditionNode.getNodeName();

		switch (stopConditionType) {
		case ("DistanceStopCondition"): {
			int stopDistance = Integer.parseInt(attributes.getNamedItem("Dist_Inches").getNodeValue());
			ArrayList<Encoder> encoders = new ArrayList<Encoder>();
			SensorController sensorController = SensorController.getInstance();
			encoders.add((Encoder) sensorController.getSensor("ENCODER0"));
			encoders.add((Encoder) sensorController.getSensor("ENCODER1"));
			return new DistanceStopCondition(encoders, stopDistance);
		}
		case ("SummativeDistanceStopCondition"): {
			int stopDistance = Integer.parseInt(attributes.getNamedItem("Dist_Inches").getNodeValue());
			ArrayList<Encoder> encoders = new ArrayList<Encoder>();
			SensorController sensorController = SensorController.getInstance();
			encoders.add((Encoder) sensorController.getSensor("ENCODER0"));
			encoders.add((Encoder) sensorController.getSensor("ENCODER1"));
			return new SummativeDistanceStopCondition(encoders, stopDistance);
		}
		case ("PIDDistanceStopCondition"): {
			int stopDistance = Integer.parseInt(attributes.getNamedItem("Dist_Inches").getNodeValue());
			ArrayList<Encoder> encoders = new ArrayList<Encoder>();
			SensorController sensorController = SensorController.getInstance();
			encoders.add((Encoder) sensorController.getSensor("ENCODER0"));
			encoders.add((Encoder) sensorController.getSensor("ENCODER1"));
			return new PIDDistanceStopCondition(encoders, stopDistance, 1, 200);
		}
		case ("TimerStopCondition"): {
			long stopTime = Long.parseLong(attributes.getNamedItem("Timer").getNodeValue());
			return new TimerStopCondition(stopTime);
		}
		case ("LiftStopCondition"): {
			// Get Lift component
			Lift lift = (Lift) Global.controlObjects.get("LIFT");

			// Get target position
			int targetPosition = lift.getLiftCounts();

			String height = attributes.getNamedItem("Height").getNodeValue();
			switch (height) {
			case ("BOTTOM"): {
				targetPosition = LiftPosition.BOTTOM.getNumber();
				break;
			}
			case ("SWITCH"): {
				targetPosition = LiftPosition.SWITCH.getNumber();
				break;
			}
			case ("LOWSCALE"): {
				targetPosition = LiftPosition.LOWSCALE.getNumber();
				break;
			}
			case ("MIDSCALE"): {
				targetPosition = LiftPosition.MIDSCALE.getNumber();
				break;
			}
			case ("HIGHSCALE"): {
				targetPosition = LiftPosition.HIGHSCALE.getNumber();
				break;
			}
			default: {
				targetPosition = Integer.parseInt(height);
				break;
			}
			}

			return new LiftStopCondition(lift, targetPosition);
		}
		case ("IntakeStopCondition"): {
			return new IntakeStopCondition();
		}
		}
		return new TimerStopCondition(0);
	}

	/**
	 * Prints the xml file on console
	 */
	public void printFile() {
		printFile(xmlFile.getDocumentElement(), 0);
	}

	/**
	 * Prints the xml file that was read
	 * 
	 * @param cur
	 *            current Node
	 * @param depth
	 *            How deep you are in the tree
	 */
	public void printFile(Node cur, int depth) {
		String line = "";
		for (int i = 0; i < depth; i++) {
			line += "\t";
		}

		line += cur.getNodeName() + " ";

		NamedNodeMap attributes = cur.getAttributes();

		for (int i = 0; i < attributes.getLength(); i++) {
			Node currentNode = attributes.item(i);

			line += currentNode.getNodeName() + "= " + currentNode.getNodeValue() + " ";
		}

		System.out.println(line);
		NodeList childNodes = cur.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node currentNode = childNodes.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				// calls this method for all the children which is Element
				printFile(currentNode, depth + 1);
			}
		}
	}
}
