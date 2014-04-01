package rdbms;

import java.io.Serializable;

import rdbms.User.Type;

public class User implements Serializable {
	enum Type {
		B("User-B"),
		A("User-A"),
		ADMIN("Admin");
		
		private String stringRepresentation;
		
		Type(String s) {
			stringRepresentation = s;
		}

		public static Type fromString(String s) {
			// TODO Auto-generated method stub
			if (s != null) {
				for (Type t : Type.values()) {
					if (s.equalsIgnoreCase(t.stringRepresentation)) {
						return t;
					}
				}
			}
			return null;
		}
	}
	
	Type type;
	String name;
	
	public User(String username, Type usertype) {
		this.name = username;
		this.type = usertype;
	}

	public Type getType() {
		return type;
	}
	
	public boolean equals(Object o) {
		User other;
		try {
			other = (User) o;
		} catch (ClassCastException e) {
			return false;
		}
		if (!other.name.equals(this.name)) return false;
		if (!other.type.equals(this.type)) return false;
		
		return true;
		
	}

	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	
}
