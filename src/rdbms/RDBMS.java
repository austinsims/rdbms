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
		String username = null;
		String filename = null;
		// Check arguments.
		if (args.length == 1) {
			username = args[0];
		} else if (args.length == 2) {
			username = args[0];
			filename = args[1];
		} else {
			System.err.println("Usage: java rdbms.RDBMS username [statements.sql]");
		}

		load();

		// Get user object from username arg
		User loggedInUser = Database.getUser(username);
		if (loggedInUser == null) {
			System.err.println("User " + username + " not found. Exiting.");
			System.exit(1);
		}
		Database.login(loggedInUser);

		Scanner in = null;
		if (filename != null) {
			try {
				in = new Scanner(new File(filename));
			} catch (FileNotFoundException e) {
				System.err.println("Could not read from the file " + filename);
				System.exit(1);
			}
		} else {
			in = new Scanner(System.in);
			// Display welcome
			System.out.println("Welcome to SimsSQL!  Input commands, or say HELP if you're clueless.");
			System.out.println("You are currently logged in as " + loggedInUser.getName() + ", and you are of type " + loggedInUser.getType());
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
			} catch (PermissionException e) {
				// TODO Auto-generated catch block
				System.err.println("Permissions exception: " + e.getMessage());
			}
		}
		in.close();

		save();
	}

	public static void save() {
		// Save all tables to files.
		try {
			Database.tables.saveTableFiles(new File(System.getProperty("user.dir")));
			Database.saveUsers(new File("users.ser"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void load() {
		// Load all tables and users from files.
		try {
			Database.tables.load(new File(System.getProperty("user.dir")));
			File usersFile = new File("users.ser");
			if (usersFile.exists())
				Database.loadUsers(usersFile);
		} catch (ClassNotFoundException e) {
			System.err.println("Could not find Table class. Something has gone horribly wrong.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Could not load file users.ser: " + e.getMessage());
		}
	}
}
