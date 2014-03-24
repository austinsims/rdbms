package grading;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

import rdbms.InvalidSQLException;
import rdbms.SQLParser;
import rdbms.Table;

public class Admin1 {
	ByteArrayOutputStream myOut;

	@Before
	public void setUp() throws Exception {
		myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));
		Table.dropEverything();
	}

	@Test
	public void createTables() throws InvalidSQLException {
		// TODO: Log in as admin user
		
		SQLParser.parse("CREATE TABLE DEPARTMENT(deptid INT CHECK(deptid>0 AND deptid<100), dname CHAR(30), location CHAR(10), PRIMARY KEY(deptid));");
		
		SQLParser.parse("CREATE TABLE FACULTY(fid INT CHECK(fid!=0), fname CHAR(30), dept INT, PRIMARY KEY(fid), FOREIGN KEY(dept) REFERENCES DEPARTMENT(deptid));");

		SQLParser.parse("CREATE TABLE STUDENT(snum INT, sname CHAR(30), dep INT, slevel CHAR(10) CHECK(slevel='JR' OR slevel='SR' OR slevel='SO' OR slevel='FR'), age INT CHECK(age>16), PRIMARY KEY(snum), FOREIGN KEY (dep) REFERENCES DEPARTMENT(deptid));");

		SQLParser.parse("CREATE TABLE CLASS(cname CHAR(30), meets_at CHAR(30), room CHAR(10), faculty_id INT, PRIMARY KEY(cname), FOREIGN KEY(faculty_id) REFERENCES FACULTY(fid));");

		SQLParser.parse("CREATE TABLE ENROLLED(student_num INT, class_name CHAR(30), PRIMARY KEY(student_num, class_name), FOREIGN KEY(student_num) REFERENCES STUDENT(snum), FOREIGN KEY(class_name) REFERENCES CLASS(cname));");

		SQLParser.parse("CREATE TABLE ENROLLED(student_num INT, class_name CHAR(30), PRIMARY KEY(student_num, class_name), FOREIGN KEY(student_num) REFERENCES STUDENT(snum), FOREIGN KEY(class_name) REFERENCES CLASS(cname));");

		SQLParser.parse("CREATE TABLE GRADE(student_num INT, class_name CHAR(30), PRIMARY KEY(student_num, class_name), FOREIGN KEY(student_num) REFERENCES STUDENT(num), FOREIGN KEY(class_name) REFERENCES CLASS(cname));");

	}
	
	@Test
	public void insertData() throws InvalidSQLException {
		// TODO: Log in as admin user
		
	    SQLParser.parse("INSERT INTO DEPARTMENT VALUES (101, 'Computer Sciences','West Lafayette');");

	    SQLParser.parse("INSERT INTO FACULTY VALUES (0,'Layton',11);");

	    SQLParser.parse("INSERT INTO STUDENT VALUES (16711,'A.Smith',22,'A',20);");

	    SQLParser.parse("INSERT INTO DEPARTMENT VALUES (11,'Computer Sciences','West Lafayette');");

	    SQLParser.parse("INSERT INTO DEPARTMENT VALUES (22,'Management','West Lafayette');");

	    SQLParser.parse("INSERT INTO DEPARTMENT VALUES (33,'Medical Education','Purdue Calumet');");

	    SQLParser.parse("INSERT INTO DEPARTMENT VALUES (44,'Education','Purdue North Central');");

	    SQLParser.parse("INSERT INTO DEPARTMENT VALUES (1,'Pharmacal Sciences','Indianapolis');");

	    SQLParser.parse("INSERT INTO DEPARTMENT VALUES (66,'Physics', 'West Lafayette');");

	    SQLParser.parse("INSERT INTO STUDENT VALUES (14181,'Jack',11,'SO',17);");

	    SQLParser.parse("INSERT INTO STUDENT VALUES (16711,'A.Smith',22,'FR',20);");

	    SQLParser.parse("INSERT INTO STUDENT VALUES (12341,'Banks',33,'SR',18);");

	    SQLParser.parse("INSERT INTO STUDENT VALUES (37261,'M.Lee',1,'SO',22);");

	    SQLParser.parse("INSERT INTO STUDENT VALUES (48291,'Bale',33,'JR',22);");

	    SQLParser.parse("INSERT INTO STUDENT VALUES (57651,'Lim',11,'SR',19);");

	    SQLParser.parse("INSERT INTO STUDENT VALUES (10191,'Sharon',22,'FR',22);");

	    SQLParser.parse("INSERT INTO STUDENT VALUES (73571,'Johnson',11,'JR',27);");

	    SQLParser.parse("INSERT INTO STUDENT VALUES (80161,'E.Cho',1,'JR',27);");

	    SQLParser.parse("INSERT INTO STUDENT VALUES (80162,'Angin',11,'SR',25);");

	    SQLParser.parse("INSERT INTO FACULTY VALUES (1010,'Layton',11);");

	    SQLParser.parse("INSERT INTO FACULTY VALUES (1020,'Jungles',22);");

	    SQLParser.parse("INSERT INTO FACULTY VALUES (1030,'Guzaldo',1);");

	    SQLParser.parse("INSERT INTO FACULTY VALUES (1040,'Boling',44);");

	    SQLParser.parse("INSERT INTO FACULTY VALUES (1050,'Mason',11);");

	    SQLParser.parse("INSERT INTO FACULTY VALUES (1060,'Zwink',22);");

	    SQLParser.parse("INSERT INTO FACULTY VALUES (1070,'Walton',1);");

	    SQLParser.parse("INSERT INTO FACULTY VALUES (1080,'Teach',1);");

	    SQLParser.parse("INSERT INTO FACULTY VALUES (1090,'Jason',1);");

	    SQLParser.parse("INSERT INTO CLASS VALUES ('ENG400','8:30','U003',1040);");

	    SQLParser.parse("INSERT INTO CLASS VALUES ('ENG320', '10:30','R128',1040);");

	    SQLParser.parse("INSERT INTO CLASS VALUES ('COM10000', '12:30','L108',1040);");

	    SQLParser.parse("INSERT INTO CLASS VALUES ('ME30800', '12:00','R128',1020);");

	    SQLParser.parse("INSERT INTO CLASS VALUES ('CS448', '11:00','R128',1010);");

	    SQLParser.parse("INSERT INTO CLASS VALUES ('HIS21000', '11:00','L108',1040);");

	    SQLParser.parse("INSERT INTO CLASS VALUES ('MATH27500', '15:30','L108',1050);");

	    SQLParser.parse("INSERT INTO CLASS VALUES ('STAT11000', '13:00','R128',1050);");

	    SQLParser.parse("INSERT INTO CLASS VALUES ('PHYS10000', '14:00','U003',1010);");

	    SQLParser.parse("INSERT INTO ENROLLED VALUES (14181,'CS448');");

	    SQLParser.parse("INSERT INTO ENROLLED VALUES (14181,'MATH27500');");

	    SQLParser.parse("INSERT INTO ENROLLED VALUES (12341,'ENG400');");

	    SQLParser.parse("INSERT INTO ENROLLED VALUES (12341,'MATH27500');");

	    SQLParser.parse("INSERT INTO ENROLLED VALUES (80161,'ENG400');");

	    SQLParser.parse("INSERT INTO ENROLLED VALUES (80161,'ENG320');");

	    SQLParser.parse("INSERT INTO ENROLLED VALUES (80161,'HIS21000');");

	    SQLParser.parse("INSERT INTO ENROLLED VALUES (80161,'STAT11000');");

	    SQLParser.parse("INSERT INTO ENROLLED VALUES (14181,'STAT11000');");

	    SQLParser.parse("INSERT INTO ENROLLED VALUES (12341,'COM10000');");

	    SQLParser.parse("INSERT INTO ENROLLED VALUES (16711,'ENG400');");

	    SQLParser.parse("INSERT INTO ENROLLED VALUES (16711,'STAT11000');");

	    SQLParser.parse("INSERT INTO ENROLLED VALUES (12341,'HIS21000');");

	    SQLParser.parse("INSERT INTO ENROLLED VALUES (57651,'PHYS10000');");

	    SQLParser.parse("INSERT INTO ENROLLED VALUES (57651,'ENG320');");

	    SQLParser.parse("INSERT INTO ENROLLED VALUES (57651,'COM10000');");

	}


}
