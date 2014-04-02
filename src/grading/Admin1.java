package grading;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import rdbms.Database;
import rdbms.InvalidSQLException;
import rdbms.PermissionException;
import rdbms.RDBMS;
import rdbms.SQLParser;
import static org.junit.Assert.*;
import static rdbms.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Admin1 {
	
	ByteArrayOutputStream myOut;

	@Before
	public void setUp() throws Exception {
		myOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(myOut));
		System.setErr(new PrintStream(myOut));

	}

	@Test
	public void test() throws InvalidSQLException, PermissionException {
		
		Database.clear();
		
		// 1
		SQLParser.parse("CREATE TABLE DEPARTMENT(deptid INT CHECK(deptid>0 AND deptid<100), dname CHAR(30), location CHAR(30), PRIMARY KEY(deptid));");
		assertLinesEqual("Table created successfully", myOut.toString());
		myOut.reset();
		
		// 2
		SQLParser.parse("CREATE TABLE FACULTY(fid INT CHECK(fid!=0), fname CHAR(30), dept INT, PRIMARY KEY(fid), FOREIGN KEY(dept) REFERENCES DEPARTMENT(deptid));");
		assertLinesEqual("Table created successfully", myOut.toString());
		myOut.reset();
		
		// 3
		SQLParser.parse("CREATE TABLE STUDENT(snum INT, sname CHAR(30), dep INT, slevel CHAR(10) CHECK(slevel='JR' OR slevel='SR' OR slevel='SO' OR slevel='FR'), age INT CHECK(age>16), PRIMARY KEY(snum), FOREIGN KEY (dep) REFERENCES DEPARTMENT(deptid));");
		assertLinesEqual("Table created successfully", myOut.toString());
		myOut.reset();
		
		// 4
		SQLParser.parse("CREATE TABLE CLASS(cname CHAR(30), meets_at CHAR(30), room CHAR(10), faculty_id INT, PRIMARY KEY(cname), FOREIGN KEY(faculty_id) REFERENCES FACULTY(fid));");
		assertLinesEqual("Table created successfully", myOut.toString());
		myOut.reset();

		// 5
		SQLParser.parse("CREATE TABLE ENROLLED(student_num INT, class_name CHAR(30), PRIMARY KEY(student_num, class_name), FOREIGN KEY(student_num) REFERENCES STUDENT(snum), FOREIGN KEY(class_name) REFERENCES CLASS(cname));");
		assertLinesEqual("Table created successfully", myOut.toString());
		myOut.reset();

		// 6
		SQLParser.parse("CREATE TABLE ENROLLED(student_num INT, class_name CHAR(30), PRIMARY KEY(student_num, class_name), FOREIGN KEY(student_num) REFERENCES STUDENT(snum), FOREIGN KEY(class_name) REFERENCES CLASS(cname));");
		assertLinesEqual("Error: Duplicate table!", myOut.toString());
		myOut.reset();
		
		// 7
		SQLParser.parse("CREATE TABLE GRADE(student_num INT, class_name CHAR(30), PRIMARY KEY(student_num, class_name), FOREIGN KEY(student_num) REFERENCES STUDENT(num), FOREIGN KEY(class_name) REFERENCES CLASS(cname));");
		assertLinesEqual("The requested schema does not contain the attribute num", myOut.toString());
		myOut.reset();
		
		// 8
	    SQLParser.parse("INSERT INTO DEPARTMENT VALUES (101, 'Computer Sciences','West Lafayette');");
	    assertLinesEqual("Error: Domain constraint violation!", myOut.toString());
	    myOut.reset();

	    // 9
	    SQLParser.parse("INSERT INTO FACULTY VALUES (0,'Layton',11);");
	    assertLinesEqual("Error: Domain constraint violation!", myOut.toString());
	    myOut.reset();
	    
	    // 10
	    SQLParser.parse("INSERT INTO STUDENT VALUES (16711,'A.Smith',22,'A',20);");
	    assertLinesEqual("Error: Domain constraint violation!", myOut.toString());
	    myOut.reset();
	    
	    // 12
	    SQLParser.parse("INSERT INTO DEPARTMENT VALUES (11,'Computer Sciences','West Lafayette');");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 13
	    SQLParser.parse("INSERT INTO DEPARTMENT VALUES (22,'Management','West Lafayette');");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();
	    
	    // 14 
	    SQLParser.parse("INSERT INTO DEPARTMENT VALUES (33,'Medical Education','Purdue Calumet');");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 15
	    SQLParser.parse("INSERT INTO DEPARTMENT VALUES (44,'Education','Purdue North Central');");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 16
	    SQLParser.parse("INSERT INTO DEPARTMENT VALUES (1,'Pharmacal Sciences','Indianapolis');");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 17
	    SQLParser.parse("INSERT INTO DEPARTMENT VALUES (66,'Physics', 'West Lafayette');");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 18
	    SQLParser.parse("INSERT INTO STUDENT VALUES (14181,'Jack',11,'SO',17);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 19
	    SQLParser.parse("INSERT INTO STUDENT VALUES (16711,'A.Smith',22,'FR',20);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 20
	    SQLParser.parse("INSERT INTO STUDENT VALUES (12341,'Banks',33,'SR',18);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 21
	    SQLParser.parse("INSERT INTO STUDENT VALUES (37261,'M.Lee',1,'SO',22);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 22
	    SQLParser.parse("INSERT INTO STUDENT VALUES (48291,'Bale',33,'JR',22);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 23
	    SQLParser.parse("INSERT INTO STUDENT VALUES (57651,'Lim',11,'SR',19);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 24
	    SQLParser.parse("INSERT INTO STUDENT VALUES (10191,'Sharon',22,'FR',22);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 25
	    SQLParser.parse("INSERT INTO STUDENT VALUES (73571,'Johnson',11,'JR',27);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 26
	    SQLParser.parse("INSERT INTO STUDENT VALUES (80161,'E.Cho',1,'JR',27);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 27
	    SQLParser.parse("INSERT INTO STUDENT VALUES (80162,'Angin',11,'SR',25);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 28
	    SQLParser.parse("INSERT INTO FACULTY VALUES (1010,'Layton',11);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 29
	    SQLParser.parse("INSERT INTO FACULTY VALUES (1020,'Jungles',22);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 30
	    SQLParser.parse("INSERT INTO FACULTY VALUES (1030,'Guzaldo',1);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 31
	    SQLParser.parse("INSERT INTO FACULTY VALUES (1040,'Boling',44);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 32
	    SQLParser.parse("INSERT INTO FACULTY VALUES (1050,'Mason',11);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 33
	    SQLParser.parse("INSERT INTO FACULTY VALUES (1060,'Zwink',22);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 34
	    SQLParser.parse("INSERT INTO FACULTY VALUES (1070,'Walton',1);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 35
	    SQLParser.parse("INSERT INTO FACULTY VALUES (1080,'Teach',1);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 36
	    SQLParser.parse("INSERT INTO FACULTY VALUES (1090,'Jason',1);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 37
	    SQLParser.parse("INSERT INTO CLASS VALUES ('ENG400','8:30','U003',1040);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 38
	    SQLParser.parse("INSERT INTO CLASS VALUES ('ENG320', '10:30','R128',1040);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 39
	    SQLParser.parse("INSERT INTO CLASS VALUES ('COM10000', '12:30','L108',1040);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 40
	    SQLParser.parse("INSERT INTO CLASS VALUES ('ME30800', '12:00','R128',1020);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 41	    
	    SQLParser.parse("INSERT INTO CLASS VALUES ('CS448', '11:00','R128',1010);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 42
	    SQLParser.parse("INSERT INTO CLASS VALUES ('HIS21000', '11:00','L108',1040);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 43
	    SQLParser.parse("INSERT INTO CLASS VALUES ('MATH27500', '15:30','L108',1050);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 44
	    SQLParser.parse("INSERT INTO CLASS VALUES ('STAT11000', '13:00','R128',1050);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 45	    
	    SQLParser.parse("INSERT INTO CLASS VALUES ('PHYS10000', '14:00','U003',1010);");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 46
	    SQLParser.parse("INSERT INTO ENROLLED VALUES (14181,'CS448');");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 47
	    SQLParser.parse("INSERT INTO ENROLLED VALUES (14181,'MATH27500');");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 48
	    SQLParser.parse("INSERT INTO ENROLLED VALUES (12341,'ENG400');");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 49	    
	    SQLParser.parse("INSERT INTO ENROLLED VALUES (12341,'MATH27500');");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 50
	    SQLParser.parse("INSERT INTO ENROLLED VALUES (80161,'ENG400');");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 51
	    SQLParser.parse("INSERT INTO ENROLLED VALUES (80161,'ENG320');");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 52
	    SQLParser.parse("INSERT INTO ENROLLED VALUES (80161,'HIS21000');");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 53
	    SQLParser.parse("INSERT INTO ENROLLED VALUES (80161,'STAT11000');");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 54
	    SQLParser.parse("INSERT INTO ENROLLED VALUES (14181,'STAT11000');");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 55
	    SQLParser.parse("INSERT INTO ENROLLED VALUES (12341,'COM10000');");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 56
	    SQLParser.parse("INSERT INTO ENROLLED VALUES (16711,'ENG400');");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 57
	    SQLParser.parse("INSERT INTO ENROLLED VALUES (16711,'STAT11000');");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 58
	    SQLParser.parse("INSERT INTO ENROLLED VALUES (12341,'HIS21000');");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 59
	    SQLParser.parse("INSERT INTO ENROLLED VALUES (57651,'PHYS10000');");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 60
	    SQLParser.parse("INSERT INTO ENROLLED VALUES (57651,'ENG320');");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();

	    // 61
	    SQLParser.parse("INSERT INTO ENROLLED VALUES (57651,'COM10000');");
	    assertLinesEqual("Tuple inserted successfully", myOut.toString());
	    myOut.reset();
	    
	    SQLParser.parse("CREATE USER user1 User-A;");
	    assertLinesEqual("User created successfully", myOut.toString());
	    myOut.reset();

	    SQLParser.parse("CREATE USER user2 User-B;");
	    assertLinesEqual("User created successfully", myOut.toString());
	    myOut.reset();

	    SQLParser.parse("DELETE USER user3;");
	    assertLinesEqual("The user user3 does not exist.", myOut.toString());
	    myOut.reset();
	    
	    SQLParser.parse("CREATE USER user4 User-A;");
        assertLinesEqual("User created successfully", myOut.toString());
	    myOut.reset();

	    SQLParser.parse("DELETE USER user4;");
        assertLinesEqual("User deleted successfully", myOut.toString());
        myOut.reset();

	    SQLParser.parse("CREATE SUBSCHEMA STUDENT sname,slevel;");
        assertLinesEqual("Subschema created successfully", myOut.toString());
        myOut.reset();

	    SQLParser.parse("CREATE SUBSCHEMA DEPARTMENT dname,location;");
        assertLinesEqual("Subschema created successfully", myOut.toString());
        myOut.reset();

	    SQLParser.parse("CREATE SUBSCHEMA GRADE cname;");
        assertLinesEqual("The table named GRADE does not exist", myOut.toString());
        myOut.reset();

	    SQLParser.parse("CREATE SUBSCHEMA FACULTY fffid,fname;");
        assertLinesEqual("The requested schema does not contain the attribute fffid", myOut.toString());
        myOut.reset();

        Set<String> expected = new HashSet<String>();
		expected.addAll(Arrays.asList(new String[] {"DEPARTMENT", "FACULTY", "STUDENT", "CLASS", "ENROLLED"}));
		SQLParser.parse("HELP TABLES;");
		// Make sure every table is printed, but order doesn't matter
		String[] tables = myOut.toString().split("\n");
		Set<String> actual = new HashSet<String>();
		actual.addAll(Arrays.asList(tables));
		assertEquals(expected, actual);
		myOut.reset();

	    SQLParser.parse("HELP DESCRIBE CLASS;");
	    assertTrue(linesEqualIgnoreOrder(
	    		"cname -- CHAR(30) -- PRIMARY KEY\n"+
	    		"meets_at -- CHAR(30)\n"+
	    		"room -- CHAR(10)\n"+
	    		"faculty_id -- INT -- FOREIGN KEY REFERENCES FACULTY (fid)\n",
	    				myOut.toString()));
	    
	    
	    RDBMS.save();
        
	}

}
