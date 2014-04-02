package grading;

import static rdbms.Assert.assertLinesEqual;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

import rdbms.InvalidSQLException;
import rdbms.PermissionException;
import rdbms.SQLParser;

public class User2 {
	ByteArrayOutputStream myOut;

	@Before
	public void setUp() throws Exception {
		// TODO: Log in as user2
		
		myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));
	}

	@Test
	public void help() throws InvalidSQLException, PermissionException {
		SQLParser.parse("HELP TABLES;");
	}

	@Test
	public void helpDescribe() throws InvalidSQLException, PermissionException {
		SQLParser.parse("HELP DESCRIBE STUDENT;");
		assertLinesEqual(
				"sname -- CHAR(30)\n" + 
				"slevel -- CHAR(10) -- slevel = 'JR' OR slevel = 'SR' OR slevel = 'SO' OR slevel = 'FR'\n",
				myOut.toString());
		myOut.reset();
		SQLParser.parse("HELP DESCRIBE DEPARTMENT;");
		SQLParser.parse("HELP DESCRIBE FACULTY;");
	}

	@Test
	public void deleteFromGrade() throws InvalidSQLException, PermissionException {
		SQLParser.parse("DELETE FROM GRADE;");
	}

	@Test
	public void dropGrade() throws InvalidSQLException, PermissionException {
		SQLParser.parse("DROP TABLE GRADE;");
	}

	@Test
	public void selectAllFromGrade() throws InvalidSQLException, PermissionException {
		SQLParser.parse("SELECT * FROM GRADE;");
	}

	@Test
	public void selectFromStudent() throws InvalidSQLException, PermissionException {
		SQLParser.parse("SELECT * FROM STUDENT;");
		SQLParser.parse("SELECT snum FROM STUDENT;");
	}

	@Test
	public void selectFromDepartment() throws InvalidSQLException, PermissionException {
		SQLParser.parse("SELECT deptid,dname FROM DEPARTMENT;");
	}

	@Test
	public void quit() throws InvalidSQLException, PermissionException {
		SQLParser.parse("QUIT;");
	}

}
