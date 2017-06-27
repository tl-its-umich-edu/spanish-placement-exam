package edu.umich.its.spe;

public class PersistBlobException extends Exception {

	/*
	 * Exception for un-recoverable errors in PersistString 
	 */
	
	private static final long serialVersionUID = -2232997115239589804L;

	PersistBlobException(String message) {
		super(message);
	}

	public PersistBlobException(String message, Throwable e) {
		super(message,e);
	}

}
