package rdbms;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.StringTokenizer;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class SQLSelectTest {
	ByteArrayOutputStream myOut;

	@Before
	public void setUp() {
		myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));
		Table.dropEverything();
	}
	
	@Test
	public void basic() throws InvalidAttributeException, SchemaViolationException, InvalidSQLException  {
        String[] expected = new String[] {"thing", "hello"};


        	// Create a new table
        	Attributes schema = new Attributes();
        		schema.add(new Attribute("thing", Attribute.Type.CHAR, 50));
        	Attributes pk = new Attributes();
        	pk.add(schema.get(0));
        	Table stuff = new Table("stuff", schema, pk);
        	Table.insertIntoDB(stuff);

        	// Insert a row into it
        	Row row = new Row(schema);
				row.set(0, new CharValue("hello"));
        	stuff.insert(row);
        	
        	// Try to retrieve from it
        	SQLParser.parse("SELECT * FROM stuff;");
		
        StringTokenizer st = new StringTokenizer(myOut.toString());
        String[] actual = new String[st.countTokens()];
        int i=0;
        while (st.hasMoreTokens()) {
        	actual[i] = st.nextToken();
        	i++;
        }
        
        assertEquals(expected, actual);
	}
	
}
