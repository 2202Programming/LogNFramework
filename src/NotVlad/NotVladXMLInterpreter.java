package NotVlad;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.ni.vision.NIVision.GetGeometricTemplateFeatureInfoResult;

import auto.CommandList;
import auto.ICommand;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.io.File;

public class NotVladXMLInterpreter {
	private Document xmlFile;

	public NotVladXMLInterpreter(File f) {
		readFile(f);
	}

	public void readFile(File f) {
		try {

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			xmlFile = dBuilder.parse(f);
			xmlFile.getDocumentElement().normalize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CommandList getPathList(String id) {
		Node xmlPath = xmlFile.getElementById(id);
		
		NodeList xmlCommands = xmlPath.getChildNodes();
		
		CommandList path = new CommandList();
		for(int i = 0; i < xmlCommands.getLength(); i++) {
			Node currentNode = xmlCommands.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE && currentNode.getNodeName().equals("Command")) {
				// calls this method for all the children that are command lists
				path.addCommand(getCommand(currentNode));
			}
		}
		
		return path;
	}
	
	public ICommand getCommand(Node n) {
		
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

			line += currentNode.getNodeName() + "= " + currentNode.getNodeValue();
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
