package grading;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

import rdbms.InvalidSQLException;
import rdbms.PermissionException;
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
	public void selectFromGrade() throws InvalidSQLException, PermissionException {
		SQLParser.parse("SELECT * FROM GRADE;");
	}

	@Test
	public void deleteFromGrade() throws InvalidSQLException, PermissionException {
		SQLParser.parse("DELETE FROM GRADE;");
	}

	@Test
	public void selectFromGradeAgain() throws InvalidSQLException, PermissionException {
		SQLParser.parse("SELECT * FROM GRADE;");
	}

	@Test
	public void dropGrade() throws InvalidSQLException, PermissionException {
		SQLParser.parse("DROP TABLE GRADE;");
	}

	@Test
	public void showTables() throws InvalidSQLException, PermissionException {
		SQLParser.parse("HELP TABLES;");
	}

	@Test
	public void describeGrade() throws InvalidSQLException, PermissionException {
		SQLParser.parse("HELP DESCRIBE GRADE;");
	}

	@Test
	public void createSubschema() throws InvalidSQLException, PermissionException {
		SQLParser.parse("CREATE SUBSCHEMA STUDENT sname,slevel,age;");
	}

	@Test
	public void createUser() throws InvalidSQLException, PermissionException {
		SQLParser.parse("CREATE USER user5 User-B;");
	}

	@Test
	public void deleteFromEnrolled() throws InvalidSQLException, PermissionException {
		SQLParser.parse("DELETE FROM ENROLLED WHERE student_num=16711 OR student_num=11;");
	}

	@Test
	public void quit() throws InvalidSQLException, PermissionException {
		SQLParser.parse("QUIT;");
	}

}
