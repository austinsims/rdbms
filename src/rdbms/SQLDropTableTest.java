package rdbms;
import static org.junit.Assert.*;

import org.junit.Test;


public class SQLDropTableTest {

	@Test
	public void test() {
		try {
			SQLParser.parse("CREATE TABLE stuff ( thing int , PRIMARY KEY ( thing ) );");
			SQLParser.parse("DROP TABLE stuff;");
			SQLParser.parse("CREATE TABLE stuff ( thing int , PRIMARY KEY ( thing ) );");
		} catch (InvalidSQLException e) {
			fail("Could not create table again after dropping it: " + e.getMessage());
		}
	}

}
