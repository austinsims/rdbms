import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class ConditionsTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testTest() throws InvalidAttributeException, SchemaViolationException {
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
		cond.add(age, Conditions.Operator.EQUAL, new IntValue(22));	
		assertTrue(cond.test(row));
		
		cond = new Conditions();
		args = new Arguments();
		
		// Name < 'Lucy'
		cond.add(name, Conditions.Operator.LESS, new CharValue("Lucy"));
		assertTrue(cond.test(row));
		
		cond = new Conditions();
		args = new Arguments();
		
		// Salary > 20000.0
		cond.add(salary, Conditions.Operator.GREATER, new DecValue(20000.0));
		assertTrue(cond.test(row));
		
	}

}
