package rdbms;

import static org.junit.Assert.assertEquals;

import java.util.Scanner;

import org.junit.Test;

public class SQLParserParseToOpTest {

	@Test
	public void testEq() {
		Scanner s = new Scanner("attr = 10");
		assertEquals("attr", s.next());
		assertEquals(Operator.EQUAL, SQLParser.parseToOperator(s));
		assertEquals("10", s.next());
	}
	
	@Test
	public void testNotEq() {
		Scanner s = new Scanner("attr ! = 10");
		assertEquals("attr", s.next());
		assertEquals(Operator.NOT_EQUAL, SQLParser.parseToOperator(s));
		assertEquals("10", s.next());
	}
	
	@Test
	public void testLt() {
		Scanner s = new Scanner("attr < 10");
		assertEquals("attr", s.next());
		assertEquals(Operator.LESS, SQLParser.parseToOperator(s));
		assertEquals("10", s.next());		
	}
	
	@Test
	public void testGt() {
		Scanner s = new Scanner("attr > 10");
		assertEquals("attr", s.next());
		assertEquals(Operator.GREATER, SQLParser.parseToOperator(s));
		assertEquals("10", s.next());
	}
	
	@Test
	public void testLtEq() {
		Scanner s = new Scanner("attr < = 10");
		assertEquals("attr", s.next());
		assertEquals(Operator.LESS_OR_EQUAL, SQLParser.parseToOperator(s));
		assertEquals("10", s.next());
	}
	
	@Test
	public void testGtEq() {
		Scanner s = new Scanner("attr > = 10");
		assertEquals("attr", s.next());
		assertEquals(Operator.GREATER_OR_EQUAL, SQLParser.parseToOperator(s));
		assertEquals("10", s.next());		
	}

}
