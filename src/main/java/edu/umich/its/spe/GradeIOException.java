package edu.umich.its.spe;

public class GradeIOException extends Exception {

	/*
	 * Exception for non-recoverable errors in ESB access. 
	 */
	
	private static final long serialVersionUID = -2232997115239589804L;

	GradeIOException(String message) {
		super(message);
	}

	public GradeIOException(String message, Throwable e) {
		super(message,e);
	}

}
