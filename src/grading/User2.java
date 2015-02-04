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
		assertTrue(linesEqualIgnoreOrder(
				"DEPARTMENT"+ENDL+
				"FACULTY"+ENDL+
				"STUDENT"+ENDL+
				"CLASS"+ENDL+
				"ENROLLED"+ENDL+
				"GRADE"+ENDL,
				myOut.toString()));
        myOut.reset();

		SQLParser.parse("HELP DESCRIBE STUDENT;");
		assertLinesEqual(
				"sname -- CHAR(30)"+ENDL + 
				"slevel -- CHAR(10) -- slevel = 'JR' OR slevel = 'SR' OR slevel = 'SO' OR slevel = 'FR'"+ENDL,
				myOut.toString());
		myOut.reset();

		SQLParser.parse("HELP DESCRIBE DEPARTMENT;");
		assertLinesEqual(
				"dname -- CHAR(30)"+ENDL+
				"location -- CHAR(30)"+ENDL,
				myOut.toString()
				);
        myOut.reset();

		SQLParser.parse("HELP DESCRIBE FACULTY;");
		assertLinesEqual(
				"fid -- INT -- PRIMARY KEY -- fid != 0"+ENDL+
				"fname -- CHAR(30)"+ENDL+
				"dept -- INT -- FOREIGN KEY REFERENCES DEPARTMENT (deptid)"+ENDL,
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
				"stu_num	classname	grade"+ENDL+
				"14181	ENG400	A"+ENDL+
				"80161	ENG400	B"+ENDL,
				myOut.toString()));
        myOut.reset();

		SQLParser.parse("SELECT * FROM STUDENT;");
		assertTrue(linesEqualIgnoreOrder(
				"sname	slevel"+ENDL+
				"Jack	SO"+ENDL+
				"Smith	FR"+ENDL+
				"Banks	SR"+ENDL+
				"M.Lee	SO	"+ENDL+
				"Bale	JR"+ENDL+
				"Lim	SR"+ENDL+
				"Sharon	FR"+ENDL+
				"Johnson	JR"+ENDL+
				"E.Cho	JR"+ENDL+
				"Angin	SR	"+ENDL,
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
