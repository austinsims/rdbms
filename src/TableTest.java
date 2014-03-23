import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TableTest {
	ByteArrayOutputStream myOut;
	Attributes schema;
	Attributes pk;
	Table t;
	Row malibu, explorer;

	@Before
	public void setUp() throws InvalidAttributeException,
			SchemaViolationException {
		Table.dropEverything();
		
		schema = new Attributes();
		schema.add(new Attribute("make", Attribute.Type.CHAR, 50));
		schema.add(new Attribute("model", Attribute.Type.CHAR, 50));
		schema.add(new Attribute("hp", Attribute.Type.INT));
		schema.add(new Attribute("color", Attribute.Type.CHAR, 50));
		schema.add(new Attribute("price", Attribute.Type.DECIMAL));

		pk = new Attributes();
		pk.add(schema.get(0));
		pk.add(schema.get(1));

		t = new Table("car", schema, pk);
		Table.insertIntoDB(t);

		malibu = new Row(schema);
		malibu.set(new Value[] { new CharValue("Chevrolet"),
				new CharValue("Malibu"), new IntValue(150),
				new CharValue("beige"), new DecValue(25000.25) });

		explorer = new Row(schema);
		explorer.set(new Value[] { new CharValue("Ford"),
				new CharValue("Explorer"), new IntValue(200),
				new CharValue("green"), new DecValue(525.0) });

		t.insert(malibu);
		t.insert(explorer);

	}

	/**
	 * Make sure attributes come out in the same order they went in
	 */
	@Test
	public void testAttrOrder() {
		Attribute[] in = { new Attribute("first", Attribute.Type.INT),
				new Attribute("second", Attribute.Type.INT),
				new Attribute("third", Attribute.Type.INT), };

		Attributes pk = new Attributes();
		pk.add(in[0]);

		Attributes attrs = new Attributes();
		attrs.addAll(Arrays.asList(in));

		Table t = new Table("test", attrs, pk);

		// mysterious as to why i can't cast Attributes.toArray() to Attribute[]
		Object[] out = t.getAttributes().toArray();

		assertEquals(in, out);
	}

	/**
	 * Test the static select method Select all columns of all rows
	 */
	@Test
	public void testSelect() throws Exception {
		// Select all attributes.
		LinkedList<String> selectedAttributes = new LinkedList<String>();
		for (Attribute a : schema) {
			selectedAttributes.add(a.getName());
		}
		LinkedList<String> selectedTables = new LinkedList<String>();
		selectedTables.add(t.getName());

		Rows expected = new Rows(schema);
		expected.add(malibu);
		expected.add(explorer);

		Rows actual = Table.select(selectedAttributes, selectedTables, null);

		assertEquals(expected, actual);
	}

	/**
	 * Try a select with a simple projection, without any conditions
	 */
	@Test
	public void testSimpleProjection() throws Exception {
		Rows big = new Rows(schema);
		big.add(explorer);
		big.add(malibu);

		Attributes subschema = new Attributes();
		// Select just the model and price
		subschema.add(schema.get(1));
		subschema.add(schema.get(4));

		Rows expected = new Rows(subschema);
		Row subMalibu = new Row(subschema);
		subMalibu.set(new Value[] { new CharValue("Malibu"),
				new DecValue(25000.25) });

		Row subExplorer = new Row(subschema);
		subExplorer.set(new Value[] { new CharValue("Explorer"),
				new DecValue(525.0) });

		expected.add(subMalibu);
		expected.add(subExplorer);

		Rows actual = big.project(subschema);

		// Compare HashSets of the results to ignore order
		assertTrue(actual.containsAll(expected));
		assertEquals(actual.size(), expected.size());
	}
	
	/**
	 * Try a select with a no projection, with simple conditions
	 * @throws SchemaViolationException 
	 */
	@Test
	public void testSimpleCondition() throws SchemaViolationException {
		Rows expected = new Rows(schema);
		expected.add(malibu);
		
		Conditions conditions = new Conditions();
		conditions.add(schema.get(0), Conditions.Operator.EQUAL, new CharValue("Chevrolet"));
		List<String> selAttr = new ArrayList<String>();
		for (Attribute a : schema) {
			selAttr.add(a.getName());
		}
		List<String> selTab = Arrays.asList(new String[] {"car"});
		Rows actual = Table.select(
				selAttr, 
				selTab, 
				conditions
			);
		
		assertEquals(expected, actual);
	}
	
	/**
	 * Try a select with a simple projection, with simple conditions
	 * @throws SchemaViolationException 
	 */
	@Test
	public void testSimpleConditionWithProjection() throws SchemaViolationException {
		Attributes subschema = new Attributes();
		subschema.addAll(pk);
		
		Rows expected = new Rows(subschema);
		expected.add(malibu.project(pk));
		
		Conditions conditions = new Conditions();
		conditions.add(schema.get(0), Conditions.Operator.EQUAL, new CharValue("Chevrolet"));
		List<String> selAttr = new ArrayList<String>();
		for (Attribute a : pk) {
			selAttr.add(a.getName());
		}
		List<String> selTab = Arrays.asList(new String[] {"car"});
		Rows actual = Table.select(
				selAttr, 
				selTab, 
				conditions
			);
		
		assertEquals(expected, actual);
	}
	

	
	/**
	 * Make sure rows with duplicate PKs are not allowed
	 * @throws SchemaViolationException 
	 */

	@Test
	public void noDuplicatePKs() throws SchemaViolationException {
		Row malibuDupe = new Row(malibu);
		// Change something but leave the PK the same
		malibuDupe.set(schema.get("color"), new CharValue("Orange"));
		try {
			t.insert(malibuDupe);
			fail("Did not catch exception on duplicate row.");
		} catch (SchemaViolationException e) {
			// Nothing; this is the expected result
		}
		
	}


}






















