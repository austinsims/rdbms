package grading;

import static rdbms.Assert.assertLinesEqual;

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

public class User1 {
	ByteArrayOutputStream myOut;

	@Before
	public void setUp() throws Exception {
		// TODO: Log in as user1
		User user1 = Database.getUser("user1");
		Database.login(user1);
		RDBMS.load();
		
		myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));
	}

	@Test
	public void test() throws InvalidSQLException, PermissionException, PermissionException {

		SQLParser.parse("HELP TABLES;");
		assertLinesEqual("DEPARTMENT\nFACULTY\nSTUDENT\nCLASS\nENROLLED", myOut.toString());
		myOut.reset();
		
		if (true) return;
				
		SQLParser.parse("HELP DESCRIBE STUDENT;");

		// Don't bother with comparing result for help on commands
		SQLParser.parse("HELP CREATE TABLE;");

		SQLParser.parse("HELP DROP TABLE;");

		SQLParser.parse("HELP SELECT;");

		SQLParser.parse("HELP INSERT;");

		SQLParser.parse("HELP DELETE;");

		SQLParser.parse("HELP UPDATE;");

	
		SQLParser.parse("SELECT * FROM ENROLLED;");

		SQLParser.parse("DROP TABLE MANAGER;");
		SQLParser.parse("DROP TABLE STUDENT;");

		SQLParser.parse("SELECT snum,sname FROM STUDENT;");
		SQLParser.parse("SELECT * from ENROLLED WHERE student_num=14181;");
		SQLParser
				.parse("SELECT sname,cname,fname FROM STUDENT,CLASS,FACULTY,ENROLLED WHERE snum=student_num AND fid=faculty_id AND cname=class_name;");
		SQLParser.parse("SELECT sname,cname FROM STUDENT,CLASS;");
		SQLParser.parse("SELECT snum FROM DEPARTMENT;");
		SQLParser.parse("SELECT snum,deptid FROM DEPARTMENT;");

		SQLParser.parse("INSERT INTO ENROLLED VALUES(1111,'CS448');");
		SQLParser.parse("INSERT INTO ENROLLED VALUES(14181,'ENG400');");
		SQLParser.parse("INSERT INTO ENROLLED VALUES(14181,'CS448');");

		SQLParser.parse("SELECT * FROM ENROLLED WHERE student_num=14181 OR class_name='CS448' OR student_num=1111;");

		SQLParser.parse("SELECT fname FROM FACULTY WHERE fid=1111;");

		SQLParser.parse("UPDATE DEPARTMENT SET location='WLafayette' WHERE deptid=11 OR deptid=22;");

		SQLParser.parse("UPDATE STUDENT SET age=21,sname='Smith' WHERE sname='A.Smith';");

		SQLParser.parse("SELECT * FROM CLASS;");

		SQLParser.parse("UPDATE CLASS SET meets_at='12:00';");

		SQLParser.parse("SELECT * FROM CLASS;");

		SQLParser
				.parse("CREATE TABLE GRADE(stu_num INT, classname CHAR(30), grade CHAR(2), PRIMARY KEY(stu_num, classname), FOREIGN KEY (stu_num) REFERENCES STUDENT(snum), FOREIGN KEY (classname) REFERENCES CLASS(cname));");

		SQLParser.parse("INSERT INTO GRADE VALUES('ENG400', 14181, 'A');");
		SQLParser.parse("INSERT INTO GRADE VALUES(14181, 'ENG400', 'A');");
		SQLParser.parse("INSERT INTO GRADE VALUES(80161,'ENG400', 'B');");

		SQLParser.parse("CREATE USER user10 User-A;");

		SQLParser.parse("QUIT;");
	}

}
