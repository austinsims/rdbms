package grading;

import static rdbms.Assert.*;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import rdbms.Database;
import rdbms.InvalidSQLException;
import rdbms.PermissionException;
import rdbms.RDBMS;
import rdbms.SQLParser;
import rdbms.User;

public class User1 {
	ByteArrayOutputStream myOut;

	@Before
	public void setUp() throws Exception {
		RDBMS.load();
		User user1 = Database.getUser("user1");
		if (user1 == null) fail("Could not retrieve user object");
		Database.login(user1);

		
		myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));
		System.setErr(new PrintStream(myOut));
	}

	@Test
	public void test() throws InvalidSQLException, PermissionException, PermissionException {

		Set<String> expected = new HashSet<String>();
		expected.addAll(Arrays.asList(new String[] {"DEPARTMENT", "FACULTY", "STUDENT", "CLASS", "ENROLLED"}));
		SQLParser.parse("HELP TABLES;");
		// Make sure every table is printed, but order doesn't matter
		String[] tables = myOut.toString().split("\n");
		Set<String> actual = new HashSet<String>();
		actual.addAll(Arrays.asList(tables));
		assertEquals(expected, actual);
		myOut.reset();

		SQLParser.parse("HELP DESCRIBE STUDENT;");

		// Don't bother with comparing result for help on commands
		SQLParser.parse("HELP CREATE TABLE;");

		SQLParser.parse("HELP DROP TABLE;");

		SQLParser.parse("HELP SELECT;");

		SQLParser.parse("HELP INSERT;");

		SQLParser.parse("HELP DELETE;");

		SQLParser.parse("HELP UPDATE;");

		myOut.reset();
	
		SQLParser.parse("SELECT * FROM ENROLLED;");
		assertLinesEqual(
				"student_num	class_name\n" + 
				"14181	CS448\n" +
				"14181	MATH27500\n" +
				"12341	ENG400\n" +
				"12341	MATH27500\n" +
				"80161	ENG400\n" +
				"80161	ENG320\n" +
				"80161	HIS21000\n" +
				"80161	STAT11000\n" +
				"14181	STAT11000\n" +
				"12341	COM10000\n" +
				"16711	ENG400\n" +
				"16711	STAT11000\n" +
				"12341	HIS21000\n" +
				"57651	PHYS10000\n" +
				"57651	ENG320\n" +
				"57651	COM10000\n",
				myOut.toString());
		myOut.reset();

		SQLParser.parse("DROP TABLE MANAGER;");
		assertLinesEqual("The table named MANAGER does not exist", myOut.toString());
		myOut.reset();
		
		SQLParser.parse("DROP TABLE STUDENT;");
		assertLinesEqual("STUDENT cannot be dropped because ENROLLED contains a foreign key reference to it.", myOut.toString());
		myOut.reset();
		
		SQLParser.parse("SELECT snum,sname FROM STUDENT;");
		assertLinesEqual(
                         "snum	sname\n" +
                         "14181	Jack\n" +
                         "16711	A.Smith\n" +
                         "12341	Banks\n" +
                         "37261	M.Lee\n" +
                         "48291	Bale\n" +
                         "57651	Lim\n" +
                         "10191	Sharon\n" +
                         "73571	Johnson\n" +
                         "80161	E.Cho\n" +
                         "80162	Angin\n",
                         myOut.toString()
				);
		myOut.reset();
				
		SQLParser.parse("SELECT * FROM ENROLLED WHERE student_num=14181;");
		assertLinesEqual(
					"student_num	class_name\n"+
					"14181	CS448\n"+
					"14181	MATH27500\n"+
					"14181	STAT11000\n",
					myOut.toString());
		myOut.reset();
				
		SQLParser.parse("SELECT sname,cname,fname FROM STUDENT,CLASS,FACULTY,ENROLLED WHERE snum=student_num AND fid=faculty_id AND cname=class_name;");
		assertTrue(linesEqualIgnoreOrder(
				"sname	cname	fname\n"+
						"Jack	CS448	Layton\n"+
						"Jack	MATH27500	Mason\n"+
						"Banks	ENG400	Boling\n"+
						"Banks	MATH27500	Mason\n"+
						"E.Cho	ENG400	Boling\n"+
						"E.Cho	ENG320	Boling\n"+
						"E.Cho	HIS21000	Boling\n"+
						"E.Cho	STAT11000	Mason\n"+
						"Jack	STAT11000	Mason\n"+
						"Banks	COM10000	Boling\n"+
						"A.Smith	ENG400	Boling\n"+
						"A.Smith	STAT11000	Mason\n"+
						"Banks	HIS21000	Boling\n"+
						"Lim	PHYS10000	Layton\n"+
						"Lim	ENG320	Boling\n"+
						"Lim	COM10000	Boling\n",
						myOut.toString()));
		myOut.reset();
		
		
		SQLParser.parse("SELECT sname,cname FROM STUDENT,CLASS;");
		assertTrue(linesEqualIgnoreOrder(
				"sname	cname\n"+
						"Jack	ENG400\n"+
						"A.Smith	ENG400\n"+
						"Banks	ENG400\n"+
						"M.Lee	ENG400\n"+
						"Bale	ENG400\n"+
						"Lim	ENG400\n"+
						"Sharon	ENG400\n"+
						"Johnson	ENG400\n"+
						"E.Cho	ENG400\n"+
						"Angin	ENG400\n"+
						"Jack	ENG320\n"+
						"A.Smith	ENG320\n"+
						"Banks	ENG320\n"+
						"M.Lee	ENG320\n"+
						"Bale	ENG320\n"+
						"Lim	ENG320\n"+
						"Sharon	ENG320\n"+
						"Johnson	ENG320\n"+
						"E.Cho	ENG320\n"+
						"Angin	ENG320\n"+
						"Jack	COM10000\n"+
						"A.Smith	COM10000\n"+
						"Banks	COM10000\n"+
						"M.Lee	COM10000\n"+
						"Bale	COM10000\n"+
						"Lim	COM10000\n"+
						"Sharon	COM10000\n"+
						"Johnson	COM10000\n"+
						"E.Cho	COM10000\n"+
						"Angin	COM10000\n"+
						"Jack	ME30800\n"+
						"A.Smith	ME30800\n"+
						"Banks	ME30800\n"+
						"M.Lee	ME30800\n"+
						"Bale	ME30800\n"+
						"Lim	ME30800\n"+
						"Sharon	ME30800\n"+
						"Johnson	ME30800\n"+
						"E.Cho	ME30800\n"+
						"Angin	ME30800\n"+
						"Jack	CS448\n"+
						"A.Smith	CS448\n"+
						"Banks	CS448\n"+
						"M.Lee	CS448\n"+
						"Bale	CS448\n"+
						"Lim	CS448\n"+
						"Sharon	CS448\n"+
						"Johnson	CS448\n"+
						"E.Cho	CS448\n"+
						"Angin	CS448\n"+
						"Jack	HIS21000\n"+
						"A.Smith	HIS21000\n"+
						"Banks	HIS21000\n"+
						"M.Lee	HIS21000\n"+
						"Bale	HIS21000\n"+
						"Lim	HIS21000\n"+
						"Sharon	HIS21000\n"+
						"Johnson	HIS21000\n"+
						"E.Cho	HIS21000\n"+
						"Angin	HIS21000\n"+
						"Jack	MATH27500\n"+
						"A.Smith	MATH27500\n"+
						"Banks	MATH27500\n"+
						"M.Lee	MATH27500\n"+
						"Bale	MATH27500\n"+
						"Lim	MATH27500\n"+
						"Sharon	MATH27500\n"+
						"Johnson	MATH27500\n"+
						"E.Cho	MATH27500\n"+
						"Angin	MATH27500\n"+
						"Jack	STAT11000\n"+
						"A.Smith	STAT11000\n"+
						"Banks	STAT11000\n"+
						"M.Lee	STAT11000\n"+
						"Bale	STAT11000\n"+
						"Lim	STAT11000\n"+
						"Sharon	STAT11000\n"+
						"Johnson	STAT11000\n"+
						"E.Cho	STAT11000\n"+
						"Angin	STAT11000\n"+
						"Jack	PHYS10000\n"+
						"A.Smith	PHYS10000\n"+
						"Banks	PHYS10000\n"+
						"M.Lee	PHYS10000\n"+
						"Bale	PHYS10000\n"+
						"Lim	PHYS10000\n"+
						"Sharon	PHYS10000\n"+
						"Johnson	PHYS10000\n"+
						"E.Cho	PHYS10000\n"+
						"Angin	PHYS10000\n", 
				myOut.toString()));
		myOut.reset();
				
		SQLParser.parse("SELECT snum FROM DEPARTMENT;");
		assertLinesEqual("The requested schema does not contain the attribute snum", myOut.toString());
		myOut.reset();
				
		SQLParser.parse("SELECT snum,deptid FROM DEPARTMENT;");
		assertLinesEqual("The requested schema does not contain the attribute snum", myOut.toString());
		myOut.reset();
		
		SQLParser.parse("INSERT INTO ENROLLED VALUES(1111,'CS448');");
		assertLinesEqual("Could not find strictly one tuple in STUDENT that has a value 1111 for snum INT", myOut.toString());
		myOut.reset();

		SQLParser.parse("INSERT INTO ENROLLED VALUES(14181,'ENG400');");
		assertLinesEqual("Tuple inserted successfully", myOut.toString());
        myOut.reset();

		SQLParser.parse("INSERT INTO ENROLLED VALUES(14181,'CS448');");
		assertLinesEqual("Error: Duplicate entry!", myOut.toString());
        myOut.reset();

		SQLParser.parse("SELECT * FROM ENROLLED WHERE student_num=14181 OR class_name='CS448' OR student_num=1111;");
		assertTrue(linesEqualIgnoreOrder(
				"student_num	class_name\n"+
						"14181	CS448\n"+
						"14181	MATH27500\n"+
						"14181	STAT11000\n"+
						"14181	ENG400\n",
				myOut.toString()));
        myOut.reset();

		SQLParser.parse("SELECT fname FROM FACULTY WHERE fid=1111;");
		assertEquals("\n", myOut.toString());
        myOut.reset();

		SQLParser.parse("UPDATE DEPARTMENT SET location='WLafayette' WHERE deptid=11 OR deptid=22;");
		assertLinesEqual("2 rows affected", myOut.toString());
        myOut.reset();

		SQLParser.parse("UPDATE STUDENT SET age=21,sname='Smith' WHERE sname='A.Smith';");
		assertLinesEqual("1 row affected", myOut.toString());
        myOut.reset();

		SQLParser.parse("SELECT * FROM CLASS;");
		assertTrue(linesEqualIgnoreOrder(
				"cname	meets_at	room	faculty_id\n"+
						"ENG400	8:30	U003	1040\n"+
						"ENG320	10:30	R128	1040\n"+
						"COM10000	12:30	L108	1040\n"+
						"ME30800	12:00	R128	1020\n"+
						"CS448	11:00	R128	1010\n"+
						"HIS21000	11:00	L108	1040\n"+
						"MATH27500	15:30	L108	1050\n"+
						"STAT11000	13:00	R128	1050\n"+
						"PHYS10000	14:00	U003	1010\n",
				myOut.toString()
				));
        myOut.reset();

		SQLParser.parse("UPDATE CLASS SET meets_at='12:00';");
		assertLinesEqual("9 rows affected", myOut.toString());
        myOut.reset();

		SQLParser.parse("SELECT * FROM CLASS;");
		assertTrue(linesEqualIgnoreOrder(
				"cname	meets_at	room	faculty_id\n"+
						"ENG400	12:00	U003	1040\n"+
						"ENG320	12:00	R128	1040\n"+
						"COM10000	12:00	L108	1040\n"+
						"ME30800	12:00	R128	1020\n"+
						"CS448	12:00	R128	1010\n"+
						"HIS21000	12:00	L108	1040\n"+
						"MATH27500	12:00	L108	1050\n"+
						"STAT11000	12:00	R128	1050\n"+
						"PHYS10000	12:00	U003	1010\n", 
				myOut.toString()
				));
        myOut.reset();

		SQLParser.parse("CREATE TABLE GRADE(stu_num INT, classname CHAR(30), grade CHAR(2), PRIMARY KEY(stu_num, classname), FOREIGN KEY (stu_num) REFERENCES STUDENT(snum), FOREIGN KEY (classname) REFERENCES CLASS(cname));");
		assertLinesEqual("Table created successfully", myOut.toString());
        myOut.reset();

		SQLParser.parse("INSERT INTO GRADE VALUES('ENG400', 14181, 'A');");
		assertLinesEqual("The value 'ENG400' cannot be interpreted as an integer", myOut.toString());
        myOut.reset();

		SQLParser.parse("INSERT INTO GRADE VALUES(14181, 'ENG400', 'A');");
		assertLinesEqual("Tuple inserted successfully", myOut.toString());
        myOut.reset();

		SQLParser.parse("INSERT INTO GRADE VALUES(80161,'ENG400', 'B');");
		assertLinesEqual("Tuple inserted successfully", myOut.toString());
        myOut.reset();

		SQLParser.parse("CREATE USER user10 User-A;");
		assertLinesEqual("Sorry, but you must be an admin user to do that.", myOut.toString());
        myOut.reset();

	}

}
