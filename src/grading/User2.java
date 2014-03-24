package grading;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

import rdbms.InvalidSQLException;
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
	public void help() throws InvalidSQLException {
		SQLParser.parse("HELP TABLES;");
	}

	@Test
	public void helpDescribe() throws InvalidSQLException {
		SQLParser.parse("HELP DESCRIBE STUDENT;");
		SQLParser.parse("HELP DESCRIBE DEPARTMENT;");
		SQLParser.parse("HELP DESCRIBE FACULTY;");
	}

	@Test
	public void deleteFromGrade() throws InvalidSQLException {
		SQLParser.parse("DELETE FROM GRADE;");
	}

	@Test
	public void dropGrade() throws InvalidSQLException {
		SQLParser.parse("DROP TABLE GRADE;");
	}

	@Test
	public void selectAllFromGrade() throws InvalidSQLException {
		SQLParser.parse("SELECT * FROM GRADE;");
	}

	@Test
	public void selectFromStudent() throws InvalidSQLException {
		SQLParser.parse("SELECT * FROM STUDENT;");
		SQLParser.parse("SELECT snum FROM STUDENT;");
	}

	@Test
	public void selectFromDepartment() throws InvalidSQLException {
		SQLParser.parse("SELECT deptid,dname FROM DEPARTMENT;");
	}

	@Test
	public void quit() throws InvalidSQLException {
		SQLParser.parse("QUIT;");
	}

}
