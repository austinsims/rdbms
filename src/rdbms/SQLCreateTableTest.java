package rdbms;
import org.junit.Before;
import org.junit.Test;


public class SQLCreateTableTest {
	
	@Before
	public void setUp() {
		Database.clear();
	}
	
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
	public void denseQuery() throws InvalidSQLException, PermissionException {
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
	
	@Test
	public void domainConstraints() throws InvalidSQLException, PermissionException {
		SQLParser.parse("CREATE TABLE employee ( ssn int, name char(50) CHECK(name != ''), age int CHECK(age > 0), PRIMARY KEY (ssn));");
		SQLParser.parse("CREATE TABLE stuff ( a INT CHECK(a>0), b INT CHECK(b<0), c INT CHECK(c!=0), d INT CHECK(d<=0), e INT CHECK(e>=0), f INT CHECK(f=0), PRIMARY KEY (a));"); 
	}
	
	@Test
	public void compoundDomainConstraints() throws InvalidSQLException, PermissionException {
		SQLParser.parse("CREATE TABLE employee ( ssn int, name char(50) CHECK(name != ''), age int CHECK(age > 0 AND age < 150), PRIMARY KEY (ssn));");
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



















