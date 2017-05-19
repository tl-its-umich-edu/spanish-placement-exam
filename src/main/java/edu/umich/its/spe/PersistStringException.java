package edu.umich.its.spe;

public class PersistStringException extends Exception {

	/*
	 * Exception for un-recoverable errors in PersistString 
	 */
	
	private static final long serialVersionUID = -2232997115239589804L;

	PersistStringException(String message) {
		super(message);
	}

	public PersistStringException(String message, Throwable e) {
		super(message,e);
	}

}
