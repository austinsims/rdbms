package grading;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

import rdbms.InvalidSQLException;
import rdbms.SQLParser;

public class User1 {
	ByteArrayOutputStream myOut;

	@Before
	public void setUp() throws Exception {
		// TODO: Log in as user1
		
		myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));
	}

	@Test
	public void help() throws InvalidSQLException {

		SQLParser.parse("HELP TABLES;");

		SQLParser.parse("HELP DESCRIBE STUDENT;");

		SQLParser.parse("HELP CREATE TABLE;");

		SQLParser.parse("HELP DROP TABLE;");

		SQLParser.parse("HELP SELECT;");

		SQLParser.parse("HELP INSERT:");

		SQLParser.parse("HELP DELETE;");

		SQLParser.parse("HELP UPDATE;");

	}

	@Test
	public void selectFromEnrolled() throws InvalidSQLException {
		SQLParser.parse("SELECT * FROM ENROLLED;");
	}

	@Test
	public void tryToDropTables() throws InvalidSQLException {
		SQLParser.parse("DROP TABLE MANAGER;");
		SQLParser.parse("DROP TABLE STUDENT;");
	}

	@Test
	public void selectFromStudent() throws InvalidSQLException {
		SQLParser.parse("SELECT snum,sname FROM STUDENT;");
		SQLParser.parse("SELECT * from ENROLLED WHERE student_num=14181;");
		SQLParser
				.parse("SELECT sname,cname,fname FROM STUDENT,CLASS,FACULTY,ENROLLED WHERE snum=student_num AND fid=faculty_id AND cname=class_name;");
		SQLParser.parse("SELECT sname,cname FROM STUDENT,CLASS;");
		SQLParser.parse("SELECT snum FROM DEPARTMENT;");
		SQLParser.parse("SELECT snum,deptid FROM DEPARTMENT;");
	}

	@Test
	public void insertIntoEnrolled() throws InvalidSQLException {
		SQLParser.parse("INSERT INTO ENROLLED VALUES(1111,'CS448');");
		SQLParser.parse("INSERT INTO ENROLLED VALUES(14181,'ENG400');");
		SQLParser.parse("INSERT INTO ENROLLED VALUES(14181,'CS448');");
	}

	@Test
	public void selectAllFromEnrolledWithConditions() throws InvalidSQLException {
		SQLParser.parse("SELECT * FROM ENROLLED WHERE student_num=14181 OR class_name='CS448' OR student_num=1111;");
	}

	@Test
	public void selectFromFacultyWithCondition() throws InvalidSQLException {
		SQLParser.parse("SELECT fname FROM FACULTY WHERE fid=1111;");
	}

	@Test
	public void updateDepartment() throws InvalidSQLException {
		SQLParser.parse("UPDATE DEPARTMENT SET location='WLafayette' WHERE deptid=11 OR deptid=22;");
	}

	@Test
	public void updateStudent() throws InvalidSQLException {
		SQLParser.parse("UPDATE STUDENT SET age=21,sname='Smith' WHERE sname='A.Smith';");
	}

	@Test
	public void selectAllFromClass() throws InvalidSQLException {
		SQLParser.parse("SELECT * FROM CLASS;");
	}

	@Test
	public void updateClass() throws InvalidSQLException {
		SQLParser.parse("UPDATE CLASS SET meets_at='12:00';");
	}

	@Test
	public void selectAllFromClassAgain() throws InvalidSQLException {
		SQLParser.parse("SELECT * FROM CLASS;");
	}

	@Test
	public void createTable() throws InvalidSQLException {
		SQLParser
				.parse("CREATE TABLE GRADE(stu_num INT, classname CHAR(30), grade CHAR(2), PRIMARY KEY(stu_num, classname), FOREIGN KEY (stu_num) REFERENCES STUDENT(snum), FOREIGN KEY (classname) REFERENCES CLASS(cname));");
	}

	@Test
	public void insertIntoTable() throws InvalidSQLException {
		SQLParser.parse("INSERT INTO GRADE VALUES('ENG400', 14181, 'A');");
		SQLParser.parse("INSERT INTO GRADE VALUES(14181, 'ENG400', 'A');");
		SQLParser.parse("INSERT INTO GRADE VALUES(80161,'ENG400', 'B');");
	}

	@Test
	public void createUser() throws InvalidSQLException {
		SQLParser.parse("CREATE USER user10 User-A;");
	}

	@Test
	public void quit() throws InvalidSQLException {
		SQLParser.parse("QUIT;");
	}

}
