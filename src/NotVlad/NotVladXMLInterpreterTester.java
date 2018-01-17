package NotVlad;

import java.io.File;

public class NotVladXMLInterpreterTester {
	public static void main(String[] args) {
		NotVladXMLInterpreter interp = new NotVladXMLInterpreter(new File("Paths.xml"));
		interp.printFile();
		interp.getPathList("L1-1");
	}
}
