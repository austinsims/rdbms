package rdbms;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Interactive console to use the RDBMS.
 */
public class Interactive {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		while (in.hasNextLine()) {
			
			String command;
			try {
				
				command = in.nextLine();
			} catch (NoSuchElementException e) {
				continue;
			}
			try {
				SQLParser.parse(command);
			} catch (InvalidSQLException e) {
				System.err.println(e.getMessage());
			}
		}
	}
}
