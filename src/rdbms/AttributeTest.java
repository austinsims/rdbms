package rdbms;
import static org.junit.Assert.*;

import org.junit.Test;


public class AttributeTest {

	@Test
	public void testEquals() throws InvalidAttributeException {
		assertTrue(
				(new Attribute("hello", Attribute.Type.INT))
				.equals
				(new Attribute("hello", Attribute.Type.INT))
		);
		
		assertFalse(
				(new Attribute("asdff", Attribute.Type.INT))
				.equals
				(new Attribute("hello", Attribute.Type.INT))
		);
		
		try {
			assertFalse(
					(new Attribute("hello", Attribute.Type.CHAR, 50))
					.equals
					(new Attribute("hello", Attribute.Type.CHAR, 49))
					);
		} catch (InvalidAttributeException e) {
			fail(e.getMessage());
			throw e;
		}
	}
	
	/**
	 * Test attribute name check.
	 */
	
	@Test
	public void testValidAttrName() {
		assertTrue(Attribute.validName("king_kong"));
		assertFalse(Attribute.validName("1monkeys_"));
		assertTrue(Attribute.validName("_"));
		assertFalse(Attribute.validName("123"));
		assertFalse(Attribute.validName("Hello world!"));
	}
	
	@Test
	public void checkTestConstraints() throws SchemaViolationException {
		Attribute testScore = new Attribute("testScore", Attribute.Type.INT);
		testScore.addConstraint(new Constraint(Operator.GREATER_OR_EQUAL, new IntValue(0)));
		testScore.addConstraint(new Constraint(Operator.LESS_OR_EQUAL, new IntValue(100)));
		
		IntValue validScore = new IntValue(60);
		IntValue invalidScore1 = new IntValue(-50);
		IntValue invalidScore2 = new IntValue(101);
		IntValue invalidScore3 = new IntValue(150);
		
		assertTrue(testScore.checkConstraints(validScore));
		assertFalse(testScore.checkConstraints(invalidScore1));
		assertFalse(testScore.checkConstraints(invalidScore2));
		assertFalse(testScore.checkConstraints(invalidScore3));
	}

}
