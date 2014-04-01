package rdbms;

public class InvalidSQLException extends RDBMSException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3575263076306774667L;

	public InvalidSQLException(String message) {
		super(message);
	}
}
