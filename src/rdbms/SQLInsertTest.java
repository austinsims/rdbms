package rdbms;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.StringTokenizer;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SQLInsertTest {
	ByteArrayOutputStream myOut;
	
	@Before
	public void setUp() throws Exception {
		myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));
		Table.dropEverything();
	}

	
	@Test
	public void basic() throws InvalidAttributeException, InvalidSQLException {
		String[] expected = new String[] {"Tuple", "inserted", "successfully", "Tuple", "inserted", "successfully"};
		
		Attributes schema = new Attributes();
		schema.add(new Attribute("make", Attribute.Type.CHAR, 50));
		schema.add(new Attribute("model", Attribute.Type.CHAR, 50));
		schema.add(new Attribute("hp", Attribute.Type.INT));
		schema.add(new Attribute("color", Attribute.Type.CHAR, 50));
		schema.add(new Attribute("price", Attribute.Type.DECIMAL));

		Attributes pk = new Attributes();
		pk.add(schema.get(0));
		pk.add(schema.get(1));

		Table t = new Table("car", schema, pk);
		
		Table.insertIntoDB(t);

		SQLParser.parse("INSERT INTO car VALUES ( 'Rolls Royce', 'z350x', 999, 'Black', 100000 );");
		SQLParser.parse("INSERT INTO car VALUES ( 'Dodge', 'Charger', 350, 'Red', 35000 );");

		
		String actual = myOut.toString();
		StringTokenizer st = new StringTokenizer(actual);
		
		assertEquals(expected.length, st.countTokens());
		int i = 0;
		while (st.hasMoreTokens()) {
			assertEquals(expected[i],st.nextToken());
			i++;
		}
	}

}
