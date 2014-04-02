package rdbms;

import static org.junit.Assert.*;

import org.junit.Test;

public class AssertTest {

	@Test
	public void testLinesEqualIgnoreOrder() {
		String a1 = "hello\nworld";
		String a2 = "world\nhello";
		
		String b1 = "hello\nworld";
		String b2 = "hello\nshirley";
		
		assertTrue(Assert.linesEqualIgnoreOrder(a1, a2));
		assertFalse(Assert.linesEqualIgnoreOrder(b1, b2));
	}

}
