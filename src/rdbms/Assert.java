package rdbms;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class Assert {
	public static void assertLinesEqual(String expected, String actual) {
		String[] expectedLines = expected.split("\n");
		String[] actualLines = 	actual.split("\n");
		assertEquals(expectedLines.length, actualLines.length);
		for (int i=0; i<actualLines.length; i++) {
			assertEquals(expectedLines[i].trim(), actualLines[i].trim());
		}
	}
	
	public static boolean linesEqualIgnoreOrder(String expected, String actual) {
		String[] expectedLines = expected.split("\n");
		List<String> expectedTrimmedLines = new ArrayList<String>();
		for (String line : expectedLines) {
			expectedTrimmedLines.add(line.trim());
		}
		
		String[] actualLines = 	actual.split("\n");
		assertEquals(expectedLines.length, actualLines.length);
		for (String actualLine : actualLines) {
			if (!expectedTrimmedLines.contains(actualLine.trim()))
				return false;
		}
		
		return true;
	}
}
