package grading;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static rdbms.Assert.*;

import rdbms.Database;
import rdbms.InvalidSQLException;
import rdbms.PermissionException;
import rdbms.RDBMS;
import rdbms.SQLParser;
import rdbms.User;

public class User5 {
	ByteArrayOutputStream myOut;

	@Before
	public void setUp() throws Exception {
		RDBMS.load();
		User user5 = Database.getUser("user5");
		if (user5 == null) fail("Could not get user object");
		Database.login(user5);

		myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));
		System.setErr(new PrintStream(myOut));
	}

	@Test
	public void test() throws InvalidSQLException, PermissionException {
		// 1
		SQLParser.parse("HELP DESCRIBE STUDENT;");
		assertTrue(linesEqualIgnoreOrder(
				"sname -- CHAR(30)"+ENDL+
				"slevel -- CHAR(10) -- slevel = 'JR' OR slevel = 'SR' OR slevel = 'SO' OR slevel = 'FR'"+ENDL+
				"age -- INT -- age > 16"+ENDL,
				myOut.toString()
				));
		myOut.reset();

		// 2
		SQLParser.parse("SELECT * FROM GRADE;");
		assertTrue(linesEqualIgnoreOrder(
				"The table named GRADE does not exist",
				myOut.toString()
				));
		myOut.reset();

		// 3
		SQLParser.parse("SELECT * FROM STUDENT;");
		assertTrue(linesEqualIgnoreOrder(
				"sname	slevel	age"+ENDL+
				"Jack	SO	17"+ENDL+
				"Smith	FR	21"+ENDL+
				"Banks	SR	18"+ENDL+
				"M.Lee	SO	22"+ENDL+
				"Bale	JR	22"+ENDL+
				"Lim	SR	19"+ENDL+
				"Sharon	FR	22"+ENDL+
				"Johnson	JR	27"+ENDL+
				"E.Cho	JR	27"+ENDL+
				"Angin	SR	25"+ENDL,
				myOut.toString()
				));
		myOut.reset();

		// 4
		SQLParser.parse("SELECT * FROM FACULTY WHERE fid!=1010 AND fid!=1020;");
		assertTrue(linesEqualIgnoreOrder(
				"fid	fname	dept"+ENDL+
						"1030	Guzaldo	1"+ENDL+
						"1040	Boling	44"+ENDL+
						"1050	Mason	11"+ENDL+
						"1060	Zwink	22"+ENDL+
						"1070	Walton	1"+ENDL+
						"1080	Teach	1"+ENDL+
						"1090	Jason	1"+ENDL,

				myOut.toString()
				));
		myOut.reset();

		// 5
		SQLParser.parse("SELECT * FROM FACULTY WHERE fid>=1050 OR fid<1020;");
		assertTrue(linesEqualIgnoreOrder(
				"fid	fname	dept"+ENDL+
						"1050	Mason	11"+ENDL+
						"1060	Zwink	22"+ENDL+
						"1070	Walton	1"+ENDL+
						"1080	Teach	1"+ENDL+
						"1090	Jason	1"+ENDL+
						"1010	Layton	11"+ENDL,

				myOut.toString()
				));
		myOut.reset();

		// 6
		SQLParser.parse("SELECT * FROM FACULTY WHERE fid<=1030 OR fid>1050;");
		assertTrue(linesEqualIgnoreOrder(
				"fid	fname	dept"+ENDL+
						"1010	Layton	11"+ENDL+
						"1020	Jungles	22"+ENDL+
						"1030	Guzaldo	1"+ENDL+
						"1060	Zwink	22"+ENDL+
						"1070	Walton	1"+ENDL+
						"1080	Teach	1"+ENDL+
						"1090	Jason	1"+ENDL,

				myOut.toString()
				));
		myOut.reset();
		
		// 7
		SQLParser.parse("SELECT * FROM ENROLLED;");
		assertTrue(linesEqualIgnoreOrder(
				"student_num	class_name"+ENDL+
						"14181	CS448"+ENDL+
						"14181	MATH27500"+ENDL+
						"12341	ENG400"+ENDL+
						"12341	MATH27500"+ENDL+
						"80161	ENG400"+ENDL+
						"80161	ENG320"+ENDL+
						"80161	HIS21000"+ENDL+
						"80161	STAT11000"+ENDL+
						"14181	STAT11000"+ENDL+
						"12341	COM10000"+ENDL+
						"12341	HIS21000"+ENDL+
						"57651	PHYS10000"+ENDL+
						"57651	ENG320"+ENDL+
						"57651	COM10000"+ENDL+
						"14181	ENG400"+ENDL,

				myOut.toString()
				));
		myOut.reset();

	}
	
}
