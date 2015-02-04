package rdbms;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class Assert {
	
	public static final String ENDL = System.getProperty("line.separator");
	
	public static void assertLinesEqual(String expected, String actual) {
		String[] expectedLines = expected.split(ENDL);
		String[] actualLines = 	actual.split(ENDL);
		assertEquals(expectedLines.length, actualLines.length);
		for (int i=0; i<actualLines.length; i++) {
			assertEquals(expectedLines[i].trim(), actualLines[i].trim());
		}
	}
	
	public static boolean linesEqualIgnoreOrder(String expected, String actual) {
		String[] expectedLines = expected.split(ENDL);
		List<String> expectedTrimmedLines = new ArrayList<String>();
		for (String line : expectedLines) {
			expectedTrimmedLines.add(line.trim());
		}
		
		String[] actualLines = 	actual.split(ENDL);
		assertEquals(expectedLines.length, actualLines.length);
		for (String actualLine : actualLines) {
			if (!expectedTrimmedLines.contains(actualLine.trim()))
				return false;
		}
		
		return true;
	}
}
