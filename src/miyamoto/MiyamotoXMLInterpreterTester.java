package miyamoto;

import java.io.File;

import auto.CommandList;
import auto.ICommand;

/**
 * Class to test the auto path chosen by printing the commands associated with
 * said path
 */
public class MiyamotoXMLInterpreterTester {
	public static void main(String[] args) {
		CommandList test = new CommandList();
		long startParse = System.nanoTime();
		try {
			MiyamotoXMLInterpreter interp = new MiyamotoXMLInterpreter(new File("Paths.xml"));
			test = interp.getPathList("R2-1");
		} catch (Exception e) {
			System.out.println("Parse error");
			System.out.println(e.getMessage());
			System.out.println(e.toString());
			System.out.println(e.getClass().getName());
			e.printStackTrace();
		}
		long endParse = System.nanoTime();
		System.out.println((endParse - startParse) / 1000000 + " Millisecond runtime");
		System.out.println("Command List: ");
		for (int i = 0; i < test.size(); i++) {
			ICommand cur = test.getCommand(i);
			System.out.println(cur);
		}
	}
}
