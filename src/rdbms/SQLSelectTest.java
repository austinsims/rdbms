package rdbms;
import static org.junit.Assert.assertEquals;
import static rdbms.Assert.assertLinesEqual;

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
		Database.clear();
	}
	
	@Test
	public void basic() throws InvalidAttributeException, SchemaViolationException, InvalidSQLException, PermissionException  {
        String[] expected = new String[] {"thing", "hello"};


        	// Create a new table
        	Attributes schema = new Attributes();
        		schema.add(new Attribute("thing", Attribute.Type.CHAR, 50));
        	Attributes pk = new Attributes();
        	pk.add(schema.get(0));
        	Table stuff = new Table("stuff", schema, pk);
        	Database.insertIntoDB(stuff);

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
	public void simpleProject() throws InvalidSQLException, PermissionException {
		SQLParser.parse("CREATE TABLE employee ( ename char(50), eloc char(50), PRIMARY KEY (ename));");
		assertEquals("Created table successfully", myOut.toString().trim().trim());
		
		myOut.reset();
		
		SQLParser.parse("INSERT INTO employee VALUES ('John', 'Chicago');");
		assertEquals("Tuple inserted successfully", myOut.toString().trim());
		
		myOut.reset();
		
		SQLParser.parse("SELECT ename FROM employee;");
		assertLinesEqual("ename\nJohn", myOut.toString());
	}
	
	@Test
	public void simpleJoin() throws InvalidSQLException, PermissionException {
		SQLParser.parse("CREATE TABLE employee ( ename char(50), eloc char(50), PRIMARY KEY (ename));");
		assertEquals("Created table successfully", myOut.toString().trim().trim());
		
		myOut.reset();

		SQLParser.parse("CREATE TABLE location ( lname char(50), lsales int, PRIMARY KEY (lname) );");
        assertEquals("Created table successfully", myOut.toString().trim().trim());
        
        myOut.reset();

		SQLParser.parse("HELP DESCRIBE employee;");
		assertEquals("employee ( ename CHAR(50), eloc CHAR(50), PRIMARY KEY (ename, ) )", myOut.toString().trim());
		
		myOut.reset();

		SQLParser.parse("HELP DESCRIBE location;");
		assertEquals("location ( lname CHAR(50), lsales INT, PRIMARY KEY (lname, ) )", myOut.toString().trim());
		
		myOut.reset();

		SQLParser.parse("INSERT INTO employee VALUES ( 'Jack', 'Chicago' );");
		assertEquals("Tuple inserted successfully", myOut.toString().trim());
		
		myOut.reset();

		SQLParser.parse("SELECT * FROM employee;");
		assertLinesEqual("ename	eloc\nJack	Chicago", myOut.toString());
		
		myOut.reset();

		SQLParser.parse("INSERT INTO location VALUES ( 'Chicago', 5000000 );");
		assertEquals("Tuple inserted successfully", myOut.toString().trim());
		
		myOut.reset();

		SQLParser.parse("SELECT * FROM location;");
		assertLinesEqual("lname	lsales\nChicago	5000000", myOut.toString());

		myOut.reset();
		
		SQLParser.parse("SELECT ename, lsales FROM employee, location WHERE eloc = lname;");
		assertLinesEqual("ename	lsales\nJack	5000000", myOut.toString());

	}

	
}
