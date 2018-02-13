package NotVlad;

import java.io.File;

public class NotVladXMLInterpreterTester {
	public static void main(String[] args) {
		long startParse = System.nanoTime();
		try {
			NotVladXMLInterpreter interp = new NotVladXMLInterpreter(new File("Paths.xml"));
			interp.getPathList("L3-1");
		} catch (NullPointerException e) {
			System.out.println("OOF");
		}
		long endParse = System.nanoTime();
		System.out.println((endParse - startParse) / 1000000 + " Millisecond runtime");
	}
}
