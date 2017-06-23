package edu.umich.its.spe;

public class SPEEsbException extends Exception {

	/*
	 * Exception for non-recoverable errors in ESB access. 
	 */
	
	private static final long serialVersionUID = -2232997115239589804L;

	SPEEsbException(String message) {
		super(message);
	}

	public SPEEsbException(String message, Throwable e) {
		super(message,e);
	}

}
