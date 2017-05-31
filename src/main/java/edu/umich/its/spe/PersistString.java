package edu.umich.its.spe;

public interface PersistString {

	// Read the string for this path.
	String readString() throws PersistStringException;

	void writeString(String contents) throws PersistStringException;

}