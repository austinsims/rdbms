import static org.junit.Assert.*;

import org.junit.Test;


public class CreateTableTest {
	
	/**
	 * Very basic table with spaces between all tokens
	 * @throws Exception 
	 */
	@Test
	public void testBasicCreateTableSpacey() throws Exception {
		try {
			SQLParser.parse("CREATE TABLE climate ( ssn int , name char(50) , age int , PRIMARY KEY ( ssn ) );");
		} catch (Exception e) {
			throw e;			
		}
	}
	
	@Test
	public void denseQuery() throws InvalidSQLException {
		try {
			SQLParser.parse("CREATE TABLE climate(ssn int,name char(50),age int,PRIMARY KEY(ssn));");
		} catch (InvalidSQLException e) {
			throw e;
		}
	}
	
	/**
	 * Basic FK that references another table. 
	 */
	@Test
	public void testFK() throws Exception {
		SQLParser.parse("CREATE TABLE department ( name char(50) , PRIMARY KEY ( name ) );");
		SQLParser.parse("CREATE TABLE faculty ( name char(50) , department char(50) , PRIMARY KEY ( name ) , FOREIGN KEY ( department ) REFERENCES department ( name ) );");  
	}
	
	/**
	 * Try to make a foreign key that references the same table 
	 */
	@Test
	public void testSelfFK() throws Exception {
		SQLParser.parse("CREATE TABLE employee ( ssn int , manager_ssn int , PRIMARY KEY ( ssn ), FOREIGN KEY ( manager_ssn ) REFERENCES employee ( ssn ) );");
	}
	
	/**
	 * Try to create a table with no attributes
	 */
	@Test
	public void noAttrs()  {
		try {
			SQLParser.parse("CREATE TABLE ( );");
			fail("Did not throw exception for table with no attributes");
		} catch (InvalidSQLException e) {
			// Pass
		}
	}
	
	
	/**
	 * Try to create a table with an illegal name
	 */
	
	@Test
	public void badName() {
		
	}
	
	
	/**
	 * Try to create a table with illegal attribute names 
	 */
	
	
	/**
	 * Try to create a table with no PK
	 */
	

	/**
	 * Try to create a table with a PK that isn't actually an attribute
	 */
	
	
	/**
	 * Try to create a table that references a non-existent table in a foreign key
	 */
	
	
	/**
	 * Forget the semicolon at the end of a valid table 
	 */
	

}



















