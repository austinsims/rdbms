package grading;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static rdbms.Assert.*;

import rdbms.Database;
import rdbms.RDBMS;
import rdbms.RDBMSException;
import rdbms.SQLParser;
import rdbms.User;

public class Admin2 {
	ByteArrayOutputStream myOut;

	@Before
	public void setUp() throws Exception {
		RDBMS.load();
		User admin = Database.getUser("admin");
		if (admin == null) fail("Couldn't get user object");
		Database.login(admin);

		myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));
        System.setErr(new PrintStream(myOut));
	}

	@Test
	public void test() throws RDBMSException {
		SQLParser.parse("SELECT * FROM GRADE;");
		assertTrue(linesEqualIgnoreOrder(
				"stu_num	classname	grade\n"+
				"14181	ENG400	A\n"+
				"80161	ENG400	B\n",
				myOut.toString()
				));
        myOut.reset();

		SQLParser.parse("DELETE FROM GRADE;");
        assertTrue(linesEqualIgnoreOrder(
				"2 rows affected",
				myOut.toString()
				));
        myOut.reset();

		SQLParser.parse("SELECT * FROM GRADE;");
        assertTrue(linesEqualIgnoreOrder(
				"\n",
				myOut.toString()
				));
        myOut.reset();
        
        // TODO: Create subschema on GRADE then drop it, just to test drop
        // TODO: Why is DROP TABLE GRADE not actually dropping the table?  GRADE.table gets deleted, but HELP TABLES below still displays GRADE...

		SQLParser.parse("DROP TABLE GRADE;");
        assertTrue(linesEqualIgnoreOrder(
        		"Table dropped successfully\n",
				myOut.toString()
				));
        myOut.reset();

		SQLParser.parse("HELP TABLES;");
        assertTrue(linesEqualIgnoreOrder(
        		"DEPARTMENT\n"+
        		"FACULTY\n"+
        		"STUDENT\n"+
        		"CLASS\n"+
        		"ENROLLED\n",
				myOut.toString()
				));
        myOut.reset();

		SQLParser.parse("HELP DESCRIBE GRADE;");
        assertTrue(linesEqualIgnoreOrder(
				"Error: Table does not exist!\n",
				myOut.toString()
				));
        myOut.reset();

		SQLParser.parse("CREATE SUBSCHEMA STUDENT sname,slevel,age;");
        assertTrue(linesEqualIgnoreOrder(
        		"Subschema created successfully\n",
				myOut.toString()
				));
        myOut.reset();

		SQLParser.parse("CREATE USER user5 User-B;");
        assertTrue(linesEqualIgnoreOrder(
				"User created successfully\n",
				myOut.toString()
				));
        myOut.reset();

		SQLParser.parse("DELETE FROM ENROLLED WHERE student_num=16711 OR student_num=11;");
        assertTrue(linesEqualIgnoreOrder(
				"2 rows affected\n",
				myOut.toString()
				));
        myOut.reset();

        SQLParser.parse("SELECT deptid FROM DEPARTMENT WHERE location='WLafayette';");
        assertTrue(linesEqualIgnoreOrder(
        		"deptid\n"+
        		"11\n"+
        		"22\n",
				myOut.toString()
				));
        myOut.reset();

        SQLParser.parse("SELECT sname FROM STUDENT WHERE age>=21;");
        assertTrue(linesEqualIgnoreOrder(
        		"sname\n"+
        		"Smith\n"+
        		"M.Lee\n"+
        		"Bale\n"+
        		"Sharon\n"+
        		"Johnson\n"+
        		"E.Cho\n"+
        		"Angin\n",
				myOut.toString()
				));
        myOut.reset();
        
        RDBMS.save();
	}

}
