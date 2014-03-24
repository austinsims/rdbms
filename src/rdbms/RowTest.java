package rdbms;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RowTest {

	Attributes schema;
	Row r1, r2, r3;

	@Before
	public void setUp() throws Exception {
		schema = new Attributes();
		schema.addAll(Arrays.asList(new Attribute[] {
				new Attribute("name", Attribute.Type.CHAR, 50),
				new Attribute("age", Attribute.Type.INT),
				new Attribute("height", Attribute.Type.INT) }));

		r1 = new Row(schema);
		r1.set(0, new CharValue("Angela"));
		r1.set(1, new IntValue(17));
		r1.set(2, new IntValue(160));

		r2 = new Row(schema);
		r2.set(0, new CharValue("Angela"));
		r2.set(1, new IntValue(17));
		r2.set(2, new IntValue(160));
		
		r3 = new Row(schema);
		r3.set(0, new CharValue("Austin"));
		r3.set(1, new IntValue(22));
		r3.set(2, new IntValue(172));
	}

	@Test
	public void testEquals() throws Exception {
		try {
			assertTrue(r1.equals(r2));
			assertFalse(r1.equals(r3));
		} catch (Exception e) {
			fail(e.getMessage());
			throw e;
		}
	}

	/**
	 * Make sure that objects that are equal have the same hash code, and
	 * objects that aren't have different hash codes
	 */
	@Test
	public void testHashCode() {
		assertTrue(r1.hashCode() == r2.hashCode());
		assertFalse(r1.hashCode() == r3.hashCode());
		
		// Now try to add a duplicate row to a Rows (LinkedHashSet).  Identical hash codes should disallow it
		Rows rows = new Rows(schema);
		assertTrue(rows.add(r1));
		assertFalse(rows.add(r2));
		assertEquals(1, rows.size());
	}

	@Test
	public void testProject() throws Exception {
		try {
			Attributes schema = new Attributes();
			schema.addAll(Arrays.asList(new Attribute[] {
					new Attribute("name", Attribute.Type.CHAR, 50),
					new Attribute("age", Attribute.Type.INT),
					new Attribute("height", Attribute.Type.INT) }));

			Attributes subschema = new Attributes();
			subschema.add(schema.get(0));

			Row big = new Row(schema);
			big.set(0, new CharValue("Angela"));
			big.set(1, new IntValue(17));
			big.set(2, new IntValue(160));

			Row expected = new Row(subschema);
			expected.set(0, big.get(0));

			Row little = big.project(subschema);
			assertEquals(expected, little);

		} catch (Exception e) {
			fail(e.getMessage());
			throw e;
		}

	}

}
