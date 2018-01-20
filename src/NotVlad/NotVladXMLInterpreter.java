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

import auto.CommandList;
import auto.ICommand;
import auto.IStopCondition;
import auto.commands.DriveCommand;
import auto.commands.TurnCommand;
import auto.stopConditions.DistanceStopCondition;
import auto.stopConditions.TimerStopCondition;
import edu.wpi.first.wpilibj.Encoder;
import input.SensorController;

public class NotVladXMLInterpreter {

	// somebody update pls
	private static List<Encoder> tempEnc;
	private Document xmlFile;

	public NotVladXMLInterpreter(File f) {
		readFile(f);
		tempEnc = new ArrayList<Encoder>();
		SensorController sensorController = SensorController.getInstance();
		tempEnc.add((Encoder)sensorController.getSensor("ENCODER0"));
		tempEnc.add((Encoder)sensorController.getSensor("ENCODER1"));
	}

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
					System.out.println("Found Path");
					xmlPath = currentNode;
				}
			}
		}
		

		// Will throw nullPointer if the path doesn't exist
		NodeList xmlCommands = xmlPath.getChildNodes();
		
		CommandList path = new CommandList();
		for (int i = 0; i < xmlCommands.getLength(); i++) {
			Node currentNode = xmlCommands.item(i);
			System.out.println("nodeName:" + currentNode.getNodeName());
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				// calls this method for all the children that are command lists
				path.addCommand(getCommand(currentNode));
			}
		}

		return path;
	}

	public ICommand getCommand(Node n) {
		NamedNodeMap attributes = n.getAttributes();
		String commandName = n.getNodeName();

		switch (commandName) {
		case ("TurnCommand"): {
			double turnDegrees = Double.parseDouble(attributes.getNamedItem("Angle").getNodeValue());
			return new TurnCommand(turnDegrees);
		}
		case ("DriveCommand"): {
			double power = Double.parseDouble(attributes.getNamedItem("Power").getNodeValue());
			Node stopConditionNode = null;
			for(int i = 0; i < n.getChildNodes().getLength(); i++){
				//System.out.println(n.getChildNodes().item(i));
				if(n.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE){
					stopConditionNode = n.getChildNodes().item(i);
				}
			}
			System.out.println(stopConditionNode);
			String stopConditionType = stopConditionNode.getNodeName();
			IStopCondition stopCondition = new TimerStopCondition(0);
			
			if (stopConditionType.equals("DistanceStopCondition")) {
				int stopDistance = Integer.parseInt(stopConditionNode.getAttributes().item(0).getNodeValue());
				stopCondition = new DistanceStopCondition(tempEnc, stopDistance);
			} else if (stopConditionType.equals("TimerStopCondition")) {
				long stopTime = Long.parseLong(stopConditionNode.getAttributes().item(0).getNodeValue());
				stopCondition = new TimerStopCondition(stopTime);
			}

			return new DriveCommand(stopCondition, power);
		}

		}
		return new DriveCommand(new TimerStopCondition(0), 0.6);
	}

	public void printFile() {
		printFile(xmlFile.getDocumentElement(), 0);
	}

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
