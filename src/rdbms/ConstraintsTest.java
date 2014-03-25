package rdbms;

import java.util.Arrays;

import org.junit.Test;
import static org.junit.Assert.*;

public class ConstraintsTest {

	@Test
	public void testAndOperator() throws SchemaViolationException {
		Constraint aboveTen = new Constraint(Operator.GREATER, new IntValue(10));
		Constraint belowTwenty = new Constraint(Operator.LESS, new IntValue(20));
		
		Constraints betweenTenTwenty = new Constraints(Constraints.Operator.AND);
		betweenTenTwenty.addAll(Arrays.asList(new Constraint[] {aboveTen, belowTwenty}));
		
		assertTrue(betweenTenTwenty.check(new IntValue(15)));
		assertFalse(betweenTenTwenty.check(new IntValue(25)));
		assertFalse(betweenTenTwenty.check(new IntValue(5)));
	}
	
	@Test
	public void testOrOperator() throws SchemaViolationException {
		Constraint belowTen = new Constraint(Operator.LESS, new IntValue(10));
		Constraint aboveTwenty = new Constraint(Operator.GREATER, new IntValue(20));
		
		Constraints outsideTenTwenty = new Constraints(Constraints.Operator.OR);
		outsideTenTwenty.addAll(Arrays.asList(new Constraint[] {belowTen, aboveTwenty}));
		
		assertFalse(outsideTenTwenty.check(new IntValue(15)));
		assertTrue(outsideTenTwenty.check(new IntValue(25)));
		assertTrue(outsideTenTwenty.check(new IntValue(5)));
	}

}
