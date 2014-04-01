package rdbms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Store global information such as users and tables.
 */
public class Database {
	static Tables tables;
	static Set<User> users;

	static User admin = new User("admin", User.Type.ADMIN);
	static User loggedInUser = admin; // for testing purposes

	static {
		tables = new Tables();
		users = new HashSet<User>();
	}

	public static boolean addUser(User newUser) {
		return users.add(newUser);
	}

	/**
	 * Return the user that has this username, or null if the user does not
	 * exist
	 * 
	 * @param username
	 * @return
	 */
	public static User getUser(String username) {
		if (username.equals("admin"))
			return admin;

		for (User u : users) {
			if (u.getName().equals(username))
				return u;
		}
		return null;
	}

	public static void loadUsers(File file) throws IOException, ClassNotFoundException {
		FileInputStream fileIn = new FileInputStream(file);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		users = (Set<User>) in.readObject();
		in.close();
		fileIn.close();
	}

	public static void saveUsers(File file) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(file);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(users);
		out.close();
		fileOut.close();
	}

	public static void removeUser(User userToDelete) {
		users.remove(userToDelete);
	}

	public static void login(User user) {
		loggedInUser = user;
	}

	public static User getLoggedInUser() {
		return loggedInUser;
	}

	public static Rows select(List<String> selectedAttributes, List<String> selectedTables, Conditions conditions) throws SchemaViolationException {
		// if no Conditions specified, create a new one with no conditions (always passes)
		if (conditions == null)
			conditions = new Conditions();

		Table first = tables.get(selectedTables.get(0));
		// project to allowed attributes only
		Rows crossProduct = first.rows.project(first.visibleSchema());
		for (int i = 1; i < selectedTables.size(); i++) {
			Table next = tables.get(selectedTables.get(1));
			crossProduct = crossProduct.cross(next.rows.project(next.visibleSchema()));
		}

		Attributes projectedAttributes = new Attributes();
		for (String attrName : selectedAttributes) {
			projectedAttributes.add(crossProduct.schema.get(attrName));
		}

		Rows result = crossProduct.getAll(conditions).project(projectedAttributes);

		return result;
	}

	public static List<User> getUsers() {
		List<User> userList = new ArrayList<User>();
		userList.add(admin);
		userList.addAll(users);
		return userList;
	}

	static void insertIntoDB(Table t) {
		tables.add(t);
		try {
			tables.saveTableFiles(new File(System.getProperty("user.dir")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void clear() {
		tables.clear();
	}

}
