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

		SQLParser.parse("HELP TABLES;");
		assertTrue(linesEqualIgnoreOrder(
				"DEPARTMENT"+ENDL+
				"FACULTY"+ENDL+
				"STUDENT"+ENDL+
				"CLASS"+ENDL+
				"ENROLLED"+ENDL,
				myOut.toString()));
		myOut.reset();

		SQLParser.parse("HELP DESCRIBE STUDENT;");
		assertLinesEqual(
				"snum -- INT -- PRIMARY KEY"+ENDL+
				"sname -- CHAR(30) "+ENDL+
				"dep -- INT -- FOREIGN KEY REFERENCES DEPARTMENT (deptid)"+ENDL+
				"slevel -- CHAR(10) -- slevel = 'JR' OR slevel = 'SR' OR slevel = 'SO' OR slevel = 'FR'"+ENDL+
				"age -- INT -- age > 16"+ENDL,
				myOut.toString());
		myOut.reset();

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
				"student_num	class_name"+ENDL + 
				"14181	CS448"+ENDL +
				"14181	MATH27500"+ENDL +
				"12341	ENG400"+ENDL +
				"12341	MATH27500"+ENDL +
				"80161	ENG400"+ENDL +
				"80161	ENG320"+ENDL +
				"80161	HIS21000"+ENDL +
				"80161	STAT11000"+ENDL +
				"14181	STAT11000"+ENDL +
				"12341	COM10000"+ENDL +
				"16711	ENG400"+ENDL +
				"16711	STAT11000"+ENDL +
				"12341	HIS21000"+ENDL +
				"57651	PHYS10000"+ENDL +
				"57651	ENG320"+ENDL +
				"57651	COM10000"+ENDL,
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
                         "snum	sname"+ENDL +
                         "14181	Jack"+ENDL +
                         "16711	A.Smith"+ENDL +
                         "12341	Banks"+ENDL +
                         "37261	M.Lee"+ENDL +
                         "48291	Bale"+ENDL +
                         "57651	Lim"+ENDL +
                         "10191	Sharon"+ENDL +
                         "73571	Johnson"+ENDL +
                         "80161	E.Cho"+ENDL +
                         "80162	Angin"+ENDL,
                         myOut.toString()
				);
		myOut.reset();
				
		SQLParser.parse("SELECT * FROM ENROLLED WHERE student_num=14181;");
		assertLinesEqual(
					"student_num	class_name"+ENDL+
					"14181	CS448"+ENDL+
					"14181	MATH27500"+ENDL+
					"14181	STAT11000"+ENDL,
					myOut.toString());
		myOut.reset();
				
		SQLParser.parse("SELECT sname,cname,fname FROM STUDENT,CLASS,FACULTY,ENROLLED WHERE snum=student_num AND fid=faculty_id AND cname=class_name;");
		assertTrue(linesEqualIgnoreOrder(
				"sname	cname	fname"+ENDL+
						"Jack	CS448	Layton"+ENDL+
						"Jack	MATH27500	Mason"+ENDL+
						"Banks	ENG400	Boling"+ENDL+
						"Banks	MATH27500	Mason"+ENDL+
						"E.Cho	ENG400	Boling"+ENDL+
						"E.Cho	ENG320	Boling"+ENDL+
						"E.Cho	HIS21000	Boling"+ENDL+
						"E.Cho	STAT11000	Mason"+ENDL+
						"Jack	STAT11000	Mason"+ENDL+
						"Banks	COM10000	Boling"+ENDL+
						"A.Smith	ENG400	Boling"+ENDL+
						"A.Smith	STAT11000	Mason"+ENDL+
						"Banks	HIS21000	Boling"+ENDL+
						"Lim	PHYS10000	Layton"+ENDL+
						"Lim	ENG320	Boling"+ENDL+
						"Lim	COM10000	Boling"+ENDL,
						myOut.toString()));
		myOut.reset();
		
		
		SQLParser.parse("SELECT sname,cname FROM STUDENT,CLASS;");
		assertTrue(linesEqualIgnoreOrder(
				"sname	cname"+ENDL+
						"Jack	ENG400"+ENDL+
						"A.Smith	ENG400"+ENDL+
						"Banks	ENG400"+ENDL+
						"M.Lee	ENG400"+ENDL+
						"Bale	ENG400"+ENDL+
						"Lim	ENG400"+ENDL+
						"Sharon	ENG400"+ENDL+
						"Johnson	ENG400"+ENDL+
						"E.Cho	ENG400"+ENDL+
						"Angin	ENG400"+ENDL+
						"Jack	ENG320"+ENDL+
						"A.Smith	ENG320"+ENDL+
						"Banks	ENG320"+ENDL+
						"M.Lee	ENG320"+ENDL+
						"Bale	ENG320"+ENDL+
						"Lim	ENG320"+ENDL+
						"Sharon	ENG320"+ENDL+
						"Johnson	ENG320"+ENDL+
						"E.Cho	ENG320"+ENDL+
						"Angin	ENG320"+ENDL+
						"Jack	COM10000"+ENDL+
						"A.Smith	COM10000"+ENDL+
						"Banks	COM10000"+ENDL+
						"M.Lee	COM10000"+ENDL+
						"Bale	COM10000"+ENDL+
						"Lim	COM10000"+ENDL+
						"Sharon	COM10000"+ENDL+
						"Johnson	COM10000"+ENDL+
						"E.Cho	COM10000"+ENDL+
						"Angin	COM10000"+ENDL+
						"Jack	ME30800"+ENDL+
						"A.Smith	ME30800"+ENDL+
						"Banks	ME30800"+ENDL+
						"M.Lee	ME30800"+ENDL+
						"Bale	ME30800"+ENDL+
						"Lim	ME30800"+ENDL+
						"Sharon	ME30800"+ENDL+
						"Johnson	ME30800"+ENDL+
						"E.Cho	ME30800"+ENDL+
						"Angin	ME30800"+ENDL+
						"Jack	CS448"+ENDL+
						"A.Smith	CS448"+ENDL+
						"Banks	CS448"+ENDL+
						"M.Lee	CS448"+ENDL+
						"Bale	CS448"+ENDL+
						"Lim	CS448"+ENDL+
						"Sharon	CS448"+ENDL+
						"Johnson	CS448"+ENDL+
						"E.Cho	CS448"+ENDL+
						"Angin	CS448"+ENDL+
						"Jack	HIS21000"+ENDL+
						"A.Smith	HIS21000"+ENDL+
						"Banks	HIS21000"+ENDL+
						"M.Lee	HIS21000"+ENDL+
						"Bale	HIS21000"+ENDL+
						"Lim	HIS21000"+ENDL+
						"Sharon	HIS21000"+ENDL+
						"Johnson	HIS21000"+ENDL+
						"E.Cho	HIS21000"+ENDL+
						"Angin	HIS21000"+ENDL+
						"Jack	MATH27500"+ENDL+
						"A.Smith	MATH27500"+ENDL+
						"Banks	MATH27500"+ENDL+
						"M.Lee	MATH27500"+ENDL+
						"Bale	MATH27500"+ENDL+
						"Lim	MATH27500"+ENDL+
						"Sharon	MATH27500"+ENDL+
						"Johnson	MATH27500"+ENDL+
						"E.Cho	MATH27500"+ENDL+
						"Angin	MATH27500"+ENDL+
						"Jack	STAT11000"+ENDL+
						"A.Smith	STAT11000"+ENDL+
						"Banks	STAT11000"+ENDL+
						"M.Lee	STAT11000"+ENDL+
						"Bale	STAT11000"+ENDL+
						"Lim	STAT11000"+ENDL+
						"Sharon	STAT11000"+ENDL+
						"Johnson	STAT11000"+ENDL+
						"E.Cho	STAT11000"+ENDL+
						"Angin	STAT11000"+ENDL+
						"Jack	PHYS10000"+ENDL+
						"A.Smith	PHYS10000"+ENDL+
						"Banks	PHYS10000"+ENDL+
						"M.Lee	PHYS10000"+ENDL+
						"Bale	PHYS10000"+ENDL+
						"Lim	PHYS10000"+ENDL+
						"Sharon	PHYS10000"+ENDL+
						"Johnson	PHYS10000"+ENDL+
						"E.Cho	PHYS10000"+ENDL+
						"Angin	PHYS10000"+ENDL, 
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
				"student_num	class_name"+ENDL+
						"14181	CS448"+ENDL+
						"14181	MATH27500"+ENDL+
						"14181	STAT11000"+ENDL+
						"14181	ENG400"+ENDL,
				myOut.toString()));
        myOut.reset();

		SQLParser.parse("SELECT fname FROM FACULTY WHERE fid=1111;");
		assertEquals(""+ENDL, myOut.toString());
        myOut.reset();

		SQLParser.parse("UPDATE DEPARTMENT SET location='WLafayette' WHERE deptid=11 OR deptid=22;");
		assertLinesEqual("2 rows affected", myOut.toString());
        myOut.reset();

		SQLParser.parse("UPDATE STUDENT SET age=21,sname='Smith' WHERE sname='A.Smith';");
		assertLinesEqual("1 row affected", myOut.toString());
        myOut.reset();

		SQLParser.parse("SELECT * FROM CLASS;");
		assertTrue(linesEqualIgnoreOrder(
				"cname	meets_at	room	faculty_id"+ENDL+
						"ENG400	8:30	U003	1040"+ENDL+
						"ENG320	10:30	R128	1040"+ENDL+
						"COM10000	12:30	L108	1040"+ENDL+
						"ME30800	12:00	R128	1020"+ENDL+
						"CS448	11:00	R128	1010"+ENDL+
						"HIS21000	11:00	L108	1040"+ENDL+
						"MATH27500	15:30	L108	1050"+ENDL+
						"STAT11000	13:00	R128	1050"+ENDL+
						"PHYS10000	14:00	U003	1010"+ENDL,
				myOut.toString()
				));
        myOut.reset();

		SQLParser.parse("UPDATE CLASS SET meets_at='12:00';");
		assertLinesEqual("9 rows affected", myOut.toString());
        myOut.reset();

		SQLParser.parse("SELECT * FROM CLASS;");
		assertTrue(linesEqualIgnoreOrder(
				"cname	meets_at	room	faculty_id"+ENDL+
						"ENG400	12:00	U003	1040"+ENDL+
						"ENG320	12:00	R128	1040"+ENDL+
						"COM10000	12:00	L108	1040"+ENDL+
						"ME30800	12:00	R128	1020"+ENDL+
						"CS448	12:00	R128	1010"+ENDL+
						"HIS21000	12:00	L108	1040"+ENDL+
						"MATH27500	12:00	L108	1050"+ENDL+
						"STAT11000	12:00	R128	1050"+ENDL+
						"PHYS10000	12:00	U003	1010"+ENDL, 
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

        RDBMS.save();
        
	}

}
