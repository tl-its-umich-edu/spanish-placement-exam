package edu.umich.its.spe;

/*
 * Interface for object to read/write a persistent string based only on resource path name.  
 * This is meant to provide a very simple way to store very small amounts of data.
 */
public interface PersistString {

	// Read the string for this path.
	String readString() throws PersistStringException;

	// write a string to this path.
	void writeString(String contents) throws PersistStringException;
	
	// empty out string if it exists.
	void zapString() throws PersistStringException;

}