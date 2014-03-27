package rdbms;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashSet;

import sun.security.util.ObjectIdentifier;

@SuppressWarnings("unused")
public class Tables extends HashSet<Table> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7629053190541454720L;

	public boolean contains(String name) {
		for (Table t : this)
			if (t.getName().equals(name))
				return true;
		return false;
	}

	/**
	 * Return the table named "name", or null if it does not exist
	 * 
	 * @param name
	 * @return
	 */
	public Table get(String name) throws SchemaViolationException {
		for (Table t : this)
			if (t.getName().equals(name))
				return t;
		throw new SchemaViolationException("The table named " + name + " does not exist");
	}

	public void saveTableFiles(File directory) throws IOException {
		for (Table t : this) {
			String filename = t.getName() + ".table";
			File file = new File(directory, filename);
			t.save(file);
		}
	}

	public void load(File directory) throws ClassNotFoundException {
		// clear everything out first
		this.clear();
		
		FileFilter tableFileFilter = new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return pathname.getName().endsWith(".table");
			}
			
		};
		
		File[] tableFiles = directory.listFiles(tableFileFilter); 
		for (File f : tableFiles) {
			try {
				this.add(Table.load(f));
			} catch (IOException e) {
				System.err.println("Could not load file " + f.getName() + ".  Continuing.");
			}
		}

	}
}
