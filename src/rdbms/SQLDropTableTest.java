package rdbms;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;


public class SQLDropTableTest {
	ByteArrayOutputStream myOut;

	@Before
	public void setUp() {
		myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));
		Table.dropEverything();
	}
	
	@Test
	public void test() throws PermissionException {
		try {
			SQLParser.parse("CREATE TABLE stuff ( thing int , PRIMARY KEY ( thing ) );");
			assertEquals("Created table successfully", myOut.toString().trim());
			myOut.reset();
			
			SQLParser.parse("DROP TABLE stuff;");
			assertEquals("Table dropped successfully", myOut.toString().trim());
			
			myOut.reset();
			
			SQLParser.parse("CREATE TABLE stuff ( thing int , PRIMARY KEY ( thing ) );");
			assertEquals("Created table successfully", myOut.toString().trim());
			
			
		} catch (InvalidSQLException e) {
			fail("Could not create table again after dropping it: " + e.getMessage());
		}
	}

}
