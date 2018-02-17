package NotVlad;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import NotVlad.components.LiftPosition;
import auto.CommandList;
import auto.ICommand;
import auto.IStopCondition;
import auto.commands.DecelCommand;
import auto.commands.DriveAtAngle;
import auto.commands.DriveCommand;
import auto.commands.IntakeCommand;
import auto.commands.LiftCommand;
import auto.commands.OuttakeCommand;
import auto.commands.SneakDriveCommand;
import auto.commands.TurnCommand;
import auto.stopConditions.DistanceStopCondition;
import auto.stopConditions.TalonDistanceStopCondition;
import auto.stopConditions.TimerStopCondition;
import edu.wpi.first.wpilibj.Encoder;
import input.SensorController;
import physicalOutput.motors.TalonSRXMotor;
import robot.Global;

public class NotVladXMLInterpreter {

	private Document xmlFile;

	/**
	 * Loads xml file into memory
	 * 
	 * @param f
	 *            xml file path
	 */
	public NotVladXMLInterpreter(File f) {
		readFile(f);
	}

	/**
	 * Reads the xml file
	 * 
	 * @param f
	 *            xml file path
	 */
	public void readFile(File f) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			dbFactory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			xmlFile = dBuilder.parse(f);
			xmlFile.getDocumentElement().normalize();
		} catch (Exception e) {
			e.printStackTrace();
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
	public CommandList getPathList(String id) {
		// Searches the paths for the correct one
		Node xmlPath = null;
		NodeList paths = xmlFile.getElementsByTagName("Path");
		for (int i = 0; i < paths.getLength(); i++) {
			Node currentNode = paths.item(i);
			// If there is an id attribute on the path
			if (currentNode.getAttributes().item(0).getNodeName().equals("Id")) {
				// If the id matches the one we are looking for
				if (currentNode.getAttributes().item(0).getNodeValue().equals(id)) {
					System.out.println("Found Path: " + id);
					xmlPath = currentNode;
				}
			}
		}

		// Will throw nullPointer if the path doesn't exist
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
			return new TurnCommand(turnDegrees);
		}

		case ("DriveCommand"): {
			// double power =
			// Double.parseDouble(attributes.getNamedItem("Power").getNodeValue());

			// return new SneakDriveCommand(getStopCondition(n), .01);

			double power = Double.parseDouble(attributes.getNamedItem("Power").getNodeValue());
			double angle = Double.parseDouble(attributes.getNamedItem("Angle").getNodeValue());
			return new DriveAtAngle(getStopCondition(n), power, angle);
		}

		case ("DecelCommand"): {
			double maxAcceleration = Double.parseDouble(attributes.getNamedItem("MaxAcceleration").getNodeValue());
			return new DecelCommand(maxAcceleration);
		}

		case ("LiftCommand"): {
			LiftPosition targetPosition = null;
			switch (attributes.getNamedItem("Height").getNodeValue()) {
			case ("SWITCH"): {
				targetPosition = LiftPosition.SWITCH;
				break;
			}
			case ("SCALE"): {
				targetPosition = LiftPosition.HIGHSCALE;
				break;
			}
			case ("BOTTOM"): {
				targetPosition = LiftPosition.BOTTOM;
				break;
			}
			}
			System.out.println("Target: " + targetPosition.getNumber());
			return new LiftCommand(targetPosition, getStopCondition(n));
		}

		case ("OuttakeCommand"): {
			return new OuttakeCommand(getStopCondition(n));
		}

		case ("IntakeCommand"): {
			return new IntakeCommand(getStopCondition(n));
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
		String stopConditionType = stopConditionNode.getNodeName();

		switch (stopConditionType) {
		case ("DistanceStopCondition"): {
			int stopDistance = Integer.parseInt(stopConditionNode.getAttributes().item(0).getNodeValue());
			ArrayList<Encoder> encoders = new ArrayList<Encoder>();
			SensorController sensorController = SensorController.getInstance();
			encoders.add((Encoder) sensorController.getSensor("ENCODER0"));
			encoders.add((Encoder) sensorController.getSensor("ENCODER1"));
			return new DistanceStopCondition(encoders, stopDistance);
		}
		case ("TimerStopCondition"): {
			long stopTime = Long.parseLong(stopConditionNode.getAttributes().item(0).getNodeValue());
			return new TimerStopCondition(stopTime);
		}
		case ("TalonDistanceStopCondition"): {
			// Get Talon motors
			List<TalonSRXMotor> talons = new ArrayList<TalonSRXMotor>(1);
			talons.add((TalonSRXMotor) Global.controlObjects.get("LIFT_TALON"));

			// Get target position
			LiftPosition targetPosition = null;
			switch (parentNode.getAttributes().getNamedItem("Height").getNodeValue()) {
			case ("SWITCH"): {
				targetPosition = LiftPosition.SWITCH;
			}
			case ("SCALE"): {
				targetPosition = LiftPosition.HIGHSCALE;
			}
			case ("BOTTOM"): {
				targetPosition = LiftPosition.BOTTOM;
			}
			}

			return new TalonDistanceStopCondition(talons, targetPosition);
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
