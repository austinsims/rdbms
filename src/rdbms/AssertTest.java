package rdbms;

import static org.junit.Assert.*;
import static rdbms.Assert.*;

import org.junit.Test;

public class AssertTest {

	@Test
	public void testLinesEqualIgnoreOrder() {
		String a1 = "hello"+ENDL+"world";
		String a2 = "world"+ENDL+"hello";
		
		String b1 = "hello"+ENDL+"world";
		String b2 = "hello"+ENDL+"shirley";
		
		assertTrue(Assert.linesEqualIgnoreOrder(a1, a2));
		assertFalse(Assert.linesEqualIgnoreOrder(b1, b2));
	}

}
