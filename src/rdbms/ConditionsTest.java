package rdbms;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class ConditionsTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testWithValueConditions() throws InvalidAttributeException, SchemaViolationException {
		Attributes schema = new Attributes();
		Attribute name = new Attribute("name", Attribute.Type.CHAR, 50);
		Attribute age = new Attribute("age", Attribute.Type.INT);
		Attribute salary = new Attribute("salary", Attribute.Type.DECIMAL);
		Attribute title = new Attribute("title", Attribute.Type.CHAR, 50);
		schema.add(name);
		schema.add(age);
		schema.add(salary);
		schema.add(title);
		
		Row row = new Row(schema);
		row.set(new Value[] {
				new CharValue("Austin"),
				new IntValue(22),
				new DecValue(65000.50),
				new CharValue("Developer"),
				});
		
		Conditions cond = new Conditions();
		Arguments args = new Arguments();
		
		// Age == 22
		cond.add(age, Operator.EQUAL, new IntValue(22));	
		assertTrue(cond.test(row));
		
		cond = new Conditions();
		args = new Arguments();
		
		// Name < 'Lucy'
		cond.add(name, Operator.LESS, new CharValue("Lucy"));
		assertTrue(cond.test(row));
		
		cond = new Conditions();
		args = new Arguments();
		
		// Salary > 20000.0
		cond.add(salary, Operator.GREATER, new DecValue(20000.0));
		assertTrue(cond.test(row));
		
	}
	
	@Test
	public void testWithAttrConditions() throws InvalidAttributeException, SchemaViolationException {
		Attributes schema = new Attributes();
		Attribute color = new Attribute("color", Attribute.Type.CHAR, 50);
		Attribute width = new Attribute("width", Attribute.Type.INT);
		Attribute height = new Attribute("height", Attribute.Type.INT);
		schema.addAll(color,width,height);
		Attributes pk = new Attributes();
		pk.add(color);
				
		Row orange = new Row(schema);
		orange.set(new Value[] {
				new CharValue("orange"),
				new IntValue(10),
				new IntValue(10),
		});
		
		Row red = new Row(schema);
		red.set(new Value[] {
				new CharValue("red"),
				new IntValue(10),
				new IntValue(20),
		});
		
		Row blue = new Row(schema);
		blue.set(new Value[] {
				new CharValue("blue"),
				new IntValue(20),
				new IntValue(20),
		});
		
		Row green = new Row(schema);
		green.set(new Value[] {
				new CharValue("green"),
				new IntValue(45),
				new IntValue(20),
		});
		
		Conditions isSquare = new Conditions();
		isSquare.add(width, Operator.EQUAL, height);
		
		assertTrue(isSquare.test(orange));
		assertTrue(isSquare.test(blue));
		assertFalse(isSquare.test(red));
		assertFalse(isSquare.test(green));
		
	}

}

























