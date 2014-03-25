package grading;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

import rdbms.InvalidSQLException;
import rdbms.SQLParser;

public class Admin2 {
	ByteArrayOutputStream myOut;

	@Before
	public void setUp() throws Exception {
		// TODO: Log in as admin

		myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));
	}

	@Test
	public void selectFromGrade() throws InvalidSQLException {
		SQLParser.parse("SELECT * FROM GRADE;");
	}

	@Test
	public void deleteFromGrade() throws InvalidSQLException {
		SQLParser.parse("DELETE FROM GRADE;");
	}

	@Test
	public void selectFromGradeAgain() throws InvalidSQLException {
		SQLParser.parse("SELECT * FROM GRADE;");
	}

	@Test
	public void dropGrade() throws InvalidSQLException {
		SQLParser.parse("DROP TABLE GRADE;");
	}

	@Test
	public void showTables() throws InvalidSQLException {
		SQLParser.parse("HELP TABLES;");
	}

	@Test
	public void describeGrade() throws InvalidSQLException {
		SQLParser.parse("HELP DESCRIBE GRADE;");
	}

	@Test
	public void createSubschema() throws InvalidSQLException {
		SQLParser.parse("CREATE SUBSCHEMA STUDENT sname,slevel,age;");
	}

	@Test
	public void createUser() throws InvalidSQLException {
		SQLParser.parse("CREATE USER user5 User-B;");
	}

	@Test
	public void deleteFromEnrolled() throws InvalidSQLException {
		SQLParser.parse("DELETE FROM ENROLLED WHERE snum=16711 OR snum=11;");
	}

	@Test
	public void quit() throws InvalidSQLException {
		SQLParser.parse("QUIT;");
	}

}