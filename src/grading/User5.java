package grading;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

import rdbms.InvalidSQLException;
import rdbms.PermissionException;
import rdbms.SQLParser;

public class User5 {
	ByteArrayOutputStream myOut;

	@Before
	public void setUp() throws Exception {
		// TODO: Log in as user5

		myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));
	}

	@Test
	public void helpDescribeStudent() throws InvalidSQLException, PermissionException {
		SQLParser.parse("HELP DESCRIBE STUDENT;");
	}

	@Test
	public void selectFromGrade() throws InvalidSQLException, PermissionException {
		SQLParser.parse("SELECT * FROM GRADE;");
	}

	@Test
	public void selectFromStudent() throws InvalidSQLException, PermissionException {
		SQLParser.parse("SELECT * FROM STUDENT;");
	}

	@Test
	public void selectFromFaculty() throws InvalidSQLException, PermissionException {
		SQLParser.parse("SELECT * FROM FACULTY WHERE fid!=1010 AND fid!=1020;");
		SQLParser.parse("SELECT * FROM FACULTY WHERE fid>=1050 OR fid<1020;");
		SQLParser.parse("SELECT * FROM FACULTY WHERE fid<=1030 OR fid>1050;");
	}

	@Test
	public void selectFromEnrolled() throws InvalidSQLException, PermissionException {
		SQLParser.parse("SELECT * FROM ENROLLED;");
	}

	@Test
	public void quit() throws InvalidSQLException, PermissionException {
		SQLParser.parse("QUIT;");
	}

}
