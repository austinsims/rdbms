package rdbms;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class SQLDeleteFromTest {

	@Before
	public void setUp() throws Exception {
		Table.dropEverything();
	}

	@Test
	public void testDeleteFrom() throws InvalidAttributeException, SchemaViolationException, InvalidSQLException, PermissionException {
		Attributes schema = new Attributes();
		schema.add(new Attribute("city", Attribute.Type.CHAR, 50));
		schema.add(new Attribute("state", Attribute.Type.CHAR, 50));
		
		Table cities = new Table("city", schema, schema);
		Database.insertIntoDB(cities);
		
		cities.insert(new Row(schema,
				new CharValue("Bloomington"),
				new CharValue("Indiana")
				));
		
		Row ilCity = new Row(schema,
				new CharValue("Bloomington"),
				new CharValue("Illinois")
				);
		cities.insert(ilCity);
		
		cities.insert(new Row(schema,
				new CharValue("Delphi"),
				new CharValue("Indiana")
				));
		
		// Delete all Indiana cities.  Make sure that they are gone but the Illinois city remains.
		
		SQLParser.parse("DELETE FROM city WHERE state = 'Indiana';");
		
		assertTrue(cities.rows.size() == 1);
		
		Rows expectedRows = new Rows(schema);
		expectedRows.add(ilCity);
		
		assertEquals(cities.rows, expectedRows);
	}

}
