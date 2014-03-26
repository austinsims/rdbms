package rdbms;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.StringTokenizer;

import org.junit.Before;
import org.junit.Test;


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
		
        StringTokenizer st = new StringTokenizer(myOut.toString().trim());
        String[] actual = new String[st.countTokens()];
        int i=0;
        while (st.hasMoreTokens()) {
        	actual[i] = st.nextToken();
        	i++;
        }
        
        assertEquals(expected, actual);
	}
	
	@Test
	public void simpleProject() throws InvalidSQLException {
		SQLParser.parse("CREATE TABLE employee ( ename char(50), eloc char(50), PRIMARY KEY (ename));");
		assertEquals("Created table successfully", myOut.toString().trim().trim());
		
		myOut.reset();
		
		SQLParser.parse("INSERT INTO employee VALUES ('John', 'Chicago');");
		assertEquals("Tuple inserted successfully", myOut.toString().trim());
		
		myOut.reset();
		
		SQLParser.parse("SELECT ename FROM employee;");
		compareLines(new String[] {"ename","John"}, myOut.toString());
	}
	
	@Test
	public void simpleJoin() throws InvalidSQLException {
		SQLParser.parse("CREATE TABLE employee ( ename char(50), eloc char(50), PRIMARY KEY (ename));");
		assertEquals("Created table successfully", myOut.toString().trim().trim());
		
		myOut.reset();

		SQLParser.parse("CREATE TABLE location ( lname char(50), lsales int, PRIMARY KEY (lname) );");
        assertEquals("Created table successfully", myOut.toString().trim().trim());
        
        myOut.reset();

		SQLParser.parse("HELP DESCRIBE employee;");
		assertEquals("employee ( ename char(50), eloc char(50), PRIMARY KEY (ename, ) )", myOut.toString().trim());
		
		myOut.reset();

		SQLParser.parse("HELP DESCRIBE location;");
		assertEquals("location ( lname char(50), lsales int, PRIMARY KEY (lname, ) )", myOut.toString().trim());
		
		myOut.reset();

		SQLParser.parse("INSERT INTO employee VALUES ( 'Jack', 'Chicago' );");
		assertEquals("Tuple inserted successfully", myOut.toString().trim());
		
		myOut.reset();

		SQLParser.parse("SELECT * FROM employee;");
		compareLines(new String[] {"ename	eloc", "Jack	Chicago"}, myOut.toString());
		
		myOut.reset();

		SQLParser.parse("INSERT INTO location VALUES ( 'Chicago', 5000000 );");
		assertEquals("Tuple inserted successfully", myOut.toString().trim());
		
		myOut.reset();

		SQLParser.parse("SELECT * FROM location;");
		compareLines(new String[] {"lname	lsales", "Chicago	5000000"}, myOut.toString());

		myOut.reset();
		
		SQLParser.parse("SELECT ename, lsales FROM employee, location WHERE eloc = lname;");
		compareLines(new String[] {"ename	lsales","Jack	5000000"}, myOut.toString());

	}

	private void compareLines(String[] expectedLines, String actual) {
		// TODO Auto-generated method stub
		String[] actualLines = 	actual.split("\n");
		String[] actualTrimmedLines = new String[actualLines.length];
		for (int i=0; i<actualLines.length; i++)
			actualTrimmedLines[i] = actualLines[i].trim();
		assertEquals(expectedLines, actualTrimmedLines);
	}
}
