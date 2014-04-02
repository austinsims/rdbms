package grading;

import static rdbms.Assert.*;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

import rdbms.Database;
import rdbms.InvalidSQLException;
import rdbms.PermissionException;
import rdbms.RDBMS;
import rdbms.SQLParser;
import rdbms.User;

public class User2 {
	ByteArrayOutputStream myOut;

	@Before
	public void setUp() throws Exception {
		RDBMS.load();
		User user2 = Database.getUser("user2");
		if (user2 == null) fail("Could not retreive user object");
		Database.login(user2);
		
		myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));
		System.setErr(new PrintStream(myOut));
	}

	@Test
	public void test() throws InvalidSQLException, PermissionException {
		SQLParser.parse("HELP TABLES;");
		assertTrue(linesEqualIgnoreOrder("DEPARTMENT\nFACULTY\nSTUDENT\nCLASS\nENROLLED\nGRADE", myOut.toString()));
        myOut.reset();

		SQLParser.parse("HELP DESCRIBE STUDENT;");
		assertLinesEqual(
				"sname -- CHAR(30)\n" + 
				"slevel -- CHAR(10) -- slevel = 'JR' OR slevel = 'SR' OR slevel = 'SO' OR slevel = 'FR'\n",
				myOut.toString());
		myOut.reset();

		SQLParser.parse("HELP DESCRIBE DEPARTMENT;");
		assertLinesEqual(
				"dname -- CHAR(30)\n"+
				"location -- CHAR(30)\n",
				myOut.toString()
				);
        myOut.reset();

		SQLParser.parse("HELP DESCRIBE FACULTY;");
		assertLinesEqual(
				"fid -- INT -- PRIMARY KEY -- fid != 0\n"+
				"fname -- CHAR(30)\n"+
				"dept -- INT -- FOREIGN KEY REFERENCES DEPARTMENT (deptid)\n",
				myOut.toString()
				);
        myOut.reset();

		SQLParser.parse("DELETE FROM GRADE;");
		assertLinesEqual("Error: Authorization failure!", myOut.toString());
        myOut.reset();

		SQLParser.parse("DROP TABLE GRADE;");
		assertLinesEqual("Error: Authorization failure!", myOut.toString());
        myOut.reset();

		SQLParser.parse("SELECT * FROM GRADE;");
		assertTrue(linesEqualIgnoreOrder(
				"stu_num	classname	grade\n"+
				"14181	ENG400	A\n"+
				"80161	ENG400	B\n",
				myOut.toString()));
        myOut.reset();

		SQLParser.parse("SELECT * FROM STUDENT;");
		assertTrue(linesEqualIgnoreOrder(
				"sname	slevel\n"+
				"Jack	SO\n"+
				"Smith	FR\n"+
				"Banks	SR\n"+
				"M.Lee	SO	\n"+
				"Bale	JR\n"+
				"Lim	SR\n"+
				"Sharon	FR\n"+
				"Johnson	JR\n"+
				"E.Cho	JR\n"+
				"Angin	SR	\n",
				myOut.toString()));
        myOut.reset();

		SQLParser.parse("SELECT snum FROM STUDENT;");
		assertTrue(linesEqualIgnoreOrder(
				"The requested schema does not contain the attribute snum",
				myOut.toString()));
        myOut.reset();

		SQLParser.parse("SELECT deptid,dname FROM DEPARTMENT;");
		assertTrue(linesEqualIgnoreOrder(
				"The requested schema does not contain the attribute deptid",			
				myOut.toString()));
        myOut.reset();
        
        // No need to save since User-B can't change anything.
	}

}
