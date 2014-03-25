package rdbms;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConstraintTest {

	@Test
	public void testCheck() throws SchemaViolationException {
		Constraint lessThanTen = new Constraint(Operator.LESS, new IntValue(10));
		Constraint isEthan = new Constraint(Operator.EQUAL, new CharValue("Ethan"));
		Constraint notBlank = new Constraint(Operator.NOT_EQUAL, new CharValue(""));
		
		assertTrue(lessThanTen.check(new IntValue(5)));
		assertFalse(lessThanTen.check(new IntValue(10)));
		assertFalse(lessThanTen.check(new IntValue(15)));
		
		assertTrue(isEthan.check(new CharValue("Ethan")));
		assertFalse(isEthan.check(new CharValue("Sam")));
		
		assertTrue(notBlank.check(new CharValue("Hello World")));
		assertFalse(notBlank.check(new CharValue("")));
	}

}
