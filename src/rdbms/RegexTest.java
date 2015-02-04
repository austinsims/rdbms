package rdbms;

public class RegexTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str = "INSERT INTO department VALUES('Computer, (SCIE,NCE)',50,14,'Hello world (hi there!)');";

		/*
		str = str.replaceAll("(?x) " + 
		               ",          " +   // Replace ,
		               "(?=        " +   // Followed by
		               "  (?:      " +   // Start a non-capture group
		               "    [^']*  " +   // 0 or more non-single quote characters
		               "    '      " +   // 1 single quote
		               "    [^']*  " +   // 0 or more non-single quote characters
		               "    '      " +   // 1 single quote
		               "  )*       " +   // 0 or more repetition of non-capture group (multiple of 2 quotes will be even)
		               "  [^']*    " +   // Finally 0 or more non-single quotes
		               "  $        " +   // Till the end  (This is necessary, else every _ will satisfy the condition)
		               ")          " ,   // End look-ahead
		                       " , ");      // Replace with " , "
		                       */
		
		
		str = pad(str, ",");
		str = pad(str, "\\(");
		str = pad(str, "\\)");
		
		System.out.println(str);
	}
	
	public static String pad(String s, String ch) {
        return s.replaceAll("(?x) " + 
         String.format("%s         ",ch) +   // Replace ch
		               "(?=        " +   // Followed by
		               "  (?:      " +   // Start a non-capture group
		               "    [^']*  " +   // 0 or more non-single quote characters
		               "    '      " +   // 1 single quote
		               "    [^']*  " +   // 0 or more non-single quote characters
		               "    '      " +   // 1 single quote
		               "  )*       " +   // 0 or more repetition of non-capture group (multiple of 2 quotes will be even)
		               "  [^']*    " +   // Finally 0 or more non-single quotes
		               "  $        " +   // Till the end  (This is necessary, else every _ will satisfy the condition)
		               ")          " ,   // End look-ahead
         String.format(" %s ",ch));      // Replace with " , "

	}

}
