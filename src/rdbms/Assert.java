package rdbms;

import static org.junit.Assert.assertEquals;

public class Assert {
	public static void assertLinesEqual(String expected, String actual) {
		String[] expectedLines = expected.split("\n");
		String[] actualLines = 	actual.split("\n");
		assertEquals(expectedLines.length, actualLines.length);
		for (int i=0; i<actualLines.length; i++) {
			assertEquals(expectedLines[i].trim(), actualLines[i].trim());
		}
	}
}
