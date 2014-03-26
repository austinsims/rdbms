package rdbms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.io.Console;

/**
 * Interactive console to use the RDBMS.
 */
public class RDBMS {
	public static void main(String[] args) {
		
		// Load all tables from files.
		try {
			Table.tables.load(new File(System.getProperty("user.dir")));
		} catch (ClassNotFoundException e) {
			System.err.println("Could not find Table class. Something has gone horribly wrong.");
			System.exit(1);
		}
		
		// See if we have a filename as an argument
		
		Scanner in = null;
		if (args.length > 0) {
			try {
				in = new Scanner(new File(args[0]));
			} catch (FileNotFoundException e) {
				System.err.println("Could not read from the file " + args[0]);
				System.exit(1);
			}
		} else {
			in = new Scanner(System.in);
			// Display welcome
			System.out.println("Welcome to SimsSQL!  Input commands, or say HELP if you're clueless.");
		}

		while (in.hasNextLine()) {
			
			String command;
			int returnCode;
			try {
				
				command = in.nextLine();
			} catch (NoSuchElementException e) {
				continue;
			}
			try {
				returnCode = SQLParser.parse(command);
				if (returnCode == SQLParser.QUIT)
					break;
			} catch (InvalidSQLException e) {
				System.err.println(e.getMessage());
			}
		}
		in.close();
		
		// Save all tables to files.
		try {
			Table.tables.save(new File(System.getProperty("user.dir")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
