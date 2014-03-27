package rdbms;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class SQLUpdateTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testUpdate() throws InvalidAttributeException, SchemaViolationException, InvalidSQLException, PermissionException {
		Attributes schema = new Attributes();
		schema.add(new Attribute("name", Attribute.Type.CHAR, 50));
		schema.add(new Attribute("city", Attribute.Type.CHAR, 50));
				
		Attributes pk = new Attributes();
		pk.add(schema.get(0));
		
		Table table = new Table("people", schema, pk);
		Table.insertIntoDB(table);
		
		Row austin = new Row(schema, new CharValue("Austin"), new CharValue("West Lafayette"));
		Row holly = new Row(schema, new CharValue("Holly"), new CharValue("Ann Arbor"));
		Row brent = new Row(schema, new CharValue("Brent"), new CharValue("West Lafayette"));
		Row elliott = new Row(schema, new CharValue("Elliott"), new CharValue("West Lafayette"));
		Row andrew = new Row(schema, new CharValue("Andrew"), new CharValue("Bloomington"));
		Row doug = new Row(schema, new CharValue("Doug"), new CharValue("Michigan City"));
		
		table.insertAll(austin, holly, brent, elliott, andrew, doug);
		
		// Oh no!! Lafayette is annexing West Lafayette!!
		SQLParser.parse("UPDATE people SET city = 'Lafayette' WHERE city = 'West Lafayette';");
		
		assertEquals("Lafayette", austin.get(1).toString());
		assertEquals("Ann Arbor", holly.get(1).toString());
		assertEquals("Lafayette", brent.get(1).toString());
		assertEquals("Lafayette", elliott.get(1).toString());
		assertEquals("Bloomington", andrew.get(1).toString());
		assertEquals("Michigan City", doug.get(1).toString());
		
	}

}
