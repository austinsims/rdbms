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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof User))
			return false;
		User other = (User) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	
}
